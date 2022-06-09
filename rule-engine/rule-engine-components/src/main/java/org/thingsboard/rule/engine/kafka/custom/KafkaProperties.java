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
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.thingsboard.rule.engine.api.TbContext;

import java.util.Properties;

/**
 * @author Federico Serini
 */
@UtilityClass
public class KafkaProperties {

    public static Properties getKafkaPropertiesFromNodeConfiguration(TbContext ctx, TbKafkaNodeSSLConfiguration configuration) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-tb-kafka-node-" + ctx.getSelfId().getId().toString() + "-" + ctx.getServiceId());
        kafkaProperties.put(ProducerConfig.ACKS_CONFIG, configuration.getAcks());
        kafkaProperties.put(ProducerConfig.RETRIES_CONFIG, configuration.getRetries());
        kafkaProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, configuration.getBatchSize());
        kafkaProperties.put(ProducerConfig.LINGER_MS_CONFIG, configuration.getLinger());
        kafkaProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, configuration.getBufferMemory());
        kafkaProperties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "SSL");
        kafkaProperties.put("ssl.endpoint.identification.algorithm","");
        kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProperties.put("security.protocol", "SSL");
        kafkaProperties.put("ssl.truststore.type", "PEM");
        kafkaProperties.put("ssl.keystore.type", "PEM");


        String password = configuration.getKeystorePemKey().replace(" ","\n");
        String keystore = configuration.getKeystore().replace(" ","\n");
        String truststore = configuration.getTruststore().replace(" ","\n");

        kafkaProperties.put("ssl.truststore.certificates",truststore);
        kafkaProperties.put("ssl.keystore.certificate.chain",keystore);
        kafkaProperties.put("ssl.keystore.key",password);

        return kafkaProperties;
    }

}
