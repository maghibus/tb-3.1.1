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
package org.thingsboard.server.dao.sql.query;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.*;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.EntityTypeFilter;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@ConditionalOnProperty(prefix = "features", value = "multiple_customer_enabled", havingValue = "false", matchIfMissing = true)
public class DefaultEntityQueryRepository implements EntityQueryRepository {
    protected static final Map<EntityType, String> entityTableMap = new HashMap<>();
    protected static final String SELECT_PHONE = " CASE WHEN entity.entity_type = 'TENANT' THEN (select phone from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' THEN (select phone from customer where id = entity_id) END as phone";
    protected static final String SELECT_ZIP = " CASE WHEN entity.entity_type = 'TENANT' THEN (select zip from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' THEN (select zip from customer where id = entity_id) END as zip";
    protected static final String SELECT_ADDRESS_2 = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select address2 from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select address2 from customer where id = entity_id) END as address2";
    protected static final String SELECT_ADDRESS = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select address from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select address from customer where id = entity_id) END as address";
    protected static final String SELECT_CITY = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select city from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select city from customer where id = entity_id) END as city";
    protected static final String SELECT_STATE = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select state from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select state from customer where id = entity_id) END as state";
    protected static final String SELECT_COUNTRY = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select country from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select country from customer where id = entity_id) END as country";
    protected static final String SELECT_TITLE = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select title from tenant where id = entity_id) WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select title from customer where id = entity_id) END as title";
    protected static final String SELECT_LAST_NAME = " CASE WHEN entity.entity_type = 'USER'" +
            " THEN (select last_name from tb_user where id = entity_id) END as last_name";
    protected static final String SELECT_FIRST_NAME = " CASE WHEN entity.entity_type = 'USER'" +
            " THEN (select first_name from tb_user where id = entity_id) END as first_name";
    protected static final String SELECT_REGION = " CASE WHEN entity.entity_type = 'TENANT'" +
            " THEN (select region from tenant where id = entity_id) END as region";
    protected static final String SELECT_EMAIL = " CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN (select email from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select email from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select email from tb_user where id = entity_id)" +
            " END as email";
    protected static final String SELECT_CUSTOMER_ID = "CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN UUID('" + TenantId.NULL_UUID + "')" +
            " WHEN entity.entity_type = 'CUSTOMER' THEN entity_id" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select customer_id from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            //TODO: parse assigned customers or use contains?
            " THEN NULL" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select customer_id from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select customer_id from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select customer_id from entity_view where id = entity_id)" +
            " END as customer_id";
    protected static final String SELECT_TENANT_ID = "SELECT CASE" +
            " WHEN entity.entity_type = 'TENANT' THEN entity_id" +
            " WHEN entity.entity_type = 'CUSTOMER'" +
            " THEN (select tenant_id from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select tenant_id from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            " THEN (select tenant_id from dashboard where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select tenant_id from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select tenant_id from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select tenant_id from entity_view where id = entity_id)" +
            " END as tenant_id";
    protected static final String SELECT_CREATED_TIME = " CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN (select created_time from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select created_time from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select created_time from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            " THEN (select created_time from dashboard where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select created_time from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select created_time from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select created_time from entity_view where id = entity_id)" +
            " END as created_time";
    protected static final String SELECT_NAME = " CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN (select title from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select title from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select CONCAT (first_name, ' ', last_name) from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            " THEN (select title from dashboard where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select name from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select name from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select name from entity_view where id = entity_id)" +
            " END as name";
    protected static final String SELECT_TYPE = " CASE" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select authority from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select type from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select type from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select type from entity_view where id = entity_id)" +
            " ELSE entity.entity_type END as type";
    protected static final String SELECT_LABEL = " CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN (select title from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select title from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select CONCAT (first_name, ' ', last_name) from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            " THEN (select title from dashboard where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select label from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select label from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select name from entity_view where id = entity_id)" +
            " END as label";
    protected static final String SELECT_ADDITIONAL_INFO = " CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN (select additional_info from tenant where id = entity_id)" +
            " WHEN entity.entity_type = 'CUSTOMER' " +
            " THEN (select additional_info from customer where id = entity_id)" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select additional_info from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            " THEN (select '' from dashboard where id = entity_id)" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select additional_info from asset where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select additional_info from device where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select additional_info from entity_view where id = entity_id)" +
            " END as additional_info";

    static {
        entityTableMap.put(EntityType.ASSET, "asset");
        entityTableMap.put(EntityType.DEVICE, "device");
        entityTableMap.put(EntityType.ENTITY_VIEW, "entity_view");
        entityTableMap.put(EntityType.DASHBOARD, "dashboard");
        entityTableMap.put(EntityType.CUSTOMER, "customer");
        entityTableMap.put(EntityType.USER, "tb_user");
        entityTableMap.put(EntityType.TENANT, "tenant");
    }

    public static EntityType[] RELATION_QUERY_ENTITY_TYPES = new EntityType[]{
            EntityType.TENANT, EntityType.CUSTOMER, EntityType.USER, EntityType.DASHBOARD, EntityType.ASSET, EntityType.DEVICE, EntityType.ENTITY_VIEW};

    private static final String HIERARCHICAL_QUERY_TEMPLATE = " FROM (WITH RECURSIVE related_entities(from_id, from_type, to_id, to_type, relation_type, lvl) AS (" +
            " SELECT from_id, from_type, to_id, to_type, relation_type, 1 as lvl" +
            " FROM relation" +
            " WHERE $in_id = :relation_root_id and $in_type = :relation_root_type and relation_type_group = 'COMMON'" +
            " UNION ALL" +
            " SELECT r.from_id, r.from_type, r.to_id, r.to_type, r.relation_type, lvl + 1" +
            " FROM relation r" +
            " INNER JOIN related_entities re ON" +
            " r.$in_id = re.$out_id and r.$in_type = re.$out_type and" +
            " relation_type_group = 'COMMON' %s)" +
            " SELECT re.$out_id entity_id, re.$out_type entity_type, re.lvl lvl" +
            " from related_entities re" +
            " %s ) entity";
    private static final String HIERARCHICAL_TO_QUERY_TEMPLATE = HIERARCHICAL_QUERY_TEMPLATE.replace("$in", "to").replace("$out", "from");
    private static final String HIERARCHICAL_FROM_QUERY_TEMPLATE = HIERARCHICAL_QUERY_TEMPLATE.replace("$in", "from").replace("$out", "to");

    protected final NamedParameterJdbcTemplate jdbcTemplate;
    protected final TransactionTemplate transactionTemplate;
    protected final DefaultQueryLogComponent queryLog;

    public DefaultEntityQueryRepository(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, DefaultQueryLogComponent queryLog) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.queryLog = queryLog;
    }

    @Override
    public long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query) {
        EntityType entityType = resolveEntityType(query.getEntityFilter());
        QueryContext ctx = new QueryContext(new QuerySecurityContext(tenantId, customerId, entityType));
        ctx.append("select count(e.id) from ");
        ctx.append(addEntityTableQuery(ctx, query.getEntityFilter()));
        ctx.append(" e where ");
        ctx.append(buildEntityWhere(ctx, query.getEntityFilter(), Collections.emptyList()));
        return transactionTemplate.execute(status -> {
            long startTs = System.currentTimeMillis();
            try {
                return jdbcTemplate.queryForObject(ctx.getQuery(), ctx, Long.class);
            } finally {
                queryLog.logQuery(ctx, ctx.getQuery(), System.currentTimeMillis() - startTs);
            }
        });
    }

    @Override
    public PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query) {
        return transactionTemplate.execute(status -> {
            EntityType entityType = resolveEntityType(query.getEntityFilter());
            QueryContext ctx = new QueryContext(new QuerySecurityContext(tenantId, customerId, entityType));
            EntityDataPageLink pageLink = query.getPageLink();

            List<EntityKeyMapping> mappings = EntityKeyMapping.prepareKeyMapping(query);

            List<EntityKeyMapping> selectionMapping = mappings.stream().filter(EntityKeyMapping::isSelection)
                    .collect(Collectors.toList());
            List<EntityKeyMapping> entityFieldsSelectionMapping = selectionMapping.stream().filter(mapping -> !mapping.isLatest())
                    .collect(Collectors.toList());
            List<EntityKeyMapping> latestSelectionMapping = selectionMapping.stream().filter(EntityKeyMapping::isLatest)
                    .collect(Collectors.toList());

            List<EntityKeyMapping> filterMapping = mappings.stream().filter(EntityKeyMapping::hasFilter)
                    .collect(Collectors.toList());
            List<EntityKeyMapping> entityFieldsFiltersMapping = filterMapping.stream().filter(mapping -> !mapping.isLatest())
                    .collect(Collectors.toList());

            List<EntityKeyMapping> allLatestMappings = mappings.stream().filter(EntityKeyMapping::isLatest)
                    .collect(Collectors.toList());


            String entityWhereClause = DefaultEntityQueryRepository.this.buildEntityWhere(ctx, query.getEntityFilter(), entityFieldsFiltersMapping);
            String latestJoinsCnt = EntityKeyMapping.buildLatestJoins(ctx, query.getEntityFilter(), entityType, allLatestMappings, true);
            String latestJoinsData = EntityKeyMapping.buildLatestJoins(ctx, query.getEntityFilter(), entityType, allLatestMappings, false);
            String textSearchQuery = DefaultEntityQueryRepository.this.buildTextSearchQuery(ctx, selectionMapping, pageLink.getTextSearch());
            String entityFieldsSelection = EntityKeyMapping.buildSelections(entityFieldsSelectionMapping, query.getEntityFilter().getType(), entityType);
            String entityTypeStr;
            if (query.getEntityFilter().getType().equals(EntityFilterType.RELATIONS_QUERY)) {
                entityTypeStr = "e.entity_type";
            } else {
                entityTypeStr = "'" + entityType.name() + "'";
            }
            if (!StringUtils.isEmpty(entityFieldsSelection)) {
                entityFieldsSelection = String.format("e.id id, %s entity_type, %s", entityTypeStr, entityFieldsSelection);
            } else {
                entityFieldsSelection = String.format("e.id id, %s entity_type", entityTypeStr);
            }
            String latestSelection = EntityKeyMapping.buildSelections(latestSelectionMapping, query.getEntityFilter().getType(), entityType);
            String topSelection = "entities.*";
            if (!StringUtils.isEmpty(latestSelection)) {
                topSelection = topSelection + ", " + latestSelection;
            }

            String fromClauseCount = getFromClauseCount(query, ctx, entityWhereClause, latestJoinsCnt, textSearchQuery, entityFieldsSelection);
            String fromClauseData = getFromClauseData(query, ctx, entityWhereClause, latestJoinsData, textSearchQuery, entityFieldsSelection, topSelection);

            if (!StringUtils.isEmpty(pageLink.getTextSearch())) {
                //Unfortunately, we need to sacrifice performance in case of full text search, because it is applied to all joined records.
                fromClauseCount = fromClauseData;
            }
            String countQuery = getCountQuery(fromClauseCount);

            long startTs = System.currentTimeMillis();
            int totalElements;
            try {
                totalElements = jdbcTemplate.queryForObject(countQuery, ctx, Integer.class);
            } finally {
                queryLog.logQuery(ctx, countQuery, System.currentTimeMillis() - startTs);
            }

            if (totalElements == 0) {
                return new PageData<>();
            }
            String dataQuery = getDataQuery(fromClauseData);

            EntityDataSortOrder sortOrder = pageLink.getSortOrder();
            if (sortOrder != null) {
                Optional<EntityKeyMapping> sortOrderMappingOpt = mappings.stream().filter(EntityKeyMapping::isSortOrder).findFirst();
                if (sortOrderMappingOpt.isPresent()) {
                    EntityKeyMapping sortOrderMapping = sortOrderMappingOpt.get();
                    String direction = sortOrder.getDirection() == EntityDataSortOrder.Direction.ASC ? "asc" : "desc";
                    if (sortOrderMapping.getEntityKey().getType() == EntityKeyType.ENTITY_FIELD) {
                        dataQuery = String.format("%s order by %s %s", dataQuery, sortOrderMapping.getValueAlias(), direction);
                    } else {
                        dataQuery = String.format("%s order by %s %s, %s %s", dataQuery,
                                sortOrderMapping.getSortOrderNumAlias(), direction, sortOrderMapping.getSortOrderStrAlias(), direction);
                    }
                }
            }
            int startIndex = pageLink.getPageSize() * pageLink.getPage();
            if (pageLink.getPageSize() > 0) {
                dataQuery = String.format("%s limit %s offset %s", dataQuery, pageLink.getPageSize(), startIndex);
            }
            startTs = System.currentTimeMillis();
            List<Map<String, Object>> rows;
            try {
                rows = jdbcTemplate.queryForList(dataQuery, ctx);
            } finally {
                queryLog.logQuery(ctx, dataQuery, System.currentTimeMillis() - startTs);
            }
            return EntityDataAdapter.createEntityData(pageLink, selectionMapping, rows, totalElements);
        });
    }

    protected String getCountQuery(String fromClauseCount){
        return String.format("select count(id) %s", fromClauseCount);
    }

    protected String getFromClauseData(EntityDataQuery query, QueryContext ctx, String entityWhereClause, String latestJoinsData, String textSearchQuery, String entityFieldsSelection, String topSelection) {
        return String.format("from (select %s from (select %s from %s e where %s) entities %s ) result %s",
                topSelection,
                entityFieldsSelection,
                addEntityTableQuery(ctx, query.getEntityFilter()),
                entityWhereClause,
                latestJoinsData,
                textSearchQuery);
    }

    protected String getFromClauseCount(EntityDataQuery query, QueryContext ctx, String entityWhereClause, String latestJoinsCnt, String textSearchQuery, String entityFieldsSelection) {
        return String.format("from (select %s from (select %s from %s e where %s) entities %s ) result %s",
                "entities.*",
                entityFieldsSelection,
                addEntityTableQuery(ctx, query.getEntityFilter()),
                entityWhereClause,
                latestJoinsCnt,
                textSearchQuery);
    }


    protected String buildEntityWhere(QueryContext ctx, EntityFilter entityFilter, List<EntityKeyMapping> entityFieldsFilters) {
        String permissionQuery = this.buildPermissionQuery(ctx, entityFilter);
        String entityFilterQuery = this.buildEntityFilterQuery(ctx, entityFilter);
        String entityFieldsQuery = EntityKeyMapping.buildQuery(ctx, entityFieldsFilters, entityFilter.getType());
        String result = permissionQuery;
        if (!entityFilterQuery.isEmpty()) {
            result += " and (" + entityFilterQuery + ")";
        }
        if (!entityFieldsQuery.isEmpty()) {
            result += " and (" + entityFieldsQuery + ")";
        }
        return result;
    }

    protected String getDataQuery(String fromClauseData){
        return String.format("select * %s", fromClauseData);
    }

    protected String buildPermissionQuery(QueryContext ctx, EntityFilter entityFilter) {
        switch (entityFilter.getType()) {
            case RELATIONS_QUERY:
            case DEVICE_SEARCH_QUERY:
            case ASSET_SEARCH_QUERY:
            case ENTITY_VIEW_SEARCH_QUERY:
                return this.defaultPermissionQuery(ctx);
            default:
                if (ctx.getEntityType() == EntityType.TENANT) {
                    ctx.addUuidParameter("permissions_tenant_id", ctx.getTenantId().getId());
                    return "e.id=:permissions_tenant_id";
                } else {
                    return this.defaultPermissionQuery(ctx);
                }
        }
    }


    protected String defaultPermissionQuery(QueryContext ctx) {
        ctx.addUuidParameter("permissions_tenant_id", ctx.getTenantId().getId());
        if (ctx.getCustomerId() != null && !ctx.getCustomerId().isNullUid()) {
            ctx.addUuidParameter("permissions_customer_id", ctx.getCustomerId().getId());
            if (ctx.getEntityType() == EntityType.CUSTOMER) {
                return "e.tenant_id=:permissions_tenant_id and e.id=:permissions_customer_id";
            } else {
                return "e.tenant_id=:permissions_tenant_id and e.customer_id=:permissions_customer_id";
            }
        } else {
            return "e.tenant_id=:permissions_tenant_id";
        }
    }

    private String buildEntityFilterQuery(QueryContext ctx, EntityFilter entityFilter) {
        switch (entityFilter.getType()) {
            case SINGLE_ENTITY:
                return this.singleEntityQuery(ctx, (SingleEntityFilter) entityFilter);
            case ENTITY_LIST:
                return this.entityListQuery(ctx, (EntityListFilter) entityFilter);
            case ENTITY_NAME:
                return this.entityNameQuery(ctx, (EntityNameFilter) entityFilter);
            case ASSET_TYPE:
            case DEVICE_TYPE:
            case ENTITY_VIEW_TYPE:
                return this.typeQuery(ctx, entityFilter);
            case RELATIONS_QUERY:
            case DEVICE_SEARCH_QUERY:
            case ASSET_SEARCH_QUERY:
            case ENTITY_VIEW_SEARCH_QUERY:
                return "";
            default:
                throw new RuntimeException("Not implemented!");
        }
    }

    protected String addEntityTableQuery(QueryContext ctx, EntityFilter entityFilter) {
        switch (entityFilter.getType()) {
            case RELATIONS_QUERY:
                return relationQuery(ctx, (RelationsQueryFilter) entityFilter);
            case DEVICE_SEARCH_QUERY:
                DeviceSearchQueryFilter deviceQuery = (DeviceSearchQueryFilter) entityFilter;
                return entitySearchQuery(ctx, deviceQuery, EntityType.DEVICE, deviceQuery.getDeviceTypes());
            case ASSET_SEARCH_QUERY:
                AssetSearchQueryFilter assetQuery = (AssetSearchQueryFilter) entityFilter;
                return entitySearchQuery(ctx, assetQuery, EntityType.ASSET, assetQuery.getAssetTypes());
            case ENTITY_VIEW_SEARCH_QUERY:
                EntityViewSearchQueryFilter entityViewQuery = (EntityViewSearchQueryFilter) entityFilter;
                return entitySearchQuery(ctx, entityViewQuery, EntityType.ENTITY_VIEW, entityViewQuery.getEntityViewTypes());
            default:
                return entityTableSimpleQuery(ctx.getEntityType());
        }
    }

    protected String entityTableSimpleQuery(EntityType entityType) {
        return entityTableMap.get(entityType);
    }

    protected String entitySearchQuery(QueryContext ctx, EntitySearchQueryFilter entityFilter, EntityType entityType, List<String> types) {
        EntityId rootId = entityFilter.getRootEntity();
        //TODO: fetch last level only.
        //TODO: fetch distinct records.
        String lvlFilter = getLvlFilter(entityFilter.getMaxLevel());
        String selectFields = "SELECT tenant_id, " + getCustomerIdAlias(entityType) + ", id, created_time, type, name, additional_info "
                + (entityType.equals(EntityType.ENTITY_VIEW) ? "" : ", label ")
                + "FROM " + getFrom(entityType) + " WHERE id in ( SELECT entity_id";
        String from = getQueryTemplate(entityFilter.getDirection());
        String whereFilter = " WHERE";
        if (!StringUtils.isEmpty(entityFilter.getRelationType())) {
            ctx.addStringParameter("where_relation_type", entityFilter.getRelationType());
            whereFilter += " re.relation_type = :where_relation_type AND";
        }
        whereFilter += " re." + (entityFilter.getDirection().equals(EntitySearchDirection.FROM) ? "to" : "from") + "_type = :where_entity_type";

        from = String.format(from, lvlFilter, whereFilter);
        String query = "( " + selectFields + from + ")";
        if (types != null && !types.isEmpty()) {
            query += " and type in (:relation_sub_types)";
            ctx.addStringListParameter("relation_sub_types", types);
        }
        query += " )";
        ctx.addUuidParameter("relation_root_id", rootId.getId());
        ctx.addStringParameter("relation_root_type", rootId.getEntityType().name());
        ctx.addStringParameter("where_entity_type", entityType.name());
        return query;
    }

    protected String getCustomerIdAlias(EntityType entityType) {
        return " customer_id ";
    }

    protected String getFrom(EntityType entityType) {
        return entityType.name();
    }

    protected String relationQuery(QueryContext ctx, RelationsQueryFilter entityFilter) {
        EntityId rootId = entityFilter.getRootEntity();
        String lvlFilter = getLvlFilter(entityFilter.getMaxLevel());
        String selectFields = SELECT_TENANT_ID + ", " + relationQuerySelectCustomerId()
                + ", " + SELECT_CREATED_TIME + ", " +
                " entity.entity_id as id,"
                + SELECT_TYPE + ", " + SELECT_NAME + ", " + SELECT_LABEL + ", " +
                SELECT_FIRST_NAME + ", " + SELECT_LAST_NAME + ", " + SELECT_EMAIL + ", " + SELECT_REGION + ", " +
                SELECT_TITLE + ", " + SELECT_COUNTRY + ", " + SELECT_STATE + ", " + SELECT_CITY + ", " +
                SELECT_ADDRESS + ", " + SELECT_ADDRESS_2 + ", " + SELECT_ZIP + ", " + SELECT_PHONE + ", " + SELECT_ADDITIONAL_INFO +
                ", entity.entity_type as entity_type";
        String from = getQueryTemplate(entityFilter.getDirection());
        ctx.addUuidParameter("relation_root_id", rootId.getId());
        ctx.addStringParameter("relation_root_type", rootId.getEntityType().name());

        StringBuilder whereFilter = new StringBuilder();
        ;
        boolean noConditions = true;
        if (entityFilter.getFilters() != null && !entityFilter.getFilters().isEmpty()) {
            boolean single = entityFilter.getFilters().size() == 1;
            int entityTypeFilterIdx = 0;
            for (EntityTypeFilter etf : entityFilter.getFilters()) {
                String etfCondition = buildEtfCondition(ctx, etf, entityFilter.getDirection(), entityTypeFilterIdx++);
                if (!etfCondition.isEmpty()) {
                    if (noConditions) {
                        whereFilter.append(" WHERE ");
                        noConditions = false;
                    } else {
                        whereFilter.append(" OR ");
                    }
                    if (!single) {
                        whereFilter.append(" (");
                    }
                    whereFilter.append(etfCondition);
                    if (!single) {
                        whereFilter.append(" )");
                    }
                }
            }
        }
        if (noConditions) {
            whereFilter.append(" WHERE re.")
                    .append(entityFilter.getDirection().equals(EntitySearchDirection.FROM) ? "to" : "from")
                    .append("_type in (:where_entity_types").append(")");
            ctx.addStringListParameter("where_entity_types", Arrays.stream(RELATION_QUERY_ENTITY_TYPES).map(EntityType::name).collect(Collectors.toList()));
        }
        from = String.format(from, lvlFilter, whereFilter);
        return "( " + selectFields + from + ")";
    }

    protected String relationQuerySelectCustomerId() {
        return SELECT_CUSTOMER_ID;
    }

    protected String buildEtfCondition(QueryContext ctx, EntityTypeFilter etf, EntitySearchDirection direction, int entityTypeFilterIdx) {
        StringBuilder whereFilter = new StringBuilder();
        String relationType = etf.getRelationType();
        List<EntityType> entityTypes = etf.getEntityTypes();
        List<String> whereEntityTypes;
        if (entityTypes == null || entityTypes.isEmpty()) {
            whereEntityTypes = Collections.emptyList();
        } else {
            whereEntityTypes = etf.getEntityTypes().stream().map(EntityType::name).collect(Collectors.toList());
        }
        boolean hasRelationType = !StringUtils.isEmpty(relationType);
        if (hasRelationType) {
            ctx.addStringParameter("where_relation_type" + entityTypeFilterIdx, relationType);
            whereFilter
                    .append("re.relation_type = :where_relation_type").append(entityTypeFilterIdx);
        }
        if (!whereEntityTypes.isEmpty()) {
            if (hasRelationType) {
                whereFilter.append(" and ");
            }
            whereFilter.append("re.")
                    .append(direction.equals(EntitySearchDirection.FROM) ? "to" : "from")
                    .append("_type in (:where_entity_types").append(entityTypeFilterIdx).append(")");
            ctx.addStringListParameter("where_entity_types" + entityTypeFilterIdx, whereEntityTypes);
        }
        return whereFilter.toString();
    }

    protected String getLvlFilter(int maxLevel) {
        return maxLevel > 0 ? ("and lvl <= " + (maxLevel - 1)) : "";
    }

    protected String getQueryTemplate(EntitySearchDirection direction) {
        String from;
        if (direction.equals(EntitySearchDirection.FROM)) {
            from = HIERARCHICAL_FROM_QUERY_TEMPLATE;
        } else {
            from = HIERARCHICAL_TO_QUERY_TEMPLATE;
        }
        return from;
    }

    protected String buildTextSearchQuery(QueryContext ctx, List<EntityKeyMapping> selectionMapping, String searchText) {
        if (!StringUtils.isEmpty(searchText) && !selectionMapping.isEmpty()) {
            String lowerSearchText = "%" + searchText.toLowerCase() + "%";
            ctx.addStringParameter("lowerSearchTextParam", lowerSearchText);
            List<String> searchAliases = selectionMapping.stream().filter(EntityKeyMapping::isSearchable).map(EntityKeyMapping::getValueAlias).collect(Collectors.toList());
            String searchAliasesExpression;
            if (searchAliases.size() > 1) {
                searchAliasesExpression = "CONCAT(" + String.join(" , ", searchAliases) + ")";
            } else {
                searchAliasesExpression = searchAliases.get(0);
            }
            return String.format(" WHERE LOWER(%s) LIKE :%s", searchAliasesExpression, "lowerSearchTextParam");
        } else {
            return "";
        }
    }

    private String singleEntityQuery(QueryContext ctx, SingleEntityFilter filter) {
        ctx.addUuidParameter("entity_filter_single_entity_id", filter.getSingleEntity().getId());
        return "e.id=:entity_filter_single_entity_id";
    }

    private String entityListQuery(QueryContext ctx, EntityListFilter filter) {
        ctx.addUuidListParameter("entity_filter_entity_ids", filter.getEntityList().stream().map(UUID::fromString).collect(Collectors.toList()));
        return "e.id in (:entity_filter_entity_ids)";
    }

    private String entityNameQuery(QueryContext ctx, EntityNameFilter filter) {
        ctx.addStringParameter("entity_filter_name_filter", filter.getEntityNameFilter());
        return "lower(e.search_text) like lower(concat(:entity_filter_name_filter, '%%'))";
    }

    private String typeQuery(QueryContext ctx, EntityFilter filter) {
        String type;
        String name;
        switch (filter.getType()) {
            case ASSET_TYPE:
                type = ((AssetTypeFilter) filter).getAssetType();
                name = ((AssetTypeFilter) filter).getAssetNameFilter();
                break;
            case DEVICE_TYPE:
                type = ((DeviceTypeFilter) filter).getDeviceType();
                name = ((DeviceTypeFilter) filter).getDeviceNameFilter();
                break;
            case ENTITY_VIEW_TYPE:
                type = ((EntityViewTypeFilter) filter).getEntityViewType();
                name = ((EntityViewTypeFilter) filter).getEntityViewNameFilter();
                break;
            default:
                throw new RuntimeException("Not supported!");
        }
        ctx.addStringParameter("entity_filter_type_query_type", type);
        ctx.addStringParameter("entity_filter_type_query_name", name);
        return "e.type = :entity_filter_type_query_type and lower(e.search_text) like lower(concat(:entity_filter_type_query_name, '%%'))";
    }

    protected EntityType resolveEntityType(EntityFilter entityFilter) {
        switch (entityFilter.getType()) {
            case SINGLE_ENTITY:
                return ((SingleEntityFilter) entityFilter).getSingleEntity().getEntityType();
            case ENTITY_LIST:
                return ((EntityListFilter) entityFilter).getEntityType();
            case ENTITY_NAME:
                return ((EntityNameFilter) entityFilter).getEntityType();
            case ASSET_TYPE:
            case ASSET_SEARCH_QUERY:
                return EntityType.ASSET;
            case DEVICE_TYPE:
            case DEVICE_SEARCH_QUERY:
                return EntityType.DEVICE;
            case ENTITY_VIEW_TYPE:
            case ENTITY_VIEW_SEARCH_QUERY:
                return EntityType.ENTITY_VIEW;
            case RELATIONS_QUERY:
                return ((RelationsQueryFilter) entityFilter).getRootEntity().getEntityType();
            default:
                throw new RuntimeException("Not implemented!");
        }
    }
}
