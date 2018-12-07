package jokrey.utilities.asap_queue;

import java.util.*;

/**
 * "Queue" that stores calls that may be executable at a later time.
 * Requires a later call to one of {@link #tryCallAllAsap()}, {@link #callAndRemoveAll_orCancel()},  {@link #callAndRemoveAll_orIgnore()}.
 * Has it's applications.
 */
public class ASAP_Queue {
    private final HashMap<Integer, PredeterminableCall> once_calls = new HashMap<>();
    private final Queue<PredeterminableCall> call_queue = new LinkedList<>();

    /**
     * @return whether any calls are in the queue
     */
    public boolean hasCalls() {
        return call_queue.isEmpty() && once_calls.isEmpty();
    }

    /**
     * Will go through each call and attempt to call them.
     * If a call cannot be called then it goes back into the queue.
     *    Unless they indicate that they can never be called.
     */
    public void tryCallAllAsap() {
        PredeterminableCall call;
        while((call = call_queue.poll()) != null) {
            callAsap(call);//would re-add on fail, unless can never be called
        }

        Iterator<Map.Entry<Integer, PredeterminableCall>> once_iter = once_calls.entrySet().iterator();
        while(once_iter.hasNext()) {
            Map.Entry<Integer, PredeterminableCall> once_call = once_iter.next();
            once_iter.remove();
            callOnceAsap(once_call.getKey(), once_call.getValue());
        }
    }

    /**
     * Will remove every call and attempt each of them one last time.
     */
    public void callAndRemoveAll_orIgnore() {
        PredeterminableCall call;
        while((call = call_queue.poll()) != null) {
            tryCall_ignore(call);
        }

        Iterator<Map.Entry<Integer, PredeterminableCall>> once_iter = once_calls.entrySet().iterator();
        while(once_iter.hasNext()) {
            Map.Entry<Integer, PredeterminableCall> once_call = once_iter.next();
            once_iter.remove();
            tryCall_ignore(once_call.getValue());
        }
    }

    /**
     * Will remove every call and attempt each of them one last time.
     * If a call fails it will throw an exception and not be removed.
     * @throws CannotBeExecutedException if a call cannot be executed
     */
    public void callAndRemoveAll_orCancel() throws CannotBeExecutedException {
        Iterator<PredeterminableCall> queue_iter = call_queue.iterator();
        while(queue_iter.hasNext()) {
            PredeterminableCall call = queue_iter.next();
            tryCall(call);
            queue_iter.remove();
        }

        Iterator<Map.Entry<Integer, PredeterminableCall>> once_iter = once_calls.entrySet().iterator();
        while(once_iter.hasNext()) {
            Map.Entry<Integer, PredeterminableCall> once_call = once_iter.next();
            tryCall(once_call.getValue());
            once_iter.remove();
        }
    }


    public void callPostponed(Call call) {
        callPostponed(PredeterminableCall.fromCall(call));
    }
    public void callPostponed(PredeterminableCall call) {
        call_queue.add(call);
    }

    public boolean callAsap(Call call) {
        return callAsap(PredeterminableCall.fromCall(call));
    }
    public boolean callAsap(PredeterminableCall call) {
        try {
            if(!call.canBeCalled()) {
                callPostponed(call);
                return false;
            } else if(!call.canEverBeCalled()) {
                return false;
            } else {
                call.call();
                return true;
            }
        } catch (CannotBeExecutedYetException e) {
            callPostponed(call);
            return false;
        } catch (CanNeverBeExecutedException e) {
            return false;
        }
    }

    public void callOncePostponed(int uid, Call call) {
        callOncePostponed(new Integer(uid), PredeterminableCall.fromCall(call));
    }
    public void callOncePostponed(int uid, PredeterminableCall call) {
        callOncePostponed(new Integer(uid), call);
    }
    private void callOncePostponed(Integer uid, PredeterminableCall call) {
        once_calls.put(uid, call);
    }

    public boolean callOnceAsap(int uid, Call call) {
        return callOnceAsap(new Integer(uid), PredeterminableCall.fromCall(call));
    }
    public boolean callOnceAsap(int uid, PredeterminableCall call) {
        return callOnceAsap(new Integer(uid), call);
    }
    private boolean callOnceAsap(Integer uid, PredeterminableCall call) {
        try {
            if(!call.canBeCalled()) {
                callOncePostponed(uid, call);
                return false;
            } else if(!call.canEverBeCalled()) {
                return false;
            } else {
                call.call();
                return true;
            }
        } catch (CannotBeExecutedYetException e) {
            callOncePostponed(uid, call);
            return false;
        } catch (CanNeverBeExecutedException e) {
            return false;
        }
    }

    private void tryCall(PredeterminableCall call) throws CannotBeExecutedYetException, CanNeverBeExecutedException {
        if(!call.canBeCalled()) {
            throw new CannotBeExecutedYetException();
        } else if(!call.canEverBeCalled()) {
            throw new CanNeverBeExecutedException();
        } else {
            call.call();
        }
    }
    private void tryCall_ignore(PredeterminableCall call) {
        if(call.canBeCalled() && call.canEverBeCalled()) {
            try {
                call.call();
            } catch (CannotBeExecutedYetException | CanNeverBeExecutedException ignored) {}
        }
    }
}