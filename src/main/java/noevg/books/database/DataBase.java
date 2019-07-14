/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noevg.books.database;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;

/**
 *
 * @author eon
 */
public class DataBase {
    private MongoClient mongoClient;
    private MongoDatabase mongoDB;
    
    //private String url = "mongodb://noe:1234lucas@ds119788.mlab.com:19788/books";
    private String url ;
    private int port;
    private String dbUser;
    private String dbPass;
    private String dbName;
    

    
    
    public boolean createConnection(){
        boolean status = false;
        try{
            
            /*
            MongoCredential credential = MongoCredential.createCredential(getDbUser(), getDbName(), getDbPass().toCharArray());
            
            MongoClientSettings settings = MongoClientSettings.builder()
            .credential(credential)
            .applyToSslSettings(builder -> builder.enabled(true))
            .applyToClusterSettings(builder -> 
                builder.hosts(Arrays.asList(new ServerAddress(getUrl(), getPort()))))
            .build();
                        
            mongoClient = MongoClients.create(settings);
            */
            
            mongoClient = MongoClients.create("mongodb://"+getDbUser()+":"+getDbPass()+"@"+getUrl()+":"+getPort()+"/"+getDbName());
            mongoDB = mongoClient.getDatabase(getDbName());
            mongoDB.listCollectionNames().first();            
            status = true;
            
        }catch(Exception e){
            System.err.println("Error: "+e.getClass().getName()+": "+ e.getMessage());
            status = false;
        }
        return status;
    }    
    public boolean createCollection(String nameCollection){
        boolean status = true;
        mongoDB = mongoClient.getDatabase(getDbName());
        try{
            mongoDB.createCollection(nameCollection);            
        }catch(MongoCommandException e){
            System.out.println("Existe!!");
            status = false;
        }
        return status;
    }
    public boolean deleteCollection(String nameCollection){
        boolean status = true;
        mongoDB = mongoClient.getDatabase(getDbName());
        try{
            mongoDB.getCollection(nameCollection).drop();
        }catch(MongoCommandException e){
            System.out.println("No Existe!!");
            status = false;
        }
        return status;
    }
    public MongoIterable<String> getCollections(){
        mongoDB = mongoClient.getDatabase(getDbName());
        return mongoDB.listCollectionNames();
    }
    public String  getCountDocumentsDataCollection(String nameCollection){
        mongoDB = mongoClient.getDatabase(getDbName());
        MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
        return String.valueOf(collection.countDocuments());
    }
    public boolean addDocument(String nameCollection,Document document){
        boolean status = true;
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            collection.insertOne(document);
        }catch(MongoWriteException e){
            status = false;
        }
        return status;
    }
    public boolean updateAddDocument(String nameCollection,String idDocument,Document document){
        boolean status = true;
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            collection.updateOne(Filters.eq("_id", idDocument),Updates.push("books", document));
        }catch(MongoWriteException e){
            status = false;
        }
        return status;
    }
    public boolean removeBook(String nameCollection,String idDocument,String idBook){
        boolean status = true;
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            
            collection.updateOne(Filters.eq("_id", idDocument),
                                 Updates.pull("books", new Document().append("_id", idBook))
                                );
          
        }catch(MongoWriteException e){
            status = false;
        }
        return status;
    }
    public MongoIterable<Document> getDocuments(String nameCollection){
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            
            return collection.find();
        }catch(MongoException e){
            return null;
        }
    }
    public Document getDocument(String nameCollection,String idDocument){
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            return collection.find(eq("_id",idDocument)).first();
            
        }catch(MongoException e){
            return null;
        }
    }
    public boolean deleteTopic(String nameCollection,String idDocument){
        boolean status = true;
        try{
            mongoDB = mongoClient.getDatabase(getDbName());
            MongoCollection<Document> collection = mongoDB.getCollection(nameCollection);
            collection.deleteOne(eq("_id",idDocument));
        }catch(MongoException e){
            status = false;
        }
        return status;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
