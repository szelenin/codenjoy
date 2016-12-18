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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.dao.ActionLogger;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin31415")
public class AdminController {

    public static final String GAME_NAME = "gameName";

    @Autowired private TimerService timerService;
    @Autowired private PlayerService playerService;
    @Autowired private SaveService saveService;
    @Autowired private GameService gameService;
    @Autowired private ActionLogger actionLogger;

    public AdminController() {
    }

    //for unit test
    AdminController(TimerService timerService, PlayerService playerService) {
        this.timerService = timerService;
        this.playerService = playerService;
    }

    @RequestMapping(params = "save", method = RequestMethod.GET)
    public String savePlayerGame(@RequestParam("save") String name, Model model, HttpServletRequest request) {
        saveService.save(name);
        return getAdmin(request);
    }

    private String getAdmin(HttpServletRequest request) {
        return getAdmin(getGameName(request));
    }

    @RequestMapping(params = "gameVersion", method = RequestMethod.GET)
    public @ResponseBody String getGameVersion(@RequestParam("gameVersion") String gameName) {
        return gameService.getGame(gameName).getVersion();
    }

    @RequestMapping(params = "saveAll", method = RequestMethod.GET)
    public String saveAllGames(Model model, HttpServletRequest request) {
        saveService.saveAll();
        return getAdmin(request);
    }

    @RequestMapping(params = "load", method = RequestMethod.GET)
    public String loadPlayerGame(@RequestParam("load") String name, Model model, HttpServletRequest request) {
        saveService.load(name);
        return getAdmin(request);
    }

    @RequestMapping(params = "reloadAI", method = RequestMethod.GET)
    public String reloadAI(@RequestParam("reloadAI") String name, Model model, HttpServletRequest request) {
        playerService.reloadAI(name);
        return getAdmin(request);
    }

    @RequestMapping(params = "loadAll", method = RequestMethod.GET)
    public String loadAllGames(Model model, HttpServletRequest request) {
        saveService.loadAll();
        return getAdmin(request);
    }

    @RequestMapping(params = "gameOver", method = RequestMethod.GET)
    public String removePlayer(@RequestParam("gameOver") String name, Model model, HttpServletRequest request) {
        playerService.remove(name);
        return getAdmin(request);
    }

    @RequestMapping(params = "removeSave", method = RequestMethod.GET)
    public String removePlayerSave(@RequestParam("removeSave") String name, Model model, HttpServletRequest request) {
        saveService.removeSave(name);
        return getAdmin(request);
    }

    @RequestMapping(params = "removeSaveAll", method = RequestMethod.GET)
    public String removePlayerSave(Model model, HttpServletRequest request) {
        saveService.removeAllSaves();
        return getAdmin(request);
    }

    @RequestMapping(params = "gameOverAll", method = RequestMethod.GET)
    public String gameOverAllPlayers(Model model, HttpServletRequest request) {
        playerService.removeAll();
        return getAdmin(request);
    }

    @RequestMapping(params = "pause", method = RequestMethod.GET)
    public String pauseGame(Model model, HttpServletRequest request) {
        timerService.pause();
        return getAdmin(request);
    }

    private void checkGameStatus(Model model) {
        model.addAttribute("paused", timerService.isPaused());
    }

    private void checkRecordingStatus(Model model) {
        model.addAttribute("recording", actionLogger.isRecording());
    }

    @RequestMapping(params = "resume", method = RequestMethod.GET)
    public String resumeGame(Model model, HttpServletRequest request) {
        timerService.resume();
        return getAdmin(request);
    }

    @RequestMapping(params = "recording", method = RequestMethod.GET)
    public String recordingGame(Model model, HttpServletRequest request) {
        actionLogger.resume();
        return getAdmin(request);
    }

