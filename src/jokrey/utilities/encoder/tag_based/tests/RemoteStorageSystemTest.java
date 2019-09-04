package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.thread_safe.SynchronizingTagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorage;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorageMCNPCauses;
import jokrey.utilities.transparent_storage.bytes.remote.server.RemoteStorageServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 *
 * @author jokrey
 */
public class RemoteStorageSystemTest {
    private RemoteStorageServer server;
    private LITagBytesEncoder client;

    @Before
    public void initiate() throws IOException {
        server = new RemoteStorageServer(new File(System.getProperty("user.home")+"/Desktop/remote_storage_test.stuff"));
        server.setContent(new byte[] {});
        client = new LITagBytesEncoder(new RemoteStorage("localhost", RemoteStorageMCNPCauses.DEFAULT_SERVER_PORT));
    }

    @After
    public void clean_up() throws IOException {
        server.close();
        ((RemoteStorage)client.getRawStorageSystem()).close();
    }

    @Test
    public void many_threaded_clients_read_test() throws Throwable {
        TagSystemTestHelper.enter_values(client);


        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);

        for(int i=0;i<100;i++) {
            executor.execute(() -> {
                try(RemoteStorage remote_storage = new RemoteStorage("localhost", RemoteStorageMCNPCauses.DEFAULT_SERVER_PORT)) {
                    Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                    SynchronizingTagBasedEncoder<byte[]> thread_local_client =
                            new SynchronizingTagBasedEncoder<>(
                                new LITagBytesEncoder(remote_storage));
                    TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                } catch (IOException | InterruptedException e) {
                    fail("exception thrown in thread. Should not happen");
                    e.printStackTrace();
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


    /**
     * NOTES: THREAD SAFETY
     * This doesn't work yet(mostly), because libae isn't thread safe(and it actually, since this stuff is distributed, would have to be process safe).
     * So libae, distributed, does multiple calls to the storage system. But the storage system may change between calls..
     * So while the storage system may or may not be thread safe, that is not even enough..
     *
     */
//    @Test
    public void concurrent_read_write_test() throws Throwable {
        String tag = "ä";
        byte[] val = new byte[100000];
        for(int i=0;i<val.length;i++)
            val[i] = (byte) TagSystemTestHelper.getRandomNr(0,255);

        Runnable r1_write = () -> client.addEntry_nocheck(tag, val); //actually just checks that it is thread safe
        Runnable r2_write = () -> {
            try {
                Thread.sleep(TagSystemTestHelper.getRandomNr(50, 1000));
                LITagBytesEncoder thread_local_client = new LITagBytesEncoder(new RemoteStorage("localhost", RemoteStorageMCNPCauses.DEFAULT_SERVER_PORT));
                TagSystemTestHelper.do_tag_system_assertions_without_delete(thread_local_client);
                thread_local_client.addEntry_nocheck(tag, val);
                ((RemoteStorage)thread_local_client.getRawStorageSystem()).close();
            } catch (IOException | InterruptedException e) {
                fail("exception thrown in thread. Should not happen");
                e.printStackTrace();
            }
        };

        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);
        for(int i = 0; i < 50; i++)
            executor.execute(r1_write);
        for(int i = 0; i < 50; i++)
            executor.execute(r2_write);

        System.out.println("Waiting for all threads to finish");

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        assertArrayEquals(val, client.getEntry(tag));
    }


    @Test
    public void simple_test_tag_system() {
//        //order, values and type don't matter
//        //just keep in mind that EACH tag has to have a different name!!!!
//
//        TagSystemTestHelper.enter_values(client);
//
//
//        //client can also retrieve
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
//
//
//        //encoded bytes works, test.
//        byte[] encodedArray = client.getEncodedBytes();
//        LITagBytesEncoder decoder = new LITagBytesEncoder(encodedArray);
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(decoder);
//        TagSystemTestHelper.do_tag_system_assertions_delete(decoder);
//
//        //encoded bytes test didn't do anything to client
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
//
//
//
//        //server side can also properly read stuff
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(server);
//
//        //server side test didn't do anything to client
//        TagSystemTestHelper.do_tag_system_assertions_without_delete(client);
//
//        //deleting on server deletes on client
//        assertEquals(client.getEntryT(TagSystemTestHelper.bool_test_tag, boolean.class), TagSystemTestHelper.bool_test_value);
//        assertEquals(server.deleteEntryT(TagSystemTestHelper.bool_test_tag, boolean.class), TagSystemTestHelper.bool_test_value);
//        assertNull(client.getEntryT(TagSystemTestHelper.bool_test_tag, boolean.class));
//
//        //deleting on server deletes on client
//        assertArrayEquals(server.getEntryT(TagSystemTestHelper.intarr_test_tag, int[].class), TagSystemTestHelper.intarr_test_value);
//        assertArrayEquals(client.deleteEntryT(TagSystemTestHelper.intarr_test_tag, int[].class), TagSystemTestHelper.intarr_test_value);
//        assertNull(server.getEntryT(TagSystemTestHelper.intarr_test_tag, boolean.class));
//
//        TagSystemTestHelper.do_tag_system_assertions_delete(client);
//
//
        TagSystemTestHelper.basic_typed_system_test(client);
        System.out.println(Arrays.toString(client.getRawStorageSystem().getContent()));
        System.out.println(Arrays.toString(server.getContent()));

        LITagBytesEncoder litbe = new LITagBytesEncoder();
        litbe.readFromEncoded(client.getRawStorageSystem().getContent());
        TagSystemTestHelper.basic_typed_system_test(litbe);

        TagSystemTestHelper.basic_typed_system_test(new LITagBytesEncoder(server));
//
//        client.readFromEncodedBytes(new byte[]{});
    }

    @Test
    public void do_tag_test_twice() {
        simple_test_tag_system();
        simple_test_tag_system();
    }

    @Test
    public void do_set_content_get_content_content_size_tests() {
        byte[] rand_cont = new byte[] {123,43,54,65,76,87,98,9,12,23,34,115,63,2,1,11,111,32,123,124,109};
        client.getRawStorageSystem().setContent(rand_cont);
        assertArrayEquals(rand_cont, client.getRawStorageSystem().getContent());
        assertEquals(rand_cont.length, client.getRawStorageSystem().contentSize());
        client.getRawStorageSystem().setContent(new byte[] {});
        assertArrayEquals(new byte[] {}, client.getRawStorageSystem().getContent());
        assertEquals(0, client.getRawStorageSystem().contentSize());
    }
}
