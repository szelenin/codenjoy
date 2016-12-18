package com.codenjoy.dojo.web.controller;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.dao.Registration;
import com.codenjoy.dojo.services.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired private PlayerService playerService;
    @Autowired private Registration registration;
    @Autowired private GameService gameService;
    @Autowired private MailService mailService;
    @Autowired private LinkService linkService;

    @Value("${email.verification}")
    private boolean isEmailVerificationNeeded;

    @Value("${page.registration}")
    private String registrationPage;

    public RegistrationController() {
    }

    //for unit test
    RegistrationController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String openRegistrationForm(HttpServletRequest request, Model model) {
        String ip = getIp(request);

        Player player = new Player();
        player.setName(request.getParameter("name"));
        player.setGameName(request.getParameter(AdminController.GAME_NAME));
        model.addAttribute("player", player);

        player.setCallbackUrl("http://" + ip + ":8888");

        return getRegister(model);
    }

    private String getRegister(Model model) {
        model.addAttribute("opened", playerService.isRegistrationOpened());
        model.addAttribute("gameNames", gameService.getGameNames());

        if (StringUtils.isEmpty(registrationPage)) {
            return "register";
        } else {
            model.addAttribute("url", registrationPage);
            return "redirect";
        }
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    @RequestMapping(params = "approve", method = RequestMethod.GET)
    public String approveEmail(Model model, @RequestParam("approve") String link) {
        Map<String, Object> data = linkService.getData(link);
        if (data == null) {
            model.addAttribute("message", "Ошибка регистрации. Повтори еще раз.");
            return "error";
        }
        String code = (String) data.get("code");
        String name = (String) data.get("name");
        String ip = (String) data.get("ip");
        String gameName = (String) data.get("gameName");
        registration.approve(code);
        return "redirect:/" + register(name, code, gameName, ip);
    }

    @RequestMapping(params = "approved", method = RequestMethod.GET)
    public @ResponseBody String isEmailApproved(@RequestParam("approved") String email) throws InterruptedException {
        while (!registration.approved(email)) {
            Thread.sleep(2000);
        }
        Player player = null;
        while ((player = playerService.get(email)) == NullPlayer.INSTANCE) {
            Thread.sleep(2000);
        }
        String code = registration.getCode(email);
        return getBoardUrl(code, player);
    }

    @RequestMapping(params = "remove_me", method = RequestMethod.GET)
    public String removeUserFromGame(@RequestParam("code") String code) {
        String name = registration.getEmail(code);
        Player player = playerService.get(name);
        playerService.remove(player.getName());
        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitRegistrationForm(Player player, BindingResult result, HttpServletRequest request, Model model) {
        if (result.hasErrors()) {
            return openRegistrationForm(request, model);
        }

        String code = "";
        boolean registered = registration.registered(player.getName());
        boolean approved = registration.approved(player.getName());
        if (registered && approved) {
            code = registration.login(player.getName(), player.getPassword());
            if (code == null) {
                model.addAttribute("bad_pass", true);

                return openRegistrationForm(request, model);
            }
        } else {
            if (!registered) {
                code = registration.register(player.getName(), player.getPassword(), player.getData());
            } else {
                code = registration.getCode(player.getName());
            }

            if (!approved) {
                if (isEmailVerificationNeeded) {
                    LinkService.LinkStorage storage = linkService.forLink();
                    Map<String, Object> map = storage.getMap();
                    String email = player.getName();
                    map.put("name", email);
                    map.put("code", code);
                    map.put("gameName", player.getGameName());
                    map.put("ip", request.getRemoteAddr());
                    map.put("host", player.getGameName());

                    String host = WebSocketRunner.Host.REMOTE.host;
                    String link = "http://" + host + "/codenjoy-contest/register?approve=" + storage.getLink();
                    try {
                        mailService.sendEmail(email, "Codenjoy регистрация",
                                "Пожалуйста, подтверди регистрацию кликом на этот линк<br>" +
                                        "<a target=\"_blank\" href=\"" + link + "\">" + link + "</a><br>" +
                                        "Он направит тебя к игре.<br>" +
                                        "<br>" +
                                        "Если тебя удивило это письмо, просто удали его.<br>" +
                                        "<br>" +
                                        "<a href=\"http://codenjoy.com\">Команда Codenjoy</a>");
                    } catch (MessagingException e) {
                        model.addAttribute("message", e.toString());
                        return "error";
                    }
                } else {
                    registration.approve(code);
                    approved = true;
                }
            }
        }
        player.setCode(code);

        if (approved) {
            return "redirect:/" + register(player.getName(), player.getCode(),
                            player.getGameName(), request.getRemoteAddr());
        } else {
            model.addAttribute("wait_approve", true);
            return openRegistrationForm(request, model);
        }
    }

    private String register(String name, String code, String gameName, String ip) {
        Player player = playerService.register(name, ip, gameName);
        return getBoardUrl(code, player);
    }

    private String getBoardUrl(String code, Player player) {
//        if (player.getGameType().isSingleBoard()) {
            return "board/player/" + player.getName() + "?code=" + code;
//        } else {
//            return "board/?code=" + code;
//        }
    }
}
