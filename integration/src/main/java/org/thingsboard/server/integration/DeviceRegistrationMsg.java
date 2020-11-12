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
package org.thingsboard.server.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.UUID;

@JsonIgnoreProperties
public class DeviceRegistrationMsg {

    @JsonProperty(value = "tenant", required=true)
    String tenantId;

    @JsonProperty(value = "customerId")
    String customerId;

    @Getter
    @JsonProperty(value = "identificationEndpoint", required=true)
    String name;

    @Getter
    @JsonProperty(value = "type", required=true)
    String type;

    @Getter
    @JsonProperty(value = "name")
    String label;

    @Getter
    @JsonProperty(value = "ts")
    String timestamp;

    @Getter
    @JsonProperty(value = "geopositionLatitude")
    Double lat;

    @Getter
    @JsonProperty(value = "geopositionLongitude")
    String lon;

    public TenantId getTenantId() {
        return new TenantId(UUID.fromString(this.tenantId));
    }

    public CustomerId getCustomerId() {
        try {
            return new CustomerId(UUID.fromString(this.customerId));
        } catch (Exception e) {
            return null;
        }
    }
}

