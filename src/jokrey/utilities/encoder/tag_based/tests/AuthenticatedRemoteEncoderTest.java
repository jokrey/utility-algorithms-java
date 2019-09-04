package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderMCNPCauses;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoder_Observer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteUpdateListener;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.remote.encoder.authenticated.LIAuthenticatedRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.remote.encoder.authenticated.LIAuthenticatedRemoteEncoderServer_MultiFile;
import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import jokrey.utilities.network.mcnp.io.MCNP_ClientIO;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author jokrey
 */
public class AuthenticatedRemoteEncoderTest {
    @Test
    public void multi_file() throws Throwable {
        String url = "localhost";
        int port = 45268;

        File directory = new File(System.getProperty("user.home")+"/Desktop/areb_test_java_server/");
        directory.mkdirs();
        AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer_MultiFile(port, directory);
        server.clear();
        AuthenticatedRemoteEncoderBytes client = AuthenticatedRemoteEncoderBytes.register(url, port, "java", "java");
        run(server, client, url, port);
    }

    @Test
    public void single_file() throws Throwable {
        String url = "localhost";
        int port = 45267;

        AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer(port, new File(System.getProperty("user.home")+"/Desktop/areb_test_java_server.litbe"));
        server.clear();
        AuthenticatedRemoteEncoderBytes client = AuthenticatedRemoteEncoderBytes.register(url, port, "java", "java");
        run(server, client, url, port);
    }
    
    public void run(AuthenticatedRemoteEncoderServer server, AuthenticatedRemoteEncoderBytes client, String url, int port) throws Throwable {
        initiate(server, url, port);
        enter_values_test(client);
        keep_it_simple(client);
        x_many_threaded_clients_read_test(server, client, url, port);
        x_concurrent_read_write_thread_safe_test(client, url, port);
        x_concurrent_random_consistency_test(server, client, url, port);
        do_tag_test_again_and_again(client);
        simple_test_tag_system(server, client);

        clean_up(server, client);
    }

