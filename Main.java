import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static final Queue<String> queue = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "10000")
        );

        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", port), 0
        );

        server.createContext("/", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );
                Map<String, String> data = new HashMap<>();

                for (String part : body.split("&")) {
                    String[] pair = part.split("=", 2);
                    if (pair.length == 2) {
                        data.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                        );
                    }
                }

                synchronized (queue) {
                    String action = data.getOrDefault("action", "");
                    if ("add".equals(action)) {
                        String name = data.getOrDefault("name", "").trim();
                        if (!name.isEmpty() && name.length() <= 100) {
                            queue.offer(name);
                        }
                    } else if ("serve".equals(action)) {
                        queue.poll();
                    }
                }

                exchange.getResponseHeaders().set("Location", "/");
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                exchange.sendResponseHeaders(303, -1);
                exchange.close();
                return;
            }

            StringBuilder html = new StringBuilder("""
                <!DOCTYPE html><html><head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Bank Queue Management</title>
                <style>
                body{font-family:Arial;max-width:600px;margin:30px auto;
                padding:16px;background:#f3f6fa;color:#172554}
                section{background:white;padding:20px;border-radius:12px;margin:15px 0}
                input,button{padding:12px;margin:5px 0;font-size:16px}
                input{box-sizing:border-box;width:100%}
                button{background:#155eef;color:white;border:0;border-radius:6px}
                li{padding:6px}
                </style></head><body>
                <h1>Bank Queue Management System</h1>
                <section><h2>Register customer</h2>
                <form method="post">
                <input type="hidden" name="action" value="add">
                <input name="name" maxlength="100" required placeholder="Customer name">
                <button type="submit">Add customer</button></form></section>
                <section><h2>Waiting queue</h2><ol>
                """);

            synchronized (queue) {
                for (String name : queue) {
                    html.append("<li>").append(escape(name)).append("</li>");
                }
                html.append("</ol><p>Customers waiting: ")
                    .append(queue.size()).append("</p>");
            }

            html.append("""
                <form method="post">
                <input type="hidden" name="action" value="serve">
                <button type="submit">Serve next customer</button>
                </form></section></body></html>
                """);

            byte[] response = html.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8"
            );
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response);
            }
        });

        server.start();
        System.out.println("Bank Queue System started on port " + port);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
