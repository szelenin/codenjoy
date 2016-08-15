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
var currentBoardSize = null;

function initCanvases(players, allPlayersScreen, singleBoardGame, boardSize, gameName, enablePlayerInfo){
    var canvases = new Object();
    var infoPools = new Object();
    currentBoardSize = boardSize;

    if (!enablePlayerInfo) {
        $(".player_info").hide();
    }

    function toId(email) {
        return email.replace(/[@.]/gi, "_");
    }

    for (var i in players) {
        var player = players[i];
        canvases[player] = createCanvas(toId(player));
        infoPools[player] = [];
    }

    function decode(gameName, color) {
        return plots[color];
    }

    function drawBoardForPlayer(playerName, gameName, board, coordinates) {
        var playerCanvas = canvases[playerName];
        var drawLayer = function(layer){
            var x = 0;
            var y = boardSize - 1;
            for (var index in layer) {
                var color = layer[index];
                playerCanvas.drawPlot(decode(gameName, color), x, y);
                x++;
                if (x == boardSize) {
                   x = 0;
                   y--;
                }
            }
        }

        playerCanvas.clear();
        if ($('#_background').length) {
            var x = boardSize / 2 - 0.5;
            playerCanvas.drawPlot('_background', x, 0);
        }
        try {
            var json = $.parseJSON(board);
            $.each(json.layers, function(index, layer) {
                drawLayer(layer);
            });
        } catch (err) {
            drawLayer(board);
        }
        if ($('#_fog').length) {
            var x = boardSize / 2 - 0.5;
            playerCanvas.drawPlot('_fog', x, 0);
        }

        if (singleBoardGame) {
            $.each(coordinates, function(name, pt) {
                playerCanvas.drawPlayerName(name, pt);
            });
        }
    }

    function calculateTextSize(text) {
        var div = $("#width_calculator_container");
        div.html(text);
        div.css('display', 'block');
        return div[0];
    }

    function showScoreInformation(playerName, information) {
        var infoPool = infoPools[playerName];

        if (information != '') {
            var arr = information.split(', ');
            for (var i in arr) {
                if (arr[i] == '0') {
                    continue;
                }
                infoPool.push(arr[i]);
            }
        }
        if (infoPool.length == 0) return;

        var score = $("#score_info_" + toId(playerName));
        if (score.is(':visible')) {
            return;
        }

        var text = '<center>' + infoPool.join('<br><br>') + '</center>';
        infoPool.splice(0, infoPool.length);

        var canvas = $("#" + toId(playerName));
        var size = calculateTextSize(text);
        score.css({
                position: "absolute",
                marginLeft: 0,
                marginTop: 0,
                left: canvas.position().left + canvas.width()/2 - size.clientWidth/2,
                top: canvas.position().top + canvas.height()/2 - size.clientHeight/2
            });

        score.html(text);

        score.show().delay(300).fadeOut(1600, function() {
            score.hide();

            showScoreInformation(playerName, '');
        });
    }

    function createCanvas(canvasName) {
        var canvasImages = $('#systemCanvas img');
        var canvas = $("#" + canvasName);

        var plotSize = 0;
        var canvasSize = 0;
        var calcSize = function() {
            plotSize = canvasImages[0].width;
            canvasSize = plotSize * boardSize;
            if (canvas[0].width != canvasSize || canvas[0].height != canvasSize) {
                canvas[0].width = canvasSize;
                canvas[0].height = canvasSize;
            }
        }

        var plots = {};
        $.each(canvasImages, function(index, plot) {
            var color = plot.id;
            var image = new Image();
            image.onload = function() {
                if (plotSize == 0) {
                    calcSize();
                }
            }
            image.src = plot.src;
            plots[color] = image;
        });

        var drawPlot = function(color, x, y) {
            var image = plots[color];
            var ctx = canvas[0].getContext("2d");
            ctx.drawImage(
                image,
                x * plotSize - (image.width - plotSize)/2,
                (boardSize - 1 - y) * plotSize - (image.height - plotSize)
            );
        };

        var drawPlayerName = function(email, pt) {
            var name = email.substring(0, email.indexOf('@'));

            if (pt.x == -1 || pt.y == -1) return;

            var ctx = canvas[0].getContext("2d");
            ctx.font = "15px 'Verdana, sans-serif'";
            ctx.fillStyle = "#0FF";
            ctx.textAlign = "left";
            ctx.shadowColor = "#000";
            ctx.shadowOffsetX = 0;
            ctx.shadowOffsetY = 0;
            ctx.shadowBlur = 7;
            ctx.fillText(name, (pt.x + 1) * plotSize, (boardSize - pt.y - 1) * plotSize - 5);
            ctx.shadowBlur = 0;
        }

        var clear = function() {
            canvas.clearCanvas();
        }

        var getCanvasSize = function() {
            return canvasSize;
        }

        var getPlotSize = function() {
            return plotSize;
        }

        return {
            drawPlot : drawPlot,
            drawPlayerName: drawPlayerName,
            clear : clear,
            getCanvasSize : getCanvasSize,
            getPlotSize : getPlotSize
        };
    }

    function isPlayerListEmpty(data) {
        return Object.keys(data).length == 0;
    }

    function isPlayersListChanged(data) {
        var newPlayers = Object.keys(data);
        var oldPlayers = Object.keys(players);

        if (newPlayers.length != oldPlayers.length) {
            return true;
        }

        var hasNew = false;
        newPlayers.forEach(function (newPlayer) {
            if ($.inArray(newPlayer, oldPlayers) == -1) {
                hasNew = true;
            }
        });

        return hasNew;
    }

    function drawUsersCanvas(data) {
        if (data == null) {
            $("#showdata").text("There is NO data for player available!");
            return;
        }
        $("#showdata").text('');

        if (allPlayersScreen && isPlayersListChanged(data) || isPlayerListEmpty(data)) {
            window.location.reload();
            return;
        }

        if (allPlayersScreen) {
            $.each(data, drawUserCanvas);
        } else {
            for (var i in players) {
                var player = players[i];
                drawUserCanvas(player, data[player]);
            }
        }
    }

    function drawUserCanvas(playerName, data) {
        if (currentBoardSize != data.boardSize) {    // TODO так себе решение... Почему у разных юзеров передается размер добры а не всем сразу?
            window.location.reload();
        }

        drawBoardForPlayer(playerName, data.gameName, data.board, $.parseJSON(data.coordinates));
        $("#score_" + toId(playerName)).text(data.score);
        showScoreInformation(playerName, data.info);
        if (!allPlayersScreen) {
            $("#level_" + toId(playerName)).text(data.level);
        }
    }

    $('body').bind("board-updated", function(events, data) {
        drawUsersCanvas(data);
    });

}