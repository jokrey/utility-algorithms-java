package jokrey.utilities.encoder.tag_based.tests.performance;

import jokrey.utilities.date_time.ExactDateTime;
import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.cached.CachedTagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.RemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.remote.authenticated.AuthenticatedRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.thread_safe.SynchronizingTagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.LITagCachedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.remote.encoder.LIRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.remote.encoder.authenticated.LIAuthenticatedRemoteEncoderServer;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.remote.encoder.authenticated.LIAuthenticatedRemoteEncoderServer_MultiFile;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.encoder.tag_based.tests.AuthenticatedRemoteEncoderTest;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorageLegacy;
import jokrey.utilities.transparent_storage.bytes.remote.RemoteStorage;
import jokrey.utilities.transparent_storage.bytes.remote.server.RemoteStorageServer;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceTests {
    private static final File result_log_file = new File(System.getProperty("user.home")+"/Desktop/perf_test_results_log_"+ ExactDateTime.todayandnow().asFileTimeStamp() +".txt");
    private static FileWriter result_writer;
    private static final int[] available_ports = {58241, 58242, 58243, 58244, 58245, 58246, 58247, 58248, 58249, 58250, 58251, 58252, 58253, 58254, 58255};
    private static int port_pointer = 0;
    private static int getNextPort() {
        return available_ports[port_pointer++];
    }

    @BeforeClass
    public static void init() {
        try {
            result_writer = new FileWriter(result_log_file);
        } catch (IOException e) {
            result_writer = null;
            e.printStackTrace();
        }
    }

    @Test
    public void a0_lame_attempt_to_remove_class_loader_impact_on_measurement() {
        GenericPerformanceTest.run_standard_test(null, null, new LITagBytesEncoder());
    }

    @Test
    public void litbe_ramStorage() {
        GenericPerformanceTest.run_standard_test("LITagBytesEncoder | RAM storage", PerformanceTests::writeResults, new LITagBytesEncoder());
    }

    @Test
    public void litbe_ramStoragePRINTONLY() {
        GenericPerformanceTest.run_standard_test_many("LITagBytesEncoder | RAM storage", (introduction_text, results) -> {
            AverageCallTimeMarker.print_all(results, introduction_text);
        }, new LITagBytesEncoder());
    }

    @Test
    public void litbe_ramStorage_threadsafeWrapper() {
        GenericPerformanceTest.run_standard_test("SynchronizedTagBasedEncoder | RAM storage", PerformanceTests::writeResults, new SynchronizingTagBasedEncoder<>(new LITagBytesEncoder()));
    }

    @Test
    public void litbe_ramStorage_legacy() {
        GenericPerformanceTest.run_standard_test("OLD VERSION | LITagBytesEncoder | RAM storage | OLD VERSION", PerformanceTests::writeResults, new LITagBytesEncoder(new ByteArrayStorageLegacy()));
    }


    @Test
    public void x1_use_test() {
        GenericPerformanceTest.run_standard_test_string("LITagStringEncoder", PerformanceTests::writeResults, new LITagStringEncoder());
    }

    @Test
    public void x2_litbe_ramStorage_memory_efficient() {
        GenericPerformanceTest.run_standard_test("LITagBytesEncoder | RAM storage | Memory over Performance", PerformanceTests::writeResults, new LITagBytesEncoder(new ByteArrayStorage(true, new byte[0], 0)));
    }

    @Test
    public void x3_litbe_fileStorage_fastDrive() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_file_storage_perf_test.litbe");
        try(FileStorage storage = new FileStorage(f)) {
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | FILE storage", PerformanceTests::writeResults, new LITagBytesEncoder(storage));
        }
    }

    @Test
    public void x3_litbe_fileStorage_fastDrivePRINTONLY() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_file_storage_perf_test.litbe");
        try(FileStorage storage = new FileStorage(f)) {
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | FILE storage", (introduction_text, results) -> {
                AverageCallTimeMarker.print_all(results, introduction_text);
            }, new LITagBytesEncoder(storage));
        }
    }

    @Test
    public void x6_litbe_remoteStorage_ram() throws IOException {
        int port = getNextPort();
        try(RemoteStorageServer ignored = new RemoteStorageServer(port, new ByteArrayStorage());
            RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | REMOTE storage | RAM", PerformanceTests::writeResults, new LITagBytesEncoder(storage));
        }
    }

    @Test
    public void x6_litbe_remoteStorage_file() throws IOException {
        int port = getNextPort();
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_remote_file_storage_perf_test.litbe");
        try(RemoteStorageServer server = new RemoteStorageServer(port, f);
            RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            server.clear();
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | REMOTE storage | FILE", PerformanceTests::writeResults, new LITagBytesEncoder(storage));
        }
    }

    @Test
    public void x3_litbe_fileStorage_slowishDrive() throws IOException {
        File f = new File("F:/litbe_file_storage_perf_test.litbe");
        try(FileStorage storage = new FileStorage(f)) {
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | FILE storage(slower drive)", PerformanceTests::writeResults, new LITagBytesEncoder(storage));
        }
    }

    @Test
    public void x4_rbae_ram() throws IOException {
        int port = getNextPort();
        try(RemoteEncoderServer server = new LIRemoteEncoderServer(port, new ByteArrayStorage());
            RemoteEncoderBytes rbae = new RemoteEncoderBytes("localhost", port)
        ) {
            GenericPerformanceTest.run_standard_test("RemoteEncoderBytes | RAM | Client", PerformanceTests::writeResults, rbae);
            GenericPerformanceTest.run_standard_test("RemoteEncoderBytes | RAM | Server", PerformanceTests::writeResults, server);
        }
    }

    @Test
    public void x4_rbae_file() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/rbae_file_storage_perf_test.litbe");
        int port = getNextPort();
        try(RemoteEncoderServer server = new LIRemoteEncoderServer(port, f);
            RemoteEncoderBytes rbae = new RemoteEncoderBytes("localhost", port)
        ) {
            server.clear();
            GenericPerformanceTest.run_standard_test_short("RemoteEncoderBytes | File | Client", PerformanceTests::writeResults, rbae);
            GenericPerformanceTest.run_standard_test_short("RemoteEncoderBytes | File | Server", PerformanceTests::writeResults, server);
        }
    }

    @Test
    public void x5_areb_ram() throws Exception {
        int port = getNextPort();
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer(port, new ByteArrayStorage());
            AuthenticatedRemoteEncoderBytes areb = AuthenticatedRemoteEncoderBytes.register("localhost", port, "performer", "test")
        ) {
            GenericPerformanceTest.run_standard_test("AREB - Authenticated Remote Encoder RAM", PerformanceTests::writeResults, areb);

            GenericPerformanceTest.run_standard_test("AREB - User view on server side - RAM", PerformanceTests::writeResults, server.getSubEncoder("performer"));
        }
    }

    @Test
    public void x5_areb_file() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/areb_file_storage_perf_test.litbe");
        int port = getNextPort();
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer(port, f);
            AuthenticatedRemoteEncoderBytes areb = AuthenticatedRemoteEncoderBytes.register("localhost", port, "performer", "test")
        ) {
            server.clear();
            GenericPerformanceTest.run_standard_test_short("AREB - Authenticated Remote Encoder FILE", PerformanceTests::writeResults, areb);
            GenericPerformanceTest.run_standard_test_short("AREB - User view on server side - FILE", PerformanceTests::writeResults, server.getSubEncoder("performer"));
        }
    }

    @Test
    public void x5_areb_multi_file() throws IOException {
        File dir = new File(System.getProperty("user.home")+"/Desktop/areb_file_storage_perf_test_dir/");
        dir.mkdirs();
        int port = getNextPort();
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer_MultiFile(port, dir);
            AuthenticatedRemoteEncoderBytes areb = AuthenticatedRemoteEncoderBytes.register("localhost", port, "performer", "test")
        ) {
            server.clear();
            GenericPerformanceTest.run_standard_test_short("AREB - Authenticated Remote Encoder MULTI-FILE", PerformanceTests::writeResults, areb);
            GenericPerformanceTest.run_standard_test_short("AREB - User view on server side - MULTI-FILE", PerformanceTests::writeResults, server.getSubEncoder("performer"));
        }
    }

    @Test
    public void x5_areb_file_concurrent_users_tag_system_test() throws Throwable {
        File f = new File(System.getProperty("user.home")+"/Desktop/areb_file_storage_perf_test_concurrent_users.litbe");
        int port = 10008;
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer(port, f)) {
            server.clear();
            TimeDiffMarker.println_setMark_d("");
            int concurrent_users = 50;
            int max_threads = 100;
            int number_threads = Math.min(max_threads, concurrent_users);
            ConcurrentPoolTester executor = new ConcurrentPoolTester(number_threads);
            for(int i = 0; i < concurrent_users; i++) {
                // took roughly 1h 20m 21s with 250 once   - todo: why. why is the discrepency to multi file THIS big? Especially considering they both fully block currently. Maybe: LIse usage in tags in single...
                // took roughly 11m 11s with 25 once
                int thread_id = i;
                executor.execute(() -> AuthenticatedRemoteEncoderTest.simple_test_tag_system("localhost", port,thread_id + "x" + thread_id));
            }

            System.out.println("Waiting for all threads to finish");

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            executor.throwLatestException();

            result_writer.append("\n\n"+concurrent_users+" concurrent users on "+number_threads+" areb SINGLE file\n").append(TimeDiffMarker.getDiffFor_as_string_d()).append("\n\n").flush();
        }
    }

    @Test
    public void x5_areb_multiFile_concurrent_users_tag_system_test() throws Throwable {
        File dir = new File(System.getProperty("user.home")+"/Desktop/areb_file_storage_perf_test_dir_concurrent/");
        dir.mkdirs();
        int port = 10009;
        try(AuthenticatedRemoteEncoderServer server = new LIAuthenticatedRemoteEncoderServer_MultiFile(port, dir)) {
            server.clear();
            TimeDiffMarker.println_setMark_d("");
            int concurrent_users = 250;
            int max_threads = 100;
            int number_threads = Math.min(max_threads, concurrent_users);
            ConcurrentPoolTester executor = new ConcurrentPoolTester(number_threads);
            for(int i = 0; i < concurrent_users; i++) {
                //took roughly 30s with 250 once.
                //took roughly 5s with 25 once
                int thread_id = i;
                executor.execute(() -> AuthenticatedRemoteEncoderTest.simple_test_tag_system("localhost", port,thread_id + "x" + thread_id));
            }

            System.out.println("Waiting for all threads to finish");

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            executor.throwLatestException();

            result_writer.append("\n\n"+concurrent_users+" concurrent users on "+number_threads+" areb MULTI file\n").append(TimeDiffMarker.getDiffFor_as_string_d()).append("\n\n").flush();
        }
    }

    @Test
    public void a1_litbe_liPositionCached_ramStorage() {
        GenericPerformanceTest.run_standard_test("LITagCachedEncoder | LIPositionCached RAM storage", PerformanceTests::writeResults, new LITagCachedEncoder<>(new LITagBytesEncoder()));
    }

    @Test
    public void x5_litbe_liPositionCached_fileStorage() throws FileNotFoundException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_lipositioncache_file_storage_perf_test.litbe");
        GenericPerformanceTest.run_standard_test("LITagCachedEncoder | LIPositionCached | FILE storage", PerformanceTests::writeResults, new LITagCachedEncoder<>(new LITagBytesEncoder(new FileStorage(f))));
    }

    @Test
    public void x5_litbe_liPositionCached_fileStorage_short() throws FileNotFoundException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_lipositioncache_file_storage_perf_test.litbe");
        GenericPerformanceTest.run_standard_test_short("LITagCachedEncoder | LIPositionCached | FILE storage", PerformanceTests::writeResults, new LITagCachedEncoder<>(new LITagBytesEncoder(new FileStorage(f))));
    }

    @Test
    public void x6_litbe_liPositionCached_remoteStorage_ram_short() throws IOException {
        int port = getNextPort();
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_liPositionCached_remote_file_storage_perf_test.litbe");
        try(RemoteStorageServer server = new RemoteStorageServer(port, f);
            RemoteStorage storage = new RemoteStorage("localhost", port)
        ) {
            server.clear();
            GenericPerformanceTest.run_standard_test_short("LITagCachedEncoder | LIPositionCached | REMOTE storage | RAM", PerformanceTests::writeResults, new LITagCachedEncoder<>(new LITagBytesEncoder(storage)));
        }
    }




    @Test
    public void x5_areb_ram_cached_comparison() throws Exception {
        int port = getNextPort();
        try(AuthenticatedRemoteEncoderServer ignored = new LIAuthenticatedRemoteEncoderServer(port, new ByteArrayStorage());
            AuthenticatedRemoteEncoderBytes areb = AuthenticatedRemoteEncoderBytes.register("localhost", port, "performer", "test")
        ) {
            CachedTagBasedEncoderBytes cached = new CachedTagBasedEncoderBytes(areb);

            GenericPerformanceTest.run_standard_test_big("AREB - Authenticated Remote Encoder | RAM", PerformanceTests::writeResults, areb);
            ignored.clear();
            GenericPerformanceTest.run_standard_test_big("AREB - Authenticated Remote Encoder | Cached RAM", PerformanceTests::writeResults, cached);

            ignored.clear();
            GenericPerformanceTest.run_standard_test_many("AREB - Authenticated Remote Encoder | RAM", PerformanceTests::writeResults, areb);
            ignored.clear();
            GenericPerformanceTest.run_standard_test_many("AREB - Authenticated Remote Encoder | Cached RAM", PerformanceTests::writeResults, cached);
        }
    }

    @Test
    public void x3_litbe_fileStorage_fastDrive_cached() throws IOException {
        File f = new File(System.getProperty("user.home")+"/Desktop/litbe_file_storage_perf_test_cached.litbe");
        try(FileStorage storage = new FileStorage(f)) {
            CachedTagBasedEncoderBytes cached = new CachedTagBasedEncoderBytes(new LITagBytesEncoder(storage));
            GenericPerformanceTest.run_standard_test_short("LITagBytesEncoder | Cached FILE storage", PerformanceTests::writeResults, cached);
        }
    }

    @Test
    public void x3_litbe_ram_cached() {
        CachedTagBasedEncoderBytes cached = new CachedTagBasedEncoderBytes(new LITagBytesEncoder());
        GenericPerformanceTest.run_standard_test("LITagBytesEncoder | Cached RAM", PerformanceTests::writeResults, cached);
    }


    private static void writeResults(String introduction, AverageCallTimeMarker.Call_Count_Average[] combined_res) {
        if(result_writer!=null) {
            try {
                result_writer.append(introduction).append("\n");

                for(AverageCallTimeMarker.Call_Count_Average c:combined_res)
                    result_writer.append(c.toString()).append("\n");

                result_writer.append("==================================================\n");
                result_writer.append("\n");
                result_writer.append("\n");
                result_writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
