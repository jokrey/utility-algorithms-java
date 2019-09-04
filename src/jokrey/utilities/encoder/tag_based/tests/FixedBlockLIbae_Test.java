package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.disfunct.fixed_blocks.FixedBlockBytesStorage;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.disfunct.fixed_blocks.FixedBlockLIbae;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorage;
import jokrey.utilities.transparent_storage.bytes.remote.server.RemoteStorageServer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FixedBlockLIbae_Test {
//    @Test
    public void a0_lame_attempt_to_remove_class_loader_impact_on_measurement() {
        a1_simple_tag_system_test_ram_bytes();
        a2_simple_tag_system_test_ram_string();
    }

//    @Test
    public void a1_simple_tag_system_test_ram_bytes() {
        LITagBytesEncoder encoder = new LITagBytesEncoder(new FixedBlockLIbae());
        standard_test(encoder);
        TagSystemTestHelper.do_stream_test(encoder);
    }
//    @Test
    public void a2_simple_tag_system_test_ram_string() {
//        standard_test(new LITagCachedEncoder<>(new LITagStringEncoder()));
    }

//    @Test
    public void a3_simple_tag_system_test_file() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_liPositionCached_file_storage_test.litbe");
        try(
                FileStorage storage = new FileStorage(f)
        ) {
            LITagBytesEncoder encoder = new LITagBytesEncoder(new FixedBlockLIbae(storage));
            standard_test(encoder);
            TagSystemTestHelper.do_stream_test(encoder);
        }
    }

//    @Test
    public void a4_simple_tag_system_test_remote_ram() throws IOException {
        int port = 50252;
        try(
                RemoteStorageServer ignored = new RemoteStorageServer(port, new ByteArrayStorage());
                RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            LITagBytesEncoder encoder = new LITagBytesEncoder(new FixedBlockLIbae(storage));
            standard_test(encoder);
            TagSystemTestHelper.do_stream_test(encoder);
        }
    }

    private void standard_test(LITagBytesEncoder cached_encoder) {
        TagSystemTestHelper.enter_values(cached_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(cached_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(cached_encoder);
        TagSystemTestHelper.basic_typed_system_test(cached_encoder);

        TagSystemTestHelper.read_encoded_test(cached_encoder);
    }


//    @Test
    public void storage_test() {
        TransparentBytesStorage storage = new FixedBlockBytesStorage(new ByteArrayStorage(), 1000, 1024);

        byte[] random = new byte[1000];
        new Random().nextBytes(random);

        ByteArrayOutputStream compare = new ByteArrayOutputStream(random.length*1000);
        for(int i=0;i<1000;i++) {
            assertEquals(i*random.length, storage.contentSize());
            storage.append(random);
            assertEquals((i+1)*random.length, storage.contentSize());

            compare.write(random, 0, random.length);
            assertArrayEquals(compare.toByteArray(), storage.getContent());
            assertEquals(compare.size(), storage.contentSize());
            assertArrayEquals(random, storage.sub(i*random.length, (i+1)*(random.length)));
        }

        assertArrayEquals(compare.toByteArray(), storage.getContent());

//        storage.delete(100, 101);
//        assertArrayEquals(Arrays.copyOfRange(random, 0, 100), storage.sub(0, 100));
//        assertArrayEquals(Arrays.copyOfRange(random, 101, 1000), storage.sub(100, 999));

        new Random().nextBytes(random);
        storage.set(1000, random);
        assertArrayEquals(random, storage.sub(1000, 2000));

//        assertEquals(compare.size()-1, storage.contentSize());
//        storage.clear();
//        assertEquals(0, storage.contentSize());
//        assertArrayEquals(new byte[0], storage.getContent());
//        assertArrayEquals(new byte[0], storage.sub(0, Integer.MAX_VALUE));

        storage.set(0, random);
        assertArrayEquals(random, storage.getContent());
    }
}
