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
package org.thingsboard.server.dao.devicecustomerassociation;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceCustomerAssociationId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;

public interface DeviceCustomerAssociationService {
    DeviceCustomerAssociation saveDeviceCustomerAssociation(TenantId tenantId, DeviceCustomerAssociation deviceCustomerAssociation);
    ListenableFuture<DeviceCustomerAssociation> findByIdAsync(TenantId tenantId, DeviceCustomerAssociationId deviceCustomerAssociationId);
    List<DeviceCustomerAssociation> findDeviceCustomerAssociationByDeviceId(DeviceId deviceId);
    List<DeviceCustomerAssociation> findDeviceCustomerAssociationByCustomerId(CustomerId customerId);
    DeviceCustomerAssociation findDeviceCustomerAssociationByDeviceIdAndCustomerId(DeviceId deviceId, CustomerId customerId);
    Integer deleteDeviceCustomerAssociation(TenantId tenantId, DeviceCustomerAssociation deviceCustomerAssociation);
    Integer deleteByDeviceId(TenantId tenantId, DeviceId deviceId);
}
