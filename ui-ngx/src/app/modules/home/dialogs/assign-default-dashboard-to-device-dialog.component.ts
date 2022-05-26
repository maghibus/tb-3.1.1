///
/// Copyright © 2016-2020 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Component, Inject, OnInit, SkipSelf } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { DeviceService } from '@core/http/device.service';
import { EntityType } from '@shared/models/entity-type.models';
import { BehaviorSubject, forkJoin, Observable } from 'rxjs';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { AssignableDashboard, DeviceDefaultDashboard, DeviceId } from '@app/shared/public-api';

export interface AssignDefaultDashboardToDeviceDialogData {
  entityIds: Array<DeviceId>;
  entityType: EntityType;
}

@Component({
  selector: 'tb-assign-default-dashboard-to-device-dialog',
  templateUrl: './assign-default-dashboard-to-device-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: AssignDefaultDashboardToDeviceDialogComponent}],
  styleUrls: []
})
export class AssignDefaultDashboardToDeviceDialogComponent extends
  DialogComponent<AssignDefaultDashboardToDeviceDialogComponent, boolean> implements OnInit, ErrorStateMatcher {

  defaultDashboardFormGroup: FormGroup;

  submitted: boolean = false;

  dialogTitle: string = 'device.default-dashboard';
  selectDashboardTitle: string = 'dashboard.select-dashboard';
  selectStateTitle: string = 'dashboard.select-state';
  selectDashboardError: string = 'device.default-dashboard-error';
  selectStateError: string = 'device.default-state-error';
  gisDeviceTypeFormLabel: string = 'device.gis-device-type-form-label';
  gisDeviceTypeRequiredError: string = 'device.gis-device-type-required-error';
  assignToCustomerText: string;
  assignableDashboards: Observable<Array<AssignableDashboard>>;
  assignableStates: BehaviorSubject<Array<string>>;
  filteredOptions: Observable<Array<AssignableDashboard>>;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: AssignDefaultDashboardToDeviceDialogData,
              private deviceService: DeviceService,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<AssignDefaultDashboardToDeviceDialogComponent, boolean>,
              public fb: FormBuilder) {
    super(store, router, dialogRef);
    this.assignableStates = new BehaviorSubject([]);
  }


  ngOnInit(): void {
    this.defaultDashboardFormGroup = this.fb.group({
      selectedDashboard: [null, [Validators.required]],
      selectedState: [null, [Validators.required]],
      gisDeviceType: [
        null,
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(20)
        ]
      ]
    });
    this.assignableDashboards = this.deviceService.getAssignableDashboards();
    this.defaultDashboardFormGroup.get('selectedDashboard').valueChanges.subscribe(this.handleDashboardSelect);
  }

  handleDashboardSelect = (value: AssignableDashboard) => {
    this.assignableStates.next(value.states);
  }

  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  assign(): void {
    this.submitted = true;
    const selectedDashboard: AssignableDashboard = this.defaultDashboardFormGroup.get('selectedDashboard').value;
    const selectedState: string = this.defaultDashboardFormGroup.get('selectedState').value;
    const gisDeviceType: string = this.defaultDashboardFormGroup.get('gisDeviceType').value;
    const tasks: Observable<any>[] = [];
    this.data.entityIds.forEach(
      (entityId) => {
        const defaultDashboard: DeviceDefaultDashboard = {
          dashboardId: selectedDashboard.id.id,
          dashboardState: selectedState,
          gisDeviceType: gisDeviceType
        }
        tasks.push(this.getAssignToCustomerTask(entityId.id, defaultDashboard));
      }
    );
    forkJoin(tasks).subscribe(
      () => {
        this.dialogRef.close(true);
      }
    );
  }

  private getAssignToCustomerTask(entityId: string, defaultDashboard: DeviceDefaultDashboard): Observable<any> {
    return this.deviceService.assignDefaultDashboardToDevice(entityId, defaultDashboard);
  }
}
