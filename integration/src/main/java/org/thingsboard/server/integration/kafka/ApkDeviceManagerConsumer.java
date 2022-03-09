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
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.integration.ApkDeviceDeleteMsg;
import org.thingsboard.server.integration.ApkDeviceRegistrationMsg;
import org.thingsboard.server.integration.ApkDeviceUpdateMsg;
import org.thingsboard.server.integration.IntegrationMsg;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ApkDeviceManagerConsumer {
    private static final String TOPIC = "iot.integration.udm.apk";
    private static final String GROUP_ID = "KafkaUDMConsumer";

    @Autowired
    DeviceService deviceService ;

    @Autowired
    AttributesService attributesService;

    @PostConstruct
    public void init() {
        log.info("Kafka apk device manager consumer started");
    }

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void listen(String kafkaMessage) {
       handleEvent(kafkaMessage);
    }

    public void handleEvent(String kafkaMessage){
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
                        ApkDeviceRegistrationMsg deviceRegistrationMsg = objectMapper.readValue(msg.getBody(), ApkDeviceRegistrationMsg.class);
                        saveDevice(deviceRegistrationMsg);
                        break;
                    case DEVICE_DELETE:
                        ApkDeviceDeleteMsg deviceCancellationMsg = objectMapper.readValue(msg.getBody(), ApkDeviceDeleteMsg.class);
                        deleteDevice(deviceCancellationMsg);
                        break;
                    case DEVICE_UPDATE:
                        ApkDeviceUpdateMsg deviceUpdateMsg = objectMapper.readValue(msg.getBody(), ApkDeviceUpdateMsg.class);
                        saveDevice(deviceUpdateMsg);
                        break;
                    default:
                        log.info("Message type " + msg.getMessageType() + " not supported, will be ignored");
                }


        } catch (Exception e) {
            log.error("Error on Device Manager event processing \n", e);
        }
    }
    private void saveDevice(ApkDeviceRegistrationMsg msg) {

        Device device = deviceService.findDeviceByTenantIdAndName(msg.getTenantId(), msg.getName());

        if (device != null) {
            device.setLabel(msg.getNameOfTheEndpoint());
            device.setType(msg.getType());
            device = deviceService.saveDevice(device);
        } else {
            log.info("Device {} doesn't exist! Creating a new one", msg.getName());

            device = new Device(msg.getTenantId(),
                    new CustomerId(EntityId.NULL_UUID),
                    msg.getName(),
                    msg.getType(),
                    msg.getNameOfTheEndpoint());

            device = deviceService.saveDevice(device);
            log.debug("Device {} with ID {} created successfully!", device.getName(), device.getId());
        }

        saveServerAttributes(device, msg);
    }

    private void saveServerAttributes(Device device, ApkDeviceRegistrationMsg deviceRegistrationMsg){

        List<AttributeKvEntry> baseAttributeKvEntryList = new LinkedList<>();

        if (deviceRegistrationMsg.getUserName() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("Utenza",deviceRegistrationMsg.getUserName())));

        if ( deviceRegistrationMsg.getSerialNumberMatricola()!= null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("Matricola", deviceRegistrationMsg.getSerialNumberMatricola())));

        if (deviceRegistrationMsg.getSerialNumber() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("serialNumber", deviceRegistrationMsg.getSerialNumber())));

        if (deviceRegistrationMsg.getHolder() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("Titolare",deviceRegistrationMsg.getHolder())));

        if (deviceRegistrationMsg.getAddress() != null)
            baseAttributeKvEntryList.add(       new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("Indirizzo",deviceRegistrationMsg.getAddress())));

        if (deviceRegistrationMsg.getTs() != null)
            baseAttributeKvEntryList.add(    new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("ts",deviceRegistrationMsg.getTs().toString())));

        if (deviceRegistrationMsg.getLat() != null)
            baseAttributeKvEntryList.add(   new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("latitude", deviceRegistrationMsg.getLat())));

        if (deviceRegistrationMsg.getLon() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("longitude", deviceRegistrationMsg.getLon())));

        if (deviceRegistrationMsg.getNameOfTheEndpoint() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("name",deviceRegistrationMsg.getNameOfTheEndpoint())));

        if (deviceRegistrationMsg.getOrganizationName() != null)
            baseAttributeKvEntryList.add(  new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("organizationName",deviceRegistrationMsg.getOrganizationName())));

        if (deviceRegistrationMsg.getDescription() != null)
            baseAttributeKvEntryList.add( new BaseAttributeKvEntry(System.currentTimeMillis() ,new StringDataEntry("description",deviceRegistrationMsg.getDescription())));

        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,baseAttributeKvEntryList);
        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SHARED_SCOPE,baseAttributeKvEntryList);
    }

    private void deleteDevice(ApkDeviceDeleteMsg msg) {
        Device device = deviceService.findDeviceByTenantIdAndName(msg.getTenantId(), msg.getName());
        if (device != null) {
            deviceService.deleteDevice(msg.getTenantId(), device.getId());
            log.debug("Device {} with ID {} deleted successfully!", device.getName(), device.getId());
        } else {
            log.info("Device {] doesn't exist!");
        }
    }
}