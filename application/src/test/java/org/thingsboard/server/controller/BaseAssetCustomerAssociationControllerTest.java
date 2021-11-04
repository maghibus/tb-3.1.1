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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.multiplecustomer.AssetWithMultipleCustomers;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseAssetCustomerAssociationControllerTest  extends AbstractControllerTest {

    private IdComparator<Device> idComparator = new IdComparator<>();

    private Tenant savedTenant;
    private User tenantAdmin;

    @Before
    public void beforeTest() throws Exception {
        loginSysAdmin();

        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        savedTenant = doPost("/api/tenant", tenant, Tenant.class);
        Assert.assertNotNull(savedTenant);

        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@thingsboard.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");

        tenantAdmin = createUserAndLogin(tenantAdmin, "testPassword1");
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/" + savedTenant.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void testAssignDeviceToPublicCustomerMultiple() throws Exception {
        Asset asset = new Asset();
        asset.setName("My asset");
        asset.setType("default");
        Asset savedAsset = doPost("/api/asset", asset, Asset.class);

        Customer customer = new Customer();
        customer = new Customer();
        customer.setTitle("Customer To ADD 1");
        Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

        String[] strCustomerIds = {customerToAdd1.getId().toString()};
        doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);

        doPatch("/api/customer/public/asset/"+savedAsset.getId().getId().toString()+"/association/multiple", AssetWithMultipleCustomers.class);

        PageData<AssetWithMultipleCustomers> assetWithMultipleCustomers = doGetTypedWithPageLink("/api/tenant/asset/info/customer/multiple?",
                new TypeReference<PageData<AssetWithMultipleCustomers>>(){}, new PageLink(5,0));

        assertEquals(1, assetWithMultipleCustomers.getData().size());
        assertTrue(assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).isCustomerIsPublic()
                || assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).isCustomerIsPublic());

        assertTrue(assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd1.getId())
                || assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd1.getId()));
    }

    @Test
    public void testGetTenantAssetInfoMultipleCustomer() throws Exception {
        Asset asset = new Asset();
        asset.setName("My asset");
        asset.setType("default");
        Asset savedAsset = doPost("/api/asset", asset, Asset.class);

        Customer customer = new Customer();
        customer.setTitle("Customer To ADD 1");
        Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To ADD 2");
        Customer customerToAdd2 = doPost("/api/customer", customer, Customer.class);

        String[] strCustomerIds = {customerToAdd1.getId().toString(), customerToAdd2.getId().toString()};
        doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);

        PageData<AssetWithMultipleCustomers> assetWithMultipleCustomers = doGetTypedWithPageLink("/api/tenant/asset/info/customer/multiple?",
                new TypeReference<PageData<AssetWithMultipleCustomers>>(){}, new PageLink(5,0));

        assertEquals(1, assetWithMultipleCustomers.getData().size());
        assertTrue(assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd1.getId())
                || assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd1.getId()));

        assertTrue(assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd2.getId())
                || assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd2.getId()));

        ObjectMapper objectMapper = new ObjectMapper();
        String deviceWithMultipleCustomersJsonString = objectMapper.writeValueAsString(assetWithMultipleCustomers);
        System.out.println(deviceWithMultipleCustomersJsonString);
    }

    @Test
    public void testUpdateCustomerAssetAssociation() throws Exception {
        Asset asset = new Asset();
        asset.setName("My asset");
        asset.setType("default");
        Asset savedAsset = doPost("/api/asset", asset, Asset.class);


        Customer customer = new Customer();
        customer.setTitle("Customer To ADD");
        Customer customerToAdd = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To REMOVE");
        Customer customerToRemove = doPost("/api/customer", customer, Customer.class);


        String[] strCustomerIds = {customerToAdd.getId().toString(), customerToRemove.getId().toString()};

        AssetWithMultipleCustomers assetWithMultipleCustomers =  doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);


        ObjectMapper objectMapper = new ObjectMapper();
        String deviceWithMultipleCustomersJsonString = objectMapper.writeValueAsString(assetWithMultipleCustomers);
        System.out.println(deviceWithMultipleCustomersJsonString);


        assertEquals(2, assetWithMultipleCustomers.getCustomerInfo().size());
        assertTrue(assetWithMultipleCustomers.getCustomerInfo().get(0).getCustomerId().equals(customerToAdd.getId())
                || assetWithMultipleCustomers.getCustomerInfo().get(1).getCustomerId().equals(customerToAdd.getId()));

        assertTrue(assetWithMultipleCustomers.getCustomerInfo().get(0).getCustomerId().equals(customerToRemove.getId())
                || assetWithMultipleCustomers.getCustomerInfo().get(1).getCustomerId().equals(customerToRemove.getId()));

        strCustomerIds = new String[]{customerToRemove.getId().toString()};
        assetWithMultipleCustomers = doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);
        assertEquals(1, assetWithMultipleCustomers.getCustomerInfo().size());
        assertEquals(assetWithMultipleCustomers.getCustomerInfo().get(0).getCustomerId(), customerToAdd.getId());
    }

    @Test
    public void testGetCustomerAssetInfos() throws Exception{
        Asset asset = new Asset();
        asset.setName("My asset");
        asset.setType("default");

        Asset savedAsset = doPost("/api/asset", asset, Asset.class);

        Customer customer = new Customer();
        customer.setTitle("Customer To ADD 1");
        Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To ADD 2");
        Customer customerToAdd2 = doPost("/api/customer", customer, Customer.class);

        String[] strCustomerIds = {customerToAdd1.getId().toString(), customerToAdd2.getId().toString()};
        doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);



        PageData<AssetWithMultipleCustomers> assetWithMultipleCustomers = doGetTypedWithPageLink("/api/customer/" +
                        customerToAdd1.getId().toString()+
                        "/asset/info/customer/multiple?",
                new TypeReference<PageData<AssetWithMultipleCustomers>>(){}, new PageLink(5,0));

        assertEquals(1, assetWithMultipleCustomers.getData().size());
        assertTrue(assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd1.getId())
                || assetWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd1.getId()));


        doPatch("/api/asset/"+savedAsset.getId().getId().toString()+"/customer/association", strCustomerIds, AssetWithMultipleCustomers.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String deviceWithMultipleCustomersJsonString = objectMapper.writeValueAsString(assetWithMultipleCustomers);
        System.out.println(deviceWithMultipleCustomersJsonString);
    }
}
