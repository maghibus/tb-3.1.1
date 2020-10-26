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
package org.thingsboard.server.integration.kafka;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.integration.DeviceRegistrationMsg;
import org.thingsboard.server.integration.IntegrationMsg;


@Service
@Slf4j
public class DeviceManagerConsumer {
    private static final String TOPIC = "iot.integration.udm";
    private static final String GROUP_ID = "KafkaUDMConsumer";

    @Autowired
    DeviceService deviceService;

    @PostConstruct
    public void init() {
        log.info("Kafka device manager consumer started");
    }


    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void listen(String kafkaMessage) {
        log.info("Device registration event received! \n" + kafkaMessage);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType type = objectMapper.getTypeFactory().constructParametricType(IntegrationMsg.class, DeviceRegistrationMsg.class);
            IntegrationMsg<DeviceRegistrationMsg> msg = objectMapper.readValue(kafkaMessage, type);
            DeviceRegistrationMsg device = msg.getBody();
            Device newDevice = new Device(device.getTenantId(),
                    device.getCustomerId(),
                    device.getName(),
                    device.getType(),
                    device.getLabel());

            Device savedDevice = deviceService.saveDevice(newDevice);
            log.info("Device with ID = '" + savedDevice.getId() + "' created successfully!");
        } catch (Exception e) {
            log.error("Error on device registration event processing \n", e);
        }
    }
}