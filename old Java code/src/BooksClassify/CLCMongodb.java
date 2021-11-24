package BooksClassify;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CLCMongodb {
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> dbCol;

    public static boolean connect(){
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("CLC");
        dbCol = database.getCollection("clcs");
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
        Document document = new Document();
        for (Map.Entry<String, Object> e : item.entrySet()) {
            document.append(e.getKey(), e.getValue());
        }
        dbCol.insertOne(document);
        return true;
    }

    public static Document search(String key, String val){
        return dbCol.find(new Document(key, val)).first();
    }


    public static boolean additems(){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("./CLC.txt"));
            String line;
            int i = 0;
            connect();


            while ( (line=reader.readLine()) != null) {
                String[] clcs = line.split("\\\\");

                int idx = clcs.length-1;
                String clc = clcs[idx];
                String clcid = clc.split(" ")[0].trim();

                HashMap<String,Object> item = new HashMap<>();
                ArrayList<String> children = new ArrayList<>();
                item.put("clc", clcid);
                item.put("fullname", clc);
                if (idx > 0) {
                    // add children fot existed item
                    String parentid = clcs[idx-1].split(" ")[0].trim();
                    item.put("parent", parentid);

                    Document parentfound = dbCol.find(new Document("clc", parentid)).first();
                    ArrayList<String> chdren = parentfound.get("children", ArrayList.class);
                    chdren.add(clcid);
                    Bson updatevalue = new Document("children", chdren);
                    Bson updateoperation = new Document("$set", updatevalue);
                    Bson filter = Filters.eq("clc", parentid);
                    dbCol.updateOne(filter, updateoperation);

                }
                item.put("children", children);

                additem(item);

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }



}
