package com.security.oauth2.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class KeyUtils {

    @Autowired
    Environment environment;

    @Value("${access-token.private}")

    private String accessPrivateKeyPath = "secret";
    @Value("${access-token.public}")
    private String accessPublicKeyPath = "secret";


    @Value("${refresh-token.private}")

    private String refreshPrivateKeyPath = "";
    @Value("${refresh-token.public}")
    private String refreshPublicKeyPath;

    private KeyPair _accessTokenPair;

    private KeyPair _refreshTokenPair;

    public KeyPair get_accessTokenPair() {
        if (Objects.isNull(_accessTokenPair)) {
            _accessTokenPair = getKeyPair(accessPublicKeyPath, accessPrivateKeyPath);
        }
        return _accessTokenPair;
    }

    public KeyPair get_refreshTokenPair() {
        if (Objects.isNull(_refreshTokenPair)) {
            _refreshTokenPair = getKeyPair(refreshPublicKeyPath, refreshPrivateKeyPath);
        }
        return _refreshTokenPair;
    }

    private KeyPair getKeyPair(String publicKeyPath, String privateKeyPath) {
        KeyPair keyPair;
        File publicKeyFile = new File(publicKeyPath);
        File privateKeyFile = new File(privateKeyPath);
        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            log.info("loading keys from file: {}, {}" , publicKeyPath, privateKeyPath );
            try {
                KeyFactory factory = KeyFactory.getInstance("RSA");

                byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = factory.generatePublic(publicKeySpec);

                byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                PrivateKey privateKey = factory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                return keyPair;
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equals("prod"))) {
                throw new RuntimeException("File Not Found !!!");
            }
        }

        File directory = new File("access-refresh-token-keys");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {

            log.info("Generated new Public and Private Keys: {}, {} ", publicKeyPath, privateKeyPath);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            try (FileOutputStream fos = new FileOutputStream(publicKeyPath)) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            try (FileOutputStream fos = new FileOutputStream(privateKeyPath)) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }


        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }

    public RSAPublicKey getAccessTokenPublicKey() {
        return (RSAPublicKey) get_accessTokenPair().getPublic();
    }

    public RSAPrivateKey getAccessTokenPrivateKey() {
        return (RSAPrivateKey) get_accessTokenPair().getPrivate();
    }

    public RSAPublicKey getRefreshTokenPublicKey() {
        return (RSAPublicKey) get_refreshTokenPair().getPublic();
    }

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        return (RSAPrivateKey) get_refreshTokenPair().getPrivate();
    }
}
