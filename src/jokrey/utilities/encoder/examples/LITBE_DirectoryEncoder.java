package jokrey.utilities.encoder.examples;

import jokrey.utilities.command.line.helper.Argument;
import jokrey.utilities.command.line.helper.CommandLoop;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.files.Simple1FileFileSystem;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Provides a simple directory to 1 file encoder using the Simple1FileFileSystem.
 * The inverting is obviously also possible.
 * Please note that empty directories are not supported and will therefore silently disappear during encoding.
 *
 * This utility provides a fast and small backup function for directory structures(unless you care about empty directories).
 *    Because there is no minimal block size for anything(like most filesystem have), the overhead of storing the file path is won. (And the target file will be smaller on disk)
 *
 *    TODO:: better error handeling
 *
 * @author jokrey
 */
public class LITBE_DirectoryEncoder {
    public static void main(String[] arguments) {
        CommandLoop loop = new CommandLoop();

        loop.addCommand("encode", "Encodes the directory at path(args[0]) to file at path(args[1])", Argument.with(String.class, String.class), args -> {
            File source_directory = new File(args[0].getRaw());
            File target_file = new File(args[1].getRaw());

            if(!source_directory.isDirectory() || !source_directory.exists())
                System.out.println("Source directory (\""+source_directory.getAbsolutePath()+"\") is not a directory or does not exist");
            else if(target_file.exists())
                System.out.println("Target file (\""+target_file.getAbsolutePath()+"\") already exists");
            else {
                System.out.println("Encoding now...");
                try {
                    long error_count = encode(source_directory, target_file);
                    System.out.println("Encoding completed with "+error_count+" errors");
                } catch (Exception e) {
                    System.out.println("Exception occurred: "+e.getMessage());
                }
            }
        },"e", "en");

        loop.addCommand("decode", "Decodes the file at path(args[0]) to directory at path(args[1])", Argument.with(String.class, String.class), args -> {
            File source_file = new File(args[0].getRaw());
            File target_directory = new File(args[1].getRaw());

            if(!source_file.exists() || source_file.isDirectory())
                System.out.println("Source file (\""+source_file.getAbsolutePath()+"\") is not a file or does not exist");
            if(target_directory.exists())
                System.out.println("Target directory (\""+target_directory.getAbsolutePath()+"\") already exists");
            else {
                System.out.println("Decoding now...");
                try {
                    long error_count = decode(source_file, target_directory);
                    System.out.println("Decoding completed with "+error_count+" errors");
                } catch (Exception e) {
                    System.out.println("Exception occurred: "+e.getMessage());
                }
            }
        }, "d", "de");

        loop.run();
    }

    /**
     * Writes every file into the created target file
     *
     * Please note that empty directories are not supported and will therefore silently disappear during encoding.
     *
     * @param source_directory has to exist and has to be a directory
     * @param target_file has to not yet exist
     * @throws IOException if any errors occur during reading.
     */
    public static long encode(File source_directory, File target_file) throws Exception {
        if(!source_directory.exists() || !source_directory.isDirectory())
            throw new IllegalArgumentException("Provided directory file(arg0) is not a valid directory");
        if(target_file.exists())
            throw new IllegalArgumentException("Provided target_file(arg1) already exists_in_cache");

        Simple1FileFileSystem effectively_the_encoder = new Simple1FileFileSystem();
        effectively_the_encoder.open(target_file.getAbsolutePath());

        long errors = effectively_the_encoder.addNewDirectoryToSystem(source_directory, source_directory);

        effectively_the_encoder.close();

        return errors;
    }


    /**
     * Decodes the content of the file into the target directory.
     * If it was previously encoded using this software, then the directory should be identical to the one that it was encoded from.
     *     Except for the missing empty directories.
     *
     * @param source_file has to exist and not be a directory.  If it furthermore is not a Simple1FileFileSystem, an exception will be thrown.
     * @param target_directory has to not exist
     * @throws IOException if something goes wrong
     * @return number of file restore operations that produced an error
     */
    public static long decode(File source_file, File target_directory) throws Exception {
        if(!source_file.exists() || source_file.isDirectory())
            throw new IllegalArgumentException("Provided directory source_file(arg0) is not a valid source file");
        if(target_directory.exists())
            throw new IllegalArgumentException("Provided target_directory(arg1) already exists_in_cache");

        Simple1FileFileSystem effectively_the_decoder = new Simple1FileFileSystem();
        effectively_the_decoder.open(source_file.getAbsolutePath());

        long errors = 0;//yes we do need a long for this. (honestly we do not actually, but I think it's funny)
        for(LITagBytesEncoder.TaggedStream ts:effectively_the_decoder.getFileIterator()) {
            try {
                String path = ts.tag;
                File target_file = new File(target_directory.getAbsolutePath() + "/" + path);
                target_file.getParentFile().mkdirs();
                if (ts.stream != null)
                    Files.copy(ts.stream, target_file.toPath());
                else {
                    System.out.println(target_file + " failed to be restored(internal stream is null)");
                    errors++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errors++;
            }
        }

        effectively_the_decoder.close();

        return errors;
    }
}