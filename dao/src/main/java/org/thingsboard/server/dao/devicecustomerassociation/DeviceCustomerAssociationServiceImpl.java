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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceCustomerAssociationId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;

@Service
@Slf4j
public class DeviceCustomerAssociationServiceImpl extends AbstractEntityService implements DeviceCustomerAssociationService {

    @Autowired
    private DeviceCustomerAssociationDao deviceCustomerAssociationDao;


    @Override
    public DeviceCustomerAssociation saveDeviceCustomerAssociation(TenantId tenantId,DeviceCustomerAssociation deviceCustomerAssociation) {
        return deviceCustomerAssociationDao.save(tenantId, deviceCustomerAssociation);
    }

    @Override
    public List<DeviceCustomerAssociation> findDeviceCustomerAssociationByDeviceId(DeviceId deviceId) {
        return deviceCustomerAssociationDao.findDeviceCustomerAssociationByDeviceId(deviceId);
    }

    @Override
    public List<DeviceCustomerAssociation> findDeviceCustomerAssociationByCustomerId(CustomerId customerId) {
        return deviceCustomerAssociationDao.findDeviceCustomerAssociationByCustomerId(customerId);
    }

    @Override
    public DeviceCustomerAssociation findDeviceCustomerAssociationByDeviceIdAndCustomerId(DeviceId deviceId, CustomerId customerId) {
        return deviceCustomerAssociationDao.findDeviceCustomerAssociationByDeviceIdAndCustomerId(deviceId, customerId);
    }

    @Override
    public ListenableFuture<DeviceCustomerAssociation> findByIdAsync(TenantId tenantId, DeviceCustomerAssociationId deviceCustomerAssociationId) {
        return deviceCustomerAssociationDao.findByIdAsync(tenantId, deviceCustomerAssociationId.getId());
    }

    @Override
    public Integer deleteDeviceCustomerAssociation(TenantId tenantId, DeviceCustomerAssociation deviceCustomerAssociation) {
        return deviceCustomerAssociationDao.deleteByDeviceIdAndCustomerId(tenantId, deviceCustomerAssociation.getDeviceId(), deviceCustomerAssociation.getCustomerId());
    }

    @Override
    public Integer deleteByDeviceId(TenantId tenantId, DeviceId deviceId) {
        return deviceCustomerAssociationDao.deleteByDeviceId(tenantId,deviceId.getId());
    }
}
