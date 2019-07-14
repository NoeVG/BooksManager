/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noevg.books.gui.ModelTable;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import noevg.books.database.DataBase;
import org.bson.Document;

/**
 *
 * @author eon
 */
public class ModelTable extends DefaultTableModel{
    private DataBase dataBase;
    public ModelTable(String heads[]){
        
        for(String head : heads){
            this.addColumn(head);
        }
    }
    public void loadDataCategories(DataBase dataBase){
        MongoIterable<String> categories = dataBase.getCollections();
        
        int r = 0;
        for(String category : categories){
            this.setNumRows(r+1);
            //value,row,col
            this.setValueAt(r, r, 0);
            this.setValueAt(category, r, 1);
            this.setValueAt(dataBase.getCountDocumentsDataCollection(category), r, 2);
            r++;
        }
    }
    public void loadDataTopics(DataBase dataBase, String nameCollection){
        MongoIterable<Document> documents = dataBase.getDocuments(nameCollection);
        int r = 0;
        for(Document document : documents){
            this.setNumRows(r+1);
            //value,row,col
            this.setValueAt(r, r, 0);
            this.setValueAt(document.getString("_id"), r, 1);
            this.setValueAt(((ArrayList<Document>)document.get("books")).size(), r, 2);
            this.setValueAt(document.getString("created"), r, 3);
            r++;
        }
    }
    public void loadDataBooks(DataBase dataBase,String nameCollection,String idDocument){
        ArrayList<Document> books = (ArrayList<Document>)dataBase.getDocument(nameCollection, idDocument).get("books");
        int r = 0;
        for(Document document : books){
            this.setNumRows(r+1);
            //value,row,col
            this.setValueAt(r, r, 0);
            this.setValueAt(document.getString("_id"), r, 1);
            this.setValueAt(document.getString("title"), r, 2);
            this.setValueAt(document.getString("author"), r, 3);
            this.setValueAt(document.getString("editorial"), r, 4);
            this.setValueAt(document.getString("path"), r, 5);
            this.setValueAt( Double.toString(document.getDouble("size")), r, 6);
            this.setValueAt( document.getString("idGoogle"), r, 7);
            r++;
        }
    }
}
