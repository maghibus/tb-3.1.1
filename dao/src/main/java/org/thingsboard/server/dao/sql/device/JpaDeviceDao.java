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
package org.thingsboard.server.dao.sql.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerDevice;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.*;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@Component
public class JpaDeviceDao extends JpaAbstractSearchTextDao<DeviceEntity, Device> implements DeviceDao {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CustomerDao customerDao;

    @Override
    protected Class<DeviceEntity> getEntityClass() {
        return DeviceEntity.class;
    }

    @Override
    protected CrudRepository<DeviceEntity, UUID> getCrudRepository() {
        return deviceRepository;
    }

    @Override
    public DeviceInfo findDeviceInfoById(TenantId tenantId, UUID deviceId) {
        return DaoUtil.getData(deviceRepository.findDeviceInfoById(deviceId));
    }

    @Override
    public PageData<Device> findDevicesByTenantId(UUID tenantId, PageLink pageLink) {
        if (StringUtils.isEmpty(pageLink.getTextSearch())) {
            return DaoUtil.toPageData(
                    deviceRepository.findByTenantId(
                            tenantId,
                            DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    deviceRepository.findByTenantId(
                            tenantId,
                            Objects.toString(pageLink.getTextSearch(), ""),
                            DaoUtil.toPageable(pageLink)));
        }
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> deviceIds) {
        return service.submit(() -> DaoUtil.convertDataList(deviceRepository.findDevicesByTenantIdAndIdIn(tenantId, deviceIds)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> deviceIds) {
        return service.submit(() -> DaoUtil.convertDataList(
                deviceRepository.findDevicesByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, deviceIds)));
    }

    @Override
    public Optional<Device> findDeviceByTenantIdAndName(UUID tenantId, String name) {
        Device device = DaoUtil.getData(deviceRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(device);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantDeviceTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantDeviceTypesToDto(tenantId, deviceRepository.findTenantDeviceTypes(tenantId)));
    }

    @Override
    public Device findDeviceByTenantIdAndId(TenantId tenantId, UUID id) {
        return DaoUtil.getData(deviceRepository.findByTenantIdAndId(tenantId.getId(), id));
    }

    @Override
    public ListenableFuture<Device> findDeviceByTenantIdAndIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() -> DaoUtil.getData(deviceRepository.findByTenantIdAndId(tenantId.getId(), id)));
    }

