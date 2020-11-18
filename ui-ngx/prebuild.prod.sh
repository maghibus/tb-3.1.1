#!/bin/bash

cp ./customer_assets/$TARGET_CUSTOMER/platform-header.config.ts ./src/app/modules/home/platform-header.config.ts 2>/dev/null || :
cp ./customer_assets/$TARGET_CUSTOMER/locale.constant-it_IT.json ./src/assets/locale/locale.constant-it_IT.json 2>/dev/null || :