    @RequestMapping(params = "stopRecording", method = RequestMethod.GET)
    public String stopRecordingGame(Model model, HttpServletRequest request) {
        actionLogger.pause();
        return getAdmin(request);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String saveSettings(AdminSettings settings, BindingResult result, Model model, HttpServletRequest request) {
        if (!result.hasErrors()) {
            // do nothing
        }
        if (settings.getPlayers() != null) {
            playerService.updateAll(settings.getPlayers());
        }

        if (settings.getTimerPeriod() != null) {
            try {
                timerService.changePeriod(Integer.valueOf(settings.getTimerPeriod()));
            } catch (NumberFormatException e) {
                // do nothing
            }
        }

        if (settings.getParameters() != null) {
            Settings gameSettings = gameService.getGame(settings.getGameName()).getSettings();
            List<Parameter> parameters = (List) gameSettings.getParameters();
            for (int index = 0; index < parameters.size(); index++) {
                parameters.get(index).update(settings.getParameters().get(index));
            }
        }

        if (settings.getGenerateNameMask() != null) {
            String mask = settings.getGenerateNameMask();
            int count = Integer.valueOf(settings.getGenerateCount());

            int created = 0;
            int index = 0;
            while (created != count) {
                String name = mask.replaceAll("%", String.valueOf(++index));

                if (playerService.contains(name) && index < playerService.getAll().size()) {
                    continue;
                }

                created++;
                playerService.register(name, "127.0.0.1", settings.getGameName());
            }
        }

        request.setAttribute(GAME_NAME, settings.getGameName());
        return getAdmin(settings.getGameName());
    }

    private String getAdmin(String gameName) {
        if (gameName == null) {
            return getAdmin();
        }
        return "redirect:/admin31415?" + GAME_NAME + "=" + gameName;
    }

    private String getAdmin() {
        return getAdmin(getDefaultGame());
    }

    private String getDefaultGame() {
        return gameService.getGameNames().iterator().next();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getAdminPage(Model model, HttpServletRequest request) {
        String gameName = getGameName(request);

        if (gameName == null) {
            return getAdmin();
        }

        GameType game = gameService.getGame(gameName);
        Settings gameSettings = game.getSettings();
        List<Parameter<?>> parameters = gameSettings.getParameters();

        AdminSettings settings = new AdminSettings();

        settings.setParameters(new LinkedList<String>());
        for (Parameter p : parameters) {
            settings.getParameters().add(p.getValue().toString());
        }

        model.addAttribute("adminSettings", settings);
        model.addAttribute("parameters", parameters);
        model.addAttribute(GAME_NAME, gameName);
        model.addAttribute("gameVersion", game.getVersion());
        model.addAttribute("generateNameMask", "demo%@codenjoy.com");
        model.addAttribute("generateCount", "30");
        model.addAttribute("timerPeriod", timerService.getPeriod());

        checkGameStatus(model);
        checkRecordingStatus(model);
        checkRegistrationClosed(model);
        prepareList(model, settings, gameName);
        return "admin";
    }

    private String getGameName(HttpServletRequest request) {
        String gameName = request.getParameter(GAME_NAME);
        if (gameName == null) {
            gameName = (String) request.getAttribute(GAME_NAME);
        }
        return gameName;
    }

    private void prepareList(Model model, AdminSettings settings, String gameName) {
        List<PlayerInfo> players = saveService.getSaves();

        Set<String> gameNames = new TreeSet<>(gameService.getGameNames());
        List<String> counts = new LinkedList<>();
        for (String name : gameNames) {
            int count = 0;
            for (PlayerInfo player : players) {
                if (name.equals(player.getGameName())) {
                    count++;
                }
            }
            String countPlayers = (count != 0) ? String.format("(%s)", count) : "";
            counts.add(countPlayers);
        }
        model.addAttribute("games", gameNames);
        model.addAttribute("gamesCount", counts);


        for (PlayerInfo player : players) {
            player.setHidden(!gameName.equals(player.getGameName()));
        }

        if (!players.isEmpty()) {
            model.addAttribute("players", players);
        }
        settings.setPlayers(players);
    }

    @RequestMapping(params = "cleanAll", method = RequestMethod.GET)
    public String cleanAllPlayersScores(Model model, HttpServletRequest request) {
        playerService.cleanAllScores();
        return getAdmin(request);
    }

    @RequestMapping(params = "select", method = RequestMethod.GET)
    public String selectGame(HttpServletRequest request, Model model, @RequestParam(GAME_NAME) String gameName) {
        if (gameName == null) {
            gameName = getDefaultGame();
        }
        request.setAttribute(GAME_NAME, gameName);
        return getAdmin(request);
    }

    @RequestMapping(params = "close", method = RequestMethod.GET)
    public String close(Model model, HttpServletRequest request) {
        playerService.closeRegistration();
        return getAdmin(request);
    }

    private void checkRegistrationClosed(Model model) {
        model.addAttribute("opened", playerService.isRegistrationOpened());
    }

    @RequestMapping(params = "open", method = RequestMethod.GET)
    public String open(Model model, HttpServletRequest request) {
        playerService.openRegistration();
        return getAdmin(request);
    }

}
