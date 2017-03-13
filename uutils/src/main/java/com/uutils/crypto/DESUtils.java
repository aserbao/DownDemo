package com.uutils.crypto;

import com.uutils.utils.UriUtils;

import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {
    private final static String DES = "DES";
    public static final String SIGN_POST_KEY = "abifb!@#*&^";

    /***
     * 获取key
     *
     * @param k
     * @return
     */
    private static String getKey(String k) {
        return ByteUtils.toHexString(k.getBytes());
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) {
        return encrypt(data, key, true);
    }

    public static String encrypt(String data, String key, boolean isHexKey) {
        byte[] keys = key.getBytes();
        if (isHexKey) {
            keys = getKey(key).getBytes();
        }
        byte[] bt = encrypt(data.getBytes(), keys);
        byte[] encodeBase64 = Base64.encode(bt);
        return new String(encodeBase64);

    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key, boolean isHexKey) {
        if (data == null) return null;
        byte[] keys = key.getBytes();
        if (isHexKey) {
            keys = getKey(key).getBytes();
        }
        byte[] encodeBase64 = Base64.decode(data.getBytes());
        byte[] bt = decrypt(encodeBase64, keys);
        return new String(bt);
    }

    public static String decrypt(String data, String key) {
        return decrypt(data, key, true);
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    @SuppressWarnings("GetInstance")
    private static byte[] encrypt(byte[] data, byte[] key) {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密钥数据创建DESKeySpec对象
        byte[] result = data;
        try {
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            result = cipher.doFinal(data);
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    @SuppressWarnings("GetInstance")
    private static byte[] decrypt(byte[] data, byte[] key) {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        byte[] result = data;
        try {
            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密钥初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            result = cipher.doFinal(data);
        } catch (Exception e) {

        }
        return result;
    }

    public static String encryptServerData(String data, String key) {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        byte[] encodeBase64 = Base64.getEncoder().encode(bt);
        return new String(encodeBase64);
    }

    public static String decryptServerData(String data, String key) {
        if (data == null) return null;
        byte[] keys = key.getBytes();
        byte[] encodeBase64 = Base64.getDecoder().decode(data.getBytes());
        byte[] bt = decrypt(encodeBase64, keys);
        return new String(bt);
    }
}