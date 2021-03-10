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

import L from 'leaflet';
import LeafletMap from '../leaflet-map';
import { UnitedMapSettings } from '../map-models';
import { WidgetContext } from '@home/models/widget-component.models';
import wms from 'leaflet.wms';

export class OpenStreetMap extends LeafletMap {
    constructor(ctx: WidgetContext, $container, options: UnitedMapSettings) {
        super(ctx, $container, options);
        const map =  L.map($container, {
          editable: !!options.editablePolygon
        }).setView(options?.defaultCenterPosition, options?.defaultZoomLevel);
        let tileLayer;
        if (options.useCustomProvider) {
          tileLayer = L.tileLayer(options.customProviderTileUrl);
        } else {
          tileLayer = (L.tileLayer as any).provider(options.mapProvider || 'OpenStreetMap.Mapnik');
        }
        tileLayer.addTo(map);
        /**/
        if (!!options.useCustomWmsProvider) {
            let data: any = options.customWmsProviderTileUrls;
          
          // data is any because ts didn't recognize replaceAll, should we target esnext?
          let data2 = data.replaceAll('\n','')
           .replace(/^var \w+\s*=\s*/, "")
           .replaceAll('L.tileLayer.wms(','{"url":')
           .replaceAll('wms.source(','{"urlWms":')
           .replaceAll("})","}}")
           .replaceAll(',{',',"data":{')
           .replaceAll("'", '"');

          let parsedData = JSON.parse(data2);
          if (!!parsedData) {
          let params = {};
          let paramsWmsSource = {};

          for (let k in parsedData) {
            if (parsedData.hasOwnProperty(k)) {
              if (parsedData[k].url && parsedData[k].url != "") {
                params[k] = L.tileLayer.wms(
                  parsedData[k].url,
                  parsedData[k].data
                );
              } else if (parsedData[k].urlWms && parsedData[k].urlWms != "") {
                paramsWmsSource[k] = wms.source(
                  parsedData[k].urlWms,
                  parsedData[k].data
                );
              }
            }
          }
          
          // Tile mode (Uses L.WMS.TileLayer)
          for (let nameSource in paramsWmsSource) {
            //paramsWmsSource[nameSource].getLayer(paramsWmsSource[nameSource].options.layers).addTo(map);
            params[nameSource] = paramsWmsSource[nameSource].getLayer(paramsWmsSource[nameSource].options.layers);
          }
          
          L.control.layers({}, params).addTo(map);
          }
        }
        /**/
        super.initSettings(options);
        super.setMap(map);
    }
}
