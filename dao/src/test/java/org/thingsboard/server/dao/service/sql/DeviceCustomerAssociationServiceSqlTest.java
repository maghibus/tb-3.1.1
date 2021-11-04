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
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.common.data.DeviceCustomerAssociation;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.multiplecustomer.DeviceWithMultipleCustomers;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.devicecustomerassociation.DeviceCustomerAssociationService;
import org.thingsboard.server.dao.service.BaseDeviceServiceTest;
import org.thingsboard.server.dao.service.DaoSqlTest;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@DaoSqlTest
@Slf4j
public class DeviceCustomerAssociationServiceSqlTest extends BaseDeviceServiceTest {

    @Autowired
    DeviceCustomerAssociationService deviceCustomerAssociationService;

    @Autowired
    DeviceService deviceService;

    @Test
    public void testFindDeviceInfoWithMultipleCustomerByTenantId() throws ThingsboardException {
        int pageSize = 20;
        int page = 0;
        PageLink pageLink = createPageLink(pageSize, page, null, null, null);
        PageData<DeviceWithMultipleCustomers> deviceWithMultipleCustomers = deviceService.findDeviceInfoWithMultipleCustomerByTenantId(tenantId,pageLink);
        System.out.println(deviceWithMultipleCustomers.toString());
    }


    @Test
    public void testFindAssociationByTenantIdAndId() throws ExecutionException, InterruptedException {
        UUID customerId1 = Uuids.timeBased();
        UUID deviceId1 = Uuids.timeBased();

        DeviceCustomerAssociation deviceCustomerAssociation = new DeviceCustomerAssociation();

        deviceCustomerAssociation.setCustomerId(customerId1);
        deviceCustomerAssociation.setDeviceId(deviceId1);
        deviceCustomerAssociation = deviceCustomerAssociationService.saveDeviceCustomerAssociation(null,deviceCustomerAssociation);

        ListenableFuture<DeviceCustomerAssociation> devicesFuture = deviceCustomerAssociationService.findByIdAsync(null, deviceCustomerAssociation.getId());
        deviceCustomerAssociation = devicesFuture.get();
        assertNotNull(deviceCustomerAssociation);
        assertNotNull(deviceCustomerAssociation.getId());
        assertNotNull(deviceCustomerAssociation.getCustomerId());
        assertNotNull(deviceCustomerAssociation.getDeviceId());

        Integer deleted = deviceCustomerAssociationService.deleteDeviceCustomerAssociation(null, deviceCustomerAssociation);
        assertTrue(deleted>1);

        devicesFuture = deviceCustomerAssociationService.findByIdAsync(null, deviceCustomerAssociation.getId());
        deviceCustomerAssociation = devicesFuture.get();

        assertNull(deviceCustomerAssociation);
    }


    @Test
    public void testDeleteAssociationByTenantIdAndId() throws ExecutionException, InterruptedException {
        UUID customerId1 = Uuids.timeBased();
        UUID deviceId1 = Uuids.timeBased();

        DeviceCustomerAssociation deviceCustomerAssociation = new DeviceCustomerAssociation();

        deviceCustomerAssociation.setCustomerId(customerId1);
        deviceCustomerAssociation.setDeviceId(deviceId1);
        deviceCustomerAssociation = deviceCustomerAssociationService.saveDeviceCustomerAssociation(null,deviceCustomerAssociation);

        ListenableFuture<DeviceCustomerAssociation> devicesFuture = deviceCustomerAssociationService.findByIdAsync(null, deviceCustomerAssociation.getId());
        deviceCustomerAssociation = devicesFuture.get();
        assertNotNull(deviceCustomerAssociation);


        Integer deleted = deviceCustomerAssociationService.deleteDeviceCustomerAssociation(null, deviceCustomerAssociation);
        assert(deleted>0);
        devicesFuture = deviceCustomerAssociationService.findByIdAsync(null, deviceCustomerAssociation.getId());
        deviceCustomerAssociation = devicesFuture.get();
        assertNull(deviceCustomerAssociation);
    }

    PageLink createPageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder) throws ThingsboardException {
        if (!StringUtils.isEmpty(sortProperty)) {
            SortOrder.Direction direction = SortOrder.Direction.ASC;
            if (!StringUtils.isEmpty(sortOrder)) {
                try {
                    direction = SortOrder.Direction.valueOf(sortOrder.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ThingsboardException("Unsupported sort order '" + sortOrder + "'! Only 'ASC' or 'DESC' types are allowed.", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
                }
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(pageSize, page, textSearch, sort);
        } else {
            return new PageLink(pageSize, page, textSearch);
        }
    }

}
