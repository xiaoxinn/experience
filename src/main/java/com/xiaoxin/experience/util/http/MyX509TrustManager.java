package com.xiaoxin.experience.util.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
/**
 * @author xiaoxin
 */
public class MyX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException
    {
        // doNothing
    }

    @Override
    public void checkServerTrusted(X509Certificate[] ax509certificate,String s) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }
}
