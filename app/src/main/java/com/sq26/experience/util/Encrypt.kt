package com.sq26.experience.util

import android.util.Base64
import android.util.Log
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 可逆式加解密
 */
class Encrypt {
    companion object {
        private val Algorithm_ARC4 = "ARC4"
        private val Algorithm_ChaCha20 = "ChaCha20"
        private val Algorithm_RSA = "RSA"
        private val Modes_ECB = "ECB"
        private val Modes_NONE = "NONE"
        private val Modes_Poly1305 = "Poly1305"

        /**
         * 随机生成一个种子
         * @param iterationCount 迭代次数  不能是0,建议1000
         * @param keyLength      密钥的长度   256位用于AES-256、128位用于AES-128等
         * @param algorithm      密钥的算法      详细信息查看encrypt文件
         * @param password       密码  不能为空和空字符
         * @return 返回一个密钥
         */
        fun getRawKey(
            iterationCount: Int,
            keyLength: Int,
            algorithm: String?,
            password: String
        ): ByteArray? {
            //盐的长度
            val saltLength = keyLength / 8 // bytes; 应与输出密钥长度大小相同 (256 / 8 = 32)

            /* 首次创建密钥时，请以此作为盐: */
            //加密强度高的随机数生成器
            val random = SecureRandom()
            //创建盐
            val salt = ByteArray(saltLength)
            //生成用户指定数量的随机字节。
            random.nextBytes(salt)

            /* 使用它从密码中获取密钥: */
            /*PBEKeySpec:
             *PBE密钥规格,可以与基于密码的加密（PBE）一起使用。密码可以看作是某种原始密钥材料，使用它的加密机制可以从中获得密码密钥。
             * password:密码
             * salt:盐
             * iterationCount:迭代次数
             *  keyLength:要派生的密钥长度
             */
            val keySpec: KeySpec =
                PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength)
            try {
                //此类表示密钥的工厂
                //返回一个SecretKeyFactory对象，该对象将转换指定算法的秘密密钥
                // algorithm加密类型必须和KeySpec的生产工程格式一致,比如PBEKeySpec生产的KeySpec只能指定PBE系列的加密类型
                val keyFactory = SecretKeyFactory.getInstance(algorithm)
                //根据提供的密钥规范（密钥材料）生成对象SecretKey。
                return keyFactory.generateSecret(keySpec).encoded
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: InvalidKeySpecException) {
                e.printStackTrace()
            }
            //        SecretKey key = new SecretKeySpec(keyBytes, "AES");
            return null
        }

        /**
         * 获取生生产RSA密钥的密钥规范（密钥材料）
         * @param keysize 密钥长度,长度是512-2048，一般为1024
         * @return 返回密钥对
         */
        fun getRSAKeyPair(keySize: Int): KeyPair {
//            try {
                //获取密钥材料
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                //设置密钥长度
                keyPairGenerator.initialize(keySize)
                //生成并返回密钥对
                return keyPairGenerator.genKeyPair()
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//            }
//            return null
        }

        //获取RSA公钥
        fun getRSAPublicKey(keyPair: KeyPair): ByteArray {
            val rsaPublicKey = keyPair.public as RSAPublicKey
            return rsaPublicKey.encoded
        }

        //获取RSA私钥
        fun getRSAPrivateKey(keyPair: KeyPair): ByteArray {
            val rsaPrivateKey = keyPair.private as RSAPrivateKey
            return rsaPrivateKey.encoded
        }
    }

    // 获得一个随机数，传入的参数为默认方式。
    // 设置一个种子，这个种子一般是用户设定的密码。也可以是其它某个固定的字符串
//            secureRandom.setSeed(seed.getBytes());
    // 获得一个key生成器（AES加密模式）
    // 设置密匙长度128位
    // 获得密匙
    // 返回密匙的byte数组供加解密使用
    //随机生成一个种子(过时的,仅供参考)
