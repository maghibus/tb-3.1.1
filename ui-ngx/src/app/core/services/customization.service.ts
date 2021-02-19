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


import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { map, pluck } from 'rxjs/operators';
import { environment } from '../../../environments/environment.prod';
import { AppState } from '../core.state';
import { Configuration } from './configuration.model';

@Injectable({
  providedIn: 'root'
})
export class CustomizationService {

  constructor(private http: HttpClient, private store: Store<AppState>,) { }


  public configuration: Configuration = new Configuration;


  load(): Promise<any> {
    const promise = this.http.get<Configuration>(environment.configurationUrl, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('access_token')}`
      })
    }).pipe(map(el => { return { platform: el.platform, header: el.header } }))
      .toPromise()
      .then(data => {
        Object.assign(this.configuration, data);
        return data;
      }).catch(async error => {
        const staticConf = await this.http.get<Configuration>('assets/config.json').toPromise();
        Object.assign(this.configuration, staticConf);
        return error;
      });
    return promise;
  }

  getPlatformConfiguration() {
    return this.configuration.platform;
  }

  getHeaderConfiguration() {
    return this.configuration.header;
  }

  changeTheme() {
    document.documentElement.style.setProperty('--primary', this.getPlatformConfiguration().primary);
    document.documentElement.style.setProperty('--secondary-color', this.getPlatformConfiguration().secondaryColor);
    document.documentElement.style.setProperty('--hue-color', this.getPlatformConfiguration().hueColor);
    document.documentElement.style.setProperty('--platform-header-background', this.getPlatformConfiguration().platformHeaderBackground);
    document.documentElement.style.setProperty('--header-gradient', this.getPlatformConfiguration().headerGradient);
    document.documentElement.style.setProperty('--toolbar-icon-color', this.getPlatformConfiguration().menuIconColor);
    document.documentElement.style.setProperty('--container-icon-color', this.getPlatformConfiguration().containerIconColor);
    document.documentElement.style.setProperty('--title-color', this.getPlatformConfiguration().titleColor);
    document.documentElement.style.setProperty('--header-logo-height', this.getPlatformConfiguration().headerLogoHeight);
  }

}
