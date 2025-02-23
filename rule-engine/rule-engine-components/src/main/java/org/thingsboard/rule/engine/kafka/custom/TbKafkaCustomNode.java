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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.thingsboard.common.util.ListeningExecutor;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.kafka.TbKafkaNodeConfiguration;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "Kafka with SSL",
        configClazz = TbKafkaNodeConfiguration.class,
        nodeDescription = "Publish messages to Kafka server with SSL",
        nodeDetails = "Will send record via Kafka producer to Kafka server. " +
                "Outbound message will contain response fields (<code>offset</code>, <code>partition</code>, <code>topic</code>)" +
                " from the Kafka in the Message Metadata. For example <b>partition</b> field can be accessed with <code>metadata.partition</code>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbActionNodeKafkaSslConfig",
        iconUrl = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUzOCIgaGVpZ2h0PSIyNTAwIiB2aWV3Qm94PSIwIDAgMjU2IDQxNiIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiBwcmVzZXJ2ZUFzcGVjdFJhdGlvPSJ4TWlkWU1pZCI+PHBhdGggZD0iTTIwMS44MTYgMjMwLjIxNmMtMTYuMTg2IDAtMzAuNjk3IDcuMTcxLTQwLjYzNCAxOC40NjFsLTI1LjQ2My0xOC4wMjZjMi43MDMtNy40NDIgNC4yNTUtMTUuNDMzIDQuMjU1LTIzLjc5NyAwLTguMjE5LTEuNDk4LTE2LjA3Ni00LjExMi0yMy40MDhsMjUuNDA2LTE3LjgzNWM5LjkzNiAxMS4yMzMgMjQuNDA5IDE4LjM2NSA0MC41NDggMTguMzY1IDI5Ljg3NSAwIDU0LjE4NC0yNC4zMDUgNTQuMTg0LTU0LjE4NCAwLTI5Ljg3OS0yNC4zMDktNTQuMTg0LTU0LjE4NC01NC4xODQtMjkuODc1IDAtNTQuMTg0IDI0LjMwNS01NC4xODQgNTQuMTg0IDAgNS4zNDguODA4IDEwLjUwNSAyLjI1OCAxNS4zODlsLTI1LjQyMyAxNy44NDRjLTEwLjYyLTEzLjE3NS0yNS45MTEtMjIuMzc0LTQzLjMzMy0yNS4xODJ2LTMwLjY0YzI0LjU0NC01LjE1NSA0My4wMzctMjYuOTYyIDQzLjAzNy01My4wMTlDMTI0LjE3MSAyNC4zMDUgOTkuODYyIDAgNjkuOTg3IDAgNDAuMTEyIDAgMTUuODAzIDI0LjMwNSAxNS44MDMgNTQuMTg0YzAgMjUuNzA4IDE4LjAxNCA0Ny4yNDYgNDIuMDY3IDUyLjc2OXYzMS4wMzhDMjUuMDQ0IDE0My43NTMgMCAxNzIuNDAxIDAgMjA2Ljg1NGMwIDM0LjYyMSAyNS4yOTIgNjMuMzc0IDU4LjM1NSA2OC45NHYzMi43NzRjLTI0LjI5OSA1LjM0MS00Mi41NTIgMjcuMDExLTQyLjU1MiA1Mi44OTQgMCAyOS44NzkgMjQuMzA5IDU0LjE4NCA1NC4xODQgNTQuMTg0IDI5Ljg3NSAwIDU0LjE4NC0yNC4zMDUgNTQuMTg0LTU0LjE4NCAwLTI1Ljg4My0xOC4yNTMtNDcuNTUzLTQyLjU1Mi01Mi44OTR2LTMyLjc3NWE2OS45NjUgNjkuOTY1IDAgMCAwIDQyLjYtMjQuNzc2bDI1LjYzMyAxOC4xNDNjLTEuNDIzIDQuODQtMi4yMiA5Ljk0Ni0yLjIyIDE1LjI0IDAgMjkuODc5IDI0LjMwOSA1NC4xODQgNTQuMTg0IDU0LjE4NCAyOS44NzUgMCA1NC4xODQtMjQuMzA1IDU0LjE4NC01NC4xODQgMC0yOS44NzktMjQuMzA5LTU0LjE4NC01NC4xODQtNTQuMTg0em0wLTEyNi42OTVjMTQuNDg3IDAgMjYuMjcgMTEuNzg4IDI2LjI3IDI2LjI3MXMtMTEuNzgzIDI2LjI3LTI2LjI3IDI2LjI3LTI2LjI3LTExLjc4Ny0yNi4yNy0yNi4yN2MwLTE0LjQ4MyAxMS43ODMtMjYuMjcxIDI2LjI3LTI2LjI3MXptLTE1OC4xLTQ5LjMzN2MwLTE0LjQ4MyAxMS43ODQtMjYuMjcgMjYuMjcxLTI2LjI3czI2LjI3IDExLjc4NyAyNi4yNyAyNi4yN2MwIDE0LjQ4My0xMS43ODMgMjYuMjctMjYuMjcgMjYuMjdzLTI2LjI3MS0xMS43ODctMjYuMjcxLTI2LjI3em01Mi41NDEgMzA3LjI3OGMwIDE0LjQ4My0xMS43ODMgMjYuMjctMjYuMjcgMjYuMjdzLTI2LjI3MS0xMS43ODctMjYuMjcxLTI2LjI3YzAtMTQuNDgzIDExLjc4NC0yNi4yNyAyNi4yNzEtMjYuMjdzMjYuMjcgMTEuNzg3IDI2LjI3IDI2LjI3em0tMjYuMjcyLTExNy45N2MtMjAuMjA1IDAtMzYuNjQyLTE2LjQzNC0zNi42NDItMzYuNjM4IDAtMjAuMjA1IDE2LjQzNy0zNi42NDIgMzYuNjQyLTM2LjY0MiAyMC4yMDQgMCAzNi42NDEgMTYuNDM3IDM2LjY0MSAzNi42NDIgMCAyMC4yMDQtMTYuNDM3IDM2LjYzOC0zNi42NDEgMzYuNjM4em0xMzEuODMxIDY3LjE3OWMtMTQuNDg3IDAtMjYuMjctMTEuNzg4LTI2LjI3LTI2LjI3MXMxMS43ODMtMjYuMjcgMjYuMjctMjYuMjcgMjYuMjcgMTEuNzg3IDI2LjI3IDI2LjI3YzAgMTQuNDgzLTExLjc4MyAyNi4yNzEtMjYuMjcgMjYuMjcxeiIvPjwvc3ZnPg=="
)
@RequiredArgsConstructor
public class TbKafkaCustomNode implements TbNode {

