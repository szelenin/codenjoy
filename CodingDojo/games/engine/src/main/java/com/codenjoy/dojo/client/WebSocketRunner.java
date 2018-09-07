package com.codenjoy.dojo.client;

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


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeException;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketRunner implements Closeable {

    public static final String DEFAULT_USER = "apofig@gmail.com";
    private static final String LOCAL = "127.0.0.1:8080";
    public static final String WS_URI_PATTERN = "ws://%s/%s/ws?user=%s&code=%s";
    public static final Pattern BOARD_PATTERN = Pattern.compile("^board=(.*)$");
    public static final String CODENJOY_COM_SERVER = "tetrisj.jvmhost.net:12270";
    public static final String CODENJOY_COM_ALIAS = "codenjoy.com:8080";
    public static String BOT_EMAIL_SUFFIX = "-super-ai@codenjoy.com";
    public static String BOT_CODE = "12345678901234567890";


    public static boolean PRINT_TO_CONSOLE = true;
    public static int TIMEOUT = 5000;
    public static Integer ATTEMPTS = 3;

    private Session session;
    private WebSocketClient client;
    private Solver solver;
    private ClientBoard board;
    private Runnable onClose;
    private boolean forceClose;

    public WebSocketRunner(Solver solver, ClientBoard board) {
        this.solver = solver;
        this.board = board;
        this.forceClose = false;
    }

    public static WebSocketRunner runClient(String url, Solver solver, ClientBoard board) {
        UrlParser parser = new UrlParser(url);
        return run(parser.server, parser.context,
                parser.userName, parser.code,
                solver, board, ATTEMPTS);
    }

    public static WebSocketRunner runAI(String aiName, Solver solver, ClientBoard board) {
        PRINT_TO_CONSOLE = false;
        return run(LOCAL, CodenjoyContext.get(), aiName, BOT_CODE, solver, board, 1);
    }

    private static WebSocketRunner run(String server, String context,
                                       String userName, String code,
                                       Solver solver, ClientBoard board,
                                       int countAttempts)
    {
        return run(getUri(server, context, userName, code), solver, board, countAttempts);
    }

    private static URI getUri(String server, String context, String userName, String code) {
        try {
            String url = String.format(WS_URI_PATTERN, server, context, userName, code);
            if (url.contains(CODENJOY_COM_ALIAS)) { // TODO это костылек пока сервер не сделаем нормальный
                url = url.replace(CODENJOY_COM_ALIAS, CODENJOY_COM_SERVER);
            }
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static WebSocketRunner run(URI uri, Solver solver, ClientBoard board, int countAttempts) {
        try {
            WebSocketRunner client = new WebSocketRunner(solver, board);
            client.start(uri, countAttempts);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (client != null) {
                    client.close();
                }
            }));

            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void start(URI uri, int countAttempts) throws Exception {
        client = new WebSocketClient();
        client.start();

        onClose = () -> {
            if (forceClose || solver instanceof OneCommandSolver) {
                return;
            }

            printReconnect();
            connectLoop(uri, countAttempts);
        };

        connectLoop(uri, countAttempts);
    }

    @Override
    public void close() {
        forceClose = true;
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
            client = null;
        } catch (Exception e) {
            print(e);
        }
    }

    @WebSocket
    public class ClientSocket {

        @OnWebSocketConnect
        public void onConnect(Session session) {
            print("Opened connection " + session.toString());
        }

        @OnWebSocketClose
        public void onClose(int closeCode, String message) {
            if (onClose != null) {
                onClose.run();
            }
            print("Closed with message: '" + message + "' and code: " + closeCode);
        }

        @OnWebSocketError
        public void onError(Session session, Throwable reason) {
            if (isUnauthorizedAccess(reason)) {
                print("Connection error: Unauthorized access. Please register user and/or write valid EMAIL/CODE in the client.");
            } else {
                print("Error with message: '" + reason.toString());
            }
        }

        @OnWebSocketMessage
        public void onMessage(String data) {
            print("Data from server: " + data);
            try {
                Matcher matcher = BOARD_PATTERN.matcher(data);
                if (!matcher.matches()) {
                    throw new RuntimeException("Error parsing data: " + data);
                }

                board.forString(matcher.group(1));
                print("Board: " + board);

                String answer = solver.get(board);
                print("Answer: " + answer);

                session.getRemote().sendString(answer);
            } catch (Exception e) {
                print(e);
            }
            printBreak();
        }
    }

    private boolean isUnauthorizedAccess(Throwable exception) {
        return exception instanceof UpgradeException && ((UpgradeException)exception).getResponseStatusCode() == 401;
    }

    private void connectLoop(URI uri, int countAttempts) {
        while (countAttempts-- > 0) {
            try {
                tryToConnect(uri);
                break;
            } catch (ExecutionException e) {
                if (!isUnauthorizedAccess(e.getCause())) {
                    print(e);
                }
                printReconnect();
            } catch (Exception e) {
                print(e);
                printReconnect();
            }
        }
    }

    private void printReconnect() {
        print("Waiting before reconnect...");
        printBreak();
        sleep(TIMEOUT);
    }

    private void tryToConnect(URI uri) throws Exception {
        print(String.format("Connecting to '%s'...", uri));

        if (session != null) {
            session.close();
        }

        session = client.connect(new ClientSocket(), uri)
                .get(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            print(e);
        }
    }

    private void printBreak() {
        print("-------------------------------------------------------------");
    }

    public static void print(String message) {
        if (PRINT_TO_CONSOLE) {
            System.out.println(message);
        }
    }

    private void print(Exception e) {
        if (PRINT_TO_CONSOLE) {
            e.printStackTrace(System.out);
        }
    }
}
