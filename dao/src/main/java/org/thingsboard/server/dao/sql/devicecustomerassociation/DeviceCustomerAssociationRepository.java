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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.sql.DeviceCustomerAssociationEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceCustomerAssociationRepository extends PagingAndSortingRepository<DeviceCustomerAssociationEntity, UUID> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM device_customer_association dca WHERE dca.device_id=:deviceId AND dca.customer_id=:customerId", nativeQuery = true)
    Integer deleteByDeviceIdAndCustomerId(@Param("deviceId") UUID deviceId, @Param("customerId") UUID customerId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM device_customer_association dca WHERE dca.device_id=:deviceId", nativeQuery = true)
    Integer deleteByDeviceId(@Param("deviceId") UUID deviceId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.DeviceCustomerAssociation(dca.deviceId, dca.customerId ) FROM DeviceCustomerAssociationEntity dca WHERE dca.deviceId = :deviceId")
    List<DeviceCustomerAssociation> findDeviceCustomerAssociationByDeviceId(@Param("deviceId") UUID deviceId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.DeviceCustomerAssociation(dca.deviceId, dca.customerId)  FROM DeviceCustomerAssociationEntity dca WHERE dca.customerId = :customerId")
    List<DeviceCustomerAssociation> findDeviceCustomerAssociationByCustomerId(@Param("customerId") UUID customerId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.DeviceCustomerAssociation(dca.deviceId, dca.customerId) FROM DeviceCustomerAssociationEntity dca WHERE dca.deviceId = :deviceId " +
            "AND dca.customerId = :customerId")
    DeviceCustomerAssociation findDeviceCustomerAssociationByDeviceIdAndCustomerId(@Param("deviceId") UUID deviceId, @Param("customerId") UUID customerId);

}
