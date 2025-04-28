import java.util.*;
import java.io.*;

public class OperationLog {
    public static List<Operation> readLog(String filename) {
        List<Operation> ops = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                Operation op = Operation.parse(line);
                if (op != null) ops.add(op);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ops;
    }


    public static void appendOperation(String filename, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
