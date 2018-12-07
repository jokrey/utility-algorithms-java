package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.encoder.tag_based.TagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.thread_safe.SynchronizingTagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.tuple.TupleTagMultiLIEncodersBytes;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.tuple.TupleTagMultiLIEncodersBytesSynchronized;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.tuple.TupleTagSingleLITagEncoderBytes;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.tuple.TupleTagSingleLITagEncoderBytesSynchronized;
import jokrey.utilities.encoder.tag_based.tuple_tag.SynchronizingTupleTagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.tuple_tag.TupleTagBasedEncoderBytes;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * @author jokrey
 */
public class SynchronizedTest {

    @Test
    public void test_normal() throws Throwable {
        synchronized_test(new SynchronizingTagBasedEncoderBytes(new LITagBytesEncoder()));
    }

    @Test
    public void test_tuple_single_NOTTHREADSAFE_fail_check() {
        try {
            TupleTagBasedEncoderBytes mult = new TupleTagSingleLITagEncoderBytes();
//        TupleTagBasedEncoderBytes mult = new SynchronizingTupleTagBasedEncoderBytes(new TupleTagSingleLITagEncoderBytes());
            synchronized_test(mult, "test", "test2", "1", "4", "9");

            synchronized_test(mult.getSubEncoder("test"));
            fail("WOW. This never worked before. It kinda shouldn't. At least not with a high probability");
        } catch (Throwable ignored) {
            //expected
        }
    }

    @Test
    public void test_tuple_multi_NOTTHREADSAFE_fail_check() {
        try {
            TupleTagBasedEncoderBytes mult = new TupleTagMultiLIEncodersBytes();
            synchronized_test(mult, "test", "test2", "1", "4", "9");

            synchronized_test(mult.getSubEncoder("test"));
            fail("WOW. This never worked before. It kinda shouldn't. At least not with a high probability");
        } catch (Throwable ignored) {
            //expected
        }
    }

    @Test
    public void test_tuple_single_synchronizing() throws Throwable {
        TupleTagBasedEncoderBytes mult = new SynchronizingTupleTagBasedEncoderBytes(new TupleTagSingleLITagEncoderBytes());
//        TupleTagBasedEncoderBytes mult = new SynchronizingTupleTagBasedEncoderBytes(new TupleTagSingleLITagEncoderBytes());
        synchronized_test(mult, "test", "test2", "1", "4", "9");

        synchronized_test(mult.getSubEncoder("test"));
    }

    @Test
    public void test_tuple_multi_synchronizing() throws Throwable {
        TupleTagBasedEncoderBytes mult = new SynchronizingTupleTagBasedEncoderBytes(new TupleTagMultiLIEncodersBytes());
        synchronized_test(mult, "test", "test2", "1", "4", "9");

        synchronized_test(mult.getSubEncoder("test"));
    }

    @Test
    public void test_tuple_single_pre_synchronized() throws Throwable {
        TupleTagBasedEncoderBytes mult = new TupleTagSingleLITagEncoderBytesSynchronized();
        synchronized_test(mult, "test", "test2", "1", "4", "9");

        synchronized_test(mult.getSubEncoder("test"));
    }

    @Test
    public void test_tuple_multi_pre_synchronized() throws Throwable {
        TupleTagBasedEncoderBytes mult = new TupleTagMultiLIEncodersBytesSynchronized();
        synchronized_test(mult, "test", "test2", "1", "4", "9");

        synchronized_test(mult.getSubEncoder("test"));
    }

    @Test
    public void test_remote_encoder() throws Throwable {
        RemoteEncoderServer server = new RemoteEncoderServer(10009, new LITagBytesEncoder());
        RemoteEncoderBytes encoder = new RemoteEncoderBytes("localhost", 10009);

        not_synchronized_test(server);
        synchronized_test(server);

        not_synchronized_test(encoder);
        synchronized_test(encoder);

        server.close();
        encoder.close();
    }

