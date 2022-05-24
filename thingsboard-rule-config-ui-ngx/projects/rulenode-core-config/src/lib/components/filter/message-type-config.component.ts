import { Component } from '@angular/core';
import { AppState } from '@core/public-api';
import { RuleNodeConfiguration, RuleNodeConfigurationComponent } from '@shared/public-api';
import { Store } from '@ngrx/store';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'tb-filter-node-message-type-config',
  templateUrl: './message-type-config.component.html',
  styleUrls: []
})
export class MessageTypeConfigComponent extends RuleNodeConfigurationComponent {

  messageTypeConfigForm: FormGroup;

  constructor(protected store: Store<AppState>,
              private fb: FormBuilder) {
    super(store);
  }

  protected configForm(): FormGroup {
    return this.messageTypeConfigForm;
  }

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.messageTypeConfigForm = this.fb.group({
      messageTypes: [configuration ? configuration.messageTypes : null, [Validators.required]]
    });
  }
}
