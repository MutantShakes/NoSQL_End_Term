import java.io.*;

public class TestCaseExecutor {
    public MongoSystem mongo = new MongoSystem();
    public PigSystem pig = new PigSystem();
    public SQLSystem sql = new SQLSystem();
//    public HiveSystem hive = new HiveSystem();
 
    public void execute(String testFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(testFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(".SET")) handleSet(line);
                else if (line.contains(".GET")) handleGet(line);
                else if (line.contains(".MERGE")) handleMerge(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSet(String line) {
        try {
            // Expected format: 1, PIG.SET((SID103,CSE016), A)
            int commaIndex = line.indexOf(",");
            if (commaIndex == -1) throw new IllegalArgumentException("Missing comma in line: " + line);

            String afterComma = line.substring(commaIndex + 1).trim();   // e.g. "PIG.SET((SID103,CSE016), A)"
            int systemDotIndex = afterComma.indexOf('.');
            String system = afterComma.substring(0, systemDotIndex).trim(); // "PIG"

            int openParen = afterComma.indexOf('(');
            int closeParen = afterComma.indexOf(')', openParen);
            String sidcid = afterComma.substring(openParen + 1, closeParen); // "SID103,CSE016"

            String[] ids = sidcid.replace("(", "").replace(")", "").split(",");
            if (ids.length < 2) throw new IllegalArgumentException("Invalid SID/CID: " + line);

            String sid = ids[0].trim();
            String cid = ids[1].trim();

            // Grade comes after ", " after the closing parenthesis
            String rest = afterComma.substring(closeParen + 2).trim();  // e.g. "A"
            rest = rest.replace(")","");
            getSystem(system).set(sid, cid, rest);
        } catch (Exception e) {
            System.err.println("❌ Error parsing SET line: " + line);
            e.printStackTrace();
        }
    }





    private void handleGet(String line) {
        try {
            // Expected: 2, PIG.GET(SID103,CSE016)
            int commaIndex = line.indexOf(",");
            if (commaIndex == -1) throw new IllegalArgumentException("Missing comma in line: " + line);

            String afterComma = line.substring(commaIndex + 1).trim(); // "PIG.GET(SID103,CSE016)"
            int systemDotIndex = afterComma.indexOf('.');
            String system = afterComma.substring(0, systemDotIndex).trim(); // "PIG"

            int openParen = afterComma.indexOf('(');
            int closeParen = afterComma.indexOf(')', openParen);
            String sidcid = afterComma.substring(openParen + 1, closeParen); // "SID103,CSE016"

            String[] ids = sidcid.split(",");
            if (ids.length < 2) throw new IllegalArgumentException("Invalid SID/CID: " + line);

            String sid = ids[0].trim();
            String cid = ids[1].trim();

            String result = getSystem(system).get(sid, cid);
            System.out.println(system + ".GET(" + sid + "," + cid + ") = " + result);
        } catch (Exception e) {
            System.err.println("❌ Error parsing GET line: " + line);
            e.printStackTrace();
        }
    }


    private void handleMerge(String line) {
        try {
            // Expected: PIG.MERGE(SQL)
            if (!line.contains(".MERGE(")) throw new IllegalArgumentException("Malformed MERGE line: " + line);

            String[] parts = line.split("\\.MERGE\\(");
            if (parts.length != 2) throw new IllegalArgumentException("Malformed MERGE syntax: " + line);

            String caller = parts[0].trim();  // e.g. "PIG"
            String callee = parts[1].replace(")", "").trim();  // e.g. "SQL"

            getSystem(caller).merge(callee);
            System.out.println(caller + " merged with " + callee);
        } catch (Exception e) {
            System.err.println("❌ Error parsing MERGE line: " + line);
            e.printStackTrace();
        }
    }


    private SystemInterface getSystem(String name) {
        return switch (name.trim().toUpperCase()) {
            case "MONGO" -> mongo;
            case "PIG" -> pig;
            case "SQL" -> sql;
//            case "HIVE" -> hive;
            default -> throw new IllegalArgumentException("Unknown system: " + name);
        };
    }


}
