package com.codenjoy.dojo.transport.auth;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.dao.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecureAuthenticationService implements AuthenticationService {

    @Autowired
    private Registration registration;

    @Override
    public String authenticate(HttpServletRequest request) {
        String user = request.getParameter("user");
        if (user.endsWith(WebSocketRunner.BOT_EMAIL_SUFFIX)) {
            return user;
        }
        String code = request.getParameter("code");
        if (WebSocketRunner.BOT_CODE.equals(code)) { // TODO test me
            return user;
        }
        if (registration.checkUser(user, code)) {
            return user;
        } else {
            return null;
        }
    }
}
