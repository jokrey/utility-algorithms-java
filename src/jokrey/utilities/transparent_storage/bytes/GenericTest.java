package jokrey.utilities.transparent_storage.bytes;

import jokrey.utilities.transparent_storage.bytes.file.FileStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorageLegacy;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorage;
import jokrey.utilities.transparent_storage.bytes.remote.server.RemoteStorageServer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class GenericTest {
    @Test
    public void byteArrayStorage_Test() {
        test(new ByteArrayStorage());
        insertTest(new ByteArrayStorage());
    }
    @Test
    public void byteArrayStorageLegacy_Test() {
        test(new ByteArrayStorageLegacy());
//        insertTest(new ByteArrayStorageLegacy()); //UNSUPPORTED
    }
    @Test
    public void fileStorage_Test() throws IOException {
        try(FileStorage storage = new FileStorage(new File(System.getProperty("user.home")+"/Desktop/storageTestRemoteServer.nothing"))) {
            test(storage);
            insertTest(storage);
        }
    }
    @Test
    public void remoteStorage_Test() throws IOException {
        try(RemoteStorageServer server = new RemoteStorageServer(1552, new File(System.getProperty("user.home")+"/Desktop/storageTestRemoteServer.nothing"));
            RemoteStorage storage = new RemoteStorage("localhost", 1552)) {
            test(server);
            test(storage);

//            insertTest(server); //UNSUPPORTED
//            insertTest(storage); //UNSUPPORTED
        }
    }


    public static void test(TransparentBytesStorage storage) {
        storage.clear();
        assertEquals(0, storage.contentSize());
        assertArrayEquals(new byte[0], storage.getContent());
        assertArrayEquals(new byte[0], storage.sub(0, Integer.MAX_VALUE));

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

        storage.delete(100, 101);
        assertArrayEquals(Arrays.copyOfRange(random, 0, 100), storage.sub(0, 100));
        assertArrayEquals(Arrays.copyOfRange(random, 101, 1000), storage.sub(100, 999));

        new Random().nextBytes(random);
        storage.set(1000, random);
        assertArrayEquals(random, storage.sub(1000, 2000));

        assertEquals(compare.size()-1, storage.contentSize());
        storage.clear();
        assertEquals(0, storage.contentSize());
        assertArrayEquals(new byte[0], storage.getContent());
        assertArrayEquals(new byte[0], storage.sub(0, Integer.MAX_VALUE));

        storage.set(0, random);
        assertArrayEquals(random, storage.getContent());

        storage.clear();
    }

    void insertTest(TransparentBytesStorage storage) {
        storage.clear();

        byte[] result = new byte[] {0,1,2,3,4,5,6,7,8,9};
        storage.append(Arrays.copyOfRange(result, 0, 5));//added 1-4
        System.out.println("storage = " + Arrays.toString(storage.getContent()));
        storage.append(Arrays.copyOfRange(result, 7, result.length));//added 7-9
        System.out.println("storage = " + Arrays.toString(storage.getContent()));
        assertEquals(8, storage.contentSize());

        System.out.println("Arrays.copyOfRange(result, 6, 8) = " + Arrays.toString(Arrays.copyOfRange(result, 5, 7)));
        storage.insert(5, Arrays.copyOfRange(result, 5, 7));
        System.out.println("storage = " + Arrays.toString(storage.getContent()));

        assertArrayEquals(result, storage.getContent());


        storage.clear();
    }
}
