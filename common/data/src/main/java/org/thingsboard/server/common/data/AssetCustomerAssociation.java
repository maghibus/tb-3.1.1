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
package org.thingsboard.server.common.data;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.AssetCustomerAssociationId;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
public class AssetCustomerAssociation extends BaseData<AssetCustomerAssociationId> {

    private UUID assetId;
    private UUID customerId;

    public AssetCustomerAssociation(){
        super();
    }

    public AssetCustomerAssociation(AssetCustomerAssociationId id){
        super(id);
    }

    public AssetCustomerAssociation(UUID assetId, UUID customerId){
        this.assetId = assetId;
        this.customerId = customerId;
    }

    public AssetCustomerAssociation(AssetCustomerAssociation data) {
        super(data);
        this.assetId = data.getAssetId();
        this.customerId = data.getCustomerId();
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
