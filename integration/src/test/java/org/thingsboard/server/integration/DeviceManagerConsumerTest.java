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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceManagerConsumerTest {


    @Test
    public void testDeserializeDeviceRegistrationMsg() throws Exception {
        String kafkaMessage = "{\"sender\":\"UDM\",\"msgType\":\"DEVICE_REGISTRATION\",\"body\":{\"type\":\"test\",\"ts\":\"2021-12-22T12:03:51Z\",\"name\":\"test-prov-rem\",\"geopositionLatitude\":38.207,\"geopositionLongitude\":15.509,\"identificationEndpoint\":\"Sensor-9004\",\"tenant\":\"ef921b80-4856-11ec-9a70-dfaccafeb23c\",\"organizationName\":null,\"description\":null}}";

        ObjectMapper objectMapper = new ObjectMapper();
        IntegrationMsg msg = objectMapper.readValue(kafkaMessage, IntegrationMsg.class);
        DeviceRegistrationMsg deviceRegistrationMsg = objectMapper.readValue(msg.getBody(), DeviceRegistrationMsg.class);
        assertNotNull(msg.getBody());

    }

}
