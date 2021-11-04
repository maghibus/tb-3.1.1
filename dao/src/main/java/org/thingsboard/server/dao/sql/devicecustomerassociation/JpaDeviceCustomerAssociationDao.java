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
package org.thingsboard.server.dao.sql.devicecustomerassociation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.devicecustomerassociation.DeviceCustomerAssociationDao;
import org.thingsboard.server.dao.model.sql.DeviceCustomerAssociationEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;

import java.util.List;
import java.util.UUID;

@Component
public class JpaDeviceCustomerAssociationDao extends JpaAbstractDao<DeviceCustomerAssociationEntity, DeviceCustomerAssociation> implements DeviceCustomerAssociationDao {

    @Autowired
    DeviceCustomerAssociationRepository deviceCustomerAssociationRepository;

    @Override
    protected Class<DeviceCustomerAssociationEntity> getEntityClass() {
        return DeviceCustomerAssociationEntity.class;
    }

    @Override
    protected CrudRepository<DeviceCustomerAssociationEntity, UUID> getCrudRepository() {
        return deviceCustomerAssociationRepository;
    }

    @Override
    public Integer deleteByDeviceIdAndCustomerId(TenantId tenantId, UUID deviceId, UUID customerId) {
        return deviceCustomerAssociationRepository.deleteByDeviceIdAndCustomerId(deviceId, customerId);
    }

    @Override
    public Integer deleteByDeviceId(TenantId tenantId, UUID deviceId) {
        return deviceCustomerAssociationRepository.deleteByDeviceId(deviceId);
    }

    @Override
    public List<DeviceCustomerAssociation> findDeviceCustomerAssociationByDeviceId(DeviceId deviceId) {
        return deviceCustomerAssociationRepository.findDeviceCustomerAssociationByDeviceId(deviceId.getId());
    }

    @Override
    public List<DeviceCustomerAssociation> findDeviceCustomerAssociationByCustomerId(CustomerId customerId) {
        return deviceCustomerAssociationRepository.findDeviceCustomerAssociationByCustomerId(customerId.getId());
    }

    @Override
    public DeviceCustomerAssociation findDeviceCustomerAssociationByDeviceIdAndCustomerId(DeviceId deviceId, CustomerId customerId) {
        return deviceCustomerAssociationRepository.findDeviceCustomerAssociationByDeviceIdAndCustomerId(deviceId.getId(),customerId.getId());
    }
}
