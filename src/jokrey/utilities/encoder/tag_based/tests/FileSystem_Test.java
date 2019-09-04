package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.files.Simple1FileFileSystem;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileSystem_Test {
    @Test
    public void test() {
        Simple1FileFileSystem system = new Simple1FileFileSystem(new LITagBytesEncoder(), "C:/Users/User/Desktop/file.encoder");
        system.setFileContent("internal1.txt", new byte[12]);
        system.setFileContent("internal2.txt", new byte[12]);
        system.setFileContent("internal3.txt", new byte[12]);
        system.setFileContent("internal4.txt", new byte[12]);
        system.setFileContent("dir1/internal1.txt", new byte[11]);
        system.setFileContent("dir1/internal2.txt", new byte[11]);
        system.setFileContent("dir1/internal3.txt", new byte[11]);
        system.setFileContent("dir2/internal1.txt", new byte[11]);
        system.setFileContent("dir2/internal2.txt", new byte[11]);
        system.setFileContent("dir2/internal3.txt", new byte[11]);

//        assertArrayEquals(new String[] {"dir2", "dir1", "internal2.txt", "internal4.txt", "internal3.txt", "internal1.txt"}, system.getVirtualPathsInVirtualDir("C:/Users/User/Desktop/file.encoder"));
        assertTrue(system.isDirectory("C:/Users/User/Desktop/file.encoder/dir1"));
        assertTrue(system.isDirectory("C:/Users/User/Desktop/file.encoder/dir2"));
        assertFalse(system.isDirectory("C:/Users/User/Desktop/file.encoder/internal3.txt"));
    }
}
