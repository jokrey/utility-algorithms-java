package jokrey.utilities.network.mcnp;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.network.mcnp.io.CauseHandlerMap;
import jokrey.utilities.network.mcnp.io.ConnectionHandler.ConnectionState;
import jokrey.utilities.network.mcnp.io.MCNP_ClientIO;
import jokrey.utilities.network.mcnp.io.MCNP_ConnectionIO;
import jokrey.utilities.network.mcnp.io.MCNP_ServerIO;
import jokrey.utilities.network.mcnp.nbio.AsyncCauseHandlerMap;
import jokrey.utilities.network.mcnp.nbio.MCNP_ConnectionAIO;
import jokrey.utilities.network.mcnp.nbio.MCNP_ServerAIO;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class Test {
    @org.junit.Test
    public void busy_test_blocking_io() throws Throwable {
        int port = 17731;
        MCNP_ServerIO<ConnectionState> server = new MCNP_ServerIO<>(port, 0, Integer.MAX_VALUE);
        initiate(server);
        runBusyConcurrentClientsTest(port, 100, 150, 500);
        server.close();
    }
    @org.junit.Test
    public void busy_test_blocking_aio() throws Throwable {
        int port = 17732;
        MCNP_ServerAIO<ConnectionState> server = new MCNP_ServerAIO<>(port, 0, 10);
        initiate(server);
        runBusyConcurrentClientsTest(port, 100, 150, 500);
        server.close();
    }


    @org.junit.Test
    public void many_idle_test_blocking_io() throws Throwable {
        int port = 17731;
        MCNP_ServerIO<ConnectionState> server = new MCNP_ServerIO<>(port, 0, Integer.MAX_VALUE);
        initiate(server);
        runManyConcurrentClientsTest(port, 7, 2000, 1, Integer.MAX_VALUE, 1);
        server.close();
    }
    @org.junit.Test
    public void many_idle_test_blocking_aio() throws Throwable {
        int port = 17732;
        MCNP_ServerAIO<ConnectionState> server = new MCNP_ServerAIO<>(port, 0, 10);
        initiate(server);
        runManyConcurrentClientsTest(port, 7, 2000, 1, Integer.MAX_VALUE, 1);
        server.close();
    }

//    @org.junit.Test
    public void cross_platform_client_only_test() throws Throwable {
        runBusyConcurrentClientsTest(17731, 100, 200, 10);
    }










    public static void initiate(MCNP_ServerAIO<ConnectionState> server) {
        server.setConnectionHandler(new AsyncCauseHandlerMap<ConnectionState>() {
            @Override public void newConnection(int initial_cause, MCNP_ConnectionAIO connection, InteractionCompletedCallback<ConnectionState> callback) {
                try {
//                    System.out.println("OK, handling c. First we expect the cause.");
//                    int cause = c.read_cause();
//                    System.out.println("cause: " + initial_cause);

                    if (initial_cause == -11) {
                        System.out.println("OK, Apparently the clients cause is to instantly disconnect again.");
                    } else if (initial_cause == 1) {
                        byte b = connection.receive_byte();
                        connection.send_byte(b);
                    } else {
                        System.out.println("Cause not recognised. Please check the clients configuration, it doesn't seem to know what we want.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                callback.completed(new ConnectionState(initial_cause));
            }

            {
                add_handler(1, 12, (connection, state, callback) -> {
                    connection.expect_fixed(1, (c, doesntmatter) -> connection.expect_fixed(4, (conn, i32_bs) -> {
                        int i32 = connection.getTransformer().detransform_int(i32_bs);
                        connection.expect_fixed(8, (conn12, doesntmatter2) -> {
                            connection.expect_fixed(8, (conn13, doesntmatter3) -> {
                                connection.write_fixed(conn.getTransformer().transform(i32), null, new CompletionHandler<Integer, Object>() {
                                    @Override public void completed(Integer result, Object attachment) {
                                        callback.completed(state);
                                    }
                                    @Override public void failed(Throwable exc, Object attachment) { }
                                });
                            });
                        });
                    }));
                });
                add_handler(1, 8, (connection, state, callback) -> {
                    connection.expect_variable((conn14, bytes) -> {
                        connection.write_variable(bytes, null, new CompletionHandler<Integer, Object>() {
                            @Override public void completed(Integer result, Object attachment) {
                                connection.expect_variable((conn1, utf8_bs) -> {
                                    connection.send_utf8(conn1.getTransformer().detransform_string(utf8_bs));

                                    byte[] hopefully_null = connection.receive_variable();
                                    if(hopefully_null == null)
                                        callback.completed(state);
                                });
                            }
                            @Override public void failed(Throwable exc, Object attachment) { }
                        });
                    });
                });
            }
        });

        server.runListenerLoopInThread();
    }
    public static void initiate(MCNP_ServerIO<ConnectionState> server) {
        server.setConnectionHandler(new CauseHandlerMap<ConnectionState>() {
            @Override public ConnectionState newConnection(int initial_cause, MCNP_ConnectionIO connection) {
                try {
//                    System.out.println("OK, handling c. First we expect the cause.");
//                    int cause = c.read_cause();
//                    System.out.println("cause: " + initial_cause);

                    if (initial_cause == -11) {
                        System.out.println("OK, Apparently the clients cause is to instantly disconnect again.");
                    } else if (initial_cause == 1) {
                        byte b = connection.receive_byte();
                        connection.send_byte(b);
                    } else {
                        System.out.println("Cause not recognised. Please check the clients configuration, it doesn't seem to know what we want.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return new ConnectionState(initial_cause);
            }

            @Override public void connectionDropped(MCNP_ConnectionIO conn, ConnectionState state, boolean eof) {
                System.err.println("connection dropped: "+conn);
            }
            @Override public void connectionDroppedWithError(Throwable t, MCNP_ConnectionIO conn, ConnectionState state) { t.printStackTrace(); }

            {
                add_handler(1, 12, (connection, state) -> {
                    byte b = connection.receive_byte();
                    int i32 = connection.receive_int32();
                    long i64 = connection.receive_int64();
                    double f64 = connection.receive_float64();
                    connection.send_int32(i32);
                    return state;
                });
                add_handler(1, 8, (connection, state) -> {
                    byte[] bytes = connection.receive_variable();
                    connection.send_variable(bytes);
//                    byte[] bytes2 = c.receive_variable();
                    String utf8_str = connection.receive_utf8();
                    connection.send_utf8(utf8_str);
                    byte[] hopefully_null = connection.receive_variable();
                    return state;
                });
            }
        });

        server.runListenerLoopInThread();
    }



    public static void do_client_init(MCNP_Connection client) throws IOException {
        client.send_cause(1);

        byte bb = (byte) 98;
        client.send_byte(bb);
        assertEquals(bb, client.receive_byte());
    }
    public static void do_client_test(MCNP_Connection client) throws IOException {
        client.send_cause(12);
        byte b = (byte) -12;
        int i32 = Integer.MIN_VALUE+54321;
        long i64 = 30;
        double f64 = -123456789.2344521;
        client.send_byte(b);
        client.send_int32(i32);
        client.send_int64(i64);
        client.send_float64(f64);
//        System.out.println("Send: "+"byte:("+b+"), int32:("+i32+"), int64:("+i64+"), float64:("+f64+")");

        int i32_back = client.receive_int32();
        assertEquals(i32, i32_back);

        client.send_cause(8);
        //sending variable chunk, in one
        byte[] bytes_of_variable_length = {12, 123,124,53,87};//add or remove anything here
        client.send_variable(bytes_of_variable_length);

        byte[] bytes_back = client.receive_variable();
        assertArrayEquals(bytes_of_variable_length, bytes_back);
//        System.out.println("Send: "+ Arrays.toString(bytes_of_variable_length));

        //Sending variable chunk, in parts
//        byte[] bytes_of_variable_length1 = {123,124,125,52,42,32,123,124,125,52,42,32,123,124,125};//add or remove anything here
//        byte[] bytes_of_variable_length2 = {-123,124,125,-52,42,-32,-123,-124,-125,52,-42,32,-123,124,125};//add or remove anything here
//        client.start_variable(bytes_of_variable_length1.length + bytes_of_variable_length2.length);
//        client.send_fixed(bytes_of_variable_length1);
//        client.send_fixed(bytes_of_variable_length2);

        //Send String of variable length
        String s = "Hallo, dies ist ein Test! Dies ist n test.";
        client.send_utf8(s);
//        System.out.println("Send: "+ s);

        String s_back = client.receive_utf8();
        assertEquals(s, s_back);

        client.send_variable(null);
    }


    private void runBusyConcurrentClientsTest(int port, int nThreads, int nClients, int serviceCallIterations) throws Throwable {
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nClients;i++) {
            pool.execute(() -> {
                try {
                    MCNP_ClientIO client = new MCNP_ClientIO("localhost", port, 3000);
                    do_client_init(client);
                    for(int r=0;r<serviceCallIterations;r++)
                        do_client_test(client);
                    client.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        pool.throwLatestException();
    }

    private void runManyConcurrentClientsTest(int port, int nThreads, int clientsPerThread, int serviceCallIterationsPerClient, int serviceCallWaitEvery, int serviceCallWaitTime) throws Throwable {
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            Thread.sleep(i*100);
            pool.execute(() -> {
                try {
                    MCNP_ClientIO[] clients = new MCNP_ClientIO[clientsPerThread];
                    for(int ci=0;ci<clients.length;ci++) {
                        clients[ci] = new MCNP_ClientIO("localhost", port, 3000);

                        do_client_init(clients[ci]);
                    }
                    for(int ci=0;ci<clients.length;ci++) {
                        for(int r=0;r<serviceCallIterationsPerClient;r++) {
                            do_client_test(clients[ci % clients.length]);
                        }
                        if(ci%serviceCallWaitEvery == 0) {
                            Thread.sleep(serviceCallWaitTime);
                        }
                    }
                    for(MCNP_ClientIO client:clients)
                        client.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        pool.throwLatestException();
    }
}
