import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import java.util.*;

public class MongoSystem implements SystemInterface {
    private final String logFile = "oplogs/oplog.mongo";
    private MongoCollection<Document> collection;
    private int counter = 1;

    public MongoSystem() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = client.getDatabase("nosql_project");
        collection = db.getCollection("grades");
    }

    public void set(String sid, String cid, String grade) {
        Document query = new Document("student_id", sid).append("course_id", cid);
        Document update = new Document("$set", new Document("grade", grade));
        collection.updateOne(query, update, new UpdateOptions().upsert(true));
        OperationLog.appendOperation(logFile, counter++ + ", SET((" + sid + "," + cid + "), " + grade + ")");
    }

    public String get(String sid, String cid) {
        Document doc = collection.find(new Document("student_id", sid).append("course_id", cid)).first();
        String result = doc != null ? doc.getString("grade") : "NULL";
        OperationLog.appendOperation(logFile, counter++ + ", GET(" + sid + "," + cid + ")");
        return result;
    }

    public void merge(String otherSystemName) {
        List<Operation> ops = OperationLog.readLog("oplogs/oplog." + otherSystemName.toLowerCase());
        for (Operation op : ops) {
            if (op.type.equals("SET")) this.set(op.studentId, op.courseId, op.grade);
        }
    }

    public void printData() {
        System.out.println("Mongo Grades:");
        collection.find().forEach(System.out::println);
    }
}