    private static final String OFFSET = "offset";
    private static final String PARTITION = "partition";
    private static final String TOPIC = "topic";
    private static final String ERROR = "error";
    public static final String TB_MSG_MD_PREFIX = "tb_msg_md_";


    private TbKafkaNodeSSLConfiguration config;
    private boolean addMetadataKeyValuesAsKafkaHeaders;
    private Charset toBytesCharset;

    private Producer<?, String> producer;

    Properties kafkaProperties = new Properties();

    
    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {

		log.info("InitSettings Layer ");
    	this.config = TbNodeUtils.convert(configuration, TbKafkaNodeSSLConfiguration.class);
        kafkaProperties = KafkaProperties.getKafkaPropertiesFromNodeConfiguration(ctx, config);
        addMetadataKeyValuesAsKafkaHeaders = BooleanUtils.toBooleanDefaultIfNull(config.isAddMetadataKeyValuesAsKafkaHeaders(), false);
        toBytesCharset = config.getKafkaHeadersCharset() != null ? Charset.forName(config.getKafkaHeadersCharset()) : StandardCharsets.UTF_8;

		try {
        	this.producer =  new KafkaProducer<>(kafkaProperties);
        }
        catch(Exception e) {
        	throw new TbNodeException(e);
        }
        

    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        String topic = TbNodeUtils.processPattern(config.getTopicPattern(), msg.getMetaData());

        try {
        	ListeningExecutor service = ctx.getExternalCallExecutor();
			service.executeAsync(() -> {
        		publish(ctx, msg, topic);
        		return null;
            });
        } catch (Exception e) {
        	ctx.tellFailure(msg, e);
		}
        

    }

    protected void publish(TbContext ctx, TbMsg msg, String topic) {
    	try {
            if (!addMetadataKeyValuesAsKafkaHeaders) {
                producer.send(new ProducerRecord<>(topic, msg.getData()),
                        (metadata, e) -> processRecord(ctx, msg, metadata, e));
            } else {
                Headers headers = new RecordHeaders();
                msg.getMetaData().values().forEach((key, value) -> headers.add(new RecordHeader(TB_MSG_MD_PREFIX + key, value.getBytes(toBytesCharset))));
                producer.send(new ProducerRecord<>(topic, null, null, null, msg.getData(), headers),
                        (metadata, e) -> processRecord(ctx, msg, metadata, e));
            }
        } catch (Exception e) {
            log.info("[{}] Failed to process message: {}", ctx.getSelfId(), msg, e);
        }
	}

    @Override
    public void destroy() {
        if (this.producer != null) {
            try {
                this.producer.close();
            } catch (Exception e) {
                log.error("Failed to close producer during destroy()", e);
            }
        }
    }

    private void processRecord(TbContext ctx, TbMsg msg, RecordMetadata metadata, Exception e) {
        if (metadata != null) {
            TbMsg next = processResponse(ctx, msg, metadata);
            ctx.tellNext(next, TbRelationTypes.SUCCESS);
        } else {
            TbMsg next = processException(ctx, msg, e);
            ctx.tellFailure(next, e);
        }
    }

    private TbMsg processResponse(TbContext ctx, TbMsg origMsg, RecordMetadata recordMetadata) {
        TbMsgMetaData metaData = origMsg.getMetaData().copy();
        metaData.putValue(OFFSET, String.valueOf(recordMetadata.offset()));
        metaData.putValue(PARTITION, String.valueOf(recordMetadata.partition()));
        metaData.putValue(TOPIC, recordMetadata.topic());
        return ctx.transformMsg(origMsg, origMsg.getType(), origMsg.getOriginator(), metaData, origMsg.getData());
    }

    private TbMsg processException(TbContext ctx, TbMsg origMsg, Exception e) {
        TbMsgMetaData metaData = origMsg.getMetaData().copy();
        metaData.putValue(ERROR, e.getClass() + ": " + e.getMessage());
        return ctx.transformMsg(origMsg, origMsg.getType(), origMsg.getOriginator(), metaData, origMsg.getData());
    }
}