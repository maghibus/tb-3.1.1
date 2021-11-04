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
package org.thingsboard.server.dao.sql.assetcustomerassociation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.dao.model.sql.AssetCustomerAssociationEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetCustomerAssociationRepository extends PagingAndSortingRepository<AssetCustomerAssociationEntity, UUID> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM asset_customer_association aca WHERE aca.asset_id=:assetId AND aca.customer_id=:customerId", nativeQuery = true)
    Integer deleteByAssetIdAndCustomerId(@Param("assetId") UUID assetId, @Param("customerId") UUID customerId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM asset_customer_association aca WHERE aca.asset_id=:assetId", nativeQuery = true)
    Integer deleteByAssetId(@Param("assetId") UUID assetId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.AssetCustomerAssociation(aca.assetId, aca.customerId ) FROM AssetCustomerAssociationEntity aca WHERE aca.assetId = :assetId")
    List<AssetCustomerAssociation> findAssetCustomerAssociationByAssetId(@Param("assetId") UUID assetId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.AssetCustomerAssociation(aca.assetId, aca.customerId)  FROM AssetCustomerAssociationEntity aca WHERE aca.customerId = :customerId")
    List<AssetCustomerAssociation> findAssetCustomerAssociationByCustomerId(@Param("customerId") UUID customerId);

    @Query(value = "SELECT new org.thingsboard.server.common.data.AssetCustomerAssociation(aca.assetId, aca.customerId) FROM AssetCustomerAssociationEntity aca WHERE aca.assetId = :assetId " +
            "AND aca.customerId = :customerId")
    AssetCustomerAssociation findAssetCustomerAssociationByAssetIdAndCustomerId(@Param("assetId") UUID assetId, @Param("customerId") UUID customerId);

}
