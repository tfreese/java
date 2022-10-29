// Created: 18.10.2005
package de.freese.sonstiges.ssl;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * @author Thomas Freese
 */
public class DateServer extends Thread
{
    /**
     * @author Thomas Freese
     */
    private static class Connect implements Runnable
    {
        /**
         *
         */
        private final Socket clientSocket;
        // /**
        // *
        // */
        // private ObjectInputStream ois;
        /**
         *
         */
        private ObjectOutputStream oos;

        /**
         * Creates a new Connect object.
         *
         * @param clientSocket {@link Socket}
         */
        public Connect(final Socket clientSocket)
        {
            this.clientSocket = clientSocket;

            try
            {
                // this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
                this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
            }
            catch (Exception ex)
            {
                try
                {
                    this.clientSocket.close();
                }
                catch (Exception ex1)
                {
                    System.out.println(ex1.getMessage());
                }
            }
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            if (this.clientSocket.isClosed() || (this.oos == null))
            {
                return;
            }

            try
            {
                System.out.println("DateServer.Connect.run()");
                this.oos.writeObject(new Date());
                this.oos.flush();

                // close streams and connections
                // this.ois.close();
                this.oos.close();
                this.clientSocket.close();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }
    }

    /**
     * @param argv String[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static void main(final String[] argv) throws Exception
    {
        DateServer server = new DateServer();
        server.start();
    }

    /**
     *
     */
    private final ServerSocket serverSocket;

    /**
     * Creates a new DateServer object.
     *
     * @throws Exception Falls was schiefgeht.
     */
    public DateServer() throws Exception
    {
        super();

        boolean isSSL = true;
        ServerSocketFactory serverSocketFactory = null;

        if (isSSL)
        {
            // SSLContext sslContext = SSLContextFactory.createDefault();
            SSLContext sslContext = DateClient.createSSLContext("src/main/resources/serverKeyStore", "server-pw".toCharArray(),
                    "src/main/resources/clientTrustStore", "client-pw".toCharArray(), "server1-cert-pw".toCharArray());

            serverSocketFactory = sslContext.getServerSocketFactory();
        }
        else
        {
            serverSocketFactory = ServerSocketFactory.getDefault();
        }

        this.serverSocket = serverSocketFactory.createServerSocket(3000);

        if (this.serverSocket instanceof SSLServerSocket s)
        {
            s.setNeedClientAuth(true);
        }

        System.out.println("Server listening on port 3000.");
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        while (true)
        {
            System.out.println("Waiting for connections.");

            try
            {
                Socket clientSocket = this.serverSocket.accept();
                System.out.println("Accepted a connection from: " + clientSocket.getInetAddress());

                Runnable connect = new Connect(clientSocket);
                ForkJoinPool.commonPool().execute(connect);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
