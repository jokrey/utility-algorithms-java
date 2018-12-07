package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.implementation.length_indicator.LITagCachedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.LITagCachedEncoderBytes;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LITagStringEncoder;
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
public class SeachedCachedLITBE_Test {
    @Test
    public void a0_lame_attempt_to_remove_class_loader_impact_on_measurement() {
        a1_simple_tag_system_test_ram_bytes();
        a2_simple_tag_system_test_ram_string();
    }

    @Test
    public void a1_simple_tag_system_test_ram_bytes() {
        LITagCachedEncoderBytes cachedEncoder = new LITagCachedEncoderBytes(new LITagBytesEncoder());
        standard_test(cachedEncoder);
        TagSystemTestHelper.do_stream_test(cachedEncoder);
    }
    @Test
    public void a2_simple_tag_system_test_ram_string() {
        standard_test(new LITagCachedEncoder<>(new LITagStringEncoder()));
    }

    @Test
    public void a3_simple_tag_system_test_file() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_liPositionCached_file_storage_test.litbe");
        try(
                FileStorage storage = new FileStorage(f)
        ) {
            LITagCachedEncoderBytes cachedEncoder = new LITagCachedEncoderBytes(new LITagBytesEncoder(storage));
            standard_test(cachedEncoder);
            TagSystemTestHelper.do_stream_test(cachedEncoder);
        }
    }

    @Test
    public void a4_simple_tag_system_test_remote_ram() throws IOException {
        int port = 50252;
        try(
                RemoteStorageServer ignored = new RemoteStorageServer(port, new ByteArrayStorage());
                RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            LITagCachedEncoderBytes cachedEncoder = new LITagCachedEncoderBytes(new LITagBytesEncoder(storage));
            standard_test(cachedEncoder);
            TagSystemTestHelper.do_stream_test(cachedEncoder);
        }
    }

    private void standard_test(LITagCachedEncoder<?> cached_encoder) {
        TagSystemTestHelper.enter_values(cached_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(cached_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(cached_encoder);
        TagSystemTestHelper.basic_typed_system_test(cached_encoder);

        TagSystemTestHelper.read_encoded_test(cached_encoder);
    }
}
