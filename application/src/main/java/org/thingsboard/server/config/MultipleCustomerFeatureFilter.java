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
package org.thingsboard.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.msg.tools.TbFeatureDisabledException;
import org.thingsboard.server.exception.ThingsboardErrorResponseHandler;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * If customer-asset/device association is enabled then apply this filter.
 */
@Component
@Slf4j
public class MultipleCustomerFeatureFilter extends GenericFilterBean {

    @Autowired
    private ThingsboardErrorResponseHandler errorResponseHandler;

    @Value("${features.multiple_customer_enabled:false}")
    private boolean isMultipleCustomerEnabled;


    Map<Pattern, String> customerSingleAssociationEndpoints = new LinkedHashMap<>();

    @PostConstruct
    private void functionalityFilterPostConstruct(){
        if (isMultipleCustomerEnabled) {
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/.+/device/.+"), "POST"); // assignDeviceToCustomer
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/device/.+"), "DELETE"); // unassignDeviceFromCustomer
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/tenant/deviceInfos"), "GET"); // getTenantDeviceInfos
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/.+/deviceInfos"), "GET"); // getCustomerDeviceInfos

            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/.+/asset/.+"), "POST"); // assignAssetToCustomer
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/asset/.+"), "DELETE"); // unassignAssetFromCustomer
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/tenant/assetInfos"), "GET"); // getTenantDeviceInfos
            customerSingleAssociationEndpoints.put(Pattern.compile("/api/customer/.+/assetInfos"), "GET"); // getCustomerAssetInfos
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isMultipleCustomerEnabled) {
            String uri = "";
            String method = "";

            if (servletRequest instanceof HttpServletRequest) {
                uri = ((HttpServletRequest) servletRequest).getRequestURI().trim();
                method = ((HttpServletRequest) servletRequest).getMethod().trim();
            }

            if (checkUriAndMethod(customerSingleAssociationEndpoints, uri, method)) {
                errorResponseHandler.handle(new TbFeatureDisabledException(EntityType.DEVICE), (HttpServletResponse) servletResponse);
                return;
            }

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean checkUriAndMethod(Map<Pattern, String> patternMap, String uri, String method){
        for(Map.Entry<Pattern, String> regexPattern : patternMap.entrySet()) {
            if(regexPattern.getKey().matcher(uri).matches() && regexPattern.getValue().equals(method))
                return true;
        }

        return false;
    }
}