//    val rawKey: ByteArray?
//        get() {
//            try {
//                //SecureRandom类提供了加密强度高的随机数生成器
//                // 获得一个随机数，传入的参数为默认方式。
//                val secureRandom = SecureRandom.getInstance("SHA1PRNG")
//                // 设置一个种子，这个种子一般是用户设定的密码。也可以是其它某个固定的字符串
////            secureRandom.setSeed(seed.getBytes());
//                // 获得一个key生成器（AES加密模式）
//                val keyGenerator = KeyGenerator.getInstance("AES")
//                // 设置密匙长度128位
//                keyGenerator.init(128, secureRandom)
//                // 获得密匙
//                val secretKey = keyGenerator.generateKey()
//                // 返回密匙的byte数组供加解密使用
//                return secretKey.encoded
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//            }
//            return null
//        }

    //加密算法
    private var algorithm: String? = null

    //加密模式
    private var Modes: String? = null

    //填充
    private var Paddings: String? = null

    //密钥
    private var key: ByteArray? = null

    //是否是公钥
    private var isPublicKey = true

    //明文
    private var plaintext: ByteArray? = null

    //模式(加密或解密)
    private var opmode = 0

    //加密完成的监听
    private lateinit var onComplete: (ByteArray) -> Unit

    //设置加密算法
    fun Algorithm(algorithm: String?): Encrypt {
        this.algorithm = algorithm
        return this
    }

    //设置加密模式
    fun Modes(Modes: String?): Encrypt {
        this.Modes = Modes
        return this
    }

    //设置填充
    fun Paddings(Paddings: String?): Encrypt {
        this.Paddings = Paddings
        return this
    }

    //设置密钥
    fun Key(key: ByteArray): Encrypt {
        this.key = key
        return this
    }

    //设置是否是公钥
    fun isPublicKey(isPublicKey: Boolean): Encrypt {
        this.isPublicKey = isPublicKey
        return this
    }

    //设置明文
    fun Plaintext(plaintext: ByteArray): Encrypt {
        this.plaintext = plaintext
        return this
    }

    //设置加密模式
    fun setOpmode(opmode: Int): Encrypt {
        this.opmode = opmode
        return this
    }

    //设置加密完成的监听
    fun setOnComplete(block: (ByteArray) -> Unit): Encrypt {
        this.onComplete = block
        return this
    }

    fun start() {
        // 它可以用来从字节数组构造密钥而不必经历（基于提供者的）SecretKeyFactory,
        //此类仅对可以表示为字节数组且没有与之关联的密钥参数（例如DES或Triple DES密钥）的原始秘密密钥有用。
        val secretKeySpec: SecretKeySpec = if (algorithm == Algorithm_ARC4) {
            SecretKeySpec(key, Modes_ECB)
        } else {
            SecretKeySpec(key, algorithm)
        }
        Log.d("getEncoded", Base64.encodeToString(secretKeySpec.encoded, Base64.DEFAULT))
        try {
            var transformation = algorithm
            //没有加密模式就不拼模式
            if (Modes != null) transformation = "$transformation/$Modes"
            //没有填充模式就不拼填充
            if (Paddings != null) transformation = "$transformation/$Paddings"
            // 获得Cypher实例对象
            val cipher = Cipher.getInstance(transformation)
            //判断算法,部分算法需要特殊调整
            when (algorithm) {
                Algorithm_ChaCha20 ->
                    //加密方式为ChaCha20时无论什么模式都需要设置iv向量,并指定iv向量长度为12
                    cipher.init(opmode, secretKeySpec, IvParameterSpec(ByteArray(12)))
                Algorithm_RSA -> {
                    //加密算法为RSA
                    val keyFactory = KeyFactory.getInstance(Algorithm_RSA)
                    //需要根据公钥和私钥走不同的方法
                    if (isPublicKey) {
                        //公钥的获取方法
                        val x509EncodedKeySpec = X509EncodedKeySpec(key)
                        try {
                            val publicKey = keyFactory.generatePublic(x509EncodedKeySpec)
                            cipher.init(opmode, publicKey)
                        } catch (e: InvalidKeySpecException) {
                            e.printStackTrace()
                        }
                    } else {
                        //私钥的获取方法
                        val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(key)
                        try {
                            val privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec)
                            cipher.init(opmode, privateKey)
                        } catch (e: InvalidKeySpecException) {
                            e.printStackTrace()
                        }
                    }
                }
                //其他情况根据加密模式走其他设置
                else -> when (Modes) {
                    Modes_ECB, Modes_NONE, Modes_Poly1305 ->
                        // 初始化设置加解密模式，并指定密匙
                        cipher.init(opmode, secretKeySpec)
                    else ->
                        //CBC,CFB,CTR,CTS,OFB,GCM模式需要设置iv向量
                        cipher.init(
                            opmode,
                            secretKeySpec,
                            IvParameterSpec(ByteArray(cipher.blockSize))
                        )
                }
            }
            //进行加解密运算
            val bytes = cipher.doFinal(plaintext)
            //判断有没有设置完成后的回调
            onComplete.invoke(bytes)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }
}