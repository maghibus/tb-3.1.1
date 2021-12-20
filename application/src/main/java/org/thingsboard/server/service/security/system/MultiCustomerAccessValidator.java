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
package org.thingsboard.server.service.security.system;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerAsset;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerDevice;
import org.thingsboard.server.service.security.AccessValidator;
import org.thingsboard.server.service.security.ValidationResult;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

@Component
@ConditionalOnProperty(prefix = "features", value = "multiple_customer_enabled", havingValue = "true")
public class MultiCustomerAccessValidator extends AccessValidator {

    protected void validateDevice(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<MultiCustomerDevice> deviceFuture = deviceService.findMultiCustomerDeviceByIdAsync(currentUser.getTenantId(), new DeviceId(entityId.getId()));
            Futures.addCallback(deviceFuture, getCallback(callback, device -> {
                if (device == null) {
                    return ValidationResult.entityNotFound(DEVICE_WITH_REQUESTED_ID_NOT_FOUND);
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.DEVICE, operation, entityId, device);
                    } catch (ThingsboardException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(device);
                }
            }), executor);
        }
    }

    protected void validateAsset(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<MultiCustomerAsset> assetFuture = assetService.findMultiCustomerAssetByIdAsync(currentUser.getTenantId(), new AssetId(entityId.getId()));
            Futures.addCallback(assetFuture, getCallback(callback, asset -> {
                if (asset == null) {
                    return ValidationResult.entityNotFound("Asset with requested id wasn't found!");
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.ASSET, operation, entityId, asset);
                    } catch (ThingsboardException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(asset);
                }
            }), executor);
        }
    }
}
