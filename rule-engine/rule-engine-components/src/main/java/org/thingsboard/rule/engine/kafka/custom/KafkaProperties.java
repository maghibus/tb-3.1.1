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
import org.thingsboard.rule.engine.api.TbContext;

import java.util.Properties;

/**
 * @author Federico Serini
 */
@UtilityClass
public class KafkaProperties {

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
        kafkaProperties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, configuration.getPassword());
        kafkaProperties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "SSL");

        kafkaProperties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        kafkaProperties.put(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, "-----BEGIN CERTIFICATE-----\n" +
                "MIIFfzCCA2egAwIBAgIJAPDV2RfFqloFMA0GCSqGSIb3DQEBCwUAMFYxCzAJBgNV\n" +
                "BAYTAlNFMQ8wDQYDVQQIDAZTd2VkZW4xEjAQBgNVBAcMCVN0b2NraG9sbTENMAsG\n" +
                "A1UECgwEU0lDUzETMBEGA1UEAwwKSG9wc1Jvb3RDQTAeFw0yMDAxMTcxNDI5MDda\n" +
                "Fw00MDAxMTIxNDI5MDdaMFYxCzAJBgNVBAYTAlNFMQ8wDQYDVQQIDAZTd2VkZW4x\n" +
                "EjAQBgNVBAcMCVN0b2NraG9sbTENMAsGA1UECgwEU0lDUzETMBEGA1UEAwwKSG9w\n" +
                "c1Jvb3RDQTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAMUNpB3hPH9i\n" +
                "BrTKdNQqoAvbbAHehTsBZ+9uCugHtGca4O1v9+R+nNJgNfct3waVFmmqiOyiZrYo\n" +
                "nhCxn+afhvS0ng9bThRTYOCmwQXLX7154m27rNTWW/jyq6806hpJU2z5Oq9k6rOW\n" +
                "HgVVyQ4SywAEvcg5nHL7W1uV82AMKSAQNm84VQyFB0j3QlG8ANIw7IHfPnMpnKwY\n" +
                "WlCoQBxz6mLFwtxojQyVvBpTaNiD5mM9wrjc7Nv0wlgoWIILp2GBMUK4P1naAm7f\n" +
                "acDJpfXtEcxfn2ipfAidlav5DMvK7cCf4299PkgCyrS+ytlpXaoYssOcMVzxlaa6\n" +
                "a3BMzaJBzAHOkjwEXBwlGJqlpCCEph1k3exJo5Mi5fqjCr2QR+69p+feXS60Uec5\n" +
                "6AxCUwxs7bmkp7pqB/sd2CWBnGzcu7DOhpXTlukR7xMXgmewSTFdI0/tOwEs5/KE\n" +
                "XfajT1zyeZCKmiOfWg1WNZbGbMiVFfzo/VyRFyKJ1PgIGuvWzUL5yPXDOKF276ol\n" +
                "Dq+rHndQcCo80B8r3D2OWzow4h9j6BI64FD6dNVfPZEtuJlc/Y4BYxhS1FnL0JkG\n" +
                "gxpzwxMOjp07eARsEKdIgNT+Rl/PjBtyEsSzw+ZwzCgUOE2bClFEX+T7tIhYXQ+Y\n" +
                "0bE+CYQgb7cdqcfWvlvPzmqZOOqy8DvFAgMBAAGjUDBOMB0GA1UdDgQWBBTz/ieU\n" +
                "huR7S5yvwtPq3blNU16SJzAfBgNVHSMEGDAWgBTz/ieUhuR7S5yvwtPq3blNU16S\n" +
                "JzAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4ICAQBaO3HLMRlSTlsXpLE1\n" +
                "/bGe1YO8UFGD+1Fp5ba8f+kFWj+/VNtEVN2I/1Y1RV/aTicqOIeSYxLNAP8JE05T\n" +
                "Cu2w2Eop+ivZ7dFgUrQmuxckAkfKnZgRo5gZ6wOLqWfaO7QWlmZxhVzuEsBArOGO\n" +
                "hVYKxh7wcTmNfEdLDNICTLQ81qNIyFIDtRnYQiM+UdD2/T+6n4/H//QEM4q3BdBd\n" +
                "QKaXHsj/o8ftcakvvOeAcC/vwQ3MIQOBtbi7BTzRGF/4zxejpnIIzQtpeXC5NP/s\n" +
                "Gbo2HfP1eXqJLycBkNdFeJ5IfnIVT6gj7p0Mr4aCYaOaDRCQXGnYoHYjkqDlmHlQ\n" +
                "6m5HCYjJaUUt/QJ6fL0g3V8UWU4IylFyGjkl2XMuCIY/tgrbmymrdmdCRaxpvWiE\n" +
                "tANUTWG0xQk5h57mNJIDlTcDc2DE04FPu5D58GIC9Lu9kozB1ZOaelrmj/MOOKsd\n" +
                "XwEcgqgdnUtRALcjPVw61BZrFprpi1ESXLEBhAeIny1WatK64IMio/SYp0bqg4/p\n" +
                "0L5HS7sBkr/QovU6EEZuyszMCgNRZhglGKMJlr+DBZ/rUMr3LslniE6VCjMUBxcr\n" +
                "3+dQ7OVhrnrW41Wd8Gdsc/sSGg4gn3K7nSiHUDtgaDZMYRtXHiO88RbRdpRohdXx\n" +
                "NgcqVoIJ8mN3jxAYh0teSwqBKw==\n" +
                "-----END CERTIFICATE-----\n");

        kafkaProperties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
        kafkaProperties.put(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, "-----BEGIN CERTIFICATE-----\n" +
                "MIIEMzCCAhugAwIBAgICEIQwDQYJKoZIhvcNAQELBQAwSjELMAkGA1UEBhMCU0Ux\n" +
                "DzANBgNVBAgMBlN3ZWRlbjENMAsGA1UECgwEU0lDUzEbMBkGA1UEAwwSSG9wc0lu\n" +
                "dGVybWVkaWF0ZUNBMB4XDTIxMTAyMTExMzIwMFoXDTMxMTAxOTExMzIwMFowITEf\n" +
                "MB0GA1UEAwwWdGVzdF95YXJuX3VpX19kZXZ1c2VyMDCBnzANBgkqhkiG9w0BAQEF\n" +
                "AAOBjQAwgYkCgYEAwgXj0tNxiCsU3lg06EIr31aRhlFEYdxFuA/2h27NwvWTNgb6\n" +
                "RXXzi+YWPV1uy7JWdET1W12fu8TfI9XRSRpXO+3nSY7cVoUNqoijbhGYW+3yvgKj\n" +
                "01oI/cBkNtO1nOQIYRAw2DbF589jT27HkK1Z3GEOj0s+vOQyAv55cFoj7UkCAwEA\n" +
                "AaOBzzCBzDAJBgNVHRMEAjAAMBEGCWCGSAGG+EIBAQQEAwIF4DAzBglghkgBhvhC\n" +
                "AQ0EJhYkT3BlblNTTCBHZW5lcmF0ZWQgQ2xpZW50IENlcnRpZmljYXRlMB0GA1Ud\n" +
                "DgQWBBTDIjnHOt0c/fpdeGwKZOUAiM1pWDAfBgNVHSMEGDAWgBRGX3KybrqKAtxy\n" +
                "Gl8KFdqpYgppdjAOBgNVHQ8BAf8EBAMCBeAwJwYDVR0lBCAwHgYIKwYBBQUHAwIG\n" +
                "CCsGAQUFBwMEBggrBgEFBQcDATANBgkqhkiG9w0BAQsFAAOCAgEAUn/l5bVsUJWQ\n" +
                "2Cct2aZbkCylZi9upYOdPtzGxwoEwHQywNsxbhzOHI2ycVuiW0N+O9rmJV8YQVZe\n" +
                "qHHIraU+mAG69PNU0aeUVaOVQfG1h1Whc23q9vbYQv8Lkxh2Wa1oelewa3fMOCok\n" +
                "dtzxJBF1GNDTa2+sN65rtAjTsS20qtwYkHpsdbz4ADROaWMJhwq7s292zSZVwpZn\n" +
                "9ArViUXt9UjjePT5KmMUnW5IxF5ufIWvmn1YnM1w7VHTcGFLLepND99ZDjuw1nvn\n" +
                "u9pMjpTP5JQsL54mz98JspTr9UxN0naENjIJgjxkATAXttQxWkC1T2OCAWD3WY7D\n" +
                "yyrAyWT3/m7QDtiMotI2GngO0IlvdZ2htUyAe41Y9kI6EwhpNdm2XJBgnPXK8jDS\n" +
                "R1ccNe3lxnFC5by1fi/FM9r45E89+Cyyth7NAUK/uXHiLIugGSsFYcnRIgEjKkL2\n" +
                "y4WPdblswfgL0ETXmCNGGko9osYU4nmgmhPTw8KTTOJnPuUHZJAkK3ROEaeBRswU\n" +
                "3WFRlJkl/T96zuqknlRmqIKB6Q9jsvl5hbQkGmLgkk++HCmyfh7lB6caNgI0cCEt\n" +
                "lnjbSqEv1hnjJnA7z3ZuNcm47wht9i/+VbNxQtIgBQTRCOrMbHJ9eG8si6upT0ti\n" +
                "UaQ2RGaSC0yFu/0PLZ6lxZL5FXt+F2A=\n" +
                "-----END CERTIFICATE-----\n");
        kafkaProperties.put(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, "-----BEGIN PRIVATE KEY-----\n" +
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMIF49LTcYgrFN5Y\n" +
                "NOhCK99WkYZRRGHcRbgP9oduzcL1kzYG+kV184vmFj1dbsuyVnRE9Vtdn7vE3yPV\n" +
                "0UkaVzvt50mO3FaFDaqIo24RmFvt8r4Co9NaCP3AZDbTtZzkCGEQMNg2xefPY09u\n" +
                "x5CtWdxhDo9LPrzkMgL+eXBaI+1JAgMBAAECgYAYnXuuYyH7tw+CN+mab862GnsC\n" +
                "8et9iN8Vf3z06LNVISfIinEU/+ZioNHAfkqQsDL0tEz2hvW9cjgnCTjwj9VyLc2A\n" +
                "daAiy1gMBtPr35pCRZtcFj9Hk+Q40bUg1b/GjgLwXdvNhhjvhYe9wUE0I1o6wH8v\n" +
                "ozSxFcR/uHCcR+5vCQJBAO2f1GooiKxvdKk63rZHVt3tqFzFgyMxPkj/UQ3gcGe9\n" +
                "i2sSa1z2LcrqU/T30NyAT0V4c59ygILFZwMsUJ88wBUCQQDRBuYu881+Id7lXimM\n" +
                "2lRtRwemmJiAvnKiFePiDEccEzeIBHkjkbKFcSsbgRnAHqe72RoiiweriD5Kc3sg\n" +
                "r9FlAkA9InrG2SjYnFA6XdAAu3fo3wUK4THs8vVgWHelB1JEDmr5ICMJJrj/VKxH\n" +
                "AR72K/i5GdU9d3sCqgDnuxX+8eVFAkEAj1CRpgOXEzIZ512GU4KmfD5FKp5ZgXwS\n" +
                "kLs9gZE1eDRc0K9wd0jAA+oxyivcAfOCwbL6zlBQs/U38Ef5VdH2WQJBAOO2Q27K\n" +
                "Iy/NnrX2t6HLPltXMn+kjMCgBxNkT08kpWG0xHB54JrfMpFsYr6ke6zlGwprilld\n" +
                "T8z4rMKhmm3+wr0=\n" +
                "-----END PRIVATE KEY-----\n");

        return kafkaProperties;
    }

}
