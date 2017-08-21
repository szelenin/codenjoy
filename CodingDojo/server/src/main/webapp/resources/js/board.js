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

function loadPlayers(onLoad) {
    loadData('rest/game/' + game.gameName + '/players', function(players) {
        if (game.allPlayersScreen) {
            game.players = players;
        } else {
            for (var index in players) {
                if (players[index].name == game.playerName) {
                    game.players = [players[index]];
                }
            }
        }

        onLoad(game.players);
    });
}

function initBoardPage(game) {
    loadContext(function(events, ctx) {
        loadData('rest/game/' + game.gameName + '/type', function(playerGameInfo) {
            game.singleBoardGame = playerGameInfo.singleBoard;
            game.boardSize = playerGameInfo.boardSize;

            loadData('rest/player/' + game.playerName + '/check/' + game.code, function(registered) {
                game.registered = registered;

                loadData('rest/sprites/' + game.gameName + '/exists', function(isGraphicOrTextGame) {
                    game.isGraphicOrTextGame = isGraphicOrTextGame;

                    loadPlayers(function(players) {
                        initBoardComponents(game);
                    });
                });
            });
        });
    });
}

function initBoardComponents(game) {
    initBoards(game.players, game.allPlayersScreen,
            game.gameName, game.contextPath);

    if (game.isGraphicOrTextGame) {
        initCanvases(game.contextPath, game.players, game.allPlayersScreen,
                    game.singleBoardGame, game.boardSize,
                    game.gameName, game.enablePlayerInfo, game.sprites);
    } else {
        initCanvasesText(game.contextPath, game.players, game.allPlayersScreen,
                        game.singleBoardGame, game.boardSize,
                        game.gameName, game.enablePlayerInfo,
                        game.playerDrawer);
    }

    if (game.enableDonate) {
        initDonate(game.contextPath);
    }

    initJoystick(game.playerName, game.registered,
            game.code, game.contextPath,
            game.enableJoystick, game.enableAlways);

    if (game.enableLeadersTable) {
        initLeadersTable(game.contextPath, game.playerName, game.code,
                function(leaderboard) {
                    if (!!$("#glasses")) {
                        $(window).resize(resize);
                        resize();
                    }
                    function resize() {
                        var width = leaderboard.width();
                        var margin = 30;

                        $("#glasses").width($(window).width() - width - 3*margin)
                                .css({ marginLeft: margin, marginTop: margin });

                        leaderboard.width(width).css({ position: "absolute",
                                        marginLeft: 0, marginTop: margin,
                                        top: 0, left: $("#glasses").width()});
                    }
                });
    }

    var gameInfo = '<h3><a href="' + game.contextPath + 'resources/help/' + game.gameName + '.html" target="_blank">How to play ' + game.gameName + '</a></h3>';

    if (game.enableChat) {
        initChat(game.playerName, game.registered,
                game.code, game.contextPath,
                game.gameName);

        if (game.enableInfo) {
            $("#chat-container").prepend(gameInfo);
        }
    } else {
        if (game.enableInfo) {
            $("#leaderboard").append(gameInfo);
        }
    }
    if (!game.enableInfo) {
        $("#fork-me").hide(gameInfo);
    }

    if (game.enableHotkeys) {
        initHotkeys(game.gameName, game.contextPath);
    }

    if (game.enableAdvertisement) {
        initAdvertisement();
    }

    if (game.showBody) {
        $(document.body).show();
    }

    if (game.allPlayersScreen) {
        if (!!game.onBoardAllPageLoad) {
            game.onBoardAllPageLoad();
        }
    } else {
        if (!!game.onBoardPageLoad) {
            game.onBoardPageLoad();
        }
    }
}