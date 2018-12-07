package jokrey.utilities.asap_queue;

/**
 * Used as flow control when the call can't be executed yet, but might be executable at a later time.
 *
 * (yeah, yeah i know: Exceptions as flow control is bad.. But there is an option not to use exceptions in {@link PredeterminableCall}).
 * @author jokrey
 */
public class CannotBeExecutedYetException extends CannotBeExecutedException {}
