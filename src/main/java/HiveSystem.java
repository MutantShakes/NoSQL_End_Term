import java.sql.*;
import java.util.List;

public class HiveSystem implements SystemInterface {
    private final String logFile = "oplog.hive";
    private Connection conn;

    public HiveSystem() {
        try {
//            Class.forName("org.apache.hive.jdbc.HiveDriver");
//            conn = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "", "");
        } catch (Exception e) {
//            System.out.println("CONNECTION ERROR !!!");
            e.printStackTrace();
        }
    }


    public void set(String sid, String cid, String grade) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO student_course_grades VALUES('" + sid + "','" + cid + "','" + grade + "')");
            OperationLog.appendOperation(logFile, System.currentTimeMillis() + ", SET((" + sid + "," + cid + "), " + grade + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String get(String sid, String cid) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT grade FROM student_course_grades WHERE student_id = '" + sid + "' AND course_id = '" + cid + "' LIMIT 1");
            if (rs.next()) return rs.getString("grade");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public void merge(String otherSystemName) {
        List<Operation> ops = OperationLog.readLog("oplogs/oplog." + otherSystemName.toLowerCase());
        for (Operation op : ops) {
            if (op.type.equals("SET")) this.set(op.studentId, op.courseId, op.grade);
        }
    }



    public void printData() {
        // optional: display full table for debugging
    }
}
