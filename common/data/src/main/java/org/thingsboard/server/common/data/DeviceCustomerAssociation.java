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
package org.thingsboard.server.common.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceCustomerAssociationId;
import org.thingsboard.server.common.data.id.DeviceId;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
public class DeviceCustomerAssociation extends BaseData<DeviceCustomerAssociationId> {

    private static final long serialVersionUID = 1L;

    private UUID deviceId;
    private UUID customerId;

    public DeviceCustomerAssociation(){
        super();
    }

    public DeviceCustomerAssociation(DeviceCustomerAssociationId id){
        super(id);
    }

    public DeviceCustomerAssociation(UUID deviceId, UUID customerId){
        this.deviceId = deviceId;
        this.customerId = customerId;
    }

    public DeviceCustomerAssociation(DeviceCustomerAssociation data) {
        super(data);
        this.deviceId = data.getDeviceId();
        this.customerId = data.getCustomerId();
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }


}
