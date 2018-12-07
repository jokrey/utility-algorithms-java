package jokrey.utilities.encoder.examples.command_line;

import jokrey.utilities.command.line.helper.Argument;
import jokrey.utilities.command.line.helper.CommandLoop;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LITagStringEncoder;

import java.util.Arrays;

public class InteractiveTbeStringEditor {
    public static void main(String[] args) {
        initiate_command_loop(new LITagStringEncoder());
    }

    public static void initiate_command_loop(TagBasedEncoder<String> tbe) {
        CommandLoop loop = new CommandLoop();
        loop.addCommand("add", "adds entry(\"args[1]\") with tag(\"args[0]\"), while checking constraint that tag(args[0]) is unique within the system",
                        Argument.with(String.class, String.class), args -> tbe.addEntry(args[0].get(), args[1].get()),
                "a", "addEntry");
        loop.addCommand("add-nocheck", "adds entry(\"args[1]\") with tag(\"args[0]\"), WITHOUT checking if tag(args[0]) is unique [USE WITH CARE]", Argument.with(String.class, String.class), args -> tbe.addEntry_nocheck(args[0].get(), args[1].get()),
                "addno", "an", "addEntry_nocheck");
        loop.addCommand("tags", "Display's all tags previously added to the system",
                        Argument.noargs(), ignore -> System.out.println("tags: "+ Arrays.toString(tbe.getTags())));
        loop.addCommand("entry", "Displays the entry at tag(\"args[0]\")",
                        Argument.with(String.class), args -> System.out.println("entry(@\""+args[0].get()+"\"): "+tbe.getEntry(args[0].get())));
        loop.addCommand("delete", "Deletes and displays the deleted entry at tag(\"args[0]\")",
                        Argument.with(String.class), args -> System.out.println("deleted entry(@\""+args[0].get()+"\"): "+tbe.deleteEntry(args[0].get())),
                "del", "deleteEntry");
        loop.addCommand("delete-noreturn", "Deletes and displays the deleted entry at tag(\"args[0]\")",
                Argument.with(String.class), args -> {
                    if(tbe.deleteEntry_noReturn(args[0].get()))
                        System.out.println("deleted entry(@\""+args[0].get()+"\")");
                    else
                        System.out.println("Could not delete: \""+args[0].get()+"\"");
                },
                "delno", "deleteEntry_noReturn");
        loop.addCommand("exists", "Decides whether or not entry at tag(\"args[0]\") exists",
                        Argument.with(String.class), args -> {
            if(tbe.exists(args[0].get()))
                System.out.println("entry(@\""+args[0].get()+"\") exists");
            else
                System.out.println("entry(@\""+args[0].get()+"\") does NOT exist");
        }, "getEntry", "get", "whatIs");
        loop.addCommand("length", "Display the length of value at tag(\"args[0]\")",
                        Argument.with(String.class), args -> System.out.println("entry(@\""+args[0].get()+"\") is \""+tbe.length(args[0].get())+"\" chars long"));
        loop.addCommand("clear", "Clears all tags and values",
        Argument.noargs(), ignore -> {tbe.clear();System.out.println("cleared");},
                "delete-all", "del-all");
        loop.addCommand("decode", "Reinitializes the encoder with \"args[0]\"",
                        Argument.with(String.class), args -> tbe.readFromEncoded(args[0].get()),
                "from", "readFrom", "setContent", "set", "clearWith");
        loop.addCommand("encode", "Displays the encoded content of this encoder",
                        Argument.noargs(), args -> System.out.println("Encoded: "+tbe.getEncoded()),
                "display", "show", "displayEncoded", "encoded");

//        loop.addCommandsFromPublicMethodsOf(tbe);

        System.out.println("Welcome to the interactive tag based encoder.\nUse 'help' to query whats possible.");
        loop.run();
    }
}
