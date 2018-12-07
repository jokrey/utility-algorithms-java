package jokrey.utilities.command.line.helper;

import java.util.Arrays;
import java.util.Objects;

public abstract class ConstrainedCommand implements Command {
    private final String description;
    private final Argument[] argumentTemplates;

    public ConstrainedCommand(String description) {
        this(description, null);
    }

    /**
     * @param argumentTemplates null indicates that no check will be made regarding arguments
     */
    public ConstrainedCommand(String description, Argument[] argumentTemplates) {
        this.description = description;
        this.argumentTemplates = argumentTemplates;
    }

    public Argument[] accept(String[] args) {
        if(argumentTemplates!=null) {
//            for (Argument[] argumentTemplate : argumentTemplates) {
                if(args.length == argumentTemplates.length) {
                    boolean failureForInner = false;
                    for (int i = 0; i < args.length; i++) {
                        Argument a = argumentTemplates[i];
                        if (!a.set(args[i]))
                            failureForInner = true;
                            //do not break, why?
                    }
                    if (!failureForInner)
                        return argumentTemplates;
                }
//            }
            return null;
        }
        return Argument.from(args);
    }




    @Override public String toString() {
        return description + (argumentTemplates!=null? "  (args: "+ Arrays.toString(argumentTemplates) +")":"");
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstrainedCommand that = (ConstrainedCommand) o;
        return Arrays.equals(argumentTemplates, that.argumentTemplates);
    }
    @Override public int hashCode() {
        return Arrays.hashCode(argumentTemplates);
    }
}
