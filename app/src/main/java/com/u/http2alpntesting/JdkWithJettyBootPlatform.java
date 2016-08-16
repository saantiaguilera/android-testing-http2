package com.u.http2alpntesting;

import android.support.annotation.NonNull;

import org.eclipse.jetty.alpn.ALPN;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import javax.net.ssl.SSLSocket;
import okhttp3.Protocol;
import okhttp3.internal.Platform;
import okhttp3.internal.Util;
import sun.security.ssl.ALPNExtension;

/**
 * OpenJDK 7 or OpenJDK 8 with {@code org.mortbay.jetty.alpn/alpn-boot} in the boot class path.
 */
class JdkWithJettyBootPlatform extends Platform {

    @Override
    public void configureTlsExtensions(
            final SSLSocket sslSocket, String hostname, final List<Protocol> protocols) {
        final List<String> names = alpnProtocolNames(protocols);

        NegotiationProvider provider = new NegotiationProvider(names);
        provider.attachSslSocket(sslSocket);

        ALPN.put(sslSocket, provider);
    }

    @Override
    public void afterHandshake(SSLSocket sslSocket) {
        try {
            ALPN.remove(sslSocket);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public String getSelectedProtocol(SSLSocket socket) {
        try {
            NegotiationProvider provider = (NegotiationProvider) ALPN.get(socket);

            if (provider.isUnsupported() && provider.getSelectedProtocol() == null) {
                Platform.get().log(INFO, "ALPN callback dropped: SPDY and HTTP/2 are disabled. "
                        + "Is alpn-boot on the boot class path?", null);
                return null;
            }
            return provider.isUnsupported() ? null : provider.getSelectedProtocol();
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    public static Platform buildIfSupported() {
        // Find Jetty's ALPN extension for OpenJDK. And the methods. This will tell us if exists or not what we need.
        try {
            String negoClassName = "org.eclipse.jetty.alpn.ALPN";
            Class<?> negoClass = Class.forName(negoClassName);
            Class<?> providerClass = Class.forName(negoClassName + "$Provider");
            Class.forName(negoClassName + "$ClientProvider");
            negoClass.getMethod("put", SSLSocket.class, providerClass);
            negoClass.getMethod("get", SSLSocket.class);
            negoClass.getMethod("remove", SSLSocket.class);
            return new JdkWithJettyBootPlatform();
        } catch (Exception ignored) {
        }

        return null;
    }

    private class NegotiationProvider implements ALPN.ClientProvider {

        private boolean unsupported;
        private String selected;

        private List<String> protocols;

        //TODO Use weak ref
        private SSLSocket socket;

        public NegotiationProvider(@NonNull List<String> protocols) {
            this.protocols = protocols;
            this.unsupported = true;
            this.selected = null;
            this.socket = null;
        }

        public void attachSslSocket(@NonNull SSLSocket socket) {
            this.socket = socket;
        }

        @Override
        public List<String> protocols() {
            return protocols;
        }

        @Override
        public void unsupported() {
            ALPN.remove(socket);
            unsupported = true;
        }

        @Override
        public void selected(String protocol) {
            ALPN.remove(socket);
            selected = protocol;
            System.out.println("Protocol Selected is: " + protocol);
        }

        public boolean isUnsupported() {
            return unsupported;
        }

        public String getSelectedProtocol() {
            return selected;
        }

    }

}

