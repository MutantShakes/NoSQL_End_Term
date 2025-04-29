import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class SyncServer {
    private final MongoSystem mongo = new MongoSystem();
    private final SQLSystem sql = new SQLSystem();
    private final PigSystem pig = new PigSystem();
    private final HiveSystem hive = new HiveSystem();


    private int clientCount = 0;

    public static void main(String[] args) throws IOException {
        new SyncServer().start(9999);
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true); // ✅ Reuse port immediately
        serverSocket.bind(new InetSocketAddress(port));

        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.println("🟢 SyncServer started on port " + port);

        while (true) {
            Socket client = serverSocket.accept();
            int clientId = ++clientCount;
            System.out.println("👤 Client #" + clientId + " connected from " + client.getInetAddress());
            pool.execute(() -> handleClient(client, clientId));
        }
    }

    private void handleClient(Socket socket, int id) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Send initial greeting only once
            out.println("🟢 Connected as Client #" + id);

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("📨 Client #" + id + " → " + line);
                String response = process(line);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("❌ Client #" + id + " disconnected: " + e.getMessage());
        }
    }

    private String process(String input) {
        try {
            String[] parts = input.split(",");
            String op = parts[0];
            String sys = parts[1];
            SystemInterface s = switch (sys) {
                case "MONGO" -> mongo;
                case "SQL" -> sql;
                case "PIG" -> pig;
                case "HIVE" -> hive;
                default -> null;
            };
            return switch (op) {
                case "SET" -> {
                    s.set(parts[2], parts[3], parts[4]);
                    yield "✅ SET done on " + sys;
                }
                case "GET" -> {
                    String result = s.get(parts[2], parts[3]);
                    yield "📦 GET from " + sys + ": " + result;
                }
                case "MERGE" -> {
                    s.merge(parts[2]);
                    yield "🔄 MERGE " + parts[2] + " → " + sys + " complete";
                }
                default -> "❌ Unknown operation: " + op;
            };
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
