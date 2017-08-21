package com.codenjoy.console;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Created by indigo on 2017-02-26.
 */
@RestController
public class AdminController {

    @Value("${console.login}")
    private String login;

    @Value("${console.password}")
    private String password;

    @RequestMapping("/codenjoy-console")
    public String doit(@RequestParam(name = "action") String action) {
        RestartCodenjoyServer console = new RestartCodenjoyServer(login, password);

        if (!Arrays.asList("restart", "start", "stop").contains(action)) {
            return "unexpected action: " + action;
        }

        if (console.login()) {

            if (action.equals("restart")) {
                console.restart();
            } else if (action.equals("start")) {
                console.start();
            } else if (action.equals("stop")) {
                console.stop();
            } else {
                // do nothing
            }

            console.logout();
            return "success: " + action;
        } else {
            return "fail: " + action;
        }
    }
}
