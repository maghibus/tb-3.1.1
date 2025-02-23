import { TranslateService } from '@ngx-translate/core';

export default function addRuleNodeCoreLocaleEnglish(translate: TranslateService) {

    const enUS = {
      tb: {
        rulenode: {
          'create-entity-if-not-exists': 'Create new entity if not exists',
          'create-entity-if-not-exists-hint': 'Create a new entity set above if it does not exist.',
          'entity-name-pattern': 'Name pattern',
          'entity-name-pattern-required': 'Name pattern is required',
          'entity-type-pattern': 'Type pattern',
          'entity-type-pattern-required': 'Type pattern is required',
          'entity-cache-expiration': 'Entities cache expiration time (sec)',
          'entity-cache-expiration-hint':
            'Specifies maximum time interval allowed to store found entity records. 0 value means that records will never expire.',
          'entity-cache-expiration-required': 'Entities cache expiration time is required.',
          'entity-cache-expiration-range': 'Entities cache expiration time should be greater than or equal to 0.',
          'customer-name-pattern': 'Customer name pattern',
          'customer-name-pattern-required': 'Customer name pattern is required',
          'create-customer-if-not-exists': 'Create new customer if not exists',
          'customer-cache-expiration': 'Customers cache expiration time (sec)',
          'customer-cache-expiration-hint':
            'Specifies maximum time interval allowed to store found customer records. 0 value means that records will never expire.',
          'customer-cache-expiration-required': 'Customers cache expiration time is required.',
          'customer-cache-expiration-range': 'Customers cache expiration time should be greater than or equal to 0.',
          'start-interval': 'Start Interval',
          'end-interval': 'End Interval',
          'start-interval-time-unit': 'Start Interval Time Unit',
          'end-interval-time-unit': 'End Interval Time Unit',
          'fetch-mode': 'Fetch mode',
          'fetch-mode-hint': 'If selected fetch mode \'ALL\'  you able to choose telemetry sampling order.',
          'order-by': 'Order by',
          'order-by-hint': 'Select to choose telemetry sampling order.',
          limit: 'Limit',
          'limit-hint': 'Min limit value is 2, max - 1000. In case you want to fetch a single entry, ' +
            'select fetch mode \'FIRST\' or \'LAST\'.',
          'time-unit-milliseconds': 'Milliseconds',
          'time-unit-seconds': 'Seconds',
          'time-unit-minutes': 'Minutes',
          'time-unit-hours': 'Hours',
          'time-unit-days': 'Days',
          'time-value-range': 'Time value should be in a range from 1 to 2147483647.',
          'start-interval-value-required': 'Start interval value is required.',
          'end-interval-value-required': 'End interval value is required.',
          ssl: 'SSL',
          'ks-ts-password': 'The password for the keystore and truststore, only one allowed',
          'keystore': 'The Java Keystore in Base64 format',
          'truststore': 'The Java Trustore in Base64 format',
          filter: 'Filter',
          switch: 'Switch',
          'message-type': 'Message type',
          'message-type-required': 'Message type is required.',
          'message-types-filter': 'Message types filter',
          'no-message-types-found': 'No message types found',
          'no-message-type-matching': '\'{{messageType}}\' not found.',
          'create-new-message-type': 'Create a new one!',
          'message-types-required': 'Message types are required.',
          'client-attributes': 'Client attributes',
          'shared-attributes': 'Shared attributes',
          'server-attributes': 'Server attributes',
          'notify-device': 'Notify Device',
          'notify-device-hint': 'If the message arrives from the device, we will push it back to the device by default.',
          'latest-timeseries': 'Latest timeseries',
          'timeseries-key': 'Timeseries key',
          'data-keys': 'Message data',
          'metadata-keys': 'Message metadata',
          'relations-query': 'Relations query',
          'device-relations-query': 'Device relations query',
          'max-relation-level': 'Max relation level',
          'relation-type-pattern': 'Relation type pattern',
          'relation-type-pattern-required': 'Relation type pattern is required',
          'relation-types-list': 'Relation types to propagate',
          'relation-types-list-hint': 'If Propagate relation types are not selected, ' +
            'alarms will be propagated without filtering by relation type.',
          'unlimited-level': 'Unlimited level',
          'latest-telemetry': 'Latest telemetry',
          'attr-mapping': 'Attributes mapping',
          'source-attribute': 'Source attribute',
          'source-attribute-required': 'Source attribute is required.',
          'source-telemetry': 'Source telemetry',
          'source-telemetry-required': 'Source telemetry is required.',
          'target-attribute': 'Target attribute',
          'target-attribute-required': 'Target attribute is required.',
          'attr-mapping-required': 'At least one attribute mapping should be specified.',
          'fields-mapping': 'Fields mapping',
          'fields-mapping-required': 'At least one field mapping should be specified.',
          'source-field': 'Source field',
          'source-field-required': 'Source field is required.',
          'originator-source': 'Originator source',
          'originator-customer': 'Customer',
          'originator-tenant': 'Tenant',
          'originator-related': 'Related',
          'originator-alarm-originator': 'Alarm Originator',
          'clone-message': 'Clone message',
          transform: 'Transform',
          'default-ttl': 'Default TTL in seconds',
          'default-ttl-required': 'Default TTL is required.',
          'min-default-ttl-message': 'Only 0 minimum TTL is allowed.',
          'message-count': 'Message count (0 - unlimited)',
          'message-count-required': 'Message count is required.',
          'min-message-count-message': 'Only 0 minimum message count is allowed.',
          'period-seconds': 'Period in seconds',
          'period-seconds-required': 'Period is required.',
          'use-metadata-period-in-seconds-patterns': 'Use period in seconds pattern',
          'use-metadata-period-in-seconds-patterns-hint':
            'If selected, rule node use period in seconds interval pattern from message metadata or data ' +
            'assuming that intervals are in the seconds.',
          'period-in-seconds-pattern': 'Period in seconds pattern',
          'period-in-seconds-pattern-required': 'Period in seconds pattern is required',
          'min-period-seconds-message': 'Only 1 second minimum period is allowed.',
          originator: 'Originator',
          'message-body': 'Message body',
          'message-metadata': 'Message metadata',
          generate: 'Generate',
          'test-generator-function': 'Test generator function',
          generator: 'Generator',
          'test-filter-function': 'Test filter function',
          'test-switch-function': 'Test switch function',
          'test-transformer-function': 'Test transformer function',
          transformer: 'Transformer',
          'alarm-create-condition': 'Alarm create condition',
          'test-condition-function': 'Test condition function',
          'alarm-clear-condition': 'Alarm clear condition',
          'alarm-details-builder': 'Alarm details builder',
          'test-details-function': 'Test details function',
          'alarm-type': 'Alarm type',
          'alarm-type-required': 'Alarm type is required.',
          'alarm-severity': 'Alarm severity',
          'alarm-severity-required': 'Alarm severity is required',
          'alarm-severity-pattern': 'Alarm severity pattern',
          'alarm-status-filter': 'Alarm status filter',
          'alarm-status-list-empty': 'Alarm status list is empty',
          'no-alarm-status-matching': 'No alarm status matching were found.',
          propagate: 'Propagate alarm to related entities',
          'propagate-to-owner': 'Propagate alarm to entity owner (Customer or Tenant)',
          'propagate-to-tenant': 'Propagate alarm to Tenant',
          condition: 'Condition',
          details: 'Details',
          'to-string': 'To string',
          'test-to-string-function': 'Test to string function',
          'from-template': 'From Template',
          'from-template-required': 'From Template is required',
          'to-template': 'To Template',
          'to-template-required': 'To Template is required',
          'mail-address-list-template-hint':
            'Comma separated address list, use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">' +
            '&#125;</span></code> for value from metadata, <code><span style="color: #000;">$[</span>messageKey' +
            '<span style="color: #000;">]</span></code> for value from message body',
          'cc-template': 'Cc Template',
          'bcc-template': 'Bcc Template',
          'subject-template': 'Subject Template',
          'subject-template-required': 'Subject Template is required',
          'body-template': 'Body Template',
          'body-template-required': 'Body Template is required',
          'dynamic-mail-body-type': 'Dynamic mail body type',
          'mail-body-type': 'Mail body type',
          'request-id-metadata-attribute': 'Request Id Metadata attribute name',
          'timeout-sec': 'Timeout in seconds',
          'timeout-required': 'Timeout is required',
          'min-timeout-message': 'Only 0 minimum timeout value is allowed.',
          'endpoint-url-pattern': 'Endpoint URL pattern',
          'endpoint-url-pattern-required': 'Endpoint URL pattern is required',
          'request-method': 'Request method',
          'use-simple-client-http-factory': 'Use simple client HTTP factory',
          'ignore-request-body': 'Without request body',
          'read-timeout': 'Read timeout in millis',
          'read-timeout-hint': 'The value of 0 means an infinite timeout',
          'max-parallel-requests-count': 'Max number of parallel requests',
          'max-parallel-requests-count-hint': 'The value of 0 specifies no limit in parallel processing',
          headers: 'Headers',
          'headers-hint': 'Use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">&#125;</span></code> ' +
            'for value from metadata, <code><span style="color: #000;">$[</span>messageKey<span style="color: #000;">]</span></code> ' +
            'for value from message body in header/value fields',
          header: 'Header',
          'header-required': 'Header is required',
          value: 'Value',
          'value-required': 'Value is required',
          'topic-pattern': 'Topic pattern',
          'topic-pattern-required': 'Topic pattern is required',
          topic: 'Topic',
          'topic-required': 'Topic is required',
          'bootstrap-servers': 'Bootstrap servers',
          'bootstrap-servers-required': 'Bootstrap servers value is required',
          'other-properties': 'Other properties',
          key: 'Key',
          'key-required': 'Key is required',
          retries: 'Automatically retry times if fails',
          'min-retries-message': 'Only 0 minimum retries is allowed.',
          'batch-size-bytes': 'Produces batch size in bytes',
          'min-batch-size-bytes-message': 'Only 0 minimum batch size is allowed.',
          'linger-ms': 'Time to buffer locally (ms)',
          'min-linger-ms-message': 'Only 0 ms minimum value is allowed.',
          'buffer-memory-bytes': 'Client buffer max size in bytes',
          'min-buffer-memory-message': 'Only 0 minimum buffer size is allowed.',
          acks: 'Number of acknowledgments',
          'key-serializer': 'Key serializer',
          'key-serializer-required': 'Key serializer is required',
          'value-serializer': 'Value serializer',
          'value-serializer-required': 'Value serializer is required',
          'topic-arn-pattern': 'Topic ARN pattern',
          'topic-arn-pattern-required': 'Topic ARN pattern is required',
          'aws-access-key-id': 'AWS Access Key ID',
          'aws-access-key-id-required': 'AWS Access Key ID is required',
          'aws-secret-access-key': 'AWS Secret Access Key',
          'aws-secret-access-key-required': 'AWS Secret Access Key is required',
          'aws-region': 'AWS Region',
          'aws-region-required': 'AWS Region is required',
          'exchange-name-pattern': 'Exchange name pattern',
          'routing-key-pattern': 'Routing key pattern',
          'message-properties': 'Message properties',
          host: 'Host',
          'host-required': 'Host is required',
          port: 'Port',
          'port-required': 'Port is required',
          'port-range': 'Port should be in a range from 1 to 65535.',
          'virtual-host': 'Virtual host',
          username: 'Username',
          password: 'Password',
          'automatic-recovery': 'Automatic recovery',
          'connection-timeout-ms': 'Connection timeout (ms)',
          'min-connection-timeout-ms-message': 'Only 0 ms minimum value is allowed.',
          'handshake-timeout-ms': 'Handshake timeout (ms)',
          'min-handshake-timeout-ms-message': 'Only 0 ms minimum value is allowed.',
          'client-properties': 'Client properties',
          'queue-url-pattern': 'Queue URL pattern',
          'queue-url-pattern-required': 'Queue URL pattern is required',
          'delay-seconds': 'Delay (seconds)',
          'min-delay-seconds-message': 'Only 0 seconds minimum value is allowed.',
          'max-delay-seconds-message': 'Only 900 seconds maximum value is allowed.',
          name: 'Name',
          'name-required': 'Name is required',
          'queue-type': 'Queue type',
          'sqs-queue-standard': 'Standard',
          'sqs-queue-fifo': 'FIFO',
          'gcp-project-id': 'GCP project ID',
          'gcp-project-id-required': 'GCP project ID is required',
          'gcp-service-account-key': 'GCP service account key file',
          'gcp-service-account-key-required': 'GCP service account key file is required',
          'pubsub-topic-name': 'Topic name',
          'pubsub-topic-name-required': 'Topic name is required',
          'message-attributes': 'Message attributes',
          'message-attributes-hint': 'Use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">&#125;</span></code> ' +
                        'for value from metadata, <code><span style="color: #000;">$[</span>messageKey<span style="color: #000;">]</span></code> ' +
                        'for value from message body in name/value fields',
          'connect-timeout': 'Connection timeout (sec)',
          'connect-timeout-required': 'Connection timeout is required.',
          'connect-timeout-range': 'Connection timeout should be in a range from 1 to 200.',
          'client-id': 'Client ID',
          'client-id-hint': 'Hint: Optional. Leave empty for auto-generated Client ID. Be careful when specifying the Client ID. ' +
              'Majority of the MQTT brokers will not allow multiple connections with the same Client ID. ' +
              'To connect to such brokers, your mqtt Client ID must be unique. ' +
              'When platform is running in a micro-services mode, the copy of rule node is launched in each micro-service. ' +
              'This will automatically lead to multiple mqtt clients with the same ID and may cause failures of the rule node. ' +
              'To avoid such failures enable "Add Service ID as suffix to Client ID" option below.',
          'append-client-id-suffix': 'Add Service ID as suffix to Client ID',
          'client-id-suffix-hint': 'Hint: Optional. Applied when "Client ID" specified explicitly. ' +
            'If selected then Service ID will be added to Client ID as a suffix. ' +
            'Helps to avoid failures when platform is running in a micro-services mode.',
          'device-id':'Device ID',
          'device-id-required':'Device ID is required.',
          'clean-session': 'Clean session',
          'enable-ssl': 'Enable SSL',
          credentials: 'Credentials',
          'credentials-type': 'Credentials type',
          'credentials-type-required': 'Credentials type is required.',
          'credentials-anonymous': 'Anonymous',
          'credentials-basic': 'Basic',
          'credentials-pem': 'PEM',
          'credentials-pem-hint': 'At least Server CA certificate file or a pair of Client certificate and Client private key files are required',
          'credentials-sas': 'Shared Access Signature',
          'sas-key': 'SAS Key',
          'sas-key-required': 'SAS Key is required.',
          hostname: 'Hostname',
          'hostname-required': 'Hostname is required.',
          'azure-ca-cert': 'CA certificate file',
          'username-required': 'Username is required.',
          'password-required': 'Password is required.',
          'ca-cert': 'Server CA certificate file *',
          'private-key': 'Client private key file *',
          cert: 'Client certificate file *',
          'no-file': 'No file selected.',
          'drop-file': 'Drop a file or click to select a file to upload.',
          'private-key-password': 'Private key password',
          'use-system-smtp-settings': 'Use system SMTP settings',
          'use-metadata-interval-patterns': 'Use interval patterns',
          'use-metadata-interval-patterns-hint':
            'If selected, rule node use start and end interval patterns from message metadata or data ' +
            'assuming that intervals are in the milliseconds.',
          'use-message-alarm-data': 'Use message alarm data',
          'overwrite-alarm-details': 'Overwrite alarm details',
          'use-alarm-severity-pattern': 'Use alarm severity pattern',
          'check-all-keys': 'Check that all selected keys are present',
          'check-all-keys-hint': 'If selected, checks that all specified keys are present in the message data and metadata.',
          'check-relation-to-specific-entity': 'Check relation to specific entity',
          'check-relation-hint': 'Checks existence of relation to specific entity or to any entity based on direction and relation type.',
          'delete-relation-to-specific-entity': 'Delete relation to specific entity',
          'delete-relation-hint':
            'Deletes relation from the originator of the incoming message to the specified ' +
            'entity or list of entities based on direction and type.',
          'remove-current-relations': 'Remove current relations',
          'remove-current-relations-hint':
            'Removes current relations from the originator of the incoming message based on direction and type.',
          'change-originator-to-related-entity': 'Change originator to related entity',
          'change-originator-to-related-entity-hint': 'Used to process submitted message as a message from another entity.',
          'start-interval-pattern': 'Start interval pattern',
          'end-interval-pattern': 'End interval pattern',
          'start-interval-pattern-required': 'Start interval pattern is required',
          'end-interval-pattern-required': 'End interval pattern is required',
          'smtp-protocol': 'Protocol',
          'smtp-host': 'SMTP host',
          'smtp-host-required': 'SMTP host is required.',
          'smtp-port': 'SMTP port',
          'smtp-port-required': 'You must supply a smtp port.',
          'smtp-port-range': 'SMTP port should be in a range from 1 to 65535.',
          'timeout-msec': 'Timeout ms',
          'min-timeout-msec-message': 'Only 0 ms minimum value is allowed.',
          'enter-username': 'Enter username',
          'enter-password': 'Enter password',
          'enable-tls': 'Enable TLS',
          'tls-version': 'TLS version',
          'enable-proxy': 'Enable proxy',
          'use-system-proxy-properties': 'Use system proxy properties',
          'proxy-host': 'Proxy host',
          'proxy-host-required': 'Proxy host is required.',
          'proxy-port': 'Proxy port',
          'proxy-port-required': 'Proxy port is required.',
          'proxy-port-range': 'Proxy port should be in a range from 1 to 65535.',
          'proxy-user': 'Proxy user',
          'proxy-password': 'Proxy password',
          'proxy-scheme': 'Proxy scheme',
          'numbers-to-template': 'Phone Numbers To Template',
          'numbers-to-template-required': 'Phone Numbers To Template is required',
          'numbers-to-template-hint': 'Comma separated Phone Numbers, use <code><span style="color: #000;">$&#123;</span>' +
            'metadataKey<span style="color: #000;">&#125;</span></code> for value from metadata, <code><span style="color: #000;">' +
            '$[</span>messageKey<span style="color: #000;">]</span></code> for value from message body',
          'sms-message-template': 'SMS message Template',
          'sms-message-template-required': 'SMS message Template is required',
          'use-system-sms-settings': 'Use system SMS provider settings',
          'min-period-0-seconds-message': 'Only 0 second minimum period is allowed.',
          'max-pending-messages': 'Maximum pending messages',
          'max-pending-messages-required': 'Maximum pending messages is required.',
          'max-pending-messages-range': 'Maximum pending messages should be in a range from 1 to 100000.',
          'originator-types-filter': 'Originator types filter',
          'interval-seconds': 'Interval in seconds',
          'interval-seconds-required': 'Interval is required.',
          'min-interval-seconds-message': 'Only 1 second minimum interval is allowed.',
          'output-timeseries-key-prefix': 'Output timeseries key prefix',
          'output-timeseries-key-prefix-required': 'Output timeseries key prefix required.',
          'separator-hint': 'You should press "enter" to complete field input.',
          'entity-details': 'Select entity details:',
          'entity-details-title': 'Title',
          'entity-details-country': 'Country',
          'entity-details-state': 'State',
          'entity-details-city': 'City',
          'entity-details-zip': 'Zip',
          'entity-details-address': 'Address',
          'entity-details-address2': 'Address2',
          'entity-details-additional_info': 'Additional Info',
          'entity-details-phone': 'Phone',
          'entity-details-email': 'Email',
          'add-to-metadata': 'Add selected details to message metadata',
          'add-to-metadata-hint': 'If selected, adds the selected details keys to the message metadata instead of message data.',
          'entity-details-list-empty': 'No entity details selected.',
          'no-entity-details-matching': 'No entity details matching were found.',
          'custom-table-name': 'Custom table name',
          'custom-table-name-required': 'Table Name is required',
          'custom-table-hint': 'You should enter the table name without prefix \'cs_tb_\'.',
          'message-field': 'Message field',
          'message-field-required': 'Message field is required.',
          'table-col': 'Table column',
          'table-col-required': 'Table column is required.',
          'latitude-key-name': 'Latitude key name',
          'longitude-key-name': 'Longitude key name',
          'latitude-key-name-required': 'Latitude key name is required.',
          'longitude-key-name-required': 'Longitude key name is required.',
          'fetch-perimeter-info-from-message-metadata': 'Fetch perimeter information from message metadata',
          'perimeter-key-name': 'Perimeter key name',
          'perimeter-key-name-required': 'Perimeter key name is required.',
          'perimeter-circle': 'Circle',
          'perimeter-polygon': 'Polygon',
          'perimeter-type': 'Perimeter type',
          'circle-center-latitude': 'Center latitude',
          'circle-center-latitude-required': 'Center latitude is required.',
          'circle-center-longitude': 'Center longitude',
          'circle-center-longitude-required': 'Center longitude is required.',
          'range-unit-meter': 'Meter',
          'range-unit-kilometer': 'Kilometer',
          'range-unit-foot': 'Foot',
          'range-unit-mile': 'Mile',
          'range-unit-nautical-mile': 'Nautical mile',
          'range-units': 'Range units',
          range: 'Range',
          'range-required': 'Range is required.',
          'polygon-definition': 'Polygon definition',
          'polygon-definition-required': 'Polygon definition is required.',
          'polygon-definition-hint':
            'Please, use the following format for manual definition of polygon: [[lat1,lon1],[lat2,lon2], ... ,[latN,lonN]].',
          'min-inside-duration': 'Minimal inside duration',
          'min-inside-duration-value-required': 'Minimal inside duration is required',
          'min-inside-duration-time-unit': 'Minimal inside duration time unit',
          'min-outside-duration': 'Minimal outside duration',
          'min-outside-duration-value-required': 'Minimal outside duration is required',
          'min-outside-duration-time-unit': 'Minimal outside duration time unit',
          'tell-failure-if-absent': 'Tell Failure',
          'tell-failure-if-absent-hint': 'If at least one selected key doesn\'t exist the outbound message will report "Failure".',
          'get-latest-value-with-ts': 'Fetch Latest telemetry with Timestamp',
          'get-latest-value-with-ts-hint':
            'If selected, latest telemetry values will be added to the outbound message metadata with timestamp, ' +
            'e.g: "temp": "&#123;"ts":1574329385897, "value":42&#125;"',
          'use-redis-queue': 'Use redis queue for message persistence',
          'trim-redis-queue': 'Trim redis queue',
          'redis-queue-max-size': 'Redis queue max size',
          'add-metadata-key-values-as-kafka-headers': 'Add Message metadata key-value pairs to Kafka record headers',
          'add-metadata-key-values-as-kafka-headers-hint': 'If selected, key-value pairs from message metadata will be added to the outgoing records headers as byte arrays with predefined charset encoding.',
          'charset-encoding': 'Charset encoding',
          'charset-encoding-required': 'Charset encoding is required.',
          'charset-us-ascii': 'US-ASCII',
          'charset-iso-8859-1': 'ISO-8859-1',
          'charset-utf-8': 'UTF-8',
          'charset-utf-16be': 'UTF-16BE',
          'charset-utf-16le': 'UTF-16LE',
          'charset-utf-16': 'UTF-16',
          'select-queue-hint': 'The queue name can be selected from a drop-down list or add a custom name.',
          'persist-alarm-rules': 'Persist state of alarm rules',
          'fetch-alarm-rules': 'Fetch state of alarm rules',
          'input-value-key': 'Input value key',
          'input-value-key-required': 'Input value key is required.',
          'output-value-key': 'Output value key',
          'output-value-key-required': 'Output value key is required.',
          round: 'Decimals',
          'round-range': 'Decimals should be in a range from 0 to 15.',
          'use-cache': 'Use cache for latest value',
          'tell-failure-if-delta-is-negative': 'Tell Failure if delta is negative',
          'add-period-between-msgs': 'Add period between messages',
          'period-value-key': 'Period value key',
          'period-value-key-required': 'Period value key is required.',
          'general-pattern-hint': 'Hint: use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">&#125;</span></code> ' +
            'for value from metadata, <code><span style="color: #000;">$[</span>messageKey<span style="color: #000;">]</span></code> ' +
            'for value from message body',
          'alarm-severity-pattern-hint': 'Hint: use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">&#125;</span></code> ' +
            'for value from metadata, <code><span style="color: #000;">$[</span>messageKey<span style="color: #000;">]</span></code> ' +
            'for value from message body. Alarm severity should be system (CRITICAL, MAJOR etc.)',
          'output-node-name-hint': 'The <b>rule node name</b> corresponds to the <b>relation type</b> of the output message, and it is used to forward messages to other rule nodes in the caller rule chain.',
          'skip-latest-persistence': 'Skip latest persistence',
          'use-server-ts': 'Use server ts',
          'use-server-ts-hint': 'Enable this setting to use the timestamp of the message processing instead of the timestamp from the message. Useful for all sorts of sequential processing if you merge messages from multiple sources (devices, assets, etc).',
          'kv-map-pattern-hint': 'Hint: use <code><span style="color: #000;">$&#123;</span>metadataKey<span style="color: #000;">&#125;</span></code> ' +
            'for value from metadata, <code><span style="color: #000;">$[</span>messageKey<span style="color: #000;">]</span></code> ' +
            'for value from message body to substitute "Source" and "Target" key names',
        },
        'key-val': {
          key: 'Key',
          value: 'Value',
          'remove-entry': 'Remove entry',
          'add-entry': 'Add entry'
        },
        'mail-body-type': {
          'plain-text': 'Plain Text',
          html: 'HTML',
          dynamic: 'Dynamic'
        }
      }
    };
    translate.setTranslation('en_US', enUS, true);
}
