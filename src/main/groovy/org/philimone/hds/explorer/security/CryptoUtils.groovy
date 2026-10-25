package org.philimone.hds.explorer.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

class CryptoUtils {

    private static final String ALGORITHM = "AES"
    // Fixed 16-byte key salt for AES-128 encryption
    private static final byte[] AES_KEY_BYTES = [0x48, 0x44, 0x53, 0x45, 0x78, 0x70, 0x6C, 0x6F, 0x72, 0x65, 0x72, 0x53, 0x65, 0x63, 0x75, 0x72] as byte[]
    private static final String ENC_PREFIX = "ENC:"

    static String encrypt(String plainText) {
        if (!plainText || plainText.trim().isEmpty() || plainText.startsWith(ENC_PREFIX)) {
            return plainText
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(AES_KEY_BYTES, ALGORITHM)
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8))
            return ENC_PREFIX + Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (Exception e) {
            return plainText
        }
    }

    static String decrypt(String encryptedText) {
        if (!encryptedText || !encryptedText.startsWith(ENC_PREFIX)) {
            return encryptedText
        }
        try {
            String cipherSub = encryptedText.substring(ENC_PREFIX.length())
            byte[] encryptedBytes = Base64.getDecoder().decode(cipherSub)
            SecretKeySpec secretKey = new SecretKeySpec(AES_KEY_BYTES, ALGORITHM)
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes)
            return new String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (Exception e) {
            return encryptedText
        }
    }
}
