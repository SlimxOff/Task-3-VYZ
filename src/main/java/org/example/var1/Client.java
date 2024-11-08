package org.example.var1;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class Client {
    private static PrivateKey privateKey;

    public static void main(String[] args) throws Exception {
        // Загрузка приватного ключа
        privateKey = KeyLoader.loadPrivateKey("private.key");

        // Отправка запроса на сервер
        URL url = new URL("http://localhost:8080/verify");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        // Подпись сообщения
        String message = "ClientMessage";
        Signature sign = Signature.getInstance("SHA256withDSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes());
        byte[] signature = sign.sign();

        // Кодирование подписи в Base64
        String encodedSignature = Base64.getEncoder().encodeToString(signature);

        // Отправка подписанного сообщения
        con.getOutputStream().write((message + ":" + encodedSignature).getBytes());

        // Получение ответа от сервера
        int status = con.getResponseCode();
        System.out.println("Response Code: " + status);
    }
}