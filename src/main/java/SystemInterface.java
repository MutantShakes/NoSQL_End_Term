public interface SystemInterface {
    void set(String studentId, String courseId, String grade);
    String get(String studentId, String courseId);
    void merge(String otherSystem);
    void printData();
}
