#!/bin/bash
echo "🔁 Compiling server..."
mvn compile

echo "🚀 Starting SyncServer..."
mvn exec:java -Dexec.mainClass=SyncServer

