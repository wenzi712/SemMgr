/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.qihoo<br/>
 * <b>文件名：</b>Encrypt.java<br/>
 * <b>日期：</b>2019-5-21-下午11:42:33<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.qihoo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * <b>类名称：</b>Encrypt<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-21 下午11:42:33<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class Encrypt {
    /**
     * AES 加密 密钥是apiSecret 的前16位 向量是apiSecret 的后16位
     * 
     * @param password
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String getAesEncrypt(String password, String apiSecret) throws Exception {
        String key = apiSecret.substring(0, 16);
        String iv = apiSecret.substring(16);
        String md5Pass = getMd5(password);

        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(md5Pass.getBytes("UTF-8"));

        return bytesToHexString(encrypted);
    }

    /**
     * 
     * @param s
     *            :要进行加密的字符串
     * @return 字符串的md5值
     */
    public static String getMd5(String s) {
        char hexChar[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
                'C', 'D', 'E', 'F' };
        // md5加密算法的加密对象为字符数组，这里是为了得到加密的对象
        byte[] b = s.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(b);
            byte[] b2 = md.digest();// 进行加密并返回字符数组
            char str[] = new char[b2.length << 1];
            int len = 0;
            // 将字符数组转换成十六进制串，形成最终的密文
            for (int i = 0; i < b2.length; i++) {
                byte val = b2[i];
                str[len++] = hexChar[(val >>> 4) & 0xf];
                str[len++] = hexChar[val & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String bytesToHexString(byte[] bs) {
        StringBuffer sb = new StringBuffer();
        String hex = "";
        for (int i = 0; i < bs.length; i++) {
            hex = Integer.toHexString(bs[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
