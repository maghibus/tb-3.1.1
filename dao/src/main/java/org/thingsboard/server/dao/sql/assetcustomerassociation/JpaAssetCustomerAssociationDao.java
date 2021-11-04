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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.assetcustomerassociation.AssetCustomerAssociationDao;
import org.thingsboard.server.dao.model.sql.AssetCustomerAssociationEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;

import java.util.List;
import java.util.UUID;

@Component
public class JpaAssetCustomerAssociationDao extends JpaAbstractDao<AssetCustomerAssociationEntity, AssetCustomerAssociation> implements AssetCustomerAssociationDao {
    @Autowired
    AssetCustomerAssociationRepository assetCustomerAssociationRepository;

    @Override
    protected Class<AssetCustomerAssociationEntity> getEntityClass() {
        return AssetCustomerAssociationEntity.class;
    }

    @Override
    protected CrudRepository<AssetCustomerAssociationEntity, UUID> getCrudRepository() {
        return assetCustomerAssociationRepository;
    }

    @Override
    public Integer deleteByAssetIdAndCustomerId(TenantId tenantId, UUID assetId, UUID customerId) {
        return assetCustomerAssociationRepository.deleteByAssetIdAndCustomerId(assetId, customerId);
    }

    @Override
    public Integer deleteByAssetId(TenantId tenantId, UUID assetId) {
        return assetCustomerAssociationRepository.deleteByAssetId(assetId);
    }

    @Override
    public List<AssetCustomerAssociation> findAssetCustomerAssociationByAssetId(AssetId assetId) {
        return assetCustomerAssociationRepository.findAssetCustomerAssociationByAssetId(assetId.getId());
    }

    @Override
    public List<AssetCustomerAssociation> findAssetCustomerAssociationByCustomerId(CustomerId customerId) {
        return assetCustomerAssociationRepository.findAssetCustomerAssociationByCustomerId(customerId.getId());
    }

    @Override
    public AssetCustomerAssociation findAssetCustomerAssociationByAssetIdAndCustomerId(AssetId assetId, CustomerId customerId) {
        return assetCustomerAssociationRepository.findAssetCustomerAssociationByAssetIdAndCustomerId(assetId.getId(),customerId.getId());
    }
}
