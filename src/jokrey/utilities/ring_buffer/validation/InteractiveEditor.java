package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.command.line.helper.Argument;
import jokrey.utilities.command.line.helper.CommandLoop;
import jokrey.utilities.ring_buffer.VarSizedRingBuffer;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;

import static jokrey.utilities.ring_buffer.validation.VSBRDebugPrint.elementsToList;

public class InteractiveEditor {
    public static void main(String[] a) {
        int max = VarSizedRingBuffer.START + 20;
        TransparentBytesStorage storage = new ByteArrayStorage(max);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(storage, max);

        CommandLoop loop = new CommandLoop();
        loop.addCommand("append", "append given data", Argument.with(String.class), args -> {
            vsrb.append(args[0].get().toString().getBytes());
            VSBRDebugPrint.printMemoryLayout(vsrb, storage, String::new);
        },"a", "add", "+");
        loop.addCommand("print", "print a detailed analysis of the current state",
                Argument.noargs(),
                args -> VSBRDebugPrint.printContents("Command:", vsrb, storage, String::new),
                "details", "p", "?"
        );
        loop.addCommand("list", "print a detailed analysis of the current state",
                Argument.noargs(),
                args -> System.out.println(elementsToList(vsrb, String::new)),
                "l", "ls", "!"
        );
        loop.addCommand("deleteFirst", "print a detailed analysis of the current state",
                Argument.noargs(),
                args -> {
                    boolean success = vsrb.deleteFirst();
                    if(success)
                        System.out.println("deleted");
                    else
                        System.out.println("could not delete");
                    VSBRDebugPrint.printMemoryLayout(vsrb, storage, String::new);
                },
                "-", "df"
        );

        loop.run();
    }
}