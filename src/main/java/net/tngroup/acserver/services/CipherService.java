package net.tngroup.acserver.services;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class CipherService {

    public static String generateAes256Key() throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            // Логгирование
            throw new Exception("Cipher error");
        }
    }

    public static String aes256(String input, SecretKey key, boolean encode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        if (encode) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        return new String(cipher.doFinal(input.getBytes()));
    }
}
