package jokrey.utilities.asap_queue;

/**
 * Used as flow control when the call can never be executed and retrying is futile.
 * The call will therefore be removed from the queue, if this exception was thrown.
 *
 * (yeah, yeah i know: Exceptions as flow control is bad.. But there is an option not to use exceptions in {@link PredeterminableCall}).
 * @author jokrey
 */
public class CanNeverBeExecutedException extends CannotBeExecutedException {}
