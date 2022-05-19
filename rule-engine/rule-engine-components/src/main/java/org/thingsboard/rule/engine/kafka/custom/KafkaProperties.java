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
        return kafkaProperties;
    }

}
