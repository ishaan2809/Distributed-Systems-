import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the Calculator RMI service.
 * This interface defines the operations that can be invoked remotely by clients.
 * Clients can push values and operations onto a shared stack on the server,
 * pop results, check if the stack is empty, and perform delayed operations.
 * All methods throw RemoteException because they are remote calls
 * and may fail due to network issues.
 **/

public interface Calculator extends Remote {

    /**
     * Pushes an integer value onto the stack.
     * @param val the integer value to push
     * @throws RemoteException if a remote communication error occurs
     * @return none
     **/
    void pushValue(int val) throws RemoteException;

    /**
     * Pushes an operation onto the stack. The operation will cause the server
     * to pop all values currently on the stack and compute a result.
     * Supported operations:
     * - "min": pushes the minimum of all popped values
     * - "max": pushes the maximum of all popped values
     * - "gcd": pushes the greatest common divisor of all popped values
     * - "lcm": pushes the least common multiple of all popped values
     * @param operator the operation to perform ("min", "max", "gcd", "lcm")
     * @throws RemoteException if a remote communication error occurs
     * @return none
     **/
    void pushOperation(String operator) throws RemoteException;

    /**
     * Pops the top value from the stack and returns it.
     * @return the integer value popped from the stack
     * @throws RemoteException if the stack is empty or a remote error occurs
     **/
    int pop() throws RemoteException;

    /**
     * Checks if the stack is empty.
     * @return true if the stack is empty, false otherwise
     * @throws RemoteException if a remote communication error occurs
     **/
    boolean isEmpty() throws RemoteException;

    /**
     * Waits for a specified number of milliseconds before popping
     * the top value from the stack.
     * @param millis the delay in milliseconds
     * @return the integer value popped after the delay
     * @throws RemoteException if interrupted during delay or a remote error occurs
     **/
    int delayPop(int millis) throws RemoteException;
}
