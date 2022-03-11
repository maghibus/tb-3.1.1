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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.Date;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ApkDeviceRegistrationMsg {

    @JsonProperty(value = "utenza", required=true)
    private String userName;

    @JsonProperty(value = "matricola", required=true)
    private String serialNumberMatricola;

    @JsonProperty(value = "seriale", required=true)
    private String serialNumber;

    @JsonProperty(value = "titolare", required=true)
    private String holder;

    @JsonProperty(value = "indirizzo", required=true)
    private String address;

    @JsonProperty(value = "type", required=true)
    private String type;

    @JsonProperty(value = "ts", required=true)
    private Date ts;

    @JsonProperty(value = "name", required=true)
    private String nameOfTheEndpoint;

    @JsonProperty(value = "geopositionLatitude", required = true)
    private Double lat;

    @JsonProperty(value = "geopositionLongitude", required = true)
    private Double lon;

    /**
     * pay attention this is the real name of the device !!
     */
    @JsonProperty(value = "identificationEndpoint", required=true)
    private String name;

    @JsonProperty(value = "tenant", required=true)
    private String tenantId;

    @JsonProperty(value = "organizationName")
    private String organizationName;

    @JsonProperty(value = "description")
    private String description;

    public TenantId getTenantId() {
        return new TenantId(UUID.fromString(this.tenantId));
    }

}
