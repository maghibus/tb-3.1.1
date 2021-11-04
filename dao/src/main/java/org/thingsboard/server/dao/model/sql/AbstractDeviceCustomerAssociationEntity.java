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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceCustomerAssociationId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractDeviceCustomerAssociationEntity<T extends DeviceCustomerAssociation> extends BaseSqlEntity<T> implements BaseEntity<T> {

    @Column(name = ModelConstants.DEVICE_CUSTOMER_ASSOCIATION_DEVICE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID deviceId;

    @Column(name = ModelConstants.DEVICE_CUSTOMER_ASSOCIATION_CUSTOMER_ID_PROPERTY, columnDefinition = "uuid")
    private UUID customerId;

    public AbstractDeviceCustomerAssociationEntity(){
        super();
    }

    public AbstractDeviceCustomerAssociationEntity(DeviceCustomerAssociation deviceCustomerAssociation) {
        if (deviceCustomerAssociation.getDeviceId() != null) {
            this.deviceId = deviceCustomerAssociation.getDeviceId();
        }
        if (deviceCustomerAssociation.getCustomerId() != null) {
            this.customerId = deviceCustomerAssociation.getCustomerId();
        }
    }

    public AbstractDeviceCustomerAssociationEntity(DeviceCustomerAssociationEntity deviceCustomerAssociationEntity){
        if (deviceCustomerAssociationEntity.getDeviceId() != null) {
            this.deviceId = deviceCustomerAssociationEntity.getDeviceId();
        }
        if (deviceCustomerAssociationEntity.getCustomerId() != null) {
            this.customerId = deviceCustomerAssociationEntity.getCustomerId();
        }
    }

    protected DeviceCustomerAssociation toDeviceCustomerAssociation() {
        DeviceCustomerAssociation deviceCustomerAssociation = new DeviceCustomerAssociation(new DeviceCustomerAssociationId(getUuid()));
        deviceCustomerAssociation.setCreatedTime(createdTime);

        if (deviceId != null) {
            deviceCustomerAssociation.setDeviceId(deviceId);
        }
        if (customerId != null) {
            deviceCustomerAssociation.setCustomerId(customerId);
        }
        return deviceCustomerAssociation;
    }

}