    @Override
    public ListenableFuture<MultiCustomerDevice> findMultiCustomerDeviceByIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() -> deviceRepository.findById(id)
                .map(this::getMultiCustomerDevice)
                .orElse(null));
    }

    @Override
    public ListenableFuture<MultiCustomerDevice> findMultiCustomerDeviceByTenantIdAndIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() ->
                Optional.ofNullable(deviceRepository.findByTenantIdAndId(tenantId.getId(), id))
                .map(this::getMultiCustomerDevice)
                .orElse(null));
    }

    @Override
    public PageData<MultiCustomerDevice> findDeviceInfoWithMultipleCustomerByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        List<MultiCustomerDevice> multiCustomerDeviceList = new LinkedList<>();
        PageData<DeviceInfo> pageData = DaoUtil.toPageData(deviceRepository.findDeviceInfosByTenantIdAndType(tenantId, type,  Objects.toString(pageLink.getTextSearch(), ""),
                DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
        pageData.getData().forEach(elem -> {
            MultiCustomerDevice multiCustomerDevice = new MultiCustomerDevice();
            multiCustomerDevice.setId(elem.getId());
            multiCustomerDevice.setCreatedTime(elem.getCreatedTime());
            multiCustomerDevice.setTenantId(elem.getTenantId());
            multiCustomerDevice.setName(elem.getName());
            multiCustomerDevice.setLabel(elem.getLabel());
            multiCustomerDevice.setType(elem.getType());
            multiCustomerDevice.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerDeviceList.add(multiCustomerDevice);
        });

        return new PageData(multiCustomerDeviceList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public PageData<MultiCustomerDevice> findDeviceInfoWithMultipleCustomerByTenantId(UUID tenantId, PageLink pageLink) {
        List<MultiCustomerDevice> multiCustomerDeviceList = new LinkedList<>();

        PageData<DeviceInfo> pageData = DaoUtil.toPageData(deviceRepository.findDeviceInfosByTenantId(tenantId, Objects.toString(pageLink.getTextSearch(), ""),
                DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerDevice multiCustomerDevice = new MultiCustomerDevice();
            multiCustomerDevice.setId(elem.getId());
            multiCustomerDevice.setCreatedTime(elem.getCreatedTime());
            multiCustomerDevice.setTenantId(elem.getTenantId());
            multiCustomerDevice.setName(elem.getName());
            multiCustomerDevice.setLabel(elem.getLabel());
            multiCustomerDevice.setType(elem.getType());
            multiCustomerDevice.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerDeviceList.add(multiCustomerDevice);
        });

        return new PageData(multiCustomerDeviceList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public MultiCustomerDevice findDeviceInfoWithMultipleCustomerByDeviceId(UUID deviceId) {
        Optional<DeviceEntity> device = deviceRepository.findById(deviceId);

        DeviceEntity deviceEntity = device.get();
        return getMultiCustomerDevice(deviceEntity);
    }

    @Override
    public PageData<MultiCustomerDevice> findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        List<MultiCustomerDevice> multiCustomerDeviceList = new LinkedList<>();

        PageData<DeviceInfo> pageData = DaoUtil.toPageData(
                deviceRepository.findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerDevice multiCustomerDevice = new MultiCustomerDevice();
            multiCustomerDevice.setId(elem.getId());
            multiCustomerDevice.setCreatedTime(elem.getCreatedTime());
            multiCustomerDevice.setTenantId(elem.getTenantId());
            multiCustomerDevice.setName(elem.getName());
            multiCustomerDevice.setLabel(elem.getLabel());
            multiCustomerDevice.setType(elem.getType());
            multiCustomerDevice.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerDeviceList.add(multiCustomerDevice);
        });

        return new PageData(multiCustomerDeviceList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public PageData<MultiCustomerDevice> findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        List<MultiCustomerDevice> multiCustomerDeviceList = new LinkedList<>();

        PageData<DeviceInfo> pageData = DaoUtil.toPageData(
                deviceRepository.findDeviceInfoWithMultipleCustomerByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerDevice multiCustomerDevice = new MultiCustomerDevice();
            multiCustomerDevice.setId(elem.getId());
            multiCustomerDevice.setCreatedTime(elem.getCreatedTime());
            multiCustomerDevice.setTenantId(elem.getTenantId());
            multiCustomerDevice.setName(elem.getName());
            multiCustomerDevice.setLabel(elem.getLabel());
            multiCustomerDevice.setType(elem.getType());
            multiCustomerDevice.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerDeviceList.add(multiCustomerDevice);
        });

        return new PageData(multiCustomerDeviceList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    private List<EntitySubtype> convertTenantDeviceTypesToDto(UUID tenantId, List<String> types) {
        List<EntitySubtype> list = Collections.emptyList();
        if (types != null && !types.isEmpty()) {
            list = new ArrayList<>();
            for (String type : types) {
                list.add(new EntitySubtype(new TenantId(tenantId), EntityType.DEVICE, type));
            }
        }
        return list;
    }

    private MultiCustomerDevice getMultiCustomerDevice(DeviceEntity d) {
        MultiCustomerDevice multiCustomerDevice = new MultiCustomerDevice();
        multiCustomerDevice.setId(new DeviceId(d.getId()));
        multiCustomerDevice.setCreatedTime(d.getCreatedTime());
        multiCustomerDevice.setName(d.getName());
        multiCustomerDevice.setType(d.getType());
        multiCustomerDevice.setLabel(d.getLabel());
        multiCustomerDevice.setAdditionalInfo(d.getAdditionalInfo());
        multiCustomerDevice.setTenantId(new TenantId(d.getTenantId()));
        multiCustomerDevice.setCustomerInfo(customerDao.findAssociatedCustomerInfoByDeviceId(d.getId()));
        return multiCustomerDevice;
    }

}
