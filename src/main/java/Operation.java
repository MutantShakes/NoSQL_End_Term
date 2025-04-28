public class Operation {
    public int timestamp;
    public String type;
    public String studentId;
    public String courseId;
    public String grade;

    public Operation(int timestamp, String type, String studentId, String courseId, String grade) {
        this.timestamp = timestamp;
        this.type = type;
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }

    public static Operation parse(String line) {
        try {
            String[] parts = line.split(",\\s*", 2);
            if (parts.length != 2) throw new IllegalArgumentException("Invalid format: " + line);

            int timestamp = Integer.parseInt(parts[0].trim());
            String content = parts[1].trim();

            if (content.startsWith("SET")) {
                String[] args = content.substring(4, content.length() - 1).split("\\),\\s*");
                String[] ids = args[0].replace("(", "").split(",");
                String grade = args[1];
                return new Operation(timestamp, "SET", ids[0].trim(), ids[1].trim(), grade.trim());
            } else if (content.startsWith("GET")) {
                String[] ids = content.substring(4, content.length() - 1).split(",");
                return new Operation(timestamp, "GET", ids[0].trim(), ids[1].trim(), null);
            } else {
                throw new IllegalArgumentException("Invalid operation type: " + content);
            }

        } catch (Exception e) {
            System.err.println("❌ Skipping malformed line in oplog: " + line);
            return null; // or throw if you want to fail hard
        }
    }

}
