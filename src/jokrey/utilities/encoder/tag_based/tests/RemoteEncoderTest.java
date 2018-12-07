package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.TagEntryObserver;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoder_Observer;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.remote.encoder.LIRemoteEncoderServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author jokrey
 */
public class RemoteEncoderTest {
    private static int port;
    private static RemoteEncoderServer server;
    private static RemoteEncoderBytes client;
    private static RemoteEncoder_Observer observer;

    @BeforeClass
    public static void initiate() throws IOException {
        port = 39895;
        server = new LIRemoteEncoderServer(port, new File(System.getProperty("user.home")+"/Desktop/remote_encoder_test.stuff"));
        server.clear();
        client = new RemoteEncoderBytes("localhost", port);

        //cross language test
//        port = RemoteEncoderMCNPCauses.DEFAULT_SERVER_PORT;
//        client = new RemoteEncoderBytes("localhost", port);




        TagEntryObserver rul = new TagEntryObserver() {
            @Override public void update_add(String tag) {
                System.out.println("updated_add_for: "+tag);
            }
            @Override public void update_delete(String tag) {
                System.out.println("updated_delete_for: "+tag);
            }
            @Override public void update_set_content() {
                System.out.println("update_set_content: ");
            }
        };
        observer = new RemoteEncoder_Observer("localhost", port);
        observer.addTagEntryObserver(rul);


        //test to see if it is possible to block the server with a fake request..
//        new Thread(() -> {//simple check
//            try {
//                MCNP_ClientIO fake_client = new MCNP_ClientIO("localhost", port, 1000);
//                fake_client.send_cause(RemoteEncoderMCNPCauses.CONNECTION_TYPE__IS_CLIENT);
//                fake_client.send_cause(RemoteEncoderMCNPCauses.ADD_ENTRY_BYTE_ARR);
//                Thread.sleep(100000000);
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        new Thread(() -> {//complex - exploiting inner stream functionality...
//            try {
//                MCNP_ClientIO fake_client = new MCNP_ClientIO("localhost", port, 1000);
//                fake_client.send_cause(RemoteEncoderMCNPCauses.CONNECTION_TYPE__IS_CLIENT);
//                fake_client.send_cause(RemoteEncoderMCNPCauses.ADD_ENTRY_BYTE_ARR);
//                fake_client.send_variable_chunk_bytearr("fake_tag".getBytes(StandardCharsets.UTF_8));
//                fake_client.start_variable(13);
//                byte[] to_short_array = new byte[12];
//                fake_client.send_fixed_chunk_bytes(to_short_array);
//                Thread.sleep(100000000);
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    @AfterClass
    public static void clean_up() throws IOException {
        client.close();
        observer.close();
        if(server!=null)server.close();
        System.out.println("cleaned");
    }

    @Test
    public void keep_it_simple() {
        client.readFromEncodedBytes(new byte[0]);
//        client.addEntryT("1", 1);
//        client.addEntryT("2", (byte)1);
//        client.addEntryT("3", 1234234234212L);
        TimeDiffMarker.setMark_d();
        byte[] long_arr = new byte[1000000];
        for(int i=0;i<long_arr.length;i++)
            long_arr[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);
        client.addEntry("4", long_arr);
//        TimeDiffMarker.println_setMark_d("long array, add");
//        client.addEntryT("4", (byte)90);
//        TimeDiffMarker.println_d("long array, replace");
//        TagSystemTestHelper.enter_values(client);
//        assertEquals(new Integer(1), client.getEntry_int("1"));
//        assertEquals(new Byte((byte) 90), client.getEntry_byte("4"));
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
//        if(server!=null)
//            TagSystemTestHelper.do_tag_system_assertions_without_delete(server);
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
//        TagSystemTestHelper.do_tag_system_assertions_delete(client);
//        TagSystemTestHelper.enter_values(client);
    }

    @Test
    public void do_simple_observer_test() throws IOException {
        String test_tag = "asdasdasjghkghkghjkgjhkd";

        RemoteEncoder_Observer observer = new RemoteEncoder_Observer("localhost", port);
        TagEntryObserver rul = new TagEntryObserver() {
            @Override public void update_add(String tag) {
                assertEquals(test_tag, tag);
            }
            @Override public void update_delete(String tag) {
                assertEquals(test_tag, tag);
            }
            @Override public void update_set_content() {
                fail("should not be reached in context");
            }
        };
        observer.addTagEntryObserver(rul);
        client.addEntryT(test_tag, "doesnt even matter tho");
        client.addEntryT(test_tag, "doesnt even matter tho");
        client.addEntryT(test_tag, "doesnt even matter tho");
        client.deleteEntry(test_tag);
        client.addEntryT(test_tag, "doesnt even matter tho");
        client.deleteEntry(test_tag);
        client.addEntryT(test_tag, "doesnt even matter tho");
        client.deleteEntry(test_tag);

        observer.removeTagEntryObserver(rul);

        client.addEntryT("something else, that is not the test tag, therefore the observer should not get it...", "doesnt even matter tho");

        observer.close();

        client.deleteEntry("something else");
    }

    @Test
    public void enter_values_nocheck_test() {

//        RemoteEncoderBytes client = new RemoteEncoderBytes("localhost", port);
        client.addEntryT_nocheck("this is a bool", true);
        client.addEntryT_nocheck("this is a byte", (byte) -123);
        client.addEntryT_nocheck("this is an int", -1234567890);
        client.addEntryT_nocheck("this is a long", 12341242146536L);
        client.addEntryT_nocheck("this is a string", "sup. I'm a string");

        client.addEntryT_nocheck("this is a bool arr", new boolean[] {false, true, true, false});
        byte[] arr = new byte[999999];
        for(int i=0;i<arr.length;i++) arr[i] = (byte)i;
        client.addEntryT_nocheck("this is a byte arr", arr);
        client.addEntryT_nocheck("this is a int arr", new int[] {1235, 77777, 9000, -73219});
        client.addEntryT_nocheck("this is a long arr", new long[] {12350912309129L, 77777, 9000, -7321954277123451291L});
        client.addEntryT_nocheck("this is a string arr", new String[] {"sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string"});

        client.addEntryT_nocheck("this is a string 2d arr",
                new String[][] {
                new String[] {"1",",2","324234fa"},
                new String[] {"sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string", "sup. I'm a string"},
                new String[] {"1",",2","324234fa",",2","324234fa",",2","324234fa",",2","324234fa"}});
//        client.close();

    }

    @Test
    public void enter_values_test() {

//        RemoteEncoderBytes client = new RemoteEncoderBytes("localhost", port);
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


    @Test
    public void many_threaded_clients_read_test() throws Throwable {
        TagSystemTestHelper.enter_values(client);


        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);

        for(int i=0;i<100;i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                    RemoteEncoderBytes thread_local_client = new RemoteEncoderBytes("localhost", port);
                    TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                    thread_local_client.close();
                } catch (IOException | InterruptedException e) {
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


//        client.readFromEncodedBytes(new byte[]{});
    }


    @Test
    public void concurrent_read_write_test() throws Throwable {
        String tag = "ä";
        byte[] val = new byte[100000];
        for(int i=0;i<val.length;i++)
            val[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);

        TagSystemTestHelper.enter_values(client);
//        Runnable r1_write = () -> client.addEntryT(tag, val); //actually just checks that it is thread safe
        Runnable r2_write = () -> {
            try {
                Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                RemoteEncoderBytes thread_local_client = new RemoteEncoderBytes("localhost", port);
                TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                thread_local_client.addEntry(tag, val);
                thread_local_client.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                fail();
            }
        };

        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);
        for(int i = 0; i < 50; i++)
            executor.execute(r2_write);
        for(int i = 0; i < 50; i++)
            executor.execute(r2_write);

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        assertArrayEquals(val, client.getEntry(tag));
    }

    @Test
    public void concurrent_read_write_thread_safe_test() throws Throwable {
        String tag = "ä";
        byte[] val = new byte[100000];
        for(int i=0;i<val.length;i++)
            val[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);

        TagSystemTestHelper.enter_values(client);
        Runnable r1_write = () -> {//actually just checks that it is thread safe
            client.addEntry(tag, val);
            assertArrayEquals(val, client.getEntry(tag));
        };
        Runnable r2_write = () -> {
            try {
                Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                RemoteEncoderBytes thread_local_client = new RemoteEncoderBytes("localhost", port);
                TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                thread_local_client.addEntry(tag, val);
                thread_local_client.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                fail();
            }
        };

        ConcurrentPoolTester executor = new ConcurrentPoolTester(20);
        for(int i = 0; i < 15; i++)
            executor.execute(r1_write);
        for(int i = 0; i < 5; i++)
            executor.execute(r2_write);

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        System.out.println("fin");
        assertArrayEquals(val, client.getEntry(tag));
    }

    @Test
    public void concurrent_random_consistency_test() throws Throwable {
        TagEntryObserver rul = new TagEntryObserver() {
            @Override public void update_add(String tag) {
                System.out.println("rnd test_updated_add_for: "+tag);
            }
            @Override public void update_delete(String tag) {
                System.out.println("rnd test_updated_delete_for: "+tag);
            }
            @Override public void update_set_content() {
                System.out.println("rnd test_update_set_content: ");
            }
        };
        RemoteEncoder_Observer[] observers = new RemoteEncoder_Observer[20];
        for(int i=0;i<observers.length;i++) {
            observers[i] = new RemoteEncoder_Observer("localhost", port);
            observers[i].addTagEntryObserver(rul);
        }


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
                        Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                        RemoteEncoderBytes thread_local_client = new RemoteEncoderBytes("localhost", port);
                        TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                        thread_local_client.addEntry(tag, val);
                        thread_local_client.addEntryT("test", 12);
                        thread_local_client.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        fail();
                    }
                    break;
                case 3:
                    try {
                        Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                        RemoteEncoderBytes thread_local_client = new RemoteEncoderBytes("localhost", port);
                        TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                        thread_local_client.addEntry(tag, val);
                        thread_local_client.deleteEntry("test");
                        thread_local_client.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        fail();
                    }
                    break;
                case 4:
                    try {
                        Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                        if(server!=null) {
                            server.addEntry(tag, val);
                            server.deleteEntry(tag);
                            server.getEntry(tag);
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

        //after all that commotion, normal behavior is still available. Even if the exact state is now pretty undefined
        client.addEntry(tag, val);
        assertArrayEquals(val, client.getEntry(tag));
        assertArrayEquals(val, client.deleteEntry(tag));
        assertNull(client.deleteEntry(tag));

        if(server!=null) {
            assertArrayEquals(server.getTags(), client.getTags()); //of course..
            System.out.println(Arrays.toString(server.getTags()));
        }

//        Thread.sleep(10000);

        for (RemoteEncoder_Observer observer1 : observers) {
            observer1.close();
        }
    }


    @Test
    public void simple_test_tag_system() {
        //order, values and type don't matter
        //just keep in mind that EACH tag has to have a different name!!!!

        TagSystemTestHelper.enter_values(client);

        if(server!=null)
            TagSystemTestHelper.do_tag_system_assertions_without_delete(server);

        //client can also retrieve
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);


        //encoded bytes works, test.
        byte[] encodedArray = client.getEncodedBytes();
        LITagBytesEncoder decoder = new LITagBytesEncoder(encodedArray);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(decoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(decoder);

        //encoded bytes test didn't do anything to client
        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);



        if(server!=null) {
            //server side can also properly read stuff
            TagSystemTestHelper.do_tag_system_assertions_without_delete(server);

            //server side test didn't do anything to client
            TagSystemTestHelper.do_tag_system_assertions_without_delete(client);

            //deleting on server deletes on client
            assertEquals(client.getEntryT(TagSystemTestHelper.bool_test_tag, boolean.class), TagSystemTestHelper.bool_test_value);
            assertEquals(server.deleteEntryT(TagSystemTestHelper.bool_test_tag, boolean.class), TagSystemTestHelper.bool_test_value);
            assertNull(client.getEntryT(TagSystemTestHelper.bool_test_tag, boolean.class));

            //deleting on server deletes on client
            assertArrayEquals(server.getEntryT(TagSystemTestHelper.intarr_test_tag, int[].class), TagSystemTestHelper.intarr_test_value);
            assertArrayEquals(client.deleteEntryT(TagSystemTestHelper.intarr_test_tag, int[].class), TagSystemTestHelper.intarr_test_value);
            assertNull(server.getEntryT(TagSystemTestHelper.intarr_test_tag, boolean.class));
        }

        TagSystemTestHelper.do_tag_system_assertions_delete(client);

        TagSystemTestHelper.basic_typed_system_test(client);
        TagSystemTestHelper.basic_typed_system_test(server);

//        client.readFromEncodedBytes(new byte[]{});
    }

    @Test
    public void do_tag_test_again_and_again() {
        for(int i=0;i<3;i++)
            simple_test_tag_system();
    }

    @Test
    public void do_set_content_get_content_content_size_tests() {
        byte[] rand_cont = new byte[] {123,43,54,65,76,87,98,9,12,23,34,115,63,2,1,11,111,32,123,124,109};
        client.readFromEncodedBytes(rand_cont);
        assertArrayEquals(rand_cont, client.getEncodedBytes());
        client.readFromEncodedBytes(new byte[] {});
        assertArrayEquals(new byte[] {}, client.getEncodedBytes());
    }
}
