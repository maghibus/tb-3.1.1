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
package org.thingsboard.server.dao.sql.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.multiplecustomer.MultipleCustomerInfo;
import org.thingsboard.server.dao.model.sql.CustomerEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public interface CustomerRepository extends PagingAndSortingRepository<CustomerEntity, UUID> {

    @Query("SELECT c FROM CustomerEntity c WHERE c.tenantId = :tenantId " +
            "AND LOWER(c.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<CustomerEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                        @Param("textSearch") String textSearch,
                                        Pageable pageable);

    CustomerEntity findByTenantIdAndTitle(UUID tenantId, String title);

    @Query("SELECT new org.thingsboard.server.common.data.multiplecustomer.MultipleCustomerInfo(c.title, c.additionalInfo, dca.customerId) FROM CustomerEntity c, DeviceCustomerAssociationEntity dca " +
            "WHERE c.id = dca.customerId AND dca.deviceId = :deviceId")
    List<MultipleCustomerInfo> findAssociatedCustomerInfoByDeviceId(@Param("deviceId") UUID deviceId);

    @Query("SELECT new org.thingsboard.server.common.data.multiplecustomer.MultipleCustomerInfo(c.title, c.additionalInfo, aca.customerId) FROM CustomerEntity c, AssetCustomerAssociationEntity aca " +
            "WHERE c.id = aca.customerId AND aca.assetId = :assetId")
    List<MultipleCustomerInfo> findAssociatedCustomerInfoByAssetId(@Param("assetId") UUID assetId);
}
