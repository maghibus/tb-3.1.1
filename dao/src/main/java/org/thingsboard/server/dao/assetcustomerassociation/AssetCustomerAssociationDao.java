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

import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface AssetCustomerAssociationDao extends Dao<AssetCustomerAssociation> {
    AssetCustomerAssociation save(TenantId tenantId, AssetCustomerAssociation assetCustomerAssociation);
    AssetCustomerAssociation findById(TenantId tenantId, UUID id);
    Integer deleteByAssetIdAndCustomerId(TenantId tenantId, UUID assetId, UUID customerId);
    Integer deleteByAssetId(TenantId tenantId, UUID assetId);
    List<AssetCustomerAssociation> findAssetCustomerAssociationByAssetId(AssetId assetId);
    List<AssetCustomerAssociation> findAssetCustomerAssociationByCustomerId(CustomerId customerId);
    AssetCustomerAssociation findAssetCustomerAssociationByAssetIdAndCustomerId(AssetId assetId, CustomerId customerId);
}
