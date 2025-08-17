import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Stack;

/**
 * Implementation of the Calculator remote interface.
 * This class provides the actual logic for the calculator.
 * It uses a shared stack to store values and operations.
 * All methods are synchronized to ensure thread safety when multiple
 * clients access the server concurrently.
 **/
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    private Stack<Integer> stack;

    /**
     * Constructor for CalculatorImplementation.
     * Initializes the stack.
     * Super-you are running the parent class’s constructor to do that setup, otherwise code won't compile
     * @throws RemoteException if a remote communication error occurs
     **/
    public CalculatorImplementation() throws RemoteException {
        super();
        stack = new Stack<>();
    }

    /**
     * Pushes an integer value onto the stack.
     * @param val the integer value to push.
     * @return void function returns none.
     */
    @Override
    public synchronized void pushValue(int val) throws RemoteException {
        stack.push(val);
    }

    /**
     * Pushes an operation onto the stack. The operation will cause the server
     * to pop all values currently on the stack and compute a result.
     * Supported operations:
     * - "min": pushes the minimum of all popped values
     * - "max": pushes the maximum of all popped values
     * - "gcd": pushes the greatest common divisor of all popped values
     * (Take the first number, then compare it with each remaining number to compute the GCD of all numbers in the stack.)


     * - "lcm": pushes the least common multiple of all popped values
     * (Take numbers one by one, combine them using LCM, and leave only the final result in the stack)
     * Special cases:
     * - If the stack is empty,  a RemoteException is thrown.
     * - If the operator is invalid, a RemoteException is thrown.
     * @param operator the operation to perform ("min", "max", "gcd", "lcm")
     * @throws RemoteException if a remote communication error occurs or operator is invalid
     */
    @Override
    public synchronized void pushOperation(String operator) throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty! Cannot perform operation.");
        }
        int result = 0;

        switch (operator.toLowerCase()) {
            case "min":
                result = stack.stream().min(Integer::compare).get();
                stack.clear();
                stack.push(result);
                break;

            case "max":
                result = stack.stream().max(Integer::compare).get();
                stack.clear();
                stack.push(result);
                break;

            case "gcd":
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = gcd(result, stack.pop());
                }
                stack.push(result);
                break;

            case "lcm":
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = lcm(result, stack.pop());
                }
                stack.push(result);
                break;

            default:
                throw new RemoteException("Invalid operator: " + operator);
        }
    }

    /**
     * Pops the top value from the stack and returns it.
     * Special case:
     * - If the stack is empty, a RemoteException is thrown.
     * @return the integer value popped from the stack
     * @throws RemoteException if the stack is empty or a remote error occurs
     */
    @Override
    public synchronized int pop() throws RemoteException {
        if (stack.isEmpty()) throw new RemoteException("Stack is empty!");
        return stack.pop();
    }

    /**
     * Checks if the stack is empty.
     * @return true if the stack is empty, false otherwise
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public synchronized boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    /**
     * Waits for a specified number of milliseconds before popping
     * the top value from the stack.
     * Special cases:
     * - If interrupted during the delay, a RemoteException is thrown.
     * - If the stack is empty after the delay, a RemoteException is thrown.
     * @param millis the delay in milliseconds
     * @return the integer value popped after the delay
     * @throws RemoteException if interrupted during delay or stack is empty
     */
    @Override
    public synchronized int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RemoteException("Interrupted during delay", e);
        }
        return pop();
    }


    /**
     * Helper method to compute the greatest common divisor (GCD).
     * @param a first integer
     * @param b second integer
     * @return the GCD of a and b
     */
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * Helper method to compute the least common multiple (LCM).
     * @param a first integer
     * @param b second integer
     * @return the LCM of a and b
     */
    private int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }
}
