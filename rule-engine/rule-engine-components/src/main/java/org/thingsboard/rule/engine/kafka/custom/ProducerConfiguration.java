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
