import java.sql.*;
import java.util.*;

public class SQLSystem implements SystemInterface {
    private final String logFile = "oplogs/oplog.sql";
    private Connection conn;
    private int counter = 1;

    public SQLSystem() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nosql_project", "root", "142002");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void set(String sid, String cid, String grade) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO grades (student_id, course_id, grade) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE grade = ?");
            stmt.setString(1, sid);
            stmt.setString(2, cid);
            stmt.setString(3, grade);
            stmt.setString(4, grade);
            stmt.executeUpdate();
            OperationLog.appendOperation(logFile, counter++ + ", SET((" + sid + "," + cid + "), " + grade + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String sid, String cid) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT grade FROM grades WHERE student_id = ? AND course_id = ?");
            stmt.setString(1, sid);
            stmt.setString(2, cid);
            ResultSet rs = stmt.executeQuery();
            String result = rs.next() ? rs.getString("grade") : "NULL";
            OperationLog.appendOperation(logFile, counter++ + ", GET(" + sid + "," + cid + ")");
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void merge(String otherSystemName) {
        List<Operation> ops = OperationLog.readLog("oplogs/oplog." + otherSystemName.toLowerCase());
        for (Operation op : ops) {
            if (op.type.equals("SET")) this.set(op.studentId, op.courseId, op.grade);
        }
    }

    public void printData() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM grades");
            System.out.println("SQL Grades:");
            while (rs.next()) {
                System.out.println(rs.getString("student_id") + " " + rs.getString("course_id") + " " + rs.getString("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
