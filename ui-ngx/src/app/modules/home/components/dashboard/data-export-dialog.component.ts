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

import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatCheckboxModule } from '@angular/material/checkbox';

export interface DialogData {
    data: any;
    datasources: any;
    typeAlias: string,
    timewindow: boolean;
}

@Component({
    selector: 'data-export-dialog',
    templateUrl: 'data-export-dialog.component.html',
    styleUrls: ['data-export-dialog.component.css']
})
export class DataExportDialog {
    constructor(
        public dialogRef: MatDialogRef<DataExportDialog>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

    dataSource: object = this.data;
    includeHeader: boolean = true;
    delimiter: string = ";";
    csvToExport: string = "";

    onNoClick(): void {
        this.dialogRef.close();
    }

    onDownloadClick() {
        if (this.data.timewindow === true) {
            this.data.datasources.forEach((datasource) => {
                let fileName = datasource.name + ".csv";
                fileName = fileName.replace(/[`~!@#$%^&*()_|+\-=?;:'",<>\{\}\[\]\\\/]/gi, '');
                let dataKeys = datasource.dataKeys; //array of data keys
                let rawData = this.aggregate(this.data.data, datasource);

                this.fromDatasourceToCsv(fileName, dataKeys, rawData);
                this.save(this.csvToExport, fileName, "text/csv");
            });
        } else {
            let fileName = this.data.datasources[0].aliasName + ".csv";
            fileName = fileName.replace(/[`~!@#$%^&*()_|+\-=?;:'",<>\{\}\[\]\\\/]/gi, '');

            let rawData = [];
            let dataKeys = this.data.datasources[0].dataKeys;

            this.data.datasources.forEach((elemant, elementIndex) => {
                var eIndex = parseInt(elementIndex, 10);
                var rawDataApp = this.aggregate(this.data.data, this.data.datasources[eIndex]);
                rawDataApp.forEach((rda) => {
                    rawData.push(rda);
                });
            });
            this.fromDatasourceToCsv(fileName, dataKeys, rawData);
            this.save(this.csvToExport, fileName, "text/csv");
        }

        this.dialogRef.close();
    }

    private fromDatasourceToCsv(fileName, dataKeys, rawData) {
        let headerList = ["timestamp"];
        let csvContent = "";
        let delim = this.delimiter;

        dataKeys.forEach((dataKey) => {
            headerList.push(dataKey.name);
        });

        if (this.includeHeader) {
            let row = headerList.join(delim);
            csvContent += row + "\r\n";
        }

        rawData.forEach(function (rowArray) {
            let row = rowArray.join(delim);
            csvContent += row + "\r\n";
        });
        this.csvToExport = csvContent;
    }

    private aggregate(data, datasource) {
        var rawData = [];
        var filteredData = [];

        data.forEach((d) => {
            if (d.datasource.entityName === datasource.entityName)
                filteredData.push(d);
        });
        if (filteredData.length > 0) {
            for (var i = 0; i < filteredData[0].data.length; i++) {
                var newRow = [];
                newRow.push(filteredData[0].data[i][0]);
                filteredData.forEach((d) => {
                    if (d.data.length > 0)
                        newRow.push(d.data[i][1]);
                    else newRow.push("");
                });
                rawData.push(newRow);
            }
        }

        return rawData;
    }

    private save(data, filename, type) {
        var file = new Blob([data], { type: type });
        if (window.navigator.msSaveOrOpenBlob) // IE10+
            window.navigator.msSaveOrOpenBlob(file, filename);
        else { // Others
            var a = document.createElement("a"),
                url = URL.createObjectURL(file);
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            setTimeout(function () {
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            }, 0);
        }
    }
}
