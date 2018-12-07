package jokrey.utilities.asap_queue;

/**
 * Non exceptions as flow control version of {@link Call}.
 *
 * @author jokrey
 */

public interface PredeterminableCall extends Call {
    /**
     * If it is previously determinable whether or not a call is possible, then return false here.
     *
     * (inverted pre query version of {@link CannotBeExecutedYetException}
     * @return whether the call is possible
     */
    boolean canBeCalled();

    /**
     * If it is previously determinable whether or not a call is never possible, then return false here.
     * If false is returned then the call will be removed from the queue.
     *
     * (inverted pre query version of {@link CanNeverBeExecutedException}
     * @return whether the call will ever be possible
     */
    boolean canEverBeCalled();

    static PredeterminableCall fromCall(Call call) {
        return new PredeterminableCall() {
            @Override public void call() throws CannotBeExecutedYetException, CanNeverBeExecutedException {
                call.call();
            }
            @Override public boolean canBeCalled() { return true; }
            @Override public boolean canEverBeCalled() {
                return true;
            }
        };
    }
}