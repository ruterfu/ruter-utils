package com.ruterfu.utils.aes;

import com.ruterfu.thirdpkg.apache.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AES {
    private static Charset CHARSET = StandardCharsets.UTF_8;
    private byte[] aesKey;

    /**
     * 初始化AES 256，如果key位数不足43位，会用1填充到43位
     * @param key
     */
    public AES(String key){
        String useKey;
        if(key.length() == 43) {
            useKey = key;
        } else if(key.length() > 43) {
            useKey = key.substring(0, 43);
        } else {
            int needFill = 43 - key.length();
            StringBuilder sb = new StringBuilder(key);
            for (int i = 0; i < needFill; i++) {
                sb.append("1");
            }
            useKey = sb.toString();
        }
        aesKey = Base64.decodeBase64(useKey + "=");
        if(aesKey.length != 32 && aesKey.length != 16 && aesKey.length != 24) {
            throw new SecurityException("AES Key length == 16(for aes 128) or length == 24(for aes 192) or length == 32(for aes 256) but now  length == " + aesKey.length + ".");
        }
    }

    /**
     * 初始自定义的key，key长度必须是16，24，32，分别对应 AES128，AES192，AES256
     * @param key
     */
    public AES(byte[] key){
        aesKey = key;
        if(aesKey.length != 32 && aesKey.length != 16 && aesKey.length != 24) {
            throw new SecurityException("AES Key length == 16(for aes 128) or length == 24(for aes 192) or length == 32(for aes 256) but now  length == " + aesKey.length + ".");
        }
    }

    public int getKeySize() {
        return aesKey.length;
    }

    /**
     * 将原始文本AES加密成Base64编码过的文本
     * @param originalText 原始文本
     * @return Base64编码过的文本
     */
    public String encryptStringToString(String originalText){
        return Base64.encodeBase64String(encryptString(originalText));
    }

    /**
     * 将原始文本AES加密成Base64编码过的文本(URL安全)
     * @param originalText 原始文本
     * @return Base64编码过的文本，可以在GET中传输
     */
    public String encryptStringToStringURLSafe(String originalText){
        return Base64.encodeBase64URLSafeString(encryptString(originalText));
    }


    /**
     * 将原始bytes AES加密成字符串
     * @param originalBytes 原始文本的bytes
     * @return Base64编码过的文本
     */
    public String encryptToString(byte[] originalBytes){
        return Base64.encodeBase64String(encrypt(originalBytes));
    }

    /**
     * 将原始bytes AES加密成字符串(URL安全)
     * @param originalBytes 原始文本的bytes
     * @return Base64编码过的文本，可以在GET中传输
     */
    public String encryptToStringURLSafe(byte[] originalBytes){
        return Base64.encodeBase64URLSafeString(encrypt(originalBytes));
    }

    /**
     * 将原始文本AES加密，并返回加密后的字节数组(是Base64 bytes)
     * @param originalText 原始文本
     * @return 加密后的字节数组
     */
    public byte[] encryptString(String originalText){
        return encrypt(originalText.getBytes(CHARSET));
    }

    /**
     * 将Base64后的文本进行AES解密，并返回原始文本
     * @param encryptedBase64 加密后的Base64
     * @return 原始文本
     */
    public String decryptStringToString(String encryptedBase64){
        return new String(decryptString(encryptedBase64), CHARSET);
    }

    /**
     * 将Base64后的文本进行AES解密，并返回原始文本bytes
     * @param encryptedBase64 加密后的Base64
     * @return 原始文本的bytes
     */
    public byte[] decryptString(String encryptedBase64){
        return decrypt(Base64.decodeBase64(encryptedBase64));
    }

    /**
     * 将加密后的bytes解密成原始文本
     * @param encryptedBytes 加密后的bytes(注意不是 base64后的bytes)
     * @return 原始文本
     */
    public String decryptToString(byte[] encryptedBytes){
        return new String(decrypt(encryptedBytes), CHARSET);
    }

    /**
     * 将原始文本bytes AES加密成加密后的bytes
     * @param originalBytes 原始文本bytes
     * @return 加密后的bytes
     */
    public byte[] encrypt(byte[] originalBytes){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.write(originalBytes);
            byte[] padBytes = encode(byteArrayOutputStream.size());
            byteArrayOutputStream.write(padBytes);
            byte[] unencrypted = byteArrayOutputStream.toByteArray();

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return cipher.doFinal(unencrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将加密后的bytes AES解密成原始文本bytes
     * @param encryptedBytes 加密后的bytes
     * @return 原始文本bytes
     */
    public byte[] decrypt(byte[] encryptedBytes){
        if(encryptedBytes == null) {
            return null;
        }
        byte[] original = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);
            if(encryptedBytes.length % 16 != 0){
                return null;
            }
            original = cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(original == null) {
                return null;
            }
            return decode(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static byte[] encode(int count) {
        int amountToPad = 16 - (count % 16);
        char padChr = chr(amountToPad);
        StringBuilder tmp = new StringBuilder();
        for (int index = 0; index < amountToPad; index++) {
            tmp.append(padChr);
        }
        return tmp.toString().getBytes(CHARSET);
    }

    private static byte[] decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    private static char chr(int a) {
        byte target = (byte) (a & 0xFF);
        return (char) target;
    }
}
