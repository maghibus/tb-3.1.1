/**
 * Copyright © 2016-2020 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static org.thingsboard.server.common.data.exception.ThingsboardErrorCode.BAD_REQUEST_PARAMS;

/**
 * @author Federico Serini
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
@ConditionalOnProperty(prefix = "features", value = "gis_integration", havingValue = "true")
public class GisIntegrationController extends BaseController {

    private static final String DEVICE_ID = "deviceId";
    private static final String SERVER_SCOPE = "SERVER_SCOPE";
    private static final String DASHBOARD_ID = "dashboardId";
    private static final String DASHBOARD_STATE = "dashboardState";
    private static final String GIS_DEVICE_TYPE = "gisDeviceType";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/gis/device/info/{gisDeviceType}")
    @ResponseBody
    public GisDeviceInfo getDeviceInfoForGis(@PathVariable String gisDeviceType) throws ThingsboardException {

        PageLink noPaging = new PageLink(Integer.MAX_VALUE);
        SecurityUser securityUser = getCurrentUser();
        TenantId tenantId = securityUser.getTenantId();
        CustomerId customerId = securityUser.getCustomerId();

        if (securityUser.isTenantAdmin()) {
            PageData<Device> devicePageData = deviceService.findDevicesByTenantId(tenantId, noPaging);
            return composeGisDeviceInfo(tenantId, devicePageData, gisDeviceType);
        } else if (securityUser.isCustomerUser()) {
            PageData<Device> devicePageData = deviceService.findDevicesByTenantIdAndCustomerId(tenantId, customerId, noPaging);
            return composeGisDeviceInfo(tenantId, devicePageData, gisDeviceType);
        }

        return new GisDeviceInfo();
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/gis/tenant/dashboards/states")
    @ResponseBody
    public List<DashboardInfoWithStates> getTenantDashboardsWithStates() throws ThingsboardException {
        List<DashboardInfoWithStates> dashboardInfoWithStatesList = new LinkedList<>();

        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            // This actually does not filter by tenantId
            List<Dashboard> dashboardInfo = checkNotNull(dashboardService.findDashboardByTenantId(tenantId));
            dashboardInfo = dashboardInfo.stream().filter(x -> tenantId.equals(x.getTenantId())).collect(Collectors.toList());

            for (Dashboard dashboard : dashboardInfo) {
                DashboardInfoWithStates dashboardInfoWithStates = new DashboardInfoWithStates();
                dashboardInfoWithStates.setId(dashboard.getId());
                dashboardInfoWithStates.setTitle(dashboard.getTitle());
                dashboardInfoWithStates.setAssignedCustomers(dashboard.getAssignedCustomers());
                dashboardInfoWithStates.setCreatedTime(dashboard.getCreatedTime());
                dashboardInfoWithStates.setTenantId(dashboard.getTenantId());
                dashboardInfoWithStates.setStates(extractDashboardStates(dashboard.getConfiguration()));
                dashboardInfoWithStatesList.add(dashboardInfoWithStates);
            }
        } catch (Exception e) {
            throw handleException(e);
        }

        dashboardInfoWithStatesList = dashboardInfoWithStatesList.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(DashboardInfoWithStates::getTitle))),
                        ArrayList::new));

        return dashboardInfoWithStatesList;
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/gis/dashboard/attributes/server/device/{deviceId}")
    @ResponseBody
    public DashboardServerAttributes saveServerAttribute(@PathVariable(DEVICE_ID) String strDeviceId, @RequestBody DashboardServerAttributes dashboardServerAttributes) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        TenantId tenantId = getCurrentUser().getTenantId();
        DeviceId deviceId = new DeviceId(toUUID(strDeviceId));

        if (dashboardServerAttributes.getDashboardId() != null && dashboardServerAttributes.getDashboardState() != null) {
            checkDashboardId(new DashboardId(toUUID(dashboardServerAttributes.getDashboardId())), Operation.READ);

            List<AttributeKvEntry> baseAttributeKvEntryList = new LinkedList<>();
            baseAttributeKvEntryList.add(new BaseAttributeKvEntry(System.currentTimeMillis(), new StringDataEntry(DASHBOARD_ID, dashboardServerAttributes.getDashboardId())));
            baseAttributeKvEntryList.add(new BaseAttributeKvEntry(System.currentTimeMillis(), new StringDataEntry(DASHBOARD_STATE, dashboardServerAttributes.getDashboardState())));
            baseAttributeKvEntryList.add(new BaseAttributeKvEntry(System.currentTimeMillis(), new StringDataEntry(GIS_DEVICE_TYPE, dashboardServerAttributes.getGisDeviceType())));

            attributesService.save(tenantId, deviceId, SERVER_SCOPE, baseAttributeKvEntryList);
        } else {
            throw new ThingsboardException("Missing DashboardServerAttributes body parameters", BAD_REQUEST_PARAMS);
        }

        return dashboardServerAttributes;
    }

    private GisDeviceInfo composeGisDeviceInfo(TenantId tenantId, PageData<Device> devicePageData, String gisDeviceType) {
        GisDeviceInfo gisDeviceInfo = new GisDeviceInfo();
        List<GisDevicePropertiesList> list = new LinkedList<>();
        gisDeviceType = gisDeviceType.toLowerCase();
        gisDeviceType = gisDeviceType.trim();
        for (Device x : devicePageData.getData()) {
            try {
                List<AttributeKvEntry> attributeKvEntries = attributesService.findAll(tenantId, x.getId(), SERVER_SCOPE).get();
                String type = getAttributeValueByKey(GIS_DEVICE_TYPE, attributeKvEntries);
                if (type != null) {
                    type = type.toLowerCase();
                    type = type.trim();
                    if (type.equals(gisDeviceType)) {
                        String dashboardId = getAttributeValueByKey(DASHBOARD_ID, attributeKvEntries);
                        String dashboardState = getAttributeValueByKey(DASHBOARD_STATE, attributeKvEntries);
                        String latitude = getAttributeValueByKey("latitude", attributeKvEntries);
                        String longitude = getAttributeValueByKey("longitude", attributeKvEntries);
                        String lat = getAttributeValueByKey("lat", attributeKvEntries);
                        String lon = getAttributeValueByKey("lon", attributeKvEntries);

                        GisDeviceGeometry gisDeviceGeometry = setGisDeviceGeometry(latitude, lat, longitude, lon);

                        if (gisDeviceGeometry != null) {
                            GisDevicePropertiesList gisDevicePropertiesList = new GisDevicePropertiesList();
                            gisDevicePropertiesList.setGeometry(gisDeviceGeometry);
                            gisDevicePropertiesList.setProperties(setGisDeviceProperties(x, dashboardId, dashboardState, tenantId));
                            list.add(gisDevicePropertiesList);
                        }

                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        gisDeviceInfo.setFeatures(list);
        return gisDeviceInfo;
    }

    @SneakyThrows
    private GisDeviceProperties setGisDeviceProperties(Device device, String dashboardId, String dashboardState, TenantId tenantId) {
        GisDeviceProperties gisDeviceProperties = new GisDeviceProperties();
        gisDeviceProperties.setDevice(device);

        if (dashboardId != null && dashboardState != null) {
            DashboardInfo dashboardInfo = dashboardService.findDashboardById(tenantId, new DashboardId(UUID.fromString(dashboardId)));
            gisDeviceProperties.setDashboardName(dashboardInfo.getName());
            gisDeviceProperties.setDashboardId(dashboardId);


            gisDeviceProperties.setDashboardUri("/iot-fe/dashboard/" + dashboardId + "?state=" + Base64.getEncoder().encodeToString(
                    ("[{\"id\":\"" + dashboardState + "\",\"params\":{\"entityId\":{\"entityType\":\"DEVICE\",\"id\":\"" + device.getId() + "\"},\"entityName\":\"" + device.getName() + "\"}}]").getBytes(StandardCharsets.UTF_8)
            ));
        }

        return gisDeviceProperties;
    }

    private GisDeviceGeometry setGisDeviceGeometry(String latitude, String lat, String longitude, String lon) {

        if ((longitude != null || lon != null) && (latitude != null || lat != null)) {
            GisDeviceGeometry gisDeviceGeometry = new GisDeviceGeometry();
            List<Double> doubles = new LinkedList<>();

            try {
                doubles.add(longitude != null ? new Double(longitude) : new Double(lon));
                doubles.add(latitude != null ? new Double(latitude) : new Double(lat));
            } catch (Exception e) {
                return null;
            }

            gisDeviceGeometry.setCoordinates(doubles);
            return gisDeviceGeometry;
        }

        return null;
    }

    private String getAttributeValueByKey(String key, List<AttributeKvEntry> attributeKvEntries) {

        for (AttributeKvEntry attributeKvEntry : attributeKvEntries) {
            if (attributeKvEntry.getKey().equals(key)) {
                return attributeKvEntry.getValueAsString();
            }
        }

        return null;
    }

    private List<String> extractDashboardStates(JsonNode configuration) {
        if (configuration.get("states") != null) {
            List<String> keys = new LinkedList<>();

            Iterator<String> iterator = configuration.get("states").fieldNames();
            iterator.forEachRemaining(keys::add);
            return keys;
        }

        return Collections.singletonList("default");
    }
}
