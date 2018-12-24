package com.ziroom.connect.mqtt.util;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @Author:zhangbo
 * @Date:2018/5/14 15:35
 */
public class SslUtils {


    public static SSLSocketFactory getSocketFactory(String caCrt,String cert,String key) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // 加载CA证书
        Certificate caCert = CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream(caCrt));

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // 加载客户端证书
        Certificate clientCert = CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream(cert));

        //加载客户端私钥
        PemReader reader = new PemReader(
                new InputStreamReader(new FileInputStream(key)));
        PrivateKey clientKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(reader.readPemObject().getContent()));

        // client key and certificates are sent to server so it can authenticate client
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", clientCert);
        ks.setKeyEntry("private-key", clientKey, "".toCharArray(),
                new Certificate[]{clientCert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "".toCharArray());


        // create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return context.getSocketFactory();
    }

}
