package jokrey.utilities.encoder.examples.command_line;

import jokrey.utilities.command.line.helper.Argument;
import jokrey.utilities.command.line.helper.CommandLoop;
import jokrey.utilities.encoder.tag_based.TagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.files.Simple1FileFileSystem;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;

import java.io.File;
import java.io.FileNotFoundException;

public class Simple1FileSystemEditor {
    public static void main(String[] args) throws FileNotFoundException {
        String path = System.getProperty("user.home")+"/Desktop/default.litbe";
        initiate_command_loop(new LITagBytesEncoder(new FileStorage(new File(path))), path);
    }

    public static void initiate_command_loop(TagBasedEncoderBytes encoder, String path) {
        Simple1FileFileSystem system = new Simple1FileFileSystem(encoder, path);
        StringBuilder working_directory = new StringBuilder(path);

        CommandLoop loop = new CommandLoop();

        loop.addCommand("open", "opens the file at path(args[0]), interpreting it's contents as a file system", Argument.with(String.class), args -> {
            try {
                initiate_command_loop(new LITagBytesEncoder(new FileStorage(new File(args[0].getRaw()))), args[0].getRaw());
                loop.close();
            } catch (FileNotFoundException e) {
                System.out.println("Invalid target file path(\""+e.getMessage()+"\")");
            }
        });

        loop.addCommand("cd", "changes the working directory to the root of the file system, same as \'cd /\'", Argument.noargs(), args -> {
            working_directory.setLength(0);
            working_directory.append(path);
        });
        loop.addCommand("cd", "changes the working directory relative from the current working directory\nOr not relative if preceded by a /", Argument.with(String.class), args -> {
            working_directory.setLength(0);
            working_directory.append(path);
            working_directory.append("/");
            working_directory.append(args[0].getRaw());
            System.out.println("New Working Directory: "+working_directory.toString());
            if(!system.isDirectory(working_directory.toString()))
                System.out.println("New working-directory is not currently an existing directory");
        });
        loop.addCommand("ls", "lists all files in the working directory", Argument.noargs(), args -> {
            String[] virtualPaths = system.getVirtualPathsInVirtualDir(working_directory.toString());
            for(String virtualPath:virtualPaths)
                System.out.println(virtualPath);
        });

        loop.addCommand("create", "creates a new file with name(args[0]) and no content", Argument.with(String.class), args -> {
            system.setFileContent(system.getInnerPath(working_directory+args[0].getRaw()), new byte[0]);
        }, "create-file", "createFile");

        loop.addCommand("delete", "deletes the file with name(args[0])", Argument.with(String.class), args -> {
            system.delete(system.getInnerPath(working_directory+args[0].getRaw()));
        });

        //TODO, more commands

        loop.run();
    }
}
