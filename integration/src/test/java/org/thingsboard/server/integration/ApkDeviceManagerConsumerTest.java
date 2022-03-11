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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApkDeviceManagerConsumerTest {

    String kafkaMessage = "{\n" +
            "        \"sender\": \"UDM\",\n" +
            "        \"msgType\": \"DEVICE_REGISTRATION\",\n" +
            "        \"body\": {\n" +
            "            \"utenza\": \"utenzaTest\",\n" +
            "            \"matricola\": \"matricolaTest\",\n" +
            "            \"seriale\": \"serialeTest\",\n" +
            "            \"titolare\": \"titolareTest\",\n" +
            "            \"indirizzo\": \"indirizzoTest\",\n" +
            "            \"type\": \"DS-2DF5276-A\",\n" +
            "            \"ts\": \"2022-03-01T12:02:51Z\",\n" +
            "            \"name\": \"NetworkVideoRecorderTestCSV42-192.168.162.13-update-2\",\n" +
            "            \"geopositionLatitude\": 38.194,\n" +
            "            \"geopositionLongitude\": 15.555,\n" +
            "            \"identificationEndpoint\": \"NetworkVideoRecorderTestCSV42\",\n" +
            "            \"tenant\": \"182e02e0-6d19-11ea-89d4-5db0a5de2695\",\n" +
            "            \"organizationName\": null,\n" +
            "            \"description\": null\n" +
            "        }\n" +
            "    }";

    @Test
    public void testDeserializeDeviceRegistrationMsg() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        IntegrationMsg msg = objectMapper.readValue(kafkaMessage, IntegrationMsg.class);
        ApkDeviceRegistrationMsg apkDeviceRegistrationMsg = objectMapper.readValue(msg.getBody(), ApkDeviceRegistrationMsg.class);
        assertEquals(apkDeviceRegistrationMsg.getAddress(), "indirizzoTest");
        assertNotNull(msg.getBody());
    }

}
