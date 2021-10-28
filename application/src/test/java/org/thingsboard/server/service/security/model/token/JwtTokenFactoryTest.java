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
package org.thingsboard.server.service.security.model.token;

import com.sun.tools.javac.util.List;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.thingsboard.server.config.JwtSettings;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenFactoryTest {

    @Mock
    JwtSettings jwtSettings;

    @Mock
    RawAccessJwtToken rawAccessJwtToken;

    private Claims claims;

    @Before
    public void setup() {
        claims = new DefaultClaims();
        claims.put("userId", "0f7668b0-da71-11eb-ba74-a33d0dd450b4");
        claims.put("scopes", List.of("TENANT_ADMIN"));
    }

    @Test
    public void testIsPublicAndEnabledClaimsAreOfTypeString() {
        claims.put("enabled", "true");
        claims.put("isPublic", "false");

        when(rawAccessJwtToken.parseClaims()).thenReturn(claims);
        JwtTokenFactory underTest = new JwtTokenFactory(jwtSettings);

        SecurityUser securityUser = underTest.parseAccessJwtToken(rawAccessJwtToken);

        assertTrue(securityUser.isEnabled());
        assertEquals(UserPrincipal.Type.USER_NAME, securityUser.getUserPrincipal().getType());
    }

    @Test
    public void testIsPublicAndEnabledClaimsAreOfTypeBoolean() {
        claims.put("enabled", true);
        claims.put("isPublic", false);

        when(rawAccessJwtToken.parseClaims()).thenReturn(claims);
        JwtTokenFactory underTest = new JwtTokenFactory(jwtSettings);

        SecurityUser securityUser = underTest.parseAccessJwtToken(rawAccessJwtToken);

        assertTrue(securityUser.isEnabled());
        assertEquals(UserPrincipal.Type.USER_NAME, securityUser.getUserPrincipal().getType());
    }

}
