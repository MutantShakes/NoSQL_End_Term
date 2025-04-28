import java.io.*;
import java.util.*;

public class PigSystem implements SystemInterface {
    private final String dataFile = "pig_data.csv"; // Simulates PigStorage
    private final String logFile = "oplogs/oplog.pig";
    private int counter = 1;

    public PigSystem() {
        File f = new File(dataFile);
        try {
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String sid, String cid, String grade) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, true))) {
            bw.write(sid + "," + cid + "," + grade);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OperationLog.appendOperation(logFile, counter++ + ", SET((" + sid + "," + cid + "), " + grade + ")");
    }

    public String get(String sid, String cid) {
        String latestGrade = "NULL";
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens[0].equals(sid) && tokens[1].equals(cid)) {
                    latestGrade = tokens[2]; // overwrite with latest
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        OperationLog.appendOperation(logFile, counter++ + ", GET(" + sid + "," + cid + ")");
        return latestGrade;
    }

    public void merge(String otherSystemName) {
        List<Operation> ops = OperationLog.readLog("oplogs/oplog." + otherSystemName.toLowerCase());
        for (Operation op : ops) {
            if (op.type.equals("SET")) this.set(op.studentId, op.courseId, op.grade);
        }
    }

    public void printData() {
        System.out.println("Pig simulated data:");
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