    @Test
    public void test_authenticated_remote_encoder_multi() throws Throwable {
        AuthenticatedRemoteEncoderServer server = new AuthenticatedRemoteEncoderServer(10009, new TupleTagMultiLIEncodersBytesSynchronized());
        AuthenticatedRemoteEncoderBytes encoder = AuthenticatedRemoteEncoderBytes.register("localhost", 10009, "x", "b");

        not_synchronized_test(server);
        synchronized_test(server, "test", "test2", "1", "4", "9");

        not_synchronized_test(encoder);
        synchronized_test(encoder);

        server.close();
        encoder.close();
    }

    @Test
    public void test_authenticated_remote_encoder_single() throws Throwable {
        AuthenticatedRemoteEncoderServer server = new AuthenticatedRemoteEncoderServer(10009, new TupleTagSingleLITagEncoderBytesSynchronized());
        AuthenticatedRemoteEncoderBytes encoder = AuthenticatedRemoteEncoderBytes.register("localhost", 10009, "x", "b");

        not_synchronized_test(server);
        synchronized_test(server, "test", "test2", "1", "4", "9");

        not_synchronized_test(encoder);
        synchronized_test(encoder);

        server.close();
        encoder.close();
    }




    private void not_synchronized_test(TagBasedEncoderBytes encoder) {
        System.out.println("tbe not_synchronized_test");
        TagSystemTestHelper.enter_values(encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(encoder);
    }
    private void synchronized_test(TagBasedEncoderBytes encoder) throws Throwable {
        System.out.println("tbe synchronized_test");
        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);

        Random r = new Random();
        for(int i=0;i<1000;i++) {
            executor.execute(() -> {
                if (r.nextBoolean()) encoder.addEntry("tag", new byte[60]);
                if (r.nextBoolean()) encoder.getEntry("tag"); // can return null
                if (r.nextBoolean()) encoder.deleteEntry_noReturn("tag"); //can return false
                if (r.nextBoolean()) encoder.length("tag");
                if (r.nextBoolean()) encoder.exists("tag");
                if (r.nextBoolean()) encoder.addEntryT("tag", new byte[r.nextInt(1000)]);
                if (r.nextBoolean()) encoder.getEntryT("tag", new byte[r.nextInt(1000)]);
                if (r.nextBoolean()) encoder.addEntry("tag2", new byte[60]);
                if (r.nextBoolean()) encoder.clear();
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();

        System.out.println("tags in the end: "+Arrays.toString(encoder.getTags())); //can return one of: ["tag"], ["tag", "tag2"], []
    }


    private void not_synchronized_test(TupleTagBasedEncoderBytes encoder) {
        System.out.println("ttbe not_synchronized_test");
        TagSystemTestHelper.enter_values(encoder.getSubEncoder("x"));
        TagSystemTestHelper.do_tag_system_assertions_delete(encoder.getSubEncoder("x"));
    }
    private void synchronized_test(TupleTagBasedEncoderBytes encoder, String... super_tags) throws Throwable {
        System.out.println("ttbe synchronized_test");
        ConcurrentPoolTester executor = new ConcurrentPoolTester(100);

        Random r = new Random();
        for(int i=0;i<1000;i++) {
            executor.execute(() -> {
                if(r.nextBoolean()) encoder.addEntry(super_tags[r.nextInt(super_tags.length)], "tag", new byte[60]);
                if(r.nextBoolean()) encoder.getEntry(super_tags[r.nextInt(super_tags.length)], "tag"); // can return null
                if(r.nextBoolean()) encoder.deleteEntry_noReturn(super_tags[r.nextInt(super_tags.length)], "tag"); //can return false
                if(r.nextBoolean()) encoder.clear(super_tags[r.nextInt(super_tags.length)]);
                if(r.nextBoolean()) encoder.addEntry(super_tags[r.nextInt(super_tags.length)], "tag2", new byte[60]);
                if(r.nextBoolean()) encoder.clear();
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        executor.throwLatestException();
    }
}
