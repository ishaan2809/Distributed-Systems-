# Distributed-Systems - Calculator RMI Service

## Project Overview

This project implements a **Remote Method Invocation (RMI)** based calculator service in Java, demonstrating key concepts of distributed systems. The calculator operates on a shared stack and supports various mathematical operations including finding maximum, minimum, greatest common divisor (GCD), and least common multiple (LCM) of multiple values.

### Key Features
- **Stack-based calculator** with remote access capabilities
- **Mathematical operations**: max, min, GCD, LCM
- **Concurrent access** support for multiple clients
- **Delayed operations** with configurable wait times
- **Comprehensive testing** with JUnit 5 framework
- **RMI architecture** for distributed client-server communication

### Supported Operations
- `pushValue(int val)`: Push integer values onto the stack
- `pushOperation(String operator)`: Perform operations on all stack values
- `pop()`: Retrieve top value from stack
- `isEmpty()`: Check if stack is empty
- `delayPop(int millis)`: Pop value after specified delay

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **JUnit 5**: Included in the `lib/` directory
- **RMI Support**: Built into Java SE (no additional installation required)

## Project Structure

```
Ass-1/
├── src/
│   ├── Calculator.java              # RMI interface defining remote methods
│   ├── CalculatorImplementation.java # Implementation of calculator logic
│   ├── CalculatorServer.java        # RMI server that hosts the service
│   ├── CalculatorClient.java        # Client for testing the service
│   └── CalculatorTest.java          # JUnit test suite
├── lib/
│   └── junit-platform-console-standalone-1.8.1.jar  # JUnit 5 testing framework
├── out/                             # Compiled class files
├── .idea/                           # IntelliJ IDEA project configuration
├── Ass-1.iml                       # IntelliJ IDEA module file
└── .gitignore                      # Git ignore patterns
```

## Compilation Instructions

1. **Navigate to project directory**:
   ```bash
   cd "path/to/Ass-1"
   ```

2. **Compile all Java source files**:
   ```bash
   javac -cp "lib/*" src/*.java
   ```

   This will compile all source files and place the `.class` files in the `src/` directory.

## Running Instructions

### 1. Start the RMI Server

**Terminal 1**: Start the calculator server
```bash
java -cp "src;lib/*" CalculatorServer
```

**Expected Output**:
```
Calculator Server is running...
```

The server will:
- Create an RMI registry on port 1099
- Bind the Calculator service to the registry
- Wait for client connections

### 2. Run the Calculator Client

**Terminal 2**: Test the calculator service
```bash
java -cp "src;lib/*" CalculatorClient
```

**Expected Output**:
```
Max of values: 30
GCD: 6
LCM: 12
DelayPop : 99
Is stack empty? true
```

The client demonstrates:
- Pushing multiple values onto the stack
- Performing mathematical operations (max, GCD, LCM)
- Testing delayed operations
- Verifying stack state

## Testing Instructions

### Run JUnit Test Suite

Execute the comprehensive test suite to verify all functionality:

```bash
java -cp "src;lib/*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

**Expected Output**:
```
Thanks for using JUnit! Support its development at https://junit.org/sponsoring

├─ JUnit Jupiter
│  └─ CalculatorTest
│     ├─ testPushValueAndPop()
│     ├─ testPushOperationMax()
│     ├─ testPushOperationGCD()
│     ├─ testPushOperationLCM()
│     ├─ testIsEmpty()
│     ├─ testDelayPop()
│     ├─ testPopEmptyStackThrows()
│     ├─ testInvalidOperatorThrows()
│     ├─ testDelayPopZeroMillis()
│     ├─ testConcurrentPushAndMin()
│     └─ testConcurrentDelayPopRace()

Test run finished after XXXX ms
[         3 containers found      ]
[        11 tests found           ]
[        11 tests started         ]
[        11 tests successful      ]
[         0 tests failed          ]
```

### Test Coverage

The test suite covers:
- **Basic Operations**: push, pop, isEmpty
- **Mathematical Operations**: max, min, GCD, LCM
- **Edge Cases**: empty stack handling, invalid operators
- **Concurrency**: multi-threaded access patterns
- **Timing**: delayPop functionality verification

## Architecture Details

### RMI Implementation
- **Interface**: `Calculator` extends `java.rmi.Remote`
- **Implementation**: `CalculatorImplementation` implements the interface
- **Server**: `CalculatorServer` creates registry and binds service
- **Client**: `CalculatorClient` looks up service and invokes remote methods

### Stack Operations
- Values are pushed onto a shared stack on the server
- Operations consume all values and push the result
- Thread-safe implementation for concurrent access
- Support for delayed operations with configurable timeouts

## Troubleshooting

### Common Issues

1. **Port Already in Use**: If port 1099 is occupied, modify the port in `CalculatorServer.java`
2. **Class Not Found**: Ensure all dependencies are in the classpath
3. **RMI Connection Refused**: Verify the server is running before starting the client

### Debug Mode
Enable detailed logging by adding `-Djava.rmi.server.logCalls=true` to JVM arguments.

## Contributing

This project is part of a Distributed Systems assignment. Feel free to:
- Report bugs or issues
- Suggest improvements
- Submit pull requests for enhancements

## License

This project is created for educational purposes as part of a Distributed Systems course assignment.
