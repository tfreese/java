// Created: 18.10.2005
package de.freese.sonstiges.ssl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Date;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Thomas Freese
 */
public final class DateClientMain
{
    public static SSLContext createSSLContext(final String serverKeyStoreFile, final char[] serverKeyStorePassword, final String clientTrustStoreFile,
                                              final char[] clientTrustStorePassword, final char[] certPassword)
            throws Exception
    {
        KeyStore serverKeyStore = KeyStore.getInstance("JKS", "SUN");
        KeyStore clientTrustStore = KeyStore.getInstance("JKS", "SUN");

        SSLContext sslContext = null;

        try (InputStream serverKeyStoreIS = new FileInputStream(serverKeyStoreFile);
             InputStream clientTrustStoreIS = new FileInputStream(clientTrustStoreFile))
        {
            serverKeyStore.load(serverKeyStoreIS, serverKeyStorePassword);
            clientTrustStore.load(clientTrustStoreIS, clientTrustStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            trustManagerFactory.init(clientTrustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            keyManagerFactory.init(serverKeyStore, certPassword);

            sslContext = SSLContext.getInstance("TLSv1.3", "SunJSSE");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        }

        return sslContext;
    }

    public static void main(final String[] argv)
    {
        try
        {
            boolean isSSL = true;
            SocketFactory socketFactory = null;

            if (isSSL)
            {
                // Siehe: de.freese.base.security.ssl.SSLContextFactory
                //
                // SSLContext sslContext = SSLContextFactory.createDefault();
                SSLContext sslContext = createSSLContext("src/main/resources/serverKeyStore", "server-pw".toCharArray(), "src/main/resources/clientTrustStore",
                        "client-pw".toCharArray(), "server1-cert-pw".toCharArray());

                socketFactory = sslContext.getSocketFactory();
            }
            else
            {
                socketFactory = SocketFactory.getDefault();
            }

            try (Socket socket = socketFactory.createSocket("localhost", 3000))
            {
                if (socket instanceof SSLSocket sslSocket)
                {
                    sslSocket.startHandshake();

                    SSLSession session = sslSocket.getSession();
                    System.out.println("Cipher suite in use is " + session.getCipherSuite());
                    System.out.println("Protocol is " + session.getProtocol());
                }

                // get the input and output streams from the SSL connection
                try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()))
                {
                    Date date = (Date) ois.readObject();
                    System.out.print("The date is: " + date);
                }

                // try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()))
                // {
                // }
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private DateClientMain()
    {
        super();
    }
}
