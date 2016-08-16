package com.u.http2alpntesting;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by saguilera on 8/16/16.
 */
public class TLSSocketFactory extends SSLSocketFactory {

    private Object sslParameters; //Do not change name. This uses reflection lol
    private SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();

        try {
            Field field = delegate.getClass().getDeclaredField("sslParameters");
            field.setAccessible(true);
            Object value = field.get(delegate);

            Class<?> clazz;
            try {
                clazz = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
            } catch (ClassNotFoundException e) {
                // Older platform before being unbundled.
                clazz = Class.forName(
                        "org.apache.harmony.xnet.provider.jsse.SSLParametersImpl");
            }

            sslParameters = clazz.cast(value);
        } catch (NoSuchFieldException ignored) {
        } catch (ClassNotFoundException e1) {
        } catch (IllegalAccessException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(socket, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if (socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket) socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
        }

        return socket;
    }

}
