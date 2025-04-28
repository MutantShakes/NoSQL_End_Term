#!/bin/bash
echo "🔁 Compiling GUI..."
mvn compile

echo "🖥️ Launching GUI client..."
mvn exec:java -Dexec.mainClass=SyncAppGUI

