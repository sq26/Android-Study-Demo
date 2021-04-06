## Android-Study-Demo

Android 相关的功能实现的代码块

### 功能

对称加密和非对称加密
授权申请

### 对称加密和非对称加密

使用示列

```下沉
        Encrypt()
            .Algorithm("AES")//加密算法
            .Key(byte[])//密钥
            .Modes("CBC")//加密模式
            .Paddings("PKCS5Padding")//填充模式
            .isPublicKey(true)//是否是公钥(非对称加密区分公钥和私钥,默认值true)
            .Plaintext(byte[])//内容,要加密或解密的内容
            .setOpmode(Cipher.ENCRYPT_MODE)//Cipher.ENCRYPT_MODE加密,Cipher.DECRYPT_MODE解密
            .setOnComplete { it->
                //完成后的回调
                ciphertext = Base64.encodeToString(it, Base64.DEFAULT)
            }.start()

```

### 授权申请

使用示列

```     
        //context必须是ComponentActivity的子类,比如AppCompatActivity
        //数组形式权限列表
        JPermissions(context,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                     Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .success {
                AlertDialog.Builder(context).setMessage("申请成功").setPositiveButton("确定", null).show()
            }
            .failure { successList, failure, noPrompt ->
                AlertDialog.Builder(context).setTitle("申请失败")
                    .setMessage("成功的权限:$successList\n失败的权限:$failure\n被永久拒绝的权限:$noPrompt")
                    .setPositiveButton("确定", null)
                    .setNegativeButton("打开权限设置页面") { _, _ ->
                        JPermissions.openSettings(context)
                    }
                    .show()
            }
            .start()

```

