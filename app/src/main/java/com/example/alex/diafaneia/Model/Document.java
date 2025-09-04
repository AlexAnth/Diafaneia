package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Document {

    private String documentTitle;
    private String documentId;

    public Document(String documentTitle, String documentId) {
        this.documentTitle = documentTitle;
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getDocumentId() {
        return documentId;
    }


}
