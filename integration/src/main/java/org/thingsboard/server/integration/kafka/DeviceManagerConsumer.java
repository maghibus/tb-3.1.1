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


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.integration.DeviceCancellationMsg;
import org.thingsboard.server.integration.DeviceRegistrationMsg;
import org.thingsboard.server.integration.DeviceUpdateMsg;
import org.thingsboard.server.integration.IntegrationMsg;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Service
@Slf4j
public class DeviceManagerConsumer {
    private static final String TOPIC = "iot.integration.udm";
    private static final String GROUP_ID = "KafkaUDMConsumer";

    @Autowired
    DeviceService deviceService;

    @Autowired
    AttributesService attributesService;

    @PostConstruct
    public void init() {
        log.info("Kafka device manager consumer started");
    }


    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void listen(String kafkaMessage) {
        log.debug("Device Manager event received \n" + kafkaMessage);

        if (StringUtils.isEmpty(kafkaMessage)){
            log.warn("Received empty event from Device Manager, will be ignored");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IntegrationMsg msg = objectMapper.readValue(kafkaMessage, IntegrationMsg.class);
            switch (msg.getMessageType()) {
                case DEVICE_REGISTRATION:
                    DeviceRegistrationMsg deviceRegistrationMsg = objectMapper.readValue(msg.getBody(), DeviceRegistrationMsg.class);
                    saveDevice(deviceRegistrationMsg);
                    break;
                case DEVICE_DELETE:
                    DeviceCancellationMsg deviceCancellationMsg = objectMapper.readValue(msg.getBody(), DeviceCancellationMsg.class);
                    deleteDevice(deviceCancellationMsg);
                    break;
                case DEVICE_UPDATE:
                    DeviceUpdateMsg deviceUpdateMsg = objectMapper.readValue(msg.getBody(), DeviceUpdateMsg.class);
                    updateDevice(deviceUpdateMsg);
                    break;
                default:
                    log.info("Message type " + msg.getMessageType() + " not supported, will be ignored");
            }

        } catch (Exception e) {
            log.error("Error on Device Manager event processing \n", e);
        }
    }

    private void saveDevice(DeviceRegistrationMsg msg) {
        Device newDevice = new Device(msg.getTenantId(),
                msg.getCustomerId(),
                msg.getName(),
                msg.getType(),
                msg.getLabel());
        try {
            Device savedDevice = deviceService.saveDevice(newDevice);

            if ((msg.getLon() != null && !msg.getLon().isEmpty())
                    && (msg.getLat() != null)) {
                saveServerAttributes(savedDevice, msg);
            }

            log.debug("Device {} with ID {} created successfully!", savedDevice.getName(), savedDevice.getId());
        } catch (Exception e){
            log.warn("Error creating device {} due {}", newDevice.getName(), e.getMessage());
        }
    }

    private void saveServerAttributes(Device device, DeviceRegistrationMsg deviceRegistrationMsg){
        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,
                Arrays.asList(new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("latitude", deviceRegistrationMsg.getLat())),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new StringDataEntry("longitude", deviceRegistrationMsg.getLon()))));
    }

    private void updateDevice(DeviceUpdateMsg msg) {
        Device device = deviceService.findDeviceByTenantIdAndName(msg.getTenantId(), msg.getName());
        if (device != null) {
            device.setLabel(msg.getLabel());
            device.setType(msg.getType());
            deviceService.saveDevice(device);

            if((msg.getLon() != null && !msg.getLon().isEmpty())
                    && (msg.getLat() != null)) {
                saveServerAttributes(device, msg);
            }
        } else {
            log.info("Device {} doesn't exist!", msg.getName());
        }
    }

    private void deleteDevice(DeviceCancellationMsg msg) {
        Device device = deviceService.findDeviceByTenantIdAndName(msg.getTenantId(), msg.getName());
        if (device != null) {
            deviceService.deleteDevice(msg.getTenantId(), device.getId());
            log.debug("Device {} with ID {} deleted successfully!", device.getName(), device.getId());
        } else {
            log.info("Device {] doesn't exist!");
        }
    }


}