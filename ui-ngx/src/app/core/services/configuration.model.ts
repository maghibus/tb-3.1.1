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

export class Configuration {
    platform: Platform;
    header: HeaderConfig;
}
export class Platform {
    primary: string = '#337ab7';
    headerGradient: string = '#555';
    menuIconColor: string = 'white';
    secondaryColor: string = '#527dad';
    hueColor: string = ' #a7c1de';
    platformHeaderBackground: string = '#eee';
    containerIconColor: string = '#337ab7';
    titleColor: string = '#337ab7';
    headerLogoHeight: string = "36px";
    footerImgWidth: string = "340px";
    sidemenuBg: string = '#007ab7';
    sidemenuHover: string = '#0095bb';
    sidemenuBorderColor: string = 'black';
    logo: string = 'assets/home_component_logo.png';
    footerImage: string = 'assets/pon_metro_logo.png';
    favicon: string = "assets/platform-header-logo.png";
    windowTitle: string = "IotPlatform";
    swaggerLink: boolean = true;
    defaultDeviceDashboardAssignment: boolean = false;
}

export class HeaderConfig {
    oauth2LogOutUrl: string = 'https://domain/oidc/logout';
    logoutOnCloseSession: boolean = false;
    logOutRedirectUrl: string = 'https://domain/oneadmin/login';
    federatedLogoutUrl: string = null;
    disableBigdataLogout: boolean = false;
    disableIotLogout: boolean = false;
    hideTabs: boolean;
    showAppMenuLabel: boolean;
    logoDir: string = 'assets/platform-header-logo.png';
    secondaryLogoDir: string;
    showBorderBottom: string;
    appTitle: string = 'SMART CITY PLATFORM';
    appUrlsConfig: {
        bigdata: '/giotto-web';
        iot: '/home';
        admin: '/oneadmin';
        udm: '/udm-fe'
    }
}
