/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * Database access utilities (insert, find, aggregate)
 *
 * @author ciprian
 */
public class DbUtils {

    private static MongoClient mongoClient;
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 27017;

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            mongoClient = new MongoClient(IP_ADDRESS, PORT);
        }
        return mongoClient;
    }

    public static void insert(String dbName, String collectionName, Map<String, Object> content) {
        MongoClient client = getMongoClient();
        DB db = client.getDB(dbName);
        DBCollection collection = db.getCollection(collectionName);
        BasicDBObject doc = new BasicDBObject();
        doc.putAll(content);
        collection.insert(doc);
    }

    public static List find(MongoClient client, String dbName, String collectionName, Map<String, BsonValue> content) {
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection collection = db.getCollection(collectionName);
        BsonDocument doc = new BsonDocument();
        doc.putAll(content);
        FindIterable cursor = collection.find(doc);
        cursor.sort(new BasicDBObject("datetime", 1));
        List list = new ArrayList();
        return (List) cursor.into(list);
    }

    public static List<String> findDistinct(MongoClient client, String dbName, String collectionName, String filter) {
        DB db = client.getDB(dbName);
        DBCollection collection = db.getCollection(collectionName);
        return collection.distinct(filter);
    }

    public static AggregateIterable aggregate(MongoClient client, String dbName, String collectionName,
            String groupId, Map<String, Object> matchMap, int sort) {
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection collection = db.getCollection(collectionName);

        Document matchDoc = null;
        if (matchMap != null && matchMap.size() > 0) {
            matchDoc = new Document("$match", new Document(matchMap));
        }

        List<Bson> aggregateCriteria = new ArrayList<>();

        if (matchDoc != null) {
            aggregateCriteria.add(matchDoc);
        }

        aggregateCriteria.add(new Document("$group",
                new Document("_id", "$" + groupId)
                .append("count", new Document("$sum", 1))));

        if (sort != 0) {
            aggregateCriteria.add(new Document("$sort", new Document("count", sort)));
        }

        return collection.aggregate(aggregateCriteria);
    }

    public static void closeClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
