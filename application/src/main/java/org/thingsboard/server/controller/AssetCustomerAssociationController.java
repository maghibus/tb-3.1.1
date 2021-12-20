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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerAsset;
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
public class AssetCustomerAssociationController  extends BaseController {

    private static final String ASSET_ID = "assetId";
    private static final String CUSTOMER_ID = "customerId";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/asset/info/{assetId}/association/multiple", method = RequestMethod.GET)
    @ResponseBody
    public MultiCustomerAsset getAssetInfoById(@PathVariable(ASSET_ID) String strAssetId) throws ThingsboardException {
        checkParameter(ASSET_ID, strAssetId);
        try {
            AssetId assetId = new AssetId(toUUID(strAssetId));
            return checkMultiCustomerAssetId(assetId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PatchMapping(value = "/customer/public/asset/{assetId}/association/multiple")
    @ResponseBody
    public MultiCustomerAsset assignAssetToPublicCustomer(@PathVariable(ASSET_ID) String strAssetId) throws ThingsboardException {
        checkParameter(ASSET_ID, strAssetId);
        try {
            AssetId assetId = new AssetId(toUUID(strAssetId));
            Asset asset = checkAssetId(assetId, Operation.ASSIGN_TO_CUSTOMER);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(asset.getTenantId());

            assetService.unassignAllAssetCustomerAssociation(getCurrentUser().getTenantId(), assetId);

            checkNotNull(assetService.assignAssetToCustomers(getTenantId(), assetId, publicCustomer.getId()));
            MultiCustomerAsset multiCustomerAsset = assetService.findAssetInfoWithMultipleCustomerByDeviceId(assetId);
            logEntityAction(assetId, multiCustomerAsset,
                    publicCustomer.getId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strAssetId, publicCustomer.getId().toString(), publicCustomer.getTitle());

            return multiCustomerAsset;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.ASSET), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strAssetId);

            throw handleException(e);
        }
    }

    // ex getTenantDeviceInfos
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/asset/info/customer/multiple", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<MultiCustomerAsset> getCustomerAssetInfos(
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
                return checkNotNull(assetService.findAssetInfoWithMultipleCustomerByTenantIdAndType(tenantId, type, pageLink));
            }

            return checkNotNull(assetService.findAssetInfoWithMultipleCustomerByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // ex getCustomerAssetInfos
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/asset/info/customer/multiple", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<MultiCustomerAsset> getCustomerAssetInfos(
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
                return checkNotNull(assetService.findAssetInfoWithMultipleCustomersByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            }

            return checkNotNull(assetService.findAssetInfoWithMultipleCustomersByTenantIdAndCustomerId(tenantId, customerId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // ex  assignAssetToCustomer + unassignAssetFromCustomer
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PatchMapping(value = "/asset/{assetId}/customer/association")
    @ResponseBody
    @ConditionalOnProperty(prefix = "features", value = "multiple_customer_per_device", havingValue = "true")
    public MultiCustomerAsset updateCustomerDeviceAssociation(@PathVariable(ASSET_ID) String strAssetId,
                                                              @RequestBody String[] strCustomerIds) throws ThingsboardException{
        checkParameter(ASSET_ID, strAssetId);

        try {
            AssetId assetId = new AssetId(toUUID(strAssetId));
            MultiCustomerAsset multiCustomerAsset = checkMultiCustomerAssetId(assetId, Operation.ASSIGN_TO_CUSTOMER);

            Set<CustomerId> customerIds = new HashSet<>();
            if(strCustomerIds != null){
                Arrays.stream(strCustomerIds).forEach(x -> customerIds.add(new CustomerId(toUUID(x))));
            }

            Map<CustomerId, MultipleCustomerInfo> customerInfoMap =  multiCustomerAsset
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
                    checkNotNull(assetService.assignAssetToCustomers(getCurrentUser().getTenantId(), assetId, addedCustomerId));

                    MultipleCustomerInfo customerInfo = new MultipleCustomerInfo(addedCustomer.getTitle(), addedCustomer.getAdditionalInfo(), addedCustomer.getId().getId());

                    multiCustomerAsset.getCustomerInfo().add(customerInfo);
                    logEntityAction(assetId, multiCustomerAsset,
                            addedCustomerId,
                            ActionType.ASSIGNED_TO_CUSTOMER, null, strAssetId, addedCustomerId.toString(), customerInfo.getCustomerTitle());
                }
                for (CustomerId customerId : removedCustomerIds) {
                    MultipleCustomerInfo customerInfo = customerInfoMap.get(customerId);
                    checkNotNull(assetService.unassignAssetFromCustomers(getCurrentUser().getTenantId(), assetId, customerId));
                    multiCustomerAsset.getCustomerInfo().removeIf(x -> x.getCustomerId().equals(customerId));
                    logEntityAction(assetId, multiCustomerAsset,
                            customerId,
                            ActionType.UNASSIGNED_FROM_CUSTOMER, null, strAssetId, customerId.toString(), customerInfo.getCustomerTitle());

                }
            }
            return multiCustomerAsset;
        }  catch (Exception e) {

            logEntityAction(emptyId(EntityType.ASSET), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strAssetId);

            throw handleException(e);
        }
    }
}
