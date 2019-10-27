package jokrey.utilities.command.line.helper;

import jokrey.utilities.simple.data_structure.pairs.ArrayKeyPair;
import jokrey.utilities.simple.data_structure.pairs.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandLoop implements Runnable, AutoCloseable {
    private Set<Pair<String, ConstrainedCommand>> commands = new LinkedHashSet<>();

    public CommandLoop() {
        addCommand("help", "helps", getHelpCommand());
        addCommand("exit", "exits", args -> close());
    }

    private Scanner s = new Scanner(System.in);
    public String readLine() {
        try {
            return s.nextLine();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Blocks
     */
    public void run() {
        while (true) {
            System.out.print(">>");
            String line = readLine();

            if(line==null)break;
            String[] rawSplit = quotation_split(line);
            if(rawSplit.length==0)continue;

            String commandId = rawSplit[0];
            String[] string_args = subarray(rawSplit, 1, rawSplit.length);
            ConstrainedCommand[] commandOptions = getCommandsFor(commandId);
            if (commandOptions.length==0)
                System.out.println("Unknown command(\"" + commandId + "\")");
            else {
                try {
                    if (!execute(commandId, string_args, commandOptions))
                        System.out.println("Constraints check of command failed. Please type 'help " + commandId + "' for more information.");
                } catch (Throwable t) {
                    System.out.println("exception while executing "+commandId+" - "+t.getMessage());
                }
            }
        }
        s.close();
    }

    private boolean execute(String commandId, String[] string_args, ConstrainedCommand[] commandOptions) {
        for(ConstrainedCommand command:commandOptions) {
            Argument[] args = command.accept(string_args);
            if(args != null) {
                System.out.println("Executing Command(\"" + commandId + "\") with args: " + Arrays.toString(string_args));
                command.execute(Argument.ensureInputExists(args, string_args));
                Argument.clear(args);
                return true;
            }
        }
        return false;
    }




    public void addCommand(String commandId, ConstrainedCommand command, String... alternativeCommands) {
        Pair<String, ConstrainedCommand> pair = new Pair<>(commandId, command);
        if(commands.contains(pair))
            throw new IllegalArgumentException("Command(\""+commandId+"\") is already known with given args");
        commands.add(pair);
        for(String alternativeCommand:alternativeCommands)
            commands.add( new Pair<>(alternativeCommand, command));
    }
    public void addCommand(String commandId, String description, Argument[] argumentTemplates, Command command, String... alternativeCommands) {
        addCommand(commandId, new ConstrainedCommand(description, argumentTemplates) {
            @Override public void execute(Argument[] args) {
                command.execute(args);
            }
        }, alternativeCommands);
    }
    public void addCommand(String commandId, String description, Command command, String... alternativeCommands) {
        addCommand(commandId, description, null, command, alternativeCommands);
    }
    public void addCommand(String commandId, Command command, String... alternativeCommands) {
        addCommand(commandId, "Executes \""+commandId+"\"", command, alternativeCommands);
    }
    public boolean exists(String commandId) {
        return getCommandsFor(commandId).length>0;
    }
    public void remove(String commandId) {
        commands = commands.stream().filter(pair -> !pair.l.equals(commandId)).collect(Collectors.toSet());
    }


    public void addCommandsFromPublicMethodsOf(Object o) {
        Method[] ms = o.getClass().getMethods();
        for(Method m:ms) {
            if(Modifier.isPublic(m.getModifiers())) {
                try {
                    addCommand(m.getName(), "Method(\"" + m.getName() + "\") in class(\"" + o.getClass().getSimpleName() + "\")", Argument.with(m.getParameterTypes()), args -> {
                        try {
                            Object result = m.invoke(o, Argument.getAll(args));
                            System.out.println("Call returned: "+result);
                        } catch (IllegalAccessException e) {
                            System.out.println("Caller does not have access to method(IllegalAccess:\"" + e.getMessage() + "\"). Could not call Method(\"" + m.getName() + "\") in class(\"" + o.getClass().getSimpleName() + "\") on object(\"" + o + "\") with args(\"" + Arrays.toString(Argument.getAll(args)) + "\")");
                        } catch (InvocationTargetException e) {
                            System.out.println("InvocationTargetException:\"" + e.getMessage() + "\". Could not call Method(\"" + m.getName() + "\") in class(\"" + o.getClass().getSimpleName() + "\") on object(\"" + o + "\") with args(\"" + Arrays.toString(Argument.getAll(args)) + "\")");
                        }
                    });
                } catch(IllegalArgumentException e) {} //polymorphism causes issues
            }
        }
    }

    @Override public void close() {
        s.close();//also closes System.in
    }


    private static String[] quotation_split(String raw_line) {
        if(raw_line.isEmpty())return new String[0];
        StringBuilder builder = new StringBuilder();
        ArrayList<String> result = new ArrayList<>(10);
        boolean inQuoteMode = raw_line.charAt(0) == '"';
        for(int i=0;i<raw_line.length();i++) {
            if(raw_line.charAt(i) == ' ' && !inQuoteMode) {
                if(!builder.toString().isEmpty())
                    result.add(builder.toString());
                builder = new StringBuilder();
            } else if(raw_line.charAt(i) == '"' && (i==0 || raw_line.charAt(i-1) != '\\')) {
                if(inQuoteMode) {
                    if(!builder.toString().isEmpty())
                        result.add(builder.toString());
                    builder = new StringBuilder();
                    inQuoteMode=false;
                } else
                    inQuoteMode=true;
            } else if(raw_line.charAt(i) != '\\')
                builder.append(raw_line.charAt(i));
        }
        if(!builder.toString().isEmpty())
            result.add(builder.toString());
        return result.toArray(new String[0]);
    }

    private ConstrainedCommand[] getCommandsFor(String commandID) {
        return commands.stream().filter(pair -> pair.l.equals(commandID)).map(pair -> pair.r).toArray(ConstrainedCommand[]::new);
    }


    //LITERALLY HELPER:

    private Command getHelpCommand() {
        return args -> {
            if(args.length==0) {
                System.out.println("Thanks for choosing help.\nThe following are the commands available to you:");
                for(ArrayKeyPair<String, ConstrainedCommand> entry:getSortedCommandsSet())
                    System.out.println(entry.l[0] + " - " + entry.getValue() + " |-alts: ["+toString(subarray(entry.l, 1, entry.l.length), "|")+"]");
            } else if(args.length==1) {
                String commandId = args[0].raw();
                ConstrainedCommand[] commandOptions = getCommandsFor(commandId);
                if(commandOptions.length==0)
                    System.out.println("Command(\"" + commandId + "\") is unknown");
                else {
                    System.out.println("Command Options for: "+commandId);
                    for(ConstrainedCommand command:commandOptions)
                        System.out.println(command.toString());
                }
            }
        };
    }
    private Set<ArrayKeyPair<String, ConstrainedCommand>> getSortedCommandsSet() {
        Set<ArrayKeyPair<String, ConstrainedCommand>> set = new TreeSet<>((a1, a2) -> arr_compare(a1.l, a2.l));

        for (Pair<String, ConstrainedCommand> entry : commands) {
            ArrayList<String> alts = new ArrayList<>();
            for (Pair<String, ConstrainedCommand> inner : commands) {
                if(entry.getValue() == inner.getValue()) {
                    alts.add(inner.getKey());
                }
            }

            set.add(new ArrayKeyPair<>(alts.toArray(new String[0]), entry.getValue()));
        }
        return set;
    }

    //HELPER FUNCTIONS

    private static String[] subarray(String[] es, int s, int e) {
        String[] sub = new String[e-s];
        System.arraycopy(es, s, sub, 0, e-s);
        return sub;
    }
    public static String toString(String[] arr, String splitter) {
        StringBuilder s = new StringBuilder();
        for(String a:arr)
            s.append(a).append(splitter);
        if(s.length() >= splitter.length())
            s.delete(s.length()-splitter.length(), s.length());
        return s.toString();
    }
    private static<E extends Comparable<E>> int arr_compare(E[] e1, E[] e2) {
        if(e1.length != e2.length) {
            if(e1.length>0 && e2.length>0)
                return e1[0].compareTo(e2[0]);
            else
                return e2.length - e1.length;
        } else {
            int c = 0;
            for (int i = 0; i < e1.length && c == 0; i++)
                c = e1[i].compareTo(e2[i]);
            return c;
        }
    }
}
