package BooksClassify;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class Mongodb {
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> dbCol;

    public static boolean connect(){
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("BabelLibrary");
        dbCol = database.getCollection("books");
        return true;
    }

    public static boolean isConnected(){
        MongoIterable<String> dbNameLists = mongoClient.listDatabaseNames();
        for (String sbName : dbNameLists) {
            System.out.println(sbName);
        }

        return true;
    }

    public static boolean additem(HashMap<String,Object> item){
        connect();
        Document document = new Document();
        for (Map.Entry<String, Object> e : item.entrySet()) {
            document.append(e.getKey(), e.getValue());
        }
        dbCol.insertOne(document);
        return true;
    }

}
