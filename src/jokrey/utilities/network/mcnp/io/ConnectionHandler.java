package jokrey.utilities.network.mcnp.io;

import java.io.IOException;

import jokrey.utilities.simple.data_structure.pairs.Pair;

public interface ConnectionHandler<CT extends ConnectionHandler.ConnectionState> {
    /** Called after the server has received a new c
     *
     * @param initial_cause initial cause
     * @param connection the c to the client
     * @throws IOException on i/o error
     * @return returns the generated state. Can be null.
     */
    CT newConnection(int initial_cause, MCNP_ConnectionIO connection) throws IOException;

    /** Called after the client has new data to read(without reading that data)
     * Should return to wait for new data.
     * @param type_cause interaction cause
     * @param connection the c to the client
     * @param state old state of the c
     * @return the altered or unaltered state
     * @throws IOException on i/o error
     */
    CT handleInteraction(TypedCause type_cause, MCNP_ConnectionIO connection, CT state) throws IOException;

    void connectionDropped(MCNP_ConnectionIO conn, CT state, boolean eof);
    void connectionDroppedWithError(Throwable t, MCNP_ConnectionIO conn, CT state);

    class TypedCause extends Pair<Integer, Integer> {
        public TypedCause(Integer type, Integer cause) {
            super(type, cause);
        }
        public int getType() {return getLeft();}
        public int getCause() {return getRight();}
    }
    class ConnectionState {
        public final int connection_type;
        public ConnectionState(int connection_type) {
            this.connection_type = connection_type;
        }
    }
}