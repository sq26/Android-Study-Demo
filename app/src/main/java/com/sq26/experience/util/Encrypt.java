package com.sq26.experience.util;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

    public static final String Algorithm_AES = "AES";
    public static final String Algorithm_AES_128 = "AES_128";
    public static final String Algorithm_AES_256 = "AES_256";
    public static final String Algorithm_ARC4 = "ARC4";
    //电码本模式（Electronic Codebook Book (ECB)
    //这种模式是将整个明文分成若干段相同的小段，然后对每一小段进行加密。
    public static final String Modes_ECB = "ECB";
    //密码分组链接模式（Cipher Block Chaining (CBC)）
    //这种模式是先将明文切分成若干小段，然后每一小段与初始块或者上一段的密文段进行异或运算后，再与密钥进行加密
    public static final String Modes_CBC = "CBC";
    //计算器模式（Counter (CTR)）
    //计算器模式不常见，在CTR模式中， 有一个自增的算子，这个算子用密钥加密之后的输出和明文异或的结果得到密文，相当于一次一密。
    // 这种加密方式简单快速，安全可靠，而且可以并行加密，但是在计算器不能维持很长的情况下，密钥只能使用一次。
    public static final String Modes_CTR = "CTR";
    //密码反馈模式（Cipher FeedBack (CFB)）
    public static final String Modes_CFB = "CFB";
    //输出反馈模式（Output FeedBack (OFB)）
    public static final String Modes_OFB = "OFB";
    //无填充
    public static final String Paddings_NoPadding = "NoPadding";
    public static final String Paddings_ISO10126Padding = "ISO10126Padding";
    public static final String Paddings_PKCS5Padding = "PKCS5Padding";

    //构造器类
    public static class Builder {
        //加密算法
        private String algorithm;
        //加密模式
        private String Modes;
        //填充
        private String Paddings;
        //密钥
        private byte[] key;
        //明文
        private byte[] plaintext;
        //模式(加密或解密)
        private int opmode;

        //加密完成的监听
        private OnComplete onComplete;

        //设置加密算法
        public Builder Algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        //设置加密模式
        public Builder Modes(String Modes) {
            this.Modes = Modes;
            return this;
        }

        //设置填充
        public Builder Paddings(String Paddings) {
            this.Paddings = Paddings;
            return this;
        }

        //设置密钥
        public Builder Key(byte[] key) {
            this.key = key;
            return this;
        }

        //设置明文
        public Builder Plaintext(byte[] plaintext) {
            this.plaintext = plaintext;
            return this;
        }

        //设置明文
        public Builder setOpmode(int opmode) {
            this.opmode = opmode;
            return this;
        }

        //设置加密完成的监听
        public Builder setOnComplete(OnComplete onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public void start() {
            // 根据上一步生成的密匙生成一个认证
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);

            Log.d("getEncoded", Base64.encodeToString(secretKeySpec.getEncoded(), Base64.DEFAULT));
            try {
                // 获得Cypher实例对象
                Cipher cipher = Cipher.getInstance(algorithm + "/" + Modes + "/" + Paddings);
                // 初始化模式为加密模式，并指定密匙
                cipher.init(opmode, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));

                byte[] bytes = cipher.doFinal(plaintext);
                if (onComplete != null)
                    onComplete.complete(bytes);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }

    }

    //加密完成的回调接口
    public interface OnComplete {
        //加密成功的回调方法
        abstract void complete(byte[] ciphertext);
    }

    //设置一个种子
    public static byte[] getRawKey() {
        try {
            // 获得一个随机数，传入的参数为默认方式。
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // 设置一个种子，这个种子一般是用户设定的密码。也可以是其它某个固定的字符串
//            secureRandom.setSeed(seed.getBytes());
            // 获得一个key生成器（AES加密模式）
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            // 设置密匙长度128位
            keyGenerator.init(128, secureRandom);
            // 获得密匙
            SecretKey secretKey = keyGenerator.generateKey();
            // 返回密匙的byte数组供加解密使用
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
