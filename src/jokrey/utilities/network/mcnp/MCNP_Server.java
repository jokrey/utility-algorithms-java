package jokrey.utilities.network.mcnp;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jokrey.utilities.network.mcnp.io.ConnectionHandler;
import jokrey.utilities.network.mcnp.io.ConnectionHandler.ConnectionState;

public abstract class MCNP_Server<CT extends ConnectionState> implements AutoCloseable {
    public final int port;
    protected ConnectionHandler<CT> connectionHandler;
    protected final ThreadPoolExecutor pool;
    private boolean running = true;

    public MCNP_Server(int port, int min_threads, int max_threads) {
        this.port = port;
        pool = new ThreadPoolExecutor(min_threads, max_threads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
    public MCNP_Server(int port) {
        this(port, 0, Integer.MAX_VALUE);
    }
    public MCNP_Server(int port, ConnectionHandler<CT> connectionHandler) {
        this(port);
        this.connectionHandler=connectionHandler;
    }
    public MCNP_Server(int port, int min_threads, int max_threads, ConnectionHandler<CT> connectionHandler) {
        this(port, min_threads, max_threads);
        this.connectionHandler=connectionHandler;
    }


    public void stop() {
        running=false;
    }
    public boolean isRunning() {
        return running;
    }

    public abstract boolean isProperlyClosed();


    public void runListenerLoopInThread() {
        new Thread(() -> {
            try {
                runListenerLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public abstract void runListenerLoop() throws IOException;
}
