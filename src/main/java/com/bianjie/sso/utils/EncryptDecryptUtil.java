package com.bianjie.sso.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import javax.crypto.Cipher;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EncryptDecryptUtil {

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static final String ALGORITHM = "RSA";

    private static EncryptDecryptUtil single_instance = null;

    public static EncryptDecryptUtil getInstance() {
        if (single_instance == null)
            single_instance = new EncryptDecryptUtil();
        return single_instance;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateStr 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateStr) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        //Base64解码加密后的字符串
        byte[] decode = Base64.getUrlDecoder().decode(str);
        //将PEM转为私钥PrivateKey
        PrivateKey privateKey1 = getRSAPrivateFromPemFormat(privateStr);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey1);
        byte[] plainText = cipher.doFinal(decode);
        return new String(plainText);
    }


    //私钥进行数字签名
    public static String privateSignature(String text, String keyStr) throws Exception {
        String resultText = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            //将PEM转为私钥PrivateKey
            PrivateKey privateKey = getRSAPrivateFromPemFormat(keyStr);

            byte[] encoded = privateKey.getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);
            signature.update(text.getBytes());
            byte[] sign = signature.sign();

            //对签名进行Base64加密
            resultText = Base64.getUrlEncoder().encodeToString(sign);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return resultText;
    }

    //PEM转化为私钥PrivateKey
    private static PrivateKey getRSAPrivateFromPemFormat(String keyStr) throws Exception {
        return (PrivateKey) getKeyFromPEMString(keyStr, data -> new PKCS8EncodedKeySpec(data), (kf, spec) -> {
            try {
                return kf.generatePrivate(spec);
            } catch (InvalidKeySpecException e) {
                System.out.println("Cannot generate PrivateKey from String : " + keyStr + e);
                return null;
            }
        });
    }

    //PEM转化为公钥PublicKey
    private static PublicKey getRSAPublicFromPemFormat(String keyStr) throws Exception {
        return (PublicKey) getKeyFromPEMString(keyStr, data -> new X509EncodedKeySpec(data), (kf, spec) -> {
            try {
                return kf.generatePublic(spec);
            } catch (InvalidKeySpecException e) {
                System.out.println("Cannot generate PublicKey from String : " + keyStr + e);
                return null;
            }
        });
    }

    private static Key getKeyFromPEMString(String key, Function<byte[], EncodedKeySpec> buildSpec,
                                           BiFunction<KeyFactory, EncodedKeySpec, ? extends Key> getKey) throws Exception {
        try {
            // Read PEM Format
            PemReader pemReader = new PemReader(new StringReader(key));
            PemObject pemObject = pemReader.readPemObject();
            pemReader.close();

            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            return getKey.apply(kf, buildSpec.apply(pemObject.getContent()));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}


