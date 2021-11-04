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
package org.thingsboard.server.dao.service.sql;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.common.data.AssetCustomerAssociation;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.assetcustomerassociation.AssetCustomerAssociationService;
import org.thingsboard.server.dao.service.BaseAssetServiceTest;
import org.thingsboard.server.dao.service.DaoSqlTest;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@DaoSqlTest
@Slf4j
public class AssetCustomerAssociationServiceSqlTest extends BaseAssetServiceTest {

    @Autowired
    AssetCustomerAssociationService assetCustomerAssociationService;

    @Autowired
    AssetService assetService;


    @Test
    public void testFindAssociationByTenantIdAndId() throws ExecutionException, InterruptedException {
        UUID customerId1 = Uuids.timeBased();
        UUID assetId1 = Uuids.timeBased();

        AssetCustomerAssociation assetCustomerAssociation = new AssetCustomerAssociation();

        assetCustomerAssociation.setCustomerId(customerId1);
        assetCustomerAssociation.setAssetId(assetId1);
        assetCustomerAssociation = assetCustomerAssociationService.saveAssetCustomerAssociation(null,assetCustomerAssociation);

        ListenableFuture<AssetCustomerAssociation> assetsFuture = assetCustomerAssociationService.findByIdAsync(null, assetCustomerAssociation.getId());
        assetCustomerAssociation = assetsFuture.get();
        assertNotNull(assetCustomerAssociation);
        assertNotNull(assetCustomerAssociation.getId());
        assertNotNull(assetCustomerAssociation.getCustomerId());
        assertNotNull(assetCustomerAssociation.getAssetId());

        Integer deleted = assetCustomerAssociationService.deleteAssetCustomerAssociation(null, assetCustomerAssociation);
        assert(deleted>0);
        assetsFuture = assetCustomerAssociationService.findByIdAsync(null, assetCustomerAssociation.getId());
        assetCustomerAssociation = assetsFuture.get();
        assertNull(assetCustomerAssociation);
    }



}
