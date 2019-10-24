package jokrey.utilities.network.mcnp.io;

import jokrey.utilities.network.mcnp.MCNP_Connection;
import jokrey.utilities.network.mcnp.MCNP_Server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author jokrey
 */
public class MCNP_ServerIO<CT extends ConnectionHandler.ConnectionState> extends MCNP_Server<CT> implements AutoCloseable {
    private ServerSocket serverSocket;

    public MCNP_ServerIO(int port) {
        super(port);
    }
    public MCNP_ServerIO(int port, ConnectionHandler<CT> connectionHandler) {
        super(port, connectionHandler);
    }
    /**Should not be used, too few max_threads may cause max_number_of_concurrent_connections with blocking io*/
    public MCNP_ServerIO(int port, int min_threads, int max_threads) {
        super(port, min_threads, max_threads);
    }
    /**Should not be used, too few max_threads may cause max_number_of_concurrent_connections with blocking io*/
    public MCNP_ServerIO(int port, int min_threads, int max_threads, ConnectionHandler<CT> connectionHandler) {
        super(port, min_threads, max_threads, connectionHandler);
    }

    @Override public void close() throws IOException {
        stop();
        serverSocket.close();
    }
    @Override public boolean isProperlyClosed() {
        return !isRunning() && serverSocket.isClosed();
    }

    public void setConnectionHandler(ConnectionHandler<CT> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void runListenerLoop() throws IOException {
        if(connectionHandler == null) throw new IllegalStateException("No c handler set.");

        serverSocket = new ServerSocket(port);

        System.out.println("SERVER READY");
        System.out.println("port: "+port);
        System.out.println("serverSocket.getLocalPort: "+serverSocket.getLocalPort());

        try {
            while (isRunning() && !serverSocket.isClosed()) {
                Socket newConnection = serverSocket.accept();

                Runnable r = () -> {
                    MCNP_ConnectionIO conn = null;
                    CT state = null;
                    try {
                        conn = new MCNP_ConnectionIO(newConnection);
                        int initial_cause = conn.receive_cause();
                        state = connectionHandler.newConnection(initial_cause, conn);
                        while (!conn.isClosed()) {
                            int cause = conn.receive_cause();
                            state = connectionHandler.handleInteraction(new ConnectionHandler.TypedCause(initial_cause, cause), conn, state);
                        }
                        connectionHandler.connectionDropped(conn, state, false);
                    } catch (EOFException eof) {
//                        eof.printStackTrace();
                        connectionHandler.connectionDropped(conn, state, true);
                    } catch(SocketException t) {
                        if(t.getMessage().equals("Socket closed"))
                            connectionHandler.connectionDropped(conn, state, true);
                        else
                            connectionHandler.connectionDroppedWithError(t, conn, state);
                    } catch (Throwable t) {
                        connectionHandler.connectionDroppedWithError(t, conn, state);
                    } finally {
                        if(conn != null)
                            conn.tryClose();
                    }
                };
                new Thread(r).start();
//                pool.execute(r);  //this - weirdly - does not work(even with
            }
            System.out.println("server socket closing");

        } catch (IOException e) {
            if(isRunning()) throw e;
        } finally {
            if(!isProperlyClosed())
                close();
        }
    }

    public void handleExternalInitializedConnection(CT stateG, int initial_cause, MCNP_ConnectionIO conn) {
        new Thread(() -> {
            CT state = stateG;
            try {
                while (!conn.isClosed()) {
                    int cause = conn.receive_cause();
                    state = connectionHandler.handleInteraction(new ConnectionHandler.TypedCause(initial_cause, cause), conn, state);
                }
                connectionHandler.connectionDropped(conn, state, false);
            } catch (EOFException eof) {
                connectionHandler.connectionDropped(conn, state, true);
            } catch (Throwable t) {
                connectionHandler.connectionDroppedWithError(t, conn, state);
            } finally {
                if(conn != null)
                    conn.tryClose();
            }
        }).start();
    }
}
