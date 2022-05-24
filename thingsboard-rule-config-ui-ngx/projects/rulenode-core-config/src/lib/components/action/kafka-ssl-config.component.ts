import { AfterViewInit, Component, OnInit } from '@angular/core';
import { AppState } from '@core/public-api';
import { RuleNodeConfiguration, RuleNodeConfigurationComponent } from '@shared/public-api';
import { Store } from '@ngrx/store';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToByteStandartCharsetTypes, ToByteStandartCharsetTypeTranslations } from '../../rulenode-core-config.models';

@Component({
  selector: 'tb-action-node-kafka-ssl-config',
  templateUrl: './kafka-ssl-config.component.html',
  styleUrls: []
})
export class KafkaSslConfigComponent extends RuleNodeConfigurationComponent {

  kafkaSslConfigForm: FormGroup;

  ackValues: string[] = ['all', '-1', '0', '1'];

  ToByteStandartCharsetTypesValues = ToByteStandartCharsetTypes;
  ToByteStandartCharsetTypeTranslationMap = ToByteStandartCharsetTypeTranslations;

  constructor(protected store: Store<AppState>,
              private fb: FormBuilder) {
    super(store);
  }

  protected configForm(): FormGroup {
    return this.kafkaSslConfigForm;
  }

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.kafkaSslConfigForm = this.fb.group({
      topicPattern: [configuration ? configuration.topicPattern : null, [Validators.required]],
      bootstrapServers: [configuration ? configuration.bootstrapServers : null, [Validators.required]],
      retries: [configuration ? configuration.retries : null, [Validators.min(0)]],
      batchSize: [configuration ? configuration.batchSize : null, [Validators.min(0)]],
      linger: [configuration ? configuration.linger : null, [Validators.min(0)]],
      bufferMemory: [configuration ? configuration.bufferMemory : null, [Validators.min(0)]],
      acks: [configuration ? configuration.acks : null, [Validators.required]],
      keySerializer: [configuration ? configuration.keySerializer : null, [Validators.required]],
      valueSerializer: [configuration ? configuration.valueSerializer : null, [Validators.required]],
      password: [configuration ? configuration.password : null, [Validators.required]],
      keystore: [configuration ? configuration.keystore : null, [Validators.required]],
      truststore: [configuration ? configuration.truststore : null, [Validators.required]],
      otherProperties: [configuration ? configuration.otherProperties : null, []],
      addMetadataKeyValuesAsKafkaHeaders: [configuration ? configuration.addMetadataKeyValuesAsKafkaHeaders : false, []],
      kafkaHeadersCharset: [configuration ? configuration.kafkaHeadersCharset : null, []]
    });
  }

  protected validatorTriggers(): string[] {
    return ['addMetadataKeyValuesAsKafkaHeaders'];
  }

  protected updateValidators(emitEvent: boolean) {
    const addMetadataKeyValuesAsKafkaHeaders: boolean = this.kafkaSslConfigForm.get('addMetadataKeyValuesAsKafkaHeaders').value;
    if (addMetadataKeyValuesAsKafkaHeaders) {
      this.kafkaSslConfigForm.get('kafkaHeadersCharset').setValidators([Validators.required]);
    } else {
      this.kafkaSslConfigForm.get('kafkaHeadersCharset').setValidators([]);
    }
    this.kafkaSslConfigForm.get('kafkaHeadersCharset').updateValueAndValidity({emitEvent});
  }

}
