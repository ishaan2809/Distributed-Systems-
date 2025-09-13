Weather Aggregation Server
==========================

Purpose 
-----------------------
This project is a simple, educational client/server system that collects weather data from “content servers” and serves it to “read clients”.

- ContentServer reads a tiny text file (key:value lines), turns it into JSON, and sends it to the server using an HTTP-like PUT.
- AggregationServer stores the latest data for each station, keeps time using a Lamport clock for consistency, expires old data, and saves everything to disk so it survives restarts.
- GETClient fetches the current data (all stations or a single station) and prints it as easy-to-read lines.

What you need installed
-----------------------
- Java JDK 17 or newer
- A JSON library JAR in `lib/` (the repo already downloads `json-20240303.jar` to `lib/`)

Project layout (what’s inside)
------------------------------
- `src/AggregationServer.java`  → the server process (listens on a TCP port)
- `src/ContentServer.java`      → sends weather updates to the server (reads a local file)
- `src/GETClient.java`          → reads data from the server and prints it
- `src/JSONUtils.java`          → helper to parse key:value files to JSON
- `src/LamportClock.java`       → tiny Lamport clock class used everywhere
- `src/TestRunner.java`         → runs all JUnit tests
- Tests in `src/*Test.java`     → JUnit tests (run via TestRunner)

How to build (one command)
--------------------------
From the project root:

    javac -cp ".;out;lib/*" -d out src/*.java

How to run the server
---------------------
- Default port (4567):

    java -cp ".;out;lib/*" AggregationServer

- Custom port (example: 9000):

    java -cp ".;out;lib/*" AggregationServer 9000

How to run the ContentServer
----------------------------
Create a station file (example `station.txt`) with lines like:

    id: IDS60901
    name: Adelaide (West Terrace / ngayirdapira)
    lat: -34.9
    lon: 138.6
    air_temp: 13.3

Send it to the server:

    java -cp ".;out;lib/*" ContentServer http://localhost:4567 station.txt

You’ll see a status line like `HTTP/1.1 201 Created` on the first upload and `HTTP/1.1 200 OK` on updates.

How to run the GET client
-------------------------
- Get all stations:

    java -cp ".;out;lib/*" GETClient http://localhost:4567

- Get one station by id:

    java -cp ".;out;lib/*" GETClient http://localhost:4567 IDS60901

What the wire protocol looks like
---------------------------------
- PUT /weather.json
  - Headers: `Content-Type: application/json`, `Content-Length: N`, `X-Lamport-Clock: <int>`
  - Body: JSON with at least `id`
  - Response codes: 201 (first/new-after-expiry), 200 (update), 204 (no body), 400 (bad request), 500 (invalid JSON)

- GET /weather.json[?id=...]
  - Returns JSON `{"stations":[ ... ]}`. Includes server Lamport time in the payload and header.

How consistency works (Lamport clocks)
--------------------------------------
- Every request carries a Lamport value. The server updates its clock based on what it receives.
- PUTs are applied in Lamport order (with a tiny nano tie-breaker when equal), so updates from multiple content servers are serialized safely.
- GET takes a snapshot only after all earlier PUTs (by Lamport time) are applied.

Persistence and safety
----------------------
- Data is saved atomically: write to a temp file, then move it in one step. If the server crashes mid-write, it recovers cleanly on restart.
- The server removes stations that haven’t been heard from in 30 seconds and keeps only the 20 most recent stations to bound memory.

How to run the automated tests
------------------------------
We provide a simple TestRunner that invokes JUnit directly.

1) Build everything:

    javac -cp ".;out;lib/*" -d out src/*.java

2) Run the suite:

    java -cp ".;out;lib/*" TestRunner

You'll see a detailed pass/fail report for:
- Lamport clock logic
- JSON parsing and client failure handling
- Server functionality, error codes, concurrency

**Expected Results**: All 11 tests should pass, demonstrating:
- ✅ Thread-safe Lamport clock operations
- ✅ Proper JSON parsing and validation
- ✅ HTTP protocol compliance (201/200/204/400/500 status codes)
- ✅ Concurrent PUT operations from multiple clients
- ✅ Data persistence and recovery
- ✅ Error handling for various failure scenarios

Troubleshooting (common problems)
---------------------------------
- "Could not find or load main class": ensure you ran the compile command and `out/` contains class files.
- "Connection refused" from clients: make sure the server is running and the port is correct.
- PUT returns 500: your station file must include `id:` and produce valid JSON fields.
- Nothing appears on GET: verify that you successfully sent a PUT and the station hasn’t expired (30s idle removal).

Notes
-----
- Everything is raw sockets for learning purposes (no heavy web frameworks).
- You can point multiple ContentServers at one AggregationServer to simulate many feeds.
- The code uses only standard JDK + `org.json`.

Design summary
--------------
If you’re curious about internals, `DESIGN.md` explains the main components, thread model, ordering guarantees, and persistence approach in friendly language.


