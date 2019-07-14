/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noevg.books.storage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 *
 * @author eon
 */
public class GoogleDrive extends Thread{
    private static final String APPLICATION_NAME = "books";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
//    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    
    private Drive service;
    
    private String nameFile;
    private String pathLocalFileUpload;
    private String pathLocalFileUploadCompress;
    
    private String urlFileUpload;
    private String idFileUpload;
    
    private boolean completeUpload;
    private boolean credentialBuild;
    private JProgressBar bar;
    
    public GoogleDrive(){
        
    }
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = GoogleDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
    public void BuildNeAuthorized(){
        this.setCredentialBuild(false);
        // Build a new authorized API client service.
        try{
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            this.setCredentialBuild(true);    
        }catch(GeneralSecurityException gse){
            this.setCredentialBuild(false);
            System.err.println(gse);
        }catch(IOException ioe){
            this.setCredentialBuild(false);
            System.err.println(ioe);
        }
    }
    public String uploadFile(String pathFile,String nameFile){
        String urlUploadFile = null;
        setCompleteUpload(false);
        try{
            
            File fileMetadata = new File();
            List<String> parents = Arrays.asList("1m-nj00WY7LhRFhmacJ9wkoeOLOogvzAs");
            fileMetadata.setParents(parents);
            fileMetadata.setName(nameFile);            
            java.io.File filePath = new java.io.File(pathFile);
            FileContent mediaContent = new FileContent("application/pdf", filePath);
            
            File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink, webViewLink, parents").execute();
            
            urlUploadFile = file.getWebViewLink();
            this.idFileUpload = file.getId();
        }catch (IOException ex) {
            Logger.getLogger(GoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
            urlUploadFile = null;
        }
        
        setCompleteUpload(true);
        this.urlFileUpload = urlUploadFile; 
        return urlUploadFile;
    }
    public String uploadFile(){
        String urlUploadFile = null;
        setCompleteUpload(false);
        try{
            
            File fileMetadata = new File();
            List<String> parents = Arrays.asList("1m-nj00WY7LhRFhmacJ9wkoeOLOogvzAs");
            fileMetadata.setParents(parents);
            fileMetadata.setName(this.nameFile);            
            java.io.File filePath = new java.io.File(this.pathLocalFileUpload);
            FileContent mediaContent = new FileContent("application/pdf", filePath);
            
            File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink, webViewLink, parents").execute();
            
            System.out.println("size file "+file.getSize());
            urlUploadFile = file.getWebViewLink();
        }catch (IOException ex) {
            Logger.getLogger(GoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
            urlUploadFile = null;
        }
        setCompleteUpload(true);
        this.urlFileUpload = urlUploadFile; 
        return urlUploadFile;
    }
    public void deleteFile(String idFileGoogle){
        try{
            service.files().delete(idFileGoogle).execute();
        }catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
    @Override
    public void run(){
        this.compressFile(this.getPathLocalFileUpload());
        setUrlFileUpload( uploadFile(getPathLocalFileUploadCompress(),getNameFile() ));
        this.bar.setValue(100);
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getPathLocalFileUpload() {
        return pathLocalFileUpload;
    }
    /*
    public String getPathLocalFileUploadCompress(){
        return compressFile(pathLocalFileUpload);
    }
    */
    public String getPathLocalFileUploadCompress(){
        return pathLocalFileUploadCompress;
    }
    public void setPathLocalFileUpload(String pathLocalFileUpload) {
        this.pathLocalFileUpload = pathLocalFileUpload;
    }

    public String getUrlFileUpload() {
        return urlFileUpload;
    }

    public void setUrlFileUpload(String urlFileUpload) {
        this.urlFileUpload = urlFileUpload;
    }

    public boolean isCompleteUpload() {
        return completeUpload;
    }

    public void setCompleteUpload(boolean completeUpload) {
        this.completeUpload = completeUpload;
    }

    public JProgressBar getBar() {
        return bar;
    }

    public void setBar(JProgressBar bar) {
        this.bar = bar;
    }

    public boolean isCredentialBuild() {
        return credentialBuild;
    }

    public void setCredentialBuild(boolean credentialBuild) {
        this.credentialBuild = credentialBuild;
    }

    public String getIdFileUpload() {
        return idFileUpload;
    }

    public void setIdFileUpload(String idFileUpload) {
        this.idFileUpload = idFileUpload;
    }

    public void compressFile(String filePath){  
        try{
            PdfReader reader = new PdfReader(filePath);
            PdfReader.unethicalreading = true;
            
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("my.pdf"),PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            stamper.close();
            java.io.File fileCompress = new java.io.File("my.pdf");
            if(fileCompress.exists()){
                pathLocalFileUploadCompress = fileCompress.getPath();
                System.out.println("Exist file compress: "+fileCompress.getPath());
            }
            else{
                pathLocalFileUploadCompress = filePath;
            }
        }catch(IOException | DocumentException e){
            System.out.println("Error!"+e.getMessage());
            pathLocalFileUploadCompress = filePath;
        }   
    }
}
