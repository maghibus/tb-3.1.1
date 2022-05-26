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
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.security.Authority;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Federico Serini
 */
@Slf4j
public abstract class BaseGisIntegrationControllerTest extends AbstractControllerTest {

    private Tenant savedTenant;
    private Customer savedCustomer;
    private Dashboard savedDashboard;
    private User tenantAdmin;
    private Device testDevice;

    @Before
    public void beforeTest() throws Exception {
        loginSysAdmin();

        savedTenant = doPost("/api/tenant", getNewTenant("My tenant"), Tenant.class);
        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@thingsboard.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");
        tenantAdmin = createUserAndLogin(tenantAdmin, "testPassword1");

        Dashboard dashboard = new Dashboard();
        dashboard.setTitle("My dashboard");
        savedDashboard = doPost("/api/dashboard", dashboard, Dashboard.class);
        savedDashboard.setTitle("My new dashboard");
        doPost("/api/dashboard", savedDashboard, Dashboard.class);


        Customer customer = new Customer();
        customer.setTitle("My customer");
        Customer savedCustomer = doPost("/api/customer", customer, Customer.class);
        savedCustomer.setTitle("My new customer");
        doPost("/api/customer", savedCustomer, Customer.class);
        Customer foundCustomer = doGet("/api/customer/"+savedCustomer.getId().getId().toString(), Customer.class);

        Device device = new Device();
        device.setName("Test device");
        device.setType("default");
        device.setCustomerId(foundCustomer.getId());
        testDevice = doPost("/api/device", device, Device.class);

        doPost("/api/customer/" + foundCustomer.getId().getId().toString()
                + "/dashboard/" + savedDashboard.getId().getId().toString());

        List<DashboardInfoWithStates> dashboardInfoWithStates = doGetTyped("/api/gis/tenant/dashboards/states", new TypeReference<List<DashboardInfoWithStates>>() {});


        DashboardServerAttributes dashboardServerAttributes = new DashboardServerAttributes();
        dashboardServerAttributes.setDashboardId(dashboardInfoWithStates.get(0).getId().getId().toString());
        dashboardServerAttributes.setDashboardState("detaiiill");
        dashboardServerAttributes.setGisDeviceType("elettromagnetico");
        doPost("/api/gis/dashboard/attributes/server/device/"+testDevice.getId().getId().toString(), dashboardServerAttributes, DashboardServerAttributes.class);

    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/" + savedTenant.getId().getId().toString())
                .andExpect(status().isOk());

        doDelete("/api/customer/"+savedCustomer.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void getDeviceInfoForGis() throws Exception {
        GisDeviceInfo gisDeviceInfos = doGet("/api/gis/device/info/elettromagnetico", GisDeviceInfo.class);
        assertEquals("FeatureCollection", gisDeviceInfos.getType());
        assertNotNull(gisDeviceInfos.getFeatures());
    }

    @Test
    public void saveServerAttribute() throws Exception {
        DashboardServerAttributes dashboardServerAttributes = new DashboardServerAttributes();
        dashboardServerAttributes.setDashboardId(savedDashboard.getId().getId().toString());
        dashboardServerAttributes.setDashboardState("test");
        dashboardServerAttributes.setGisDeviceType("elettromagnetico");
        doPost("/api/gis/dashboard/attributes/server/device/"+testDevice.getId().getId().toString(), dashboardServerAttributes, DashboardServerAttributes.class);
    }

    private Tenant getNewTenant(String title) {
        Tenant tenant = new Tenant();
        tenant.setTitle(title);
        return tenant;
    }
}
