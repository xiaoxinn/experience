package com.xiaoxin.experience.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/20
 */
public class M3u8Util {

    private static final Logger log = LoggerFactory.getLogger(M3u8Util.class);

    private M3u8Util(){}

    public static byte[] decrypt(String iv, String key,byte[] encryptBytes)
    {
        if (!StringUtil.validate(key))
        {
            log.debug("the file not encrypt");
            return encryptBytes;
        }
        Security.addProvider(new BouncyCastleProvider());
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            byte[] ivByte;
            if (StringUtil.validate(iv))
            {
                if (iv.startsWith("0x"))
                {
                    ivByte = hexStringToByteArray(iv.substring(2));
                } else {
                    ivByte = iv.getBytes();
                }
                if (ivByte.length != 16)
                {
                    ivByte = new byte[16];
                }
            }
            else
            {
                ivByte = new byte[16];
            }

            //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(encryptBytes, 0, encryptBytes.length);
        }
        catch (Exception e)
        {
            log.error("decrypt fail : ",e);
            return new byte[0];
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if ((len & 1) == 1) {
            s = "0" + s;
            len++;
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
