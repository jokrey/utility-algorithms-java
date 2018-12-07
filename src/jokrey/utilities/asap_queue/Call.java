package jokrey.utilities.asap_queue;

/**
 * A call
 * @author jokrey
 */
@FunctionalInterface
public interface Call {
    /**
     * Execute the call
     * @throws CannotBeExecutedYetException if the call is impossible at this time
     * @throws CanNeverBeExecutedException if the call can never be executed - will remove the call from the queue
     */
    void call() throws CannotBeExecutedYetException, CanNeverBeExecutedException;
}