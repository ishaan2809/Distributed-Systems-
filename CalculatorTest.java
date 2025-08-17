import org.junit.jupiter.api.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalculatorTest {
    private static Calculator calc;

    @BeforeAll
    public static void setup() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        calc = (Calculator) registry.lookup("CalculatorService");
    }

    @BeforeEach
    public void clearStack() throws Exception {
        while (!calc.isEmpty()) {
            calc.pop();
        }
    }

    // Single-client tests
    @Test @Order(1)
    public void testPushValueAndPop() throws Exception {
        calc.pushValue(10);
        assertEquals(10, calc.pop());
    }

    @Test @Order(2)
    public void testPushOperationMax() throws Exception {
        calc.pushValue(10); calc.pushValue(20); calc.pushValue(30);
        calc.pushOperation("max");
        assertEquals(30, calc.pop());
    }

    @Test @Order(3)
    public void testPushOperationGCD() throws Exception {
        calc.pushValue(12); calc.pushValue(18);
        calc.pushOperation("gcd");
        assertEquals(6, calc.pop());
    }

    @Test @Order(4)
    public void testPushOperationLCM() throws Exception {
        calc.pushValue(4); calc.pushValue(6);
        calc.pushOperation("lcm");
        assertEquals(12, calc.pop());
    }

    @Test @Order(5)
    public void testIsEmpty() throws Exception {
        assertTrue(calc.isEmpty());
        calc.pushValue(5);
        assertFalse(calc.isEmpty());
        calc.pop();
        assertTrue(calc.isEmpty());
    }

    @Test @Order(6)
    public void testDelayPop() throws Exception {
        calc.pushValue(42);
        long start = System.currentTimeMillis();
        int result = calc.delayPop(500);
        long end = System.currentTimeMillis();
        assertEquals(42, result);
        assertTrue((end - start) >= 500);
    }

    // Edge cases
    @Test @Order(7)
    public void testPopEmptyStackThrows() {
        assertThrows(RemoteException.class, () -> calc.pop());
    }

    @Test @Order(8)
    public void testInvalidOperatorThrows() {
        assertThrows(RemoteException.class, () -> calc.pushOperation("invalid"));
    }

    @Test @Order(9)
    public void testDelayPopZeroMillis() throws Exception {
        calc.pushValue(123);
        long start = System.currentTimeMillis();
        int result = calc.delayPop(0);
        long end = System.currentTimeMillis();
        assertEquals(123, result);
        assertTrue((end - start) < 100);
    }

    // Concurrency stress tests
    @Test @Order(10)
    public void testConcurrentPushAndMin() throws Exception {
        int threads = 5;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            final int val = (i + 1) * 10;
            new Thread(() -> {
                try {
                    ready.countDown();
                    go.await();
                    calc.pushValue(val);
                } catch (Exception ignored) {}
            }).start();
        }

        ready.await();
        go.countDown();
        Thread.sleep(300);

        calc.pushOperation("min");
        int result = calc.pop();
        assertEquals(10, result);
    }

    @Test @Order(11)
    public void testConcurrentDelayPopRace() throws Exception {
        calc.pushValue(999);

        int racers = 4;
        CountDownLatch ready = new CountDownLatch(racers);
        CountDownLatch go = new CountDownLatch(1);
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < racers; i++) {
            new Thread(() -> {
                try {
                    ready.countDown();
                    go.await();
                    int v = calc.delayPop(200);
                    results.add(v);
                } catch (Exception ignored) {}
            }).start();
        }

        ready.await();
        go.countDown();
        Thread.sleep(800);

        assertEquals(1, results.size());
        assertEquals(999, results.get(0));
        assertTrue(calc.isEmpty());
    }
}