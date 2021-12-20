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
package org.thingsboard.server.dao.sql.asset;

import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerAsset;
import org.thingsboard.server.common.data.multiplecustomer.MultiCustomerDevice;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.asset.AssetDao;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.model.sql.AssetEntity;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.*;

/**
 * Created by Valerii Sosliuk on 5/19/2017.
 */
@Component
public class JpaAssetDao extends JpaAbstractSearchTextDao<AssetEntity, Asset> implements AssetDao {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CustomerDao customerDao;

    @Override
    protected Class<AssetEntity> getEntityClass() {
        return AssetEntity.class;
    }

    @Override
    protected CrudRepository<AssetEntity, UUID> getCrudRepository() {
        return assetRepository;
    }

    @Override
    public AssetInfo findAssetInfoById(TenantId tenantId, UUID assetId) {
        return DaoUtil.getData(assetRepository.findAssetInfoById(assetId));
    }

    @Override
    public PageData<Asset> findAssetsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AssetInfo> findAssetInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> assetIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(assetRepository.findByTenantIdAndIdIn(tenantId, assetIds)));
    }

    @Override
    public PageData<Asset> findAssetsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Asset>> findAssetsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> assetIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(assetRepository.findByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, assetIds)));
    }

    @Override
    public Optional<Asset> findAssetsByTenantIdAndName(UUID tenantId, String name) {
        Asset asset = DaoUtil.getData(assetRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(asset);
    }

    @Override
    public PageData<Asset> findAssetsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    @Override
    public PageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantAssetTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantAssetTypesToDto(tenantId, assetRepository.findTenantAssetTypes(tenantId)));
    }

    @Override
    public MultiCustomerAsset findDeviceInfoWithMultipleCustomerByDeviceId(UUID id) {
        Optional<AssetEntity> asset = assetRepository.findById(id);
        AssetEntity assetEntity = asset.get();
        MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
        multiCustomerAsset.setId(new AssetId(assetEntity.getId()));
        multiCustomerAsset.setCreatedTime(assetEntity.getCreatedTime());
        multiCustomerAsset.setName(assetEntity.getName());
        multiCustomerAsset.setType(assetEntity.getType());
        multiCustomerAsset.setLabel(assetEntity.getLabel());
        multiCustomerAsset.setAdditionalInfo(assetEntity.getAdditionalInfo());
        multiCustomerAsset.setTenantId(new TenantId(assetEntity.getTenantId()));
        return multiCustomerAsset;
    }

    @Override
    public PageData<MultiCustomerAsset> findAssetInfoWithMultipleCustomerByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        List<MultiCustomerAsset> multiCustomerAssetList = new LinkedList<>();

        PageData<AssetInfo> pageData = DaoUtil.toPageData(assetRepository.findAssetInfosByTenantIdAndType(tenantId, type,  Objects.toString(pageLink.getTextSearch(), ""),
                DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
            multiCustomerAsset.setId(elem.getId());
            multiCustomerAsset.setCreatedTime(elem.getCreatedTime());
            multiCustomerAsset.setTenantId(elem.getTenantId());
            multiCustomerAsset.setName(elem.getName());
            multiCustomerAsset.setLabel(elem.getLabel());
            multiCustomerAsset.setType(elem.getType());
            multiCustomerAsset.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerAssetList.add(multiCustomerAsset);
        });

        return new PageData(multiCustomerAssetList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public PageData<MultiCustomerAsset> findAssetInfoWithMultipleCustomerByTenantId(UUID tenantId, PageLink pageLink) {
        List<MultiCustomerAsset> multiCustomerAssetList = new LinkedList<>();

        PageData<AssetInfo> pageData = DaoUtil.toPageData(assetRepository.findAssetInfosByTenantId(tenantId, Objects.toString(pageLink.getTextSearch(), ""),
                DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
            multiCustomerAsset.setId(elem.getId());
            multiCustomerAsset.setCreatedTime(elem.getCreatedTime());
            multiCustomerAsset.setTenantId(elem.getTenantId());
            multiCustomerAsset.setName(elem.getName());
            multiCustomerAsset.setLabel(elem.getLabel());
            multiCustomerAsset.setType(elem.getType());
            multiCustomerAsset.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerAssetList.add(multiCustomerAsset);
        });

        return new PageData(multiCustomerAssetList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public PageData<MultiCustomerAsset> findAssetInfoWithMultipleCustomersByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        List<MultiCustomerAsset> multiCustomerAssetList = new LinkedList<>();

        PageData<AssetInfo> pageData = DaoUtil.toPageData(
                assetRepository.findAssetInfoWithMultipleCustomersByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
            multiCustomerAsset.setId(elem.getId());
            multiCustomerAsset.setCreatedTime(elem.getCreatedTime());
            multiCustomerAsset.setTenantId(elem.getTenantId());
            multiCustomerAsset.setName(elem.getName());
            multiCustomerAsset.setLabel(elem.getLabel());
            multiCustomerAsset.setType(elem.getType());
            multiCustomerAsset.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerAssetList.add(multiCustomerAsset);
        });

        return new PageData(multiCustomerAssetList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public PageData<MultiCustomerAsset> findAssetInfoWithMultipleCustomersByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        List<MultiCustomerAsset> multiCustomerAssetList = new LinkedList<>();

        PageData<AssetInfo> pageData = DaoUtil.toPageData(
                assetRepository.findAssetInfoWithMultipleCustomersByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));

        pageData.getData().forEach(elem -> {
            MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
            multiCustomerAsset.setId(elem.getId());
            multiCustomerAsset.setCreatedTime(elem.getCreatedTime());
            multiCustomerAsset.setTenantId(elem.getTenantId());
            multiCustomerAsset.setName(elem.getName());
            multiCustomerAsset.setLabel(elem.getLabel());
            multiCustomerAsset.setType(elem.getType());
            multiCustomerAsset.setAdditionalInfo(elem.getAdditionalInfo());
            multiCustomerAssetList.add(multiCustomerAsset);
        });

        return new PageData(multiCustomerAssetList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public ListenableFuture<MultiCustomerAsset> findMultiCustomerAssetByIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() -> assetRepository.findById(id)
                .map(this::getMultiCustomerAsset)
                .orElse(null));
    }


    private List<EntitySubtype> convertTenantAssetTypesToDto(UUID tenantId, List<String> types) {
        List<EntitySubtype> list = Collections.emptyList();
        if (types != null && !types.isEmpty()) {
            list = new ArrayList<>();
            for (String type : types) {
                list.add(new EntitySubtype(new TenantId(tenantId), EntityType.ASSET, type));
            }
        }
        return list;
    }

    private MultiCustomerAsset getMultiCustomerAsset(AssetEntity a) {
        MultiCustomerAsset multiCustomerAsset = new MultiCustomerAsset();
        multiCustomerAsset.setId(new AssetId(a.getId()));
        multiCustomerAsset.setCreatedTime(a.getCreatedTime());
        multiCustomerAsset.setName(a.getName());
        multiCustomerAsset.setType(a.getType());
        multiCustomerAsset.setLabel(a.getLabel());
        multiCustomerAsset.setAdditionalInfo(a.getAdditionalInfo());
        multiCustomerAsset.setTenantId(new TenantId(a.getTenantId()));
        multiCustomerAsset.setCustomerInfo(customerDao.findAssociatedCustomerInfoByAssetId(a.getId()));
        return multiCustomerAsset;
    }


}
