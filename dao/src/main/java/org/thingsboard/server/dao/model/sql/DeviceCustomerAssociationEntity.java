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
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.DEVICE_CUSTOMER_ASSOCIATION_COLUMN_FAMILY_NAME)
public class DeviceCustomerAssociationEntity extends AbstractDeviceCustomerAssociationEntity<DeviceCustomerAssociation> {

    public DeviceCustomerAssociationEntity() {
        super();
    }

    public DeviceCustomerAssociationEntity(DeviceCustomerAssociation deviceCustomerAssociation) {
        super(deviceCustomerAssociation);
    }

    public DeviceCustomerAssociationEntity(UUID deviceId, UUID customerId){
        super();
        this.setDeviceId(deviceId);
        this.setCustomerId(customerId);
    }

    @Override
    public DeviceCustomerAssociation toData() {
        return super.toDeviceCustomerAssociation();
    }
}
