package com.saggu;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
 
public class CryptoUtils {
    private static final String TRANSFORMATION = "AES";
 
    public static byte[]  encrypt(SecretKey key, byte[] input)
            throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, key, input);
    }
 
    public static byte[]  decrypt(SecretKey key, byte[] input)
            throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, input);
    }
 
    private static byte[] doCrypto(int cipherMode, SecretKey secretKey, byte[] input
            ) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
             
           return cipher.doFinal(input);
             
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException  ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}