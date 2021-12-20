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
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerDevice;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseDeviceCustomerAssociationControllerTest extends AbstractControllerTest  {
    private IdComparator<Device> idComparator = new IdComparator<>();

    private Tenant savedTenant;
    private User tenantAdmin;
    ObjectMapper objectMapper = new ObjectMapper();

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
            ObjectMapper objectMapper = new ObjectMapper();
            Device device = new Device();
            device.setName("My device");
            device.setType("default");
            Device savedDevice = doPost("/api/device", device, Device.class);

            Customer customer = new Customer();
            customer.setTitle("Customer To ADD 1");
            Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

            String[] strCustomerIds = {customerToAdd1.getId().toString()};
            doPatch("/api/device/" + savedDevice.getId().getId().toString() + "/customer/association", strCustomerIds, MultiCustomerDevice.class);

            doPatch("/api/customer/public/device/" + savedDevice.getId().getId().toString() + "/association/multiple", MultiCustomerDevice.class);



            PageData<MultiCustomerDevice> deviceWithMultipleCustomers = doGetTypedWithPageLink("/api/tenant/device/info/customer/multiple?",
                    new TypeReference<PageData<MultiCustomerDevice>>() {
                    }, new PageLink(5, 0));

            assertEquals(1, deviceWithMultipleCustomers.getData().size());
            assertTrue(deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).isCustomerIsPublic()
                    || deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).isCustomerIsPublic());

            assertTrue(deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd1.getId())
                    || deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd1.getId()));
    }

    @Test
    public void testGetTenantDeviceInfoMultipleCustomer() throws Exception {
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        Device savedDevice = doPost("/api/device", device, Device.class);

        Customer customer = new Customer();
        customer.setTitle("Customer To ADD 1");
        Customer customerToAdd1 = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To ADD 2");
        Customer customerToAdd2 = doPost("/api/customer", customer, Customer.class);

        String[] strCustomerIds = {customerToAdd1.getId().toString(), customerToAdd2.getId().toString()};
        doPatch("/api/device/"+savedDevice.getId().getId().toString()+"/customer/association", strCustomerIds, MultiCustomerDevice.class);

        PageData<MultiCustomerDevice> deviceWithMultipleCustomers = doGetTypedWithPageLink("/api/tenant/device/info/customer/multiple?",
                new TypeReference<PageData<MultiCustomerDevice>>(){}, new PageLink(5,0));

        assertEquals(1, deviceWithMultipleCustomers.getData().size());
        assertTrue(deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd1.getId())
                || deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd1.getId()));

        assertTrue(deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd2.getId())
                || deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToAdd2.getId()));

        ObjectMapper objectMapper = new ObjectMapper();
        String deviceWithMultipleCustomersJsonString = objectMapper.writeValueAsString(deviceWithMultipleCustomers);
        System.out.println(deviceWithMultipleCustomersJsonString);
    }


    @Test
    public void testUpdateCustomerDeviceAssociation() throws Exception {
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        Device savedDevice = doPost("/api/device", device, Device.class);


        Customer customer = new Customer();
        customer.setTitle("Customer To ADD");
        Customer customerToAdd = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To REMOVE");
        Customer customerToRemove = doPost("/api/customer", customer, Customer.class);


        String[] strCustomerIds = {customerToAdd.getId().toString(), customerToRemove.getId().toString()};

        MultiCustomerDevice multiCustomerDevice = doPatch("/api/device/"+savedDevice.getId().getId().toString()+"/customer/association", strCustomerIds, MultiCustomerDevice.class);

        ObjectMapper objectMapper = new ObjectMapper();
        String deviceWithMultipleCustomersJsonString = objectMapper.writeValueAsString(multiCustomerDevice);
        System.out.println(deviceWithMultipleCustomersJsonString);


        assertEquals(2, multiCustomerDevice.getCustomerInfo().size());
        assertTrue(multiCustomerDevice.getCustomerInfo().get(0).getCustomerId().equals(customerToAdd.getId())
                || multiCustomerDevice.getCustomerInfo().get(1).getCustomerId().equals(customerToAdd.getId()));

        assertTrue(multiCustomerDevice.getCustomerInfo().get(0).getCustomerId().equals(customerToRemove.getId())
                || multiCustomerDevice.getCustomerInfo().get(1).getCustomerId().equals(customerToRemove.getId()));

        strCustomerIds = new String[]{customerToRemove.getId().toString()};
        multiCustomerDevice = doPatch("/api/device/"+savedDevice.getId().getId().toString()+"/customer/association", strCustomerIds, MultiCustomerDevice.class);
        assertEquals(1, multiCustomerDevice.getCustomerInfo().size());
        assertEquals(multiCustomerDevice.getCustomerInfo().get(0).getCustomerId(), customerToAdd.getId());
    }

    @Test
    public void testGetCustomerDeviceInfos() throws Exception {
        String isGateway = "{\"gateway\":true,\"description\":\"\"}";
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        device.setAdditionalInfo(objectMapper.readTree(isGateway));
        Device savedDevice = doPost("/api/device", device, Device.class);


        Customer customer = new Customer();
        customer.setTitle("Customer To ADD");
        Customer customerToAdd = doPost("/api/customer", customer, Customer.class);

        customer = new Customer();
        customer.setTitle("Customer To REMOVE");
        Customer customerToRemove = doPost("/api/customer", customer, Customer.class);


        String[] strCustomerIds = {customerToAdd.getId().toString(), customerToRemove.getId().toString()};
        doPatch("/api/device/"+savedDevice.getId().getId().toString()+"/customer/association", strCustomerIds, MultiCustomerDevice.class);

        PageData<MultiCustomerDevice> deviceWithMultipleCustomers = doGetTypedWithPageLink("/api/customer/" +
                        customerToAdd.getId().getId().toString()+
                        "/device/info/customer/multiple?",
                new TypeReference<PageData<MultiCustomerDevice>>(){}, new PageLink(5,0));

        assertEquals(1, deviceWithMultipleCustomers.getData().size());
        assertTrue(deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(0).getCustomerId().equals(customerToAdd.getId())
                || deviceWithMultipleCustomers.getData().get(0).getCustomerInfo().get(1).getCustomerId().equals(customerToRemove.getId()));

        deviceWithMultipleCustomers.getData().forEach(System.out::println);

        doPatch("/api/device/"+savedDevice.getId().getId().toString()+"/customer/association", strCustomerIds, MultiCustomerDevice.class);
    }


}
