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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.query.EntityDataQuery;
import org.thingsboard.server.common.data.query.EntityFilter;

@Repository
@ConditionalOnProperty(prefix = "features", value = "multiple_customer_enabled", havingValue = "true")
public class MultipleCustomerAssociationEntityQueryRepository extends DefaultEntityQueryRepository {

    public MultipleCustomerAssociationEntityQueryRepository(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, DefaultQueryLogComponent queryLog) {
        super(jdbcTemplate, transactionTemplate, queryLog);
    }

    @Override
    protected String getDataQuery(String fromClauseData){
        return String.format("select distinct * %s", fromClauseData);
    }

    @Override
    protected String getFromClauseData(EntityDataQuery query, QueryContext ctx, String entityWhereClause, String latestJoinsData, String textSearchQuery, String entityFieldsSelection, String topSelection) {
        return String.format("from (select %s from (select %s from %s where %s) entities %s ) result %s",
                topSelection,
                entityFieldsSelection,
                addEntityTableQuery(ctx, query.getEntityFilter()),
                entityWhereClause,
                latestJoinsData,
                textSearchQuery);
    }

    @Override
    protected String getFromClauseCount(EntityDataQuery query, QueryContext ctx, String entityWhereClause, String latestJoinsCnt, String textSearchQuery, String entityFieldsSelection) {
        return String.format("from (select distinct %s from (select %s from %s where %s) entities %s ) result %s",
                "entities.*",
                entityFieldsSelection,
                addEntityTableQuery(ctx, query.getEntityFilter()),
                entityWhereClause,
                latestJoinsCnt,
                textSearchQuery);
    }

    @Override
    protected String buildPermissionQuery(QueryContext ctx, EntityFilter entityFilter) {
        switch (ctx.getEntityType()) {
            case DEVICE:
            case ASSET:
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

    @Override
    protected String defaultPermissionQuery(QueryContext ctx) {
        ctx.addUuidParameter("permissions_tenant_id", ctx.getTenantId().getId());
        if (ctx.getCustomerId() != null && !ctx.getCustomerId().isNullUid()) {
            ctx.addUuidParameter("permissions_customer_id", ctx.getCustomerId().getId());
            if (ctx.getEntityType() == EntityType.CUSTOMER) {
                return "e.tenant_id=:permissions_tenant_id and e.id=:permissions_customer_id";
            } else {
                switch (ctx.getEntityType()) {
                    case DEVICE:
                        return "e.tenant_id=:permissions_tenant_id and dca.customer_id=:permissions_customer_id and e.id=dca.device_id ";
                    case ASSET:
                        return "e.tenant_id=:permissions_tenant_id and aca.customer_id=:permissions_customer_id and e.id=aca.asset_id ";
                    default:
                        return "";
                }
            }
        } else {
            switch (ctx.getEntityType()) {
                case DEVICE:
                    return "e.tenant_id=:permissions_tenant_id and e.id=dca.device_id ";
                case ASSET:
                    return "e.tenant_id=:permissions_tenant_id and e.id=aca.asset_id ";
                default:
                    return "";
            }
        }
    }

    @Override
    protected String addEntityTableQuery(QueryContext ctx, EntityFilter entityFilter) {
        switch (ctx.getEntityType()) {
            case DEVICE:
                return "device e, device_customer_association dca";
            case ASSET:
                return "asset e, asset_customer_association aca";
            default:
                return entityTableMap.get(ctx.getEntityType());
        }
    }
    
}
