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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.multiplecustomer.DeviceWithMultipleCustomers;
import org.thingsboard.server.common.data.multiplecustomer.MultipleCustomerInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@ConditionalOnProperty(prefix = "features", value = "multiple_customer_enabled", havingValue = "true")
public class DeviceCustomerAssociationController extends BaseController{

    private static final String DEVICE_ID = "deviceId";
    private static final String CUSTOMER_ID = "customerId";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/info/{deviceId}/association/multiple", method = RequestMethod.GET)
    @ResponseBody
    public DeviceWithMultipleCustomers getDeviceInfoById(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            return checkDeviceWithMultipleCustomersDeviceId(deviceId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PatchMapping(value = "/customer/public/device/{deviceId}/association/multiple")
    @ResponseBody
    public DeviceWithMultipleCustomers assignDeviceToPublicCustomer(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.ASSIGN_TO_CUSTOMER);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(device.getTenantId());

            deviceService.unassignAllDeviceCustomerAssociation(getCurrentUser().getTenantId(), deviceId);

            checkNotNull(deviceService.assignDeviceToCustomers(getCurrentUser().getTenantId(), deviceId, publicCustomer.getId()));
            DeviceWithMultipleCustomers deviceWithMultipleCustomers = deviceService.findDeviceInfoWithMultipleCustomerByDeviceId(deviceId);
            logEntityAction(deviceId, deviceWithMultipleCustomers,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strDeviceId, publicCustomer.getId().toString(), publicCustomer.getName());

            return deviceWithMultipleCustomers;
        } catch (Exception e) {
            e.printStackTrace();
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strDeviceId);
            throw handleException(e);
        }
    }

    // ex getTenantDeviceInfos
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/tenant/device/info/customer/multiple", params = {"pageSize", "page"})
    @ResponseBody
    public PageData<DeviceWithMultipleCustomers> getTenantDeviceInfoMultipleCustomer(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDeviceInfoWithMultipleCustomerByTenantIdAndType(tenantId, type, pageLink));
            }

            return checkNotNull(deviceService.findDeviceInfoWithMultipleCustomerByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // ex getCustomerDeviceInfos
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/device/info/customer/multiple", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DeviceWithMultipleCustomers> getCustomerDeviceInfos(
            @PathVariable(CUSTOMER_ID) String strCustomerId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter(CUSTOMER_ID, strCustomerId);

        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId, Operation.READ);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            }

            return checkNotNull(deviceService.findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerId(tenantId, customerId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // ex assignDeviceToCustomer + unassignDeviceFromCustomer
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PatchMapping(value = "/device/{deviceId}/customer/association")
    @ResponseBody
    public DeviceWithMultipleCustomers updateCustomerDeviceAssociation(@PathVariable(DEVICE_ID) String strDeviceId,
                                                                       @RequestBody String[] strCustomerIds) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);

        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            DeviceWithMultipleCustomers deviceWithMultipleCustomers = checkDeviceWithMultipleCustomersDeviceId(deviceId, Operation.ASSIGN_TO_CUSTOMER);

            Set<CustomerId> customerIds = new HashSet<>();
            if(strCustomerIds != null){
                Arrays.stream(strCustomerIds).forEach(x -> customerIds.add(new CustomerId(toUUID(x))));
            }

            Map<CustomerId, MultipleCustomerInfo> customerInfoMap =  deviceWithMultipleCustomers
                    .getCustomerInfo().stream().collect(Collectors.toMap(MultipleCustomerInfo::getCustomerId, Function.identity()));

            Set<CustomerId> addedCustomerIds = new HashSet<>();
            Set<CustomerId> removedCustomerIds = new HashSet<>();
            customerIds.forEach(customerId -> {
                if (!customerInfoMap.containsKey(customerId)) {
                    addedCustomerIds.add(customerId);
                } else {
                    removedCustomerIds.add(customerId);
                }
            });


            if (!addedCustomerIds.isEmpty() || !removedCustomerIds.isEmpty()) {
                for (CustomerId addedCustomerId : addedCustomerIds) {
                    Customer addedCustomer = customerService.findCustomerById(getCurrentUser().getTenantId(), addedCustomerId);
                    checkNotNull(deviceService.assignDeviceToCustomers(getCurrentUser().getTenantId(), deviceId, addedCustomerId));

                    MultipleCustomerInfo customerInfo = new MultipleCustomerInfo(addedCustomer.getTitle(), addedCustomer.getAdditionalInfo(), addedCustomer.getId().getId());

                    deviceWithMultipleCustomers.getCustomerInfo().add(customerInfo);
                    logEntityAction(deviceId, deviceWithMultipleCustomers,
                            addedCustomerId,
                            ActionType.ASSIGNED_TO_CUSTOMER, null, strDeviceId, addedCustomerId.toString(), customerInfo.getCustomerTitle());
                }
                for (CustomerId customerId : removedCustomerIds) {
                    MultipleCustomerInfo customerInfo = customerInfoMap.get(customerId);
                    checkNotNull(deviceService.unassignDeviceFromCustomer(getCurrentUser().getTenantId(), deviceId, customerId));
                    deviceWithMultipleCustomers.getCustomerInfo().removeIf(x -> x.getCustomerId().equals(customerId));
                    logEntityAction(deviceId, deviceWithMultipleCustomers,
                            customerId,
                            ActionType.UNASSIGNED_FROM_CUSTOMER, null, strDeviceId, customerId.toString(), customerInfo.getCustomerTitle());

                }
            }
            return deviceWithMultipleCustomers;
        }  catch (Exception e) {

            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strDeviceId);

            throw handleException(e);
        }
    }

}
