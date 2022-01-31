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

import { AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { fromEvent, Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { debounceTime, distinctUntilChanged, map, tap } from 'rxjs/operators';

import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { User } from '@shared/models/user.model';
import { PageComponent } from '@shared/components/page.component';
import { AppState } from '@core/core.state';
import { getCurrentAuthState, selectAuthUser, selectUserDetails } from '@core/auth/auth.selectors';
import { MediaBreakpoints } from '@shared/models/constants';
import * as _screenfull from 'screenfull';
import { MatSidenav } from '@angular/material/sidenav';
import { AuthState } from '@core/auth/auth.models';
import { WINDOW } from '@core/services/window.service';
import { instanceOfSearchableComponent, ISearchableComponent } from '@home/models/searchable-component.models';

import Header from "@giotto-jfrog/giotto-platform-header/dist/index";
import customerConfig from './customer.config';
import { AuthService } from '@app/core/auth/auth.service';
import { Router } from '@angular/router';
import { CustomizationService } from '@app/core/services/customization.service';

const screenfull = _screenfull as _screenfull.Screenfull;

@Component({
  selector: 'tb-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent extends PageComponent implements AfterViewInit, OnInit {

  authState: AuthState = getCurrentAuthState(this.store);

  forceFullscreen = this.authState.forceFullscreen;

  activeComponent: any;
  searchableComponent: ISearchableComponent;

  sidenavMode: 'over' | 'push' | 'side' = 'side';
  sidenavOpened = true;

  logo = this.customizationService.getPlatformConfiguration().logo;

  @ViewChild('sidenav')
  sidenav: MatSidenav;

  @ViewChild('searchInput') searchInputField: ElementRef;

  fullscreenEnabled = screenfull.isEnabled;

  authUser$: Observable<any>;
  userDetails$: Observable<User>;
  userDetailsString: Observable<string>;

  searchEnabled = false;
  showSearch = false;
  searchText = '';

  constructor(protected store: Store<AppState>,
    @Inject(WINDOW) private window: Window,
    public breakpointObserver: BreakpointObserver,
    private authService: AuthService,
    private router: Router,
    private customizationService: CustomizationService) {
    super(store);
  }

  ngOnInit() {
    this.authUser$ = this.store.pipe(select(selectAuthUser));
    this.userDetails$ = this.store.pipe(select(selectUserDetails));
    this.userDetailsString = this.userDetails$.pipe(map((user: User) => {
      return JSON.stringify(user);
    }));

    const isGtSm = this.breakpointObserver.isMatched(MediaBreakpoints['gt-sm']);
    this.sidenavMode = isGtSm ? 'side' : 'over';
    this.sidenavOpened = isGtSm;

    this.breakpointObserver
      .observe(MediaBreakpoints['gt-sm'])
      .subscribe((state: BreakpointState) => {
        if (state.matches) {
          this.sidenavMode = 'side';
          this.sidenavOpened = true;
        } else {
          this.sidenavMode = 'over';
          this.sidenavOpened = false;
        }
      }
      );

    const logoutTb = () => {
      this.authService.logout();
    }
    let platformHeader = document.getElementById('platform-header');
    let instance = new Header(
      platformHeader,
      {
        appTitle: this.customizationService.getHeaderConfiguration().appTitle,
        logoDir: this.customizationService.getHeaderConfiguration().logoDir,
        appContext: "iot",
        logout: logoutTb,
        hideTabs: this.customizationService.getHeaderConfiguration().hideTabs,
        showAppMenuLabel: this.customizationService.getHeaderConfiguration().showAppMenuLabel,
        oauth2LogOutUrl: this.customizationService.getHeaderConfiguration().oauth2LogOutUrl,
        logoutOnCloseSession: this.customizationService.getHeaderConfiguration().logoutOnCloseSession,
        logOutRedirectUrl: this.customizationService.getHeaderConfiguration().logOutRedirectUrl,
        disableBigdataLogout: this.customizationService.getHeaderConfiguration().disableBigdataLogout,
        disableIotLogout: this.customizationService.getHeaderConfiguration().disableIotLogout,
        federatedLogoutUrl: this.customizationService.getHeaderConfiguration().federatedLogoutUrl,
        appUrlsConfig: this.customizationService.getHeaderConfiguration().appUrlsConfig
      });
    instance.init();
    //oidc logout enabled
    if(!!this.customizationService.getHeaderConfiguration().oauth2LogOutUrl)
      this.authService.setExternalLogoutCallback(instance.logOut());
    }

  ngAfterViewInit() {
    fromEvent(this.searchInputField.nativeElement, 'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          this.searchTextUpdated();
        })
      )
      .subscribe();
  }

  sidenavClicked() {
    if (this.sidenavMode === 'over') {
      this.sidenav.toggle();
    }
  }

  toggleFullscreen() {
    if (screenfull.isEnabled) {
      screenfull.toggle();
    }
  }

  isFullscreen() {
    return screenfull.isFullscreen;
  }

  goBack() {
    this.window.history.back();
  }

  activeComponentChanged(activeComponent: any) {
    this.showSearch = false;
    this.searchText = '';
    this.activeComponent = activeComponent;
    if (this.activeComponent && instanceOfSearchableComponent(this.activeComponent)) {
      this.searchEnabled = true;
      this.searchableComponent = this.activeComponent;
    } else {
      this.searchEnabled = false;
      this.searchableComponent = null;
    }
  }

  displaySearchMode(): boolean {
    return this.searchEnabled && this.showSearch;
  }

  openSearch() {
    if (this.searchEnabled) {
      this.showSearch = true;
      setTimeout(() => {
        this.searchInputField.nativeElement.focus();
        this.searchInputField.nativeElement.setSelectionRange(0, 0);
      }, 10);
    }
  }

  closeSearch() {
    if (this.searchEnabled) {
      this.showSearch = false;
      if (this.searchText.length) {
        this.searchText = '';
        this.searchTextUpdated();
      }
    }
  }

  private searchTextUpdated() {
    if (this.searchableComponent) {
      this.searchableComponent.onSearchTextUpdated(this.searchText);
    }
  }
}
