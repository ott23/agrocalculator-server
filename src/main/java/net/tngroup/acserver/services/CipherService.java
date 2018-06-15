package net.tngroup.acserver.services;

import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class CipherService {

    public static String generateDesKey() throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            // Логгирование
            throw new Exception("Cipher error");
        }
    }

    public static String des(String input, String keyString, boolean encode) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES");
        if (encode) {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return new BASE64Encoder().encode(encValue);
        } else {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = new BASE64Decoder().decodeBuffer(input);
            byte[] decValue = cipher.doFinal(decodedValue);
            return new String(decValue, StandardCharsets.UTF_8);
        }
    }
}
