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
import org.thingsboard.server.common.data.id.TenantId;

@Repository
@ConditionalOnProperty(prefix = "features", value = "multiple_customer_enabled", havingValue = "true")
public class MultipleCustomerAssociationEntityQueryRepository extends DefaultEntityQueryRepository {

    private static final String SELECT_CUSTOMER_ID = "CASE" +
            " WHEN entity.entity_type = 'TENANT'" +
            " THEN UUID('" + TenantId.NULL_UUID + "')" +
            " WHEN entity.entity_type = 'CUSTOMER' THEN entity_id" +
            " WHEN entity.entity_type = 'USER'" +
            " THEN (select customer_id from tb_user where id = entity_id)" +
            " WHEN entity.entity_type = 'DASHBOARD'" +
            //TODO: parse assigned customers or use contains?
            " THEN NULL" +
            " WHEN entity.entity_type = 'ASSET'" +
            " THEN (select aca.customer_id from asset a" +
            " left join (select asset_id, customer_id from asset_customer_association) aca on a.id = aca.asset_id" +
            " where id = entity_id)" +
            " WHEN entity.entity_type = 'DEVICE'" +
            " THEN (select dca.customer_id from device d" +
            " left join (select device_id, customer_id from device_customer_association) dca on d.id = dca.device_id" +
            " where id = entity_id)" +
            " WHEN entity.entity_type = 'ENTITY_VIEW'" +
            " THEN (select customer_id from entity_view where id = entity_id)" +
            " END as customer_id";

    public MultipleCustomerAssociationEntityQueryRepository(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, DefaultQueryLogComponent queryLog) {
        super(jdbcTemplate, transactionTemplate, queryLog);
    }

    @Override
    protected String getDataQuery(String fromClauseData){
        return String.format("select distinct * %s", fromClauseData);
    }

    protected String entityTableSimpleQuery(EntityType entityType) {
        switch (entityType) {
            case DEVICE:
            case ASSET:
                return "(SELECT tenant_id, " + getCustomerIdAlias(entityType) + ", id, created_time, type, name, additional_info, label "
                        + "FROM " + getFrom(entityType) + ")";
            default:
                return entityTableMap.get(entityType);
        }
    }

    @Override
    protected String getCustomerIdAlias(EntityType entityType) {
        if (entityType.equals(EntityType.DEVICE)){
            return "dca.customer_id";
        } else if(entityType.equals(EntityType.ASSET)) {
            return "aca.customer_id";
        }
        return "customer_id";
    }

    @Override
    protected String getFrom(EntityType entityType) {
        if (entityType.equals(EntityType.DEVICE)){
            return "device e left join (select device_id, customer_id from device_customer_association) dca on e.id = dca.device_id";
        } else if (entityType.equals(EntityType.ASSET)) {
            return "asset e left join (select asset_id, customer_id from asset_customer_association) aca on e.id = aca.asset_id";
        }
        return entityType.name();
    }

    @Override
    protected String relationQuerySelectCustomerId() {
        return SELECT_CUSTOMER_ID;
    }
}
