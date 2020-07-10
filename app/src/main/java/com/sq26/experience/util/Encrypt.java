package com.sq26.experience.util;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 可逆式加解密
 */

public class Encrypt {
    public static final String Algorithm_AES = "AES";
    public static final String Algorithm_AES_128 = "AES_128";
    public static final String Algorithm_AES_256 = "AES_256";
    public static final String Algorithm_ARC4 = "ARC4";
    public static final String Algorithm_BLOWFISH = "BLOWFISH";
    public static final String Algorithm_ChaCha20 = "ChaCha20";
    public static final String Algorithm_DES = "DES";
    public static final String Algorithm_DESede = "DESede";
    public static final String Algorithm_RSA = "RSA";
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
    //密码学訊息鑑別碼（MAC）。它可用于验证数据完整性和訊息真伪
    public static final String Modes_Poly1305 = "Poly1305";
    //没有模式
    public static final String Modes_NONE = "NONE";

    //无填充
    public static final String Paddings_NoPadding = "NoPadding";
    public static final String Paddings_ISO10126Padding = "ISO10126Padding";
    public static final String Paddings_PKCS5Padding = "PKCS5Padding";
    public static final String Paddings_PKCS1Padding = "PKCS1Padding";


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
        //是否是公钥
        private boolean isPublicKey = true;
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

        //设置是否是公钥
        public Builder isPublicKey(boolean isPublicKey) {
            this.isPublicKey = isPublicKey;
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
            // 它可以用来从字节数组构造密钥而不必经历（基于提供者的）SecretKeyFactory,
            //此类仅对可以表示为字节数组且没有与之关联的密钥参数（例如DES或Triple DES密钥）的原始秘密密钥有用。
            SecretKeySpec secretKeySpec;
            if (algorithm.equals(Algorithm_ARC4)) {
                secretKeySpec = new SecretKeySpec(key, Modes_ECB);
            } else {
                secretKeySpec = new SecretKeySpec(key, algorithm);
            }

            Log.d("getEncoded", Base64.encodeToString(secretKeySpec.getEncoded(), Base64.DEFAULT));
            try {
                String transformation = algorithm;
                //没有加密模式就不拼模式
                if (Modes != null)
                    transformation = transformation + "/" + Modes;
                //没有填充模式就不拼填充
                if (Paddings != null)
                    transformation = transformation + "/" + Paddings;
                // 获得Cypher实例对象
                Cipher cipher = Cipher.getInstance(transformation);
                switch (algorithm) {
                    case Algorithm_ChaCha20:
                        //加密方式为ChaCha20时无论什么模式都需要设置iv向量,并指定iv向量长度为12
                        cipher.init(opmode, secretKeySpec, new IvParameterSpec(new byte[12]));
                        break;
                    case Algorithm_RSA:
                        KeyFactory keyFactory = KeyFactory.getInstance(Algorithm_RSA);
                        if (isPublicKey) {
                            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
                            try {
                                PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
                                cipher.init(opmode, publicKey);
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            }
                        } else {
                            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
                            try {
                                PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
                                cipher.init(opmode, privateKey);
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        switch (Modes) {
                            case Modes_CBC:
                                //CBC模式需要设置iv向量
                                cipher.init(opmode, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
                                break;
                            default:
                                // 初始化设置加解密模式，并指定密匙
                                cipher.init(opmode, secretKeySpec);
                                break;
                        }
                        break;
                }
                //进行加解密运算
                byte[] bytes = cipher.doFinal(plaintext);
                //判断有没有设置完成后的回调
                if (onComplete != null)
                    //接口回调返回加解密后的bytes数组
                    onComplete.complete(bytes);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }

    }

    //加密完成的回调接口
    public interface OnComplete {
        //加密成功的回调方法
        abstract void complete(byte[] ciphertext);
    }

    //随机生成一个种子(过时的,仅供参考)
    public static byte[] getRawKey() {
        try {
            //SecureRandom类提供了加密强度高的随机数生成器
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


    /**
     * 随机生成一个种子
     *
     * @param iterationCount 迭代次数  不能是0,建议1000
     * @param keyLength      密钥的长度   256位用于AES-256、128位用于AES-128等
     * @param algorithm      密钥的算法      详细信息查看encrypt文件
     * @param password       密码  不能为空和空字符
     * @return 返回一个密钥
     */
    public static byte[] getRawKey(int iterationCount, int keyLength, String algorithm, String password) {
        //盐的长度
        int saltLength = keyLength / 8; // bytes; 应与输出密钥长度大小相同 (256 / 8 = 32)

        /* 首次创建密钥时，请以此作为盐: */
        //加密强度高的随机数生成器
        SecureRandom random = new SecureRandom();
        //创建盐
        byte[] salt = new byte[saltLength];
        //生成用户指定数量的随机字节。
        random.nextBytes(salt);

        /* 使用它从密码中获取密钥: */
        /*PBEKeySpec:
         *PBE密钥规格,可以与基于密码的加密（PBE）一起使用。密码可以看作是某种原始密钥材料，使用它的加密机制可以从中获得密码密钥。
         * password:密码
         * salt:盐
         * iterationCount:迭代次数
         *  keyLength:要派生的密钥长度
         */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        try {
            //此类表示密钥的工厂
            //返回一个SecretKeyFactory对象，该对象将转换指定算法的秘密密钥
            // algorithm加密类型必须和KeySpec的生产工程格式一致,比如PBEKeySpec生产的KeySpec只能指定PBE系列的加密类型
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            //根据提供的密钥规范（密钥材料）生成对象SecretKey。
            return keyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
//        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return null;
    }

    /**
     * 获取生生产RSA密钥的密钥规范（密钥材料）
     *
     * @param keysize 密钥长度,长度是512-2048，一般为1024
     * @return 返回密钥对
     */
    public static KeyPair getRSAKeyPair(int keysize) {
        try {
            //获取密钥材料
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Algorithm_RSA);
            //设置密钥长度
            keyPairGenerator.initialize(1024);
            //生成并返回密钥对
            return keyPairGenerator.genKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getRSAPublicKey(KeyPair keyPair) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        return rsaPublicKey.getEncoded();
    }

    public static byte[] getRSAPrivateKey(KeyPair keyPair) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        return rsaPrivateKey.getEncoded();
    }
}
