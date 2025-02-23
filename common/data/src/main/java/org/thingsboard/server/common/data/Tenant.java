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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.TenantId;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.validation.NoXss;

@EqualsAndHashCode(callSuper = true)
public class Tenant extends ContactBased<TenantId> implements HasTenantId {

    private static final long serialVersionUID = 8057243243859922101L;

    @NoXss
    private String title;
    @NoXss
    private String region;
    private boolean isolatedTbCore;
    private boolean isolatedTbRuleEngine;

    public Tenant() {
        super();
    }

    public Tenant(TenantId id) {
        super(id);
    }
    
    public Tenant(Tenant tenant) {
        super(tenant);
        this.title = tenant.getTitle();
        this.region = tenant.getRegion();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    @JsonIgnore
    public TenantId getTenantId() {
        return getId();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isIsolatedTbCore() {
        return isolatedTbCore;
    }

    public void setIsolatedTbCore(boolean isolatedTbCore) {
        this.isolatedTbCore = isolatedTbCore;
    }

    public boolean isIsolatedTbRuleEngine() {
        return isolatedTbRuleEngine;
    }

    public void setIsolatedTbRuleEngine(boolean isolatedTbRuleEngine) {
        this.isolatedTbRuleEngine = isolatedTbRuleEngine;
    }

    @Override
    public String getSearchText() {
        return getTitle();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tenant [title=");
        builder.append(title);
        builder.append(", region=");
        builder.append(region);
        builder.append(", isolatedTbCore=");
        builder.append(isolatedTbCore);
        builder.append(", isolatedTbRuleEngine=");
        builder.append(isolatedTbRuleEngine);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", country=");
        builder.append(country);
        builder.append(", state=");
        builder.append(state);
        builder.append(", city=");
        builder.append(city);
        builder.append(", address=");
        builder.append(address);
        builder.append(", address2=");
        builder.append(address2);
        builder.append(", zip=");
        builder.append(zip);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", email=");
        builder.append(email);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
