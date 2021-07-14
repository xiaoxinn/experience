package com.xiaoxin.experience.util.http;

import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * @author xiaoxin
 */
public class HttpsHandler extends URLStreamHandler {

    @Override
    protected java.net.URLConnection openConnection(URL u) throws IOException
    {
        return openConnection(u, (Proxy)null);
    }

}
