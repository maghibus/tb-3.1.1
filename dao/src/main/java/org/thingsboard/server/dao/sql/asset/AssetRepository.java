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
package org.thingsboard.server.dao.sql.asset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.AssetEntity;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/21/2017.
 */
public interface AssetRepository extends PagingAndSortingRepository<AssetEntity, UUID> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.id = :assetId")
    AssetInfoEntity findAssetInfoById(@Param("assetId") UUID assetId);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetInfoEntity> findAssetInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                  @Param("customerId") UUID customerId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                @Param("customerId") UUID customerId,
                                                                @Param("searchText") String searchText,
                                                                Pageable pageable);

    List<AssetEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> assetIds);

    List<AssetEntity> findByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> assetIds);

    AssetEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);


    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId AND a.type = :type " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                         @Param("customerId") UUID customerId,
                                                         @Param("type") String type,
                                                         @Param("textSearch") String textSearch,
                                                         Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.type = :type " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                       @Param("customerId") UUID customerId,
                                                                       @Param("type") String type,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT DISTINCT a.type FROM AssetEntity a WHERE a.tenantId = :tenantId")
    List<String> findTenantAssetTypes(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a, CustomerEntity c, AssetCustomerAssociationEntity aca " +
            "WHERE c.id = aca.customerId " +
            "AND a.id = aca.assetId "+
            "AND a.tenantId = :tenantId " +
            "AND aca.customerId = :customerId " +
            "AND a.type = :type " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<AssetInfoEntity> findAssetInfoWithMultipleCustomersByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId, @Param("customerId") UUID customerId, @Param("type") String type, @Param("textSearch") String textSearch, Pageable toPageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM AssetEntity a, CustomerEntity c, AssetCustomerAssociationEntity aca  " +
            "WHERE c.id = aca.customerId " +
            "AND a.id = aca.assetId "+
            "AND a.tenantId = :tenantId " +
            "AND aca.customerId = :customerId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<AssetInfoEntity> findAssetInfoWithMultipleCustomersByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId, @Param("customerId") UUID customerId, @Param("searchText") String textSearch, Pageable toPageable);

}
