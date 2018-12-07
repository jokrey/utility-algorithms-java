package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.TagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.cached.CachedTagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoder_Observer;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorage;
import jokrey.utilities.transparent_storage.bytes.remote.server.RemoteStorageServer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;

/**
 * @author jokrey
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CachedTagBasedEncoderBytes_Test {
    @Test
    public void a0_lame_attempt_to_remove_class_loader_impact_on_measurement() {
        a1_simple_tag_system_test_ram_bytes();
    }

    @Test
    public void a1_simple_tag_system_test_ram_bytes() {
        CachedTagBasedEncoderBytes cachedEncoder = new CachedTagBasedEncoderBytes(new LITagBytesEncoder());
        standard_test(cachedEncoder);
        TagSystemTestHelper.do_stream_test(cachedEncoder);
    }

    @Test
    public void a3_simple_tag_system_test_file() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_liPositionCached_file_storage_test.litbe");
        try(
                FileStorage storage = new FileStorage(f)
        ) {
            CachedTagBasedEncoderBytes cachedEncoder = new CachedTagBasedEncoderBytes(new LITagBytesEncoder(storage));
            standard_test(cachedEncoder);
            TagSystemTestHelper.do_stream_test(cachedEncoder);
        }
    }

    @Test
    public void a4_simple_tag_system_test_remote_ram() throws IOException {
        int port = 50262;
        try(
                RemoteStorageServer ignored = new RemoteStorageServer(port, new ByteArrayStorage());
                RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            CachedTagBasedEncoderBytes cachedEncoder = new CachedTagBasedEncoderBytes(new LITagBytesEncoder(storage));
            standard_test(cachedEncoder);
            TagSystemTestHelper.do_stream_test(cachedEncoder);
        }
    }

    private void standard_test(TagBasedEncoderBytes a_encoder) {
        TagSystemTestHelper.enter_values(a_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(a_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(a_encoder);
        TagSystemTestHelper.basic_typed_system_test(a_encoder);

        TagSystemTestHelper.read_encoded_test(a_encoder);
    }



    @Test
    public void a5_test_remote_cache_consistency() throws IOException, InterruptedException {
        int port = 63759;
        new Thread(() -> {
            try {
                RemoteEncoderServer res = new RemoteEncoderServer(port, new LITagBytesEncoder());
                Thread.sleep(9000);
                res.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        CachedTagBasedEncoderBytes rbae_1 = new CachedTagBasedEncoderBytes(new RemoteEncoderBytes("localhost", port), new RemoteEncoder_Observer("localhost", port));
        rbae_1.addEntry("a", new byte[1]);
        rbae_1.addEntry("b", new byte[2]);

        System.out.println("rbae1 - a+b should be cached: "+rbae_1); //entries cached

        new Thread(() -> {
            try {
                CachedTagBasedEncoderBytes rbae_2 = new CachedTagBasedEncoderBytes(new RemoteEncoderBytes("localhost", port), new RemoteEncoder_Observer("localhost", port));
                rbae_2.getEntry("a");
                rbae_2.getEntry("b");
                System.out.println("rbae2 - a+b should be cached: "+rbae_2); //entries cached
                Thread.sleep(3000); //wait till "1" is deleted by rbae_1

                System.out.println("rbae2 - only b should be cached, remote blocks should be empty: "+rbae_2); //entries cached
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000); //wait till entries are cached by rbae_2
        System.out.println("rbae1 - a+b should be cached, remote blocks should be empty: "+rbae_1); //entries cached

        rbae_1.deleteEntry_noReturn("a");
        System.out.println("rbae1 - only b should be cached: "+rbae_1);

        Thread.sleep(1000);
        System.out.println("rbae1 - only b should be cached, blocks should be empty: "+rbae_1); //entries cached

        Thread.sleep(10000); //would kill the threads
    }

    @Test
    public void x_perf_compare_ram_and_cached_ram() {
        TimeDiffMarker.setMark_d();
        CachedTagBasedEncoderBytes cachedEncoder = new CachedTagBasedEncoderBytes(new LITagBytesEncoder());
        for(int i=0;i<15000;i++)
            standard_test(cachedEncoder);
        TimeDiffMarker.println_setMark_d("cached took : ");
        LITagBytesEncoder encoder = new LITagBytesEncoder();
        for(int i=0;i<15000;i++)
            standard_test(encoder);
        TimeDiffMarker.println_setMark_d("not cached took : ");
    }
}
