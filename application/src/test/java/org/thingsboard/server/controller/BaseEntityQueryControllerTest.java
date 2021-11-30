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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.multiplecustomer.AssetWithMultipleCustomers;
import org.thingsboard.server.common.data.multiplecustomer.DeviceWithMultipleCustomers;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.*;
import org.thingsboard.server.common.data.security.Authority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseEntityQueryControllerTest extends AbstractControllerTest {

    private Tenant savedTenant;
    private User tenantAdmin;
    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void beforeTest() throws Exception {
        loginSysAdmin();

        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        savedTenant = doPost("/api/tenant", tenant, Tenant.class);
        Assert.assertNotNull(savedTenant);

        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@thingsboard.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");

        tenantAdmin = createUserAndLogin(tenantAdmin, "testPassword1");
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/" + savedTenant.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void testCountEntitiesByQuery() throws Exception {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }
        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceType("default");
        filter.setDeviceNameFilter("");

        EntityCountQuery countQuery = new EntityCountQuery(filter);

        Long count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        filter.setDeviceType("unknown");
        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());

        filter.setDeviceType("default");
        filter.setDeviceNameFilter("Device1");

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(11, count.longValue());

        EntityListFilter entityListFilter = new EntityListFilter();
        entityListFilter.setEntityType(EntityType.DEVICE);
        entityListFilter.setEntityList(devices.stream().map(Device::getId).map(DeviceId::toString).collect(Collectors.toList()));

        countQuery = new EntityCountQuery(entityListFilter);

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());
    }

    @Test
    public void testSimpleFindEntityDataByQuery() throws Exception {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }

        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceType("default");
        filter.setDeviceNameFilter("");

        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));

        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, null, null);

        PageData<EntityData> data =
                doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
                });

        Assert.assertEquals(97, data.getTotalElements());
        Assert.assertEquals(10, data.getTotalPages());
        Assert.assertTrue(data.hasNext());
        Assert.assertEquals(10, data.getData().size());

        List<EntityData> loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(97, loadedEntities.size());

        List<EntityId> loadedIds = loadedEntities.stream().map(EntityData::getEntityId).collect(Collectors.toList());
        List<EntityId> deviceIds = devices.stream().map(Device::getId).collect(Collectors.toList());

        Assert.assertEquals(deviceIds, loadedIds);

        List<String> loadedNames = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ENTITY_FIELD).get("name").getValue()).collect(Collectors.toList());
        List<String> deviceNames = devices.stream().map(Device::getName).collect(Collectors.toList());

        Assert.assertEquals(deviceNames, loadedNames);

        sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "name"), EntityDataSortOrder.Direction.DESC
        );

        pageLink = new EntityDataPageLink(10, 0, "device1", sortOrder);
        query = new EntityDataQuery(filter, pageLink, entityFields, null, null);
        data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });
        Assert.assertEquals(11, data.getTotalElements());
        Assert.assertEquals("Device19", data.getData().get(0).getLatest().get(EntityKeyType.ENTITY_FIELD).get("name").getValue());

    }

    @Test
    public void testFindEntityDataByQueryWithAttributes() throws Exception {

        List<Device> devices = new ArrayList<>();
        List<Long> temperatures = new ArrayList<>();
        List<Long> highTemperatures = new ArrayList<>();
        for (int i = 0; i < 67; i++) {
            Device device = new Device();
            String name = "Device" + i;
            device.setName(name);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device?accessToken=" + name, device, Device.class));
            Thread.sleep(1);
            long temperature = (long) (Math.random() * 100);
            temperatures.add(temperature);
            if (temperature > 45) {
                highTemperatures.add(temperature);
            }
        }
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            String payload = "{\"temperature\":" + temperatures.get(i) + "}";
            doPost("/api/plugins/telemetry/" + device.getId() + "/" + DataConstants.SHARED_SCOPE, payload, String.class, status().isOk());
        }
        Thread.sleep(1000);

        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceType("default");
        filter.setDeviceNameFilter("");

        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));
        List<EntityKey> latestValues = Collections.singletonList(new EntityKey(EntityKeyType.ATTRIBUTE, "temperature"));

        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, null);
        PageData<EntityData> data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });

        List<EntityData> loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(67, loadedEntities.size());

        List<String> loadedTemperatures = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ATTRIBUTE).get("temperature").getValue()).collect(Collectors.toList());
        List<String> deviceTemperatures = temperatures.stream().map(aLong -> Long.toString(aLong)).collect(Collectors.toList());
        Assert.assertEquals(deviceTemperatures, loadedTemperatures);

        pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        KeyFilter highTemperatureFilter = new KeyFilter();
        highTemperatureFilter.setKey(new EntityKey(EntityKeyType.ATTRIBUTE, "temperature"));
        NumericFilterPredicate predicate = new NumericFilterPredicate();
        predicate.setValue(FilterPredicateValue.fromDouble(45));
        predicate.setOperation(NumericFilterPredicate.NumericOperation.GREATER);
        highTemperatureFilter.setPredicate(predicate);
        List<KeyFilter> keyFilters = Collections.singletonList(highTemperatureFilter);

        query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, keyFilters);

        data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });
        loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(highTemperatures.size(), loadedEntities.size());

        List<String> loadedHighTemperatures = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ATTRIBUTE).get("temperature").getValue()).collect(Collectors.toList());
        List<String> deviceHighTemperatures = highTemperatures.stream().map(aLong -> Long.toString(aLong)).collect(Collectors.toList());

        Assert.assertEquals(deviceHighTemperatures, loadedHighTemperatures);

    }


    @Test
    public void entitiesQueryFind() throws Exception {
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        Device savedDevice = doPost("/api/device", device, Device.class);

        Asset asset = new Asset();
        asset.setName("My Asset");
        asset.setType("default");
        Asset savedAsset = doPost("/api/asset", asset, Asset.class);

        Customer customer = new Customer();
        customer.setTitle("Customer To ADD 1");
        Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

        String[] strCustomerIds = {customerToAdd1.getId().toString()};
        doPatch("/api/device/" + savedDevice.getId().getId().toString() + "/customer/association", strCustomerIds, DeviceWithMultipleCustomers.class);
        doPatch("/api/asset/" + savedAsset.getId().getId().toString() + "/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);


        String json = "{\"entityFilter\": {\n" +
                "                  \"rootEntity\": {\n" +
                "                    \"id\": \"" +
                savedDevice.getId().getId().toString() +
                "\"" +
                ",\n" +
                "                    \"entityType\": \"DEVICE\"\n" +
                "                  },\n" +
                "                  \"direction\": \"FROM\",\n" +
                "                  \"filters\": [\n" +
                "                    {\n" +
                "                      \"relationType\": \"Contains\",\n" +
                "                      \"entityTypes\": []\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"maxLevel\": 1,\n" +
                "                  \"type\": \"relationsQuery\"\n" +
                "                },\n" +
                "                \"pageLink\": {\n" +
                "                  \"pageSize\": 1024,\n" +
                "                  \"page\": 0,\n" +
                "                  \"sortOrder\": {\n" +
                "                    \"key\": {\n" +
                "                      \"type\": \"ENTITY_FIELD\",\n" +
                "                      \"key\": \"createdTime\"\n" +
                "                    },\n" +
                "                    \"direction\": \"DESC\"\n" +
                "                  }\n" +
                "                },\n" +
                "                \"entityFields\": [\n" +
                "                  {\n" +
                "                    \"type\": \"ENTITY_FIELD\",\n" +
                "                    \"key\": \"name\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"type\": \"ENTITY_FIELD\",\n" +
                "                    \"key\": \"label\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"type\": \"ENTITY_FIELD\",\n" +
                "                    \"key\": \"additionalInfo\"\n" +
                "                  }\n" +
                "                ],\n" +
                "                \"latestValues\": []\n" +
                "              }";

        String json2 = "{\"entityFilter\":{\"type\":\"singleEntity\",\"singleEntity\":{\"entityType\":\"DEVICE\",\"id\":\""+
                savedDevice.getId().getId().toString()+"\"}},\"pageLink\":{\"pageSize\":1,\"page\":0,\"sortOrder\":{\"key\":{\"type\":\"ENTITY_FIELD\",\"key\":\"createdTime\"},\"direction\":\"DESC\"}},\"entityFields\":[{\"type\":\"ENTITY_FIELD\",\"key\":\"name\"},{\"type\":\"ENTITY_FIELD\",\"key\":\"label\"},{\"type\":\"ENTITY_FIELD\",\"key\":\"additionalInfo\"}]}";


        String json3 = "{\n" +
                "\"entityFilter\": {\n" +
                "\"type\": \"deviceSearchQuery\",\n" +
                "\"resolveMultiple\": true,\n" +
                "\"rootStateEntity\": false,\n" +
                "\"stateEntityParamName\": null,\n" +
                "\"defaultStateEntity\": null,\n" +
                "\"rootEntity\": {\n" +
                "\"entityType\": \"ASSET\",\n" +
                "\"id\": \"" +
                savedAsset.getId().getId().toString() +
                "\"\n" +
                "},\n" +
                "\"direction\": \"FROM\",\n" +
                "\"maxLevel\": 1,\n" +
                "\"fetchLastLevelOnly\": false,\n" +
                "\"relationType\": null,\n" +
                "\"deviceTypes\": [\n" +
                "\"Termostato\"\n" +
                "]\n" +
                "},\n" +
                "\"pageLink\": {\n" +
                "\"page\": 0,\n" +
                "\"pageSize\": 10,\n" +
                "\"textSearch\": null,\n" +
                "\"dynamic\": true,\n" +
                "\"sortOrder\": {\n" +
                "\"key\": {\n" +
                "\"key\": \"name\",\n" +
                "\"type\": \"ENTITY_FIELD\"\n" +
                "},\n" +
                "\"direction\": \"ASC\"\n" +
                "}\n" +
                "},\n" +
                "\"entityFields\": [\n" +
                "{\n" +
                "\"type\": \"ENTITY_FIELD\",\n" +
                "\"key\": \"name\"\n" +
                "},\n" +
                "{\n" +
                "\"type\": \"ENTITY_FIELD\",\n" +
                "\"key\": \"label\"\n" +
                "},\n" +
                "{\n" +
                "\"type\": \"ENTITY_FIELD\",\n" +
                "\"key\": \"additionalInfo\"\n" +
                "}\n" +
                "],\n" +
                "\"latestValues\": []\n" +
                "}";
        EntityDataQuery query = objectMapper.readValue(json3, EntityDataQuery.class);

        PageData<EntityData> data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {});
        assertTrue(data.getData().size()>0);
    }

    @Test
    public void testEntitiesListAsTenantAdmin() throws Exception {
        // given
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        Device savedDevice = doPost("/api/device", device, Device.class);

        Device device2 = new Device();
        device2.setName("My device 2");
        device2.setType("default");
        Device savedDevice2 = doPost("/api/device", device2, Device.class);

        EntityListFilter filter = new EntityListFilter();
        filter.setEntityType(EntityType.DEVICE);
        filter.setEntityList(Arrays.asList(savedDevice.getId().toString(), savedDevice2.getId().toString()));

        EntityDataSortOrder sortOrder = new EntityDataSortOrder(new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC);
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));
        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, null, null);

        // when
        PageData<EntityData> data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {});
        List loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }

        //then
        Assert.assertEquals(2, loadedEntities.size());

    }

}
