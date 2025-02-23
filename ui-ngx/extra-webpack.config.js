/*
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
const CompressionPlugin = require("compression-webpack-plugin");
const webpack = require("webpack");
const dirTree = require("directory-tree");
const FileManagerPlugin = require('filemanager-webpack-plugin');
const targetCustomer = process.env.TARGET_CUSTOMER || "default";
console.log("Target customer: ", targetCustomer);

var langs = [];

dirTree("./src/assets/locale/", {extensions: /\.json$/}, (item) => {
  /* It is expected what the name of a locale file has the following format: */
  /* 'locale.constant-LANG_CODE[_REGION_CODE].json', e.g. locale.constant-es.json or locale.constant-zh_CN.json*/
  langs.push(item.name.slice(item.name.lastIndexOf("-") + 1, -5));
});

module.exports = (env, argv) => {
  const customPlugins = {
    plugins: [
      new webpack.DefinePlugin({
        TB_VERSION: JSON.stringify(require("./package.json").version),
        SUPPORTED_LANGS: JSON.stringify(langs)
      }),
      new CompressionPlugin({
        filename: "[path].gz[query]",
        algorithm: "gzip",
        test: /\.js$|\.css$|\.html$|\.svg?.+$|\.jpg$|\.ttf?.+$|\.woff?.+$|\.eot?.+$|\.json$/,
        threshold: 10240,
        minRatio: 0.8,
        deleteOriginalAssets: false,
      })
    ]
  };
  if( env.mode !== "development" && targetCustomer !== "default") {
    customPlugins.plugins.push(
      new FileManagerPlugin({
        onStart: {
          copy: [
            // { source: `./customer_assets/${targetCustomer}/themes/*.scss`, destination: `./src` },
            { source: `./customer_assets/${targetCustomer}/images/favicon.ico`, destination: `./src` },
            // { source: `./customer_assets/${targetCustomer}/scss/*.scss`, destination: `./src/scss` },
            { source: `./customer_assets/${targetCustomer}/images/*.png`, destination: `./src/assets` }
          ],
        }
      })
    )
  }
  env.plugins = env.plugins.concat(customPlugins.plugins); 
  return env;
};
