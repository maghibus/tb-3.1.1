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
package org.thingsboard.rule.engine.kafka.custom;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.thingsboard.rule.engine.api.TbContext;

import java.util.Base64;
import java.util.Properties;

/**
 * @author Federico Serini
 */
@UtilityClass
public class KafkaProperties {

    private static final String SSL_KEYSTORE_BYTEARRAY=  "ssl.keystore.bytearray" ;
    private static final String SSL_TRUSTSTORE_BYTEARRAY=  "ssl.truststore.bytearray" ;
    private static final String SSL_ENGINE_FACTORY_CLASS = "ssl.engine.factory.class";

    public static Properties getKafkaPropertiesFromNodeConfiguration(TbContext ctx, TbKafkaNodeSSLConfiguration configuration) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-tb-kafka-node-" + ctx.getSelfId().getId().toString() + "-" + ctx.getServiceId());
        kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, configuration.getValueSerializer());
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, configuration.getKeySerializer());
        kafkaProperties.put(ProducerConfig.ACKS_CONFIG, configuration.getAcks());
        kafkaProperties.put(ProducerConfig.RETRIES_CONFIG, configuration.getRetries());
        kafkaProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, configuration.getBatchSize());
        kafkaProperties.put(ProducerConfig.LINGER_MS_CONFIG, configuration.getLinger());
        kafkaProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, configuration.getBufferMemory());
        kafkaProperties.put(SSL_ENGINE_FACTORY_CLASS, CustomSslEngineFactory.class);
        kafkaProperties.put(SSL_KEYSTORE_BYTEARRAY, Base64.getDecoder().decode(configuration.getKeystore()));
        kafkaProperties.put(SSL_TRUSTSTORE_BYTEARRAY, Base64.getDecoder().decode(configuration.getTruststore()));
        kafkaProperties.put("ssl.truststore.password", configuration.getPassword());
        kafkaProperties.put("ssl.keystore.password", configuration.getPassword());
        kafkaProperties.put("ssl.key.password", configuration.getPassword());

        return kafkaProperties;
    }

}
