import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client class to test the Calculator RMI service.
 * Connects to the RMI registry
 *  Looks up the Calculator service
 *  Calls remote methods via server object on the server
 *  Prints results to the console
 */
public class CalculatorClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Calculator calc = (Calculator) registry.lookup("CalculatorService");

            // Test pushValue
            calc.pushValue(10);
            calc.pushValue(20);
            calc.pushValue(30);

            // Test pushOperation (max)
            calc.pushOperation("max");
            System.out.println("Max of values: " + calc.pop());

            // Test (gcd)
            calc.pushValue(12);
            calc.pushValue(18);
            calc.pushOperation("gcd");
            System.out.println("GCD: " + calc.pop());

            // Test (lcm)
            calc.pushValue(4);
            calc.pushValue(6);
            calc.pushOperation("lcm");
            System.out.println("LCM: " + calc.pop());

            // Test delayPop
            calc.pushValue(99);
            System.out.println("DelayPop : " + calc.delayPop(2000));

            // Test isEmpty
            System.out.println("Is stack empty? " + calc.isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}