/**
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
package org.thingsboard.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgDataType;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.queue.TbClusterService;

@RestController
@TbCoreComponent
@RequestMapping("/api")
public class RuleEngineController extends BaseController {

    @Autowired
    protected TbClusterService tbClusterService;

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN, CUSTOMER_USER')")
    @RequestMapping(value = "/rule-engine", method = RequestMethod.POST)
    @ResponseBody
    public void call(@RequestBody String jsonBody) throws ThingsboardException {
        try {
            TbMsgMetaData metaData = new TbMsgMetaData();
            metaData.putValue("userId", getCurrentUser().getId().toString());
            metaData.putValue("userName", getCurrentUser().getName());
            TbMsg tbMsg = TbMsg.newMsg(DataConstants.RULE_ENGINE_API_REQUEST, getCurrentUser().getId(), metaData, TbMsgDataType.JSON, jsonBody);
            TenantId tenantId = getCurrentUser().getTenantId();

            tbClusterService.pushMsgToRuleEngine(tenantId, getCurrentUser().getId(), tbMsg, null);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}