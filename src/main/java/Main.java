public class Main {
    public static void main(String[] args) {
        TestCaseExecutor executor = new TestCaseExecutor();
        executor.execute("inputs/testcase.in");

        System.out.println("\nFinal States:");
        executor.mongo.printData();
        executor.pig.printData();
        executor.sql.printData();
    }
}
