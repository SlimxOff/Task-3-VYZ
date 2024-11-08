package org.example.var2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class Client {
    private static PrivateKey privateKey;

    public static void main(String[] args) throws Exception {
        // Загрузка приватного ключа
        privateKey = KeyLoader.loadPrivateKey("private2.key");

        // Получение нового сообщения от сервера
        String newMessage = getNewMessageFromServer();

        // Подпись сообщения
        Signature sign = Signature.getInstance("SHA256withDSA");
        sign.initSign(privateKey);
        sign.update(newMessage.getBytes());
        byte[] signature = sign.sign();

        // Кодирование подписи в Base64
        String encodedSignature = Base64.getEncoder().encodeToString(signature);

        // Отправка подписанного сообщения на сервер для проверки
        verifySignatureOnServer(newMessage, encodedSignature);
    }

    private static String getNewMessageFromServer() throws Exception {
        URL url = new URL("http://localhost:8080/generate");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status == 200) {
            return new String(con.getInputStream().readAllBytes());
        } else {
            throw new RuntimeException("Failed to get new message from server");
        }
    }

    private static void verifySignatureOnServer(String message, String signature) throws Exception {
        URL url = new URL("http://localhost:8080/verify");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        // Отправка подписанного сообщения
        con.getOutputStream().write((message + ":" + signature).getBytes());

        // Получение ответа от сервера
        int status = con.getResponseCode();
        System.out.println("Response Code: " + status);
    }
}