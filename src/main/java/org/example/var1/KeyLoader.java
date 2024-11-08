package org.example.var1;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyLoader {
    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        byte[] keyBytes = new byte[1024];
        try (FileInputStream fis = new FileInputStream(filename)) {
            fis.read(keyBytes);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        byte[] keyBytes = new byte[1024];
        try (FileInputStream fis = new FileInputStream(filename)) {
            fis.read(keyBytes);
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        return kf.generatePublic(spec);
    }
}