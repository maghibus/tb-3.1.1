package org.thingsboard.rule.engine.kafka.custom;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.stereotype.Component;
import org.thingsboard.rule.engine.api.TbContext;

/**
 * @author Federico Serini
 */
@Slf4j
@Component
public class ProducerConfiguration {

    protected Producer<?,String> configureProducer(TbContext ctx, TbKafkaNodeSSLConfiguration configuration) throws Exception {

        Producer<?, String> kafkaProducer;

        // Creazione producer
        try {
            kafkaProducer = new KafkaProducer<>(KafkaProperties.getKafkaPropertiesFromNodeConfiguration(ctx, configuration));
        } catch (Exception e) {
            throw new Exception(e);
        }

        return kafkaProducer;
    }
}
