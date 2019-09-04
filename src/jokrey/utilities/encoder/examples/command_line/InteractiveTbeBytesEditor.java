package jokrey.utilities.encoder.examples.command_line;

import jokrey.utilities.command.line.helper.Argument;
import jokrey.utilities.command.line.helper.CommandLoop;
import jokrey.utilities.encoder.tag_based.TagBasedEncoderBytes;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;

public class InteractiveTbeBytesEditor {
    public static void main(String[] args) {
        initiate_command_loop(new LITagBytesEncoder());
    }

    public static void initiate_command_loop(TagBasedEncoderBytes tbe) {

        CommandLoop loop = new CommandLoop();

        loop.addCommand("add","adds entry(\"args[1]\") as string with tag(\"args[0]\"), while checking constraint that tag(args[0]) is unique within the system",
                        Argument.with(String.class, String.class), args -> tbe.addEntryT(args[0].get(), args[1].raw()),
                "a", "addEntry");
        loop.addCommand("add-nocheck","adds entry(\"args[1]\") as string with tag(\"args[0]\"), WITHOUT checking if tag(args[0]) is unique [USE WITH CARE]", Argument.with(String.class, String.class), args -> tbe.addEntryT_nocheck(args[0].get(), args[1].raw()),
                "addno", "an", "addEntry_nocheck");

        loop.addCommand("entry","Displays the entry at tag(\"args[0]\")",
                        Argument.with(String.class), args -> System.out.println("entry(@\""+args[0].get()+"\"): "+ Arrays.toString(tbe.getEntry(args[0].get()))));
        loop.addCommand("delete","Deletes and displays the deleted entry at tag(\"args[0]\")",
                        Argument.with(String.class), args -> System.out.println("deleted entry(@\""+args[0].get()+"\"): "+ Arrays.toString(tbe.deleteEntry(args[0].get()))),
                "del", "deleteEntry");

        loop.addCommand("entry_string","Displays(as string) the entry at tag(\"args[0]\")",
                Argument.with(String.class), args -> System.out.println("entry(@\""+args[0].get()+"\"): "+ tbe.getEntryT(args[0].get(), String.class)),
                "entry_str", "getEntryAsString");
        loop.addCommand("delete_string","Deletes and displays(as string) the deleted entry at tag(\"args[0]\")",
                Argument.with(String.class), args -> System.out.println("deleted entry(@\""+args[0].get()+"\"): "+ tbe.deleteEntryT(args[0].get(), String.class)),
                "del_str", "deleteEntryAsString");

        loop.addCommand("delete-noreturn","Deletes and displays the deleted entry at tag(\"args[0]\")",
                        Argument.with(String.class), args -> {
                    if(tbe.deleteEntry_noReturn(args[0].get()))
                        System.out.println("deleted entry(@\""+args[0].get()+"\")");
                    else
                        System.out.println("Could not delete: \""+args[0].get()+"\"");
                },
                "delno", "deleteEntry_noReturn");

        loop.addCommand("tags","Display's all tags previously added to the system",
                Argument.noargs(), ignore -> System.out.println("tags: "+ Arrays.toString(tbe.getTags())));
        loop.addCommand("exists","Decides whether or not entry at tag(\"args[0]\") exists",
                        Argument.with(String.class), args -> {
                    if(tbe.exists(args[0].get()))
                        System.out.println("entry(@\""+args[0].get()+"\") exists");
                    else
                        System.out.println("entry(@\""+args[0].get()+"\") does NOT exist");
                },
                "getEntry", "get", "whatIs");
        loop.addCommand("length","Display the length of value at tag(\"args[0]\")",
                Argument.with(String.class), args -> System.out.println("entry(@\""+args[0].get()+"\") is \""+tbe.length(args[0].get())+"\" chars long"));
        loop.addCommand("clear","Clears all tags and values",
                        Argument.noargs(), ignore -> {tbe.clear();System.out.println("cleared");},
                "delete-all", "del-all");

        loop.addCommand("decode","Reinitializes the encoder with \"args[0]\", assuming that \"args[0]\" is a base64 encoded string representing a valid byte array",
                        Argument.with(String.class), args -> tbe.readFromEncoded(Base64.getDecoder().decode(args[0].raw())),
                "from", "readFrom", "setContent", "set", "clearWith");
        loop.addCommand("encode","Displays the encoded content of this encoder",
                        Argument.noargs(), args -> System.out.println("Encoded: "+ Arrays.toString(tbe.getEncoded())),
                "display", "show", "displayEncoded", "encoded");




        loop.addCommand("storeTo","Stores the current content to path(\"args[0]\")",
                        Argument.with(String.class), args -> {
                    try {
                        FileOutputStream fout = new FileOutputStream(new File(args[0].raw()));
                        InputStream is = tbe.getRawStorageSystem().stream();
                        byte[] buffer = new byte[4096];
                        int read;
                        while((read = is.read(buffer)) != -1) {
                            fout.write(buffer, 0, read);
                        }
                        is.close();
                        fout.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("Invalid target file path(\""+e.getMessage()+"\")");
                    } catch (IOException e) {
                        System.out.println("Error(\""+e.getMessage()+"\") while copying data");
                    }
                },
                "store", "encodeTo");
        loop.addCommand("open","sets the internal storage to read from path(\"args[0]\"), effectively opening the path - NOTE: this may discard the current content(if is only virtual).",
                        Argument.with(String.class), args -> {
                    try {
                        initiate_command_loop(new LITagBytesEncoder(new FileStorage(new File(args[0].getRaw()))));
                        loop.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("Invalid target file path(\""+e.getMessage()+"\")");
                    }
                },
                "decoderAtFile", "openFile");


//        loop.addCommandsFromPublicMethodsOf(tbe);

        System.out.println("Welcome to the interactive tag based encoder.\nUse 'help' to query whats possible.");
        loop.run();
    }
}