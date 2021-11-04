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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.common.data.id.AssetCustomerAssociationId;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractAssetCustomerAssociationEntity<T extends AssetCustomerAssociation> extends BaseSqlEntity<T> implements BaseEntity<T> {

    @Column(name = ModelConstants.ASSET_CUSTOMER_ASSOCIATION_ASSET_ID_PROPERTY, columnDefinition = "uuid")
    private UUID assetId;

    @Column(name = ModelConstants.ASSET_CUSTOMER_ASSOCIATION_CUSTOMER_ID_PROPERTY, columnDefinition = "uuid")
    private UUID customerId;


    public AbstractAssetCustomerAssociationEntity(){
        super();
    }

    public AbstractAssetCustomerAssociationEntity(AssetCustomerAssociation assetCustomerAssociation) {
        if (assetCustomerAssociation.getAssetId() != null) {
            this.assetId = assetCustomerAssociation.getAssetId();
        }
        if (assetCustomerAssociation.getCustomerId() != null) {
            this.customerId = assetCustomerAssociation.getCustomerId();
        }
    }

    public AbstractAssetCustomerAssociationEntity(AssetCustomerAssociationEntity assetCustomerAssociationEntity){
        if (assetCustomerAssociationEntity.getAssetId() != null) {
            this.assetId = assetCustomerAssociationEntity.getAssetId();
        }
        if (assetCustomerAssociationEntity.getCustomerId() != null) {
            this.customerId = assetCustomerAssociationEntity.getCustomerId();
        }
    }

    protected AssetCustomerAssociation toAssetCustomerAssociation() {
        AssetCustomerAssociation assetCustomerAssociation = new AssetCustomerAssociation(new AssetCustomerAssociationId(getUuid()));
        assetCustomerAssociation.setCreatedTime(createdTime);

        if (assetId != null) {
            assetCustomerAssociation.setAssetId(assetId);
        }
        if (customerId != null) {
            assetCustomerAssociation.setCustomerId(customerId);
        }
        return assetCustomerAssociation;
    }

}
