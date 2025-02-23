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
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.ASSET_CUSTOMER_ASSOCIATION_COLUMN_FAMILY_NAME)
public class AssetCustomerAssociationEntity extends AbstractAssetCustomerAssociationEntity<AssetCustomerAssociation> {

    public AssetCustomerAssociationEntity() {
        super();
    }

    public AssetCustomerAssociationEntity(AssetCustomerAssociation assetCustomerAssociation) {
        super(assetCustomerAssociation);
    }

    public AssetCustomerAssociationEntity(UUID assetId, UUID customerId){
        super();
        this.setAssetId(assetId);
        this.setCustomerId(customerId);
    }

    @Override
    public AssetCustomerAssociation toData() {
        return super.toAssetCustomerAssociation();
    }
}
