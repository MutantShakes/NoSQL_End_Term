import java.io.*;

public class PigRunner {
    public static void runPigScript(String scriptPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("pig", scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("🐷 " + line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Pig script executed successfully.");
            } else {
                System.err.println("❌ Pig script failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to run Pig script: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        runPigScript("load_data.pig");
    }
}
