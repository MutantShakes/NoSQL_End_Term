import java.io.*;
import java.net.*;

public class SyncClient {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 9999);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String line;
            while ((line = userInput.readLine()) != null) {
                out.println(line);
                System.out.println("🔁 " + in.readLine());
            }
        } catch (IOException e) {
            System.err.println("❌ " + e.getMessage());
        }
    }
}
