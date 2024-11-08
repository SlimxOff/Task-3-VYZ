package org.example.var1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Random;

public class Server {
    private static PublicKey publicKey;

    public static void main(String[] args) throws Exception {
        // Загрузка публичного ключа
        publicKey = KeyLoader.loadPublicKey("public.key");

        // Создание HTTP сервера
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/verify", new VerifyHandler());
        server.setExecutor(null);
        server.start();
    }

    static class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Генерация нового сообщения
                String newMessage = generateRandomMessage();

                // Получение данных от клиента
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                String[] parts = requestBody.split(":");
                String clientMessage = parts[0];
                byte[] signature = Base64.getDecoder().decode(parts[1]);

                // Проверка подписи
                Signature sign = Signature.getInstance("SHA256withDSA");
                sign.initVerify(publicKey);
                sign.update(clientMessage.getBytes());

                if (sign.verify(signature)) {
                    String response = "Signature verified: " + newMessage;
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    String response = "Signature verification failed";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String generateRandomMessage() {
            Random random = new Random();
            return "RandomMessage" + random.nextInt(10000);
        }
    }
}