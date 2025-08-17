import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Server class to start the Calculator RMI service.
 * Creates an instance of CalculatorImplementation
 * so that the server has a real "calculator object" ready.
 * Then, clients can send requests to this object to do calculations (push, pop, etc.).
 * Starts the RMI registry
 * Binds the Calculator service to the registry
 */
public class CalculatorServer {
    public static void main(String[] args) {
        try {
            CalculatorImplementation calc = new CalculatorImplementation();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CalculatorService", calc);
            System.out.println("Calculator Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}