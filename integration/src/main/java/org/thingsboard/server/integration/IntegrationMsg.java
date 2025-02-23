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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationMsg {

    @Getter
    @JsonProperty(value = "body", required = true)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    String body;

    @Getter
    @JsonProperty(value = "sender", required = true)
    String sender;

    @Getter
    @JsonProperty(value = "msgType", required = true)
    MessageType messageType;



}
