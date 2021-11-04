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
package org.thingsboard.server.dao.assetcustomerassociation;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.common.data.id.AssetCustomerAssociationId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;

@Service
@Slf4j
public class AssetCustomerAssociationServiceImpl extends AbstractEntityService implements AssetCustomerAssociationService {

    @Autowired
    private AssetCustomerAssociationDao assetCustomerAssociationDao;

    @Override
    public AssetCustomerAssociation saveAssetCustomerAssociation(TenantId tenantId, AssetCustomerAssociation assetCustomerAssociation) {
        return assetCustomerAssociationDao.save(tenantId, assetCustomerAssociation);
    }

    @Override
    public List<AssetCustomerAssociation> findAssetCustomerAssociationByAssetId(AssetId assetId) {
        return assetCustomerAssociationDao.findAssetCustomerAssociationByAssetId(assetId);
    }

    @Override
    public List<AssetCustomerAssociation> findAssetCustomerAssociationByCustomerId(CustomerId customerId) {
        return assetCustomerAssociationDao.findAssetCustomerAssociationByCustomerId(customerId);
    }

    @Override
    public AssetCustomerAssociation findAssetCustomerAssociationByAssetIdAndCustomerId(AssetId assetId, CustomerId customerId) {
        return assetCustomerAssociationDao.findAssetCustomerAssociationByAssetIdAndCustomerId(assetId, customerId);
    }

    @Override
    public ListenableFuture<AssetCustomerAssociation> findByIdAsync(TenantId tenantId, AssetCustomerAssociationId assetCustomerAssociationId) {
        return assetCustomerAssociationDao.findByIdAsync(tenantId, assetCustomerAssociationId.getId());
    }

    @Override
    public Integer deleteAssetCustomerAssociation(TenantId tenantId, AssetCustomerAssociation assetCustomerAssociation) {
        return assetCustomerAssociationDao.deleteByAssetIdAndCustomerId(tenantId, assetCustomerAssociation.getAssetId(), assetCustomerAssociation.getCustomerId());
    }

    @Override
    public Integer deleteByAssetId(TenantId tenantId, AssetId assetId) {
        return assetCustomerAssociationDao.deleteByAssetId(tenantId,assetId.getId());
    }
}