    public static void initiate(AuthenticatedRemoteEncoderServer server, String url, int port) {
//        server = new LIAuthenticatedRemoteEncoderServer_MultiFile(port, new File(System.getProperty("user.home")+"/Desktop/areb_test_java_server.litbe"));
//        File directory = new File(System.getProperty("user.home")+"/Desktop/areb_test_java_server/");
//        directory.mkdirs();
//        server = new LIAuthenticatedRemoteEncoderServer_MultiFile(port, directory);
//        client = AuthenticatedRemoteEncoderBytes.register(url, port, "java", "java");

        //cross language test
//        port = AuthenticatedRemoteEncoderMCNPCauses.DEFAULT_SERVER_PORT;
//        client = AuthenticatedRemoteEncoderBytes.register(url, port, "java", "java");



        AuthenticatedRemoteUpdateListener rul = new AuthenticatedRemoteUpdateListener() {
            @Override public void update_add(String tag) {
                System.out.println("updated_add_for: "+tag);
            }
            @Override public void update_delete(String tag) {
                System.out.println("updated_delete_for: "+tag);
            }
            @Override public void update_unregister() {
                System.out.println("update unregister: ");
            }
            @Override public void update_set_content() {
                System.out.println("update clear: ");
            }
        };
        AuthenticatedRemoteEncoder_Observer observer = new AuthenticatedRemoteEncoder_Observer(url, port, "java", "java");
        observer.addTagEntryObserver(rul);


//        test to see if it is possible to block the server with a fake request..
        //even though this might work, it still definitely is(send valid). Which is an issue.
        new Thread(() -> {//simple check
            try {
                MCNP_ClientIO fake_client = new MCNP_ClientIO(url, port, 1000);
                fake_client.send_cause(RemoteEncoderMCNPCauses.ADD_ENTRY_BYTE_ARR);
                Thread.sleep(100000000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {//complex - exploiting inner stream functionality...
            try {
                MCNP_ClientIO fake_client = new MCNP_ClientIO(url, port, 1000);
                fake_client.send_cause(RemoteEncoderMCNPCauses.ADD_ENTRY_BYTE_ARR);
                fake_client.send_variable("fake_tag".getBytes(StandardCharsets.UTF_8));
                fake_client.start_variable(13);
                byte[] to_short_array = new byte[12];
                fake_client.send_fixed(to_short_array);
                Thread.sleep(100000000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void clean_up(AuthenticatedRemoteEncoderServer server, AuthenticatedRemoteEncoderBytes client) throws IOException {
        client.unregister();
        if(server!=null)server.close();
//        observer.close();
    }



    
    public void enter_values_test(AuthenticatedRemoteEncoderBytes client) {

//        RemoteEncoderBytes client = new RemoteEncoderBytes(url, port);
        client.addEntryT("this is a bool", true);
        client.addEntryT("this is a byte", (byte) -123);
        client.addEntryT("this is an int", -1234567890);
        client.addEntryT("this is a long", 12341242146536L);
        client.addEntryT("this is a string", "sup. I'm a string");

        client.addEntryT("this is a bool arr", new boolean[] {false, true, true, false});
        byte[] arr = new byte[999999];
        for(int i=0;i<arr.length;i++) arr[i] = (byte)i;
        client.addEntry("this is a byte arr", arr);
        client.addEntryT("this is a int arr", new int[] {1235, 77777, 9000, -73219});
        client.addEntryT("this is a long arr", new long[] {12350912309129L, 77777, 9000, -7321954277123451291L});
        client.addEntryT("this is a string arr", new String[] {"sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string"});

        client.addEntryT("this is a string 2d arr",
                new String[][] {
                        new String[] {"1",",2","324234fa"},
                        new String[] {"sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string"},
                        new String[] {"1",",2","324234fa",",2","324234fa",",2","324234fa",",2","324234fa"}});
//        client.close();

    }

    
    public void keep_it_simple(AuthenticatedRemoteEncoderBytes client) {
        client.addEntryT("1", 1);
        client.addEntryT("2", (byte)1);
        client.addEntryT("3", 1234234234212L);
        TimeDiffMarker.setMark_d();
        byte[] long_arr = new byte[1000000];
        for(int i=0;i<long_arr.length;i++)
            long_arr[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);
        client.addEntry("4", long_arr);
        TimeDiffMarker.println_setMark_d("long array, add");
        client.addEntryT("4", (byte)90);
        TimeDiffMarker.println_d("long array, replace");
        TagSystemTestHelper.enter_values(client);
        assertEquals(new Integer(1), client.getEntryT("1", int.class));
        assertEquals(new Byte((byte) 90), client.getEntryT("4", byte.class));
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
        TagSystemTestHelper.do_tag_system_assertions_delete(client);
        TagSystemTestHelper.enter_values(client);
    }


    @Test
    public void concurrent_same_user_logins_registers_writes_and_unregisters() throws Throwable {
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer(10054, new ByteArrayStorage())) {
//            for(int i=0;i<10000;i++)
//                new MCNP_ClientIO("localhost", 10054, 1000000);
            concurrent_same_user_logins_registers_writes_and_unregisters(server, "localhost", 10054);
        }
    }
    public void concurrent_same_user_logins_registers_writes_and_unregisters(AuthenticatedRemoteEncoderServer server, String url, int port) throws Throwable {
        String user_name = "-u-s-e-r";
        String correct_pw = "p-w";
        String wrong_pw = "p+w";

        TagSystemTestHelper.enter_values(server);  //enter some value to be validated at the very end
                                                   //no super tag in there equals -u-s-e-r
        byte[] fake_key =  {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15, 0};
        server.addEntry("passwords", "other user", fake_key);

        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);//if putting 1000 here, then sometimes the server returns "c refused" - might be java server socket limitation

        for(int i=0;i<1000;i++) {
            executor.execute(() -> {
                AuthenticatedRemoteEncoderBytes thread_local_client;
                try {
                    switch (TagSystemTestHelper.getRandomNr(0, 4)) {
                        case 0: //registering always works
                            thread_local_client = AuthenticatedRemoteEncoderBytes.register(url, port, user_name, correct_pw);
                            thread_local_client.close();
                            break;
                        case 1: //correct login and adding works
                            try {
                                thread_local_client = AuthenticatedRemoteEncoderBytes.login(url, port, user_name, correct_pw);
                                TagSystemTestHelper.enter_values(thread_local_client);
                                for(int ind=0;ind<100;ind++)
                                    thread_local_client.addEntry("1", new byte[420]);
                                thread_local_client.getEntry("1");  //can be null already, just do not assert
                                thread_local_client.deleteEntry("1");  //can return null, do not assert
                                thread_local_client.close();
                            } catch (StorageSystemException e) {
                                assertEquals("wrong name", e.getMessage());//only exception that should be thrown
                            }
                            break;
                        case 2: //wrong login always fails
                            try {
                                AuthenticatedRemoteEncoderBytes.login(url, port, user_name, wrong_pw);
                                fail("wrong password still allowed login");
                            } catch (StorageSystemException e) {
                                assertTrue(e.getMessage().equals("wrong name") || e.getMessage().equals("wrong pw"));
                            }
                            break;
                        case 3: //unregistering always works
                            try {
                                thread_local_client = AuthenticatedRemoteEncoderBytes.login(url, port, user_name, correct_pw);
                                thread_local_client.unregister();
                            } catch (StorageSystemException e) {
                                assertEquals("wrong name", e.getMessage());//only exception that should be thrown
                            }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("exception thrown in thread. Should not happen: "+e.getMessage());
                }
            });
        }

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();


        TagSystemTestHelper.do_tag_system_assertions_without_delete(server);  //validate values added before all the bonanza
        assertArrayEquals(fake_key, server.getEntry("passwords", "other user"));
    }
    
    public void x_many_threaded_clients_read_test(AuthenticatedRemoteEncoderServer server, AuthenticatedRemoteEncoderBytes client, String url, int port) throws Throwable {
        TagSystemTestHelper.enter_values(client);


        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);

        for(int i=0;i<100;i++) {
            executor.execute(() -> {
                try {
                    AuthenticatedRemoteEncoderBytes thread_local_client = AuthenticatedRemoteEncoderBytes.login(url, port, "java", "java");
                    TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                    thread_local_client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("exception thrown in thread. Should not happen");
                }
            });
        }

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        //everything still fine
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);

        if(server!=null)
            assertArrayEquals(server.getTags("java"), client.getTags());

//        client.readFromEncodedBytes(new byte[]{});
    }




    
    public void x_concurrent_read_write_thread_safe_test(AuthenticatedRemoteEncoderBytes client, String url, int port) throws Throwable {
        String tag = "ä";
        byte[] val = new byte[1025];
        for(int i=0;i<val.length;i++)
            val[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);

        TagSystemTestHelper.enter_values(client);
        Runnable r1_write = () -> {//actually just checks that it is thread safe
            client.addEntry(tag, val);
//            assertArrayEquals(val, client.getEntry(tag));
        };
        Runnable r2_write = () -> {
            try {
                AuthenticatedRemoteEncoderBytes thread_local_client = AuthenticatedRemoteEncoderBytes.login(url, port, "java", "java");
                TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                thread_local_client.addEntry(tag, val);
                thread_local_client.close();
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        };

        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);
        for(int i = 0; i < 75; i++)
            executor.execute(r1_write);
        for(int i = 0; i < 25; i++)
            executor.execute(r2_write);

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        assertArrayEquals(val, client.getEntry(tag));
    }

    
    public void x_concurrent_random_consistency_test(AuthenticatedRemoteEncoderServer server, AuthenticatedRemoteEncoderBytes client, String url, int port) throws Throwable {
        String tag = "ä";
        byte[] val = new byte[100000];
        for(int i=0;i<val.length;i++)
            val[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);

        TagSystemTestHelper.enter_values(client);

        Runnable r1_write = () -> {//actually just checks that it is thread safe
            switch (TagSystemTestHelper.getRandomNr(0, 4)) {
                case 0:
                    client.addEntry(tag, val);
                    break;
                case 1:
                    client.deleteEntry(tag);
                    break;
                case 2:
                    try {
                        AuthenticatedRemoteEncoderBytes thread_local_client = AuthenticatedRemoteEncoderBytes.register(url, port, "java", "java");
                        TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                        thread_local_client.addEntry(tag, val);
                        thread_local_client.addEntryT("test", 12);
                        thread_local_client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Failing, because test threw an unexpected exception: "+e.getMessage());
                    }
                    break;
                case 3:
                        try {
                            //Illegal access confusion, doesn't do anything to bad
                            final RemoteEncoderBytes thread_local_bs_client = new RemoteEncoderBytes(url, port);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(250);
                                    thread_local_bs_client.close();
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                            thread_local_bs_client.addEntry(tag, val);
                            thread_local_bs_client.deleteEntry("asdasd");
                            thread_local_bs_client.deleteEntry("test");
                            thread_local_bs_client.addEntry(tag, val);
                            thread_local_bs_client.deleteEntry("2");
                            thread_local_bs_client.deleteEntry("test");
                            thread_local_bs_client.addEntry("344", val);
                            thread_local_bs_client.deleteEntry("344");
                            thread_local_bs_client.deleteEntry("test");
                            thread_local_bs_client.addEntry("555", val);
                            thread_local_bs_client.deleteEntry("555");
                            thread_local_bs_client.deleteEntry("test");
                            Thread.sleep(250);
                            fail();
                        } catch (Exception ignored) {
                            //expected
                        }
                    break;
                case 4:
                    try {
                        Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                        if (server != null) {
                            server.addEntry(tag, tag, val);
                            server.deleteEntry(tag, tag);
                            server.getEntry(tag, tag);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        fail();
                    }
                }
        };

        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);
        for(int i = 0; i < 100; i++)
            executor.execute(r1_write);

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        //after all that commotion, normal behavior is still available. Even if the exact state is now pretty hard to pre determine
        boolean was_replaced = client.addEntry(tag, val);
        System.out.println("concurrent addEntry 0: "+tag+" - "+was_replaced);
        assertArrayEquals(val, client.getEntry(tag));
        System.out.println("concurrent delete 1: "+tag);
        assertArrayEquals(val, client.deleteEntry(tag));
        System.out.println("concurrent delete 2: "+tag);
        byte[] entry = client.deleteEntry(tag);
        assertNull(entry); //double delete equals null is an important check. It checks that no entry was wrongly double added (addEntry behaving as addEntry_noCheck)

    }


    
    public static void simple_test_tag_system(AuthenticatedRemoteEncoderServer server, AuthenticatedRemoteEncoderBytes client) {
        simple_test_tag_system(client);
        if(server!=null)
            TagSystemTestHelper.basic_typed_system_test(server.getSubEncoder("java"));
    }
    public static void simple_test_tag_system(String url, int port, String non_registered_user) {
        simple_test_tag_system(AuthenticatedRemoteEncoderBytes.register(url, port, non_registered_user, non_registered_user));
    }
    public static void simple_test_tag_system(AuthenticatedRemoteEncoderBytes client) {
        //order, values and type don't matter
        //just keep in mind that EACH tag has to have a different name!!!!

        TagSystemTestHelper.enter_values(client);

//        System.out.println(Arrays.toString(server.getTags("java")));
//        for(TaggedEntry<byte[]> e:server) {
//            System.out.println("tag: "+e.tag);
//            System.out.println("val: "+ Arrays.toString(e.val));
//        }

        //client can also retrieve
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);

        TagSystemTestHelper.do_tag_system_assertions_delete(client);


        TagSystemTestHelper.basic_typed_system_test(client);

//        client.readFromEncodedBytes(new byte[]{});
    }

    
    public void do_tag_test_again_and_again(AuthenticatedRemoteEncoderBytes client) {
        for(int i=0;i<3;i++)
            simple_test_tag_system(client);
    }
}
