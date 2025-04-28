#!/bin/bash
echo "🔁 Compiling CLI Client..."
mvn compile

echo "🧑‍💻 Launching SyncClient (CLI)..."
mvn exec:java -Dexec.mainClass=SyncClient

