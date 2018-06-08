package net.tngroup.acserver.services;

import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
public class CipherService {

    private static BASE64Encoder base64Encoder = new BASE64Encoder();
    private static BASE64Decoder base64Decoder = new BASE64Decoder();

    public static String base64(String input, boolean encode) throws Exception {
        if (encode) {
            return base64Encoder.encode(input.getBytes());
        } else {
            try {
                return new String(base64Decoder.decodeBuffer(input));
            } catch (IOException e) {
                // Логгирование
                throw new Exception("Cipher error");
            }
        }
    }

    public static SecretKey generateAes256Key() throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            // Логгирование
            throw new Exception("Cipher error");
        }
    }

    public static String aes256(String input, SecretKey key, boolean encode) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            if (encode) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            return new String(cipher.doFinal(input.getBytes()));
        } catch (Exception e) {
            // Логгирование
            throw  new Exception("Cipher error");
        }

    }
}
