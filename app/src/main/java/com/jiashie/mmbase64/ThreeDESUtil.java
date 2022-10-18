package com.jiashie.mmbase64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ThreeDESUtil {

    // 定义加密算法，有DES、DESede(即3DES)、Blowfish
    private static final String ALGORITHM = "DESede";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 加密方法
     * @param key 密钥
     * @param plainText 源数据的字节数组
     * @return
     */
    public static String encrypt(String key, String plainText) {
        try {
            SecretKey deskey = new SecretKeySpec( build3DesKey( key), ALGORITHM);
            Cipher c1 = Cipher.getInstance( ALGORITHM);
            c1.init( Cipher.ENCRYPT_MODE, deskey);
            return byte2hex( c1.doFinal( plainText.getBytes( DEFAULT_CHARSET)));
        } catch(java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch(javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch(Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密函数
     * @param key 密钥
     * @param encodeStr 密文的字节数组
     * @return
     */
    public static String decrypt(String key, String encodeStr) {
        try {
            SecretKey deskey = new SecretKeySpec( build3DesKey( key), ALGORITHM);
            Cipher c1 = Cipher.getInstance( ALGORITHM);
            c1.init( Cipher.DECRYPT_MODE, deskey);
            return new String( c1.doFinal( hex2byte( encodeStr.getBytes( DEFAULT_CHARSET))));
        } catch(java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch(javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch(Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /*
     * 根据字符串生成密钥字节数组
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes(StandardCharsets.UTF_8);

        System.arraycopy(temp, 0, key, 0, Math.min(key.length, temp.length));
        return key;
    }

    private static byte[] hex2byte(byte[] b) {
        if((b.length % 2) != 0) {
            throw new IllegalArgumentException( "length must be even number");
        }
        byte[] b2 = new byte[b.length / 2];
        for(int n = 0; n < b.length; n += 2) {
            String item = new String( b, n, 2);
            b2[n / 2] = (byte)Integer.parseInt( item, 16);
        }

        return b2;
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static String byte2hex(byte[] bytes) {
        int n = bytes.length;
        char[] hexChars = new char[n * 2];
        int v;
        for (int j = 0; j < n; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}