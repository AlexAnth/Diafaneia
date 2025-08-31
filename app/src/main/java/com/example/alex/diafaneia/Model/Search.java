package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Search {

    private String sector;
    private String document;
    private String type;
    private String signer;
    private String ADA;
    private String protoc_Num;
    private String publishDate;
    private String fileURL;
    private String pathName;
    private String sbject;
    private String ID;


    public Search(String sector, String document, String type, String signer, String ADA, String protoc_Num, String fileURL, String pathName,
                  String sbject, String publishDate, String ID) {
        this.sector = sector;
        this.document = document;
        this.type = type;
        this.signer = signer;
        this.ADA = ADA;
        this.protoc_Num = protoc_Num;
        this.fileURL = fileURL;
        this.pathName = pathName;
        this.sbject = sbject;
        this.fileURL = fileURL;
        this.pathName = pathName;
        this.sbject = sbject;
        this.publishDate = publishDate;
        this.ID = ID;
    }

    public String getDocument() {
        return document;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getSector() {
        return sector;
    }

    public String getType() {
        return type;
    }

    public String getSigner() {
        return signer;
    }

    public String getADA() {
        return ADA;
    }

    public String getProtoc_Num() {
        return protoc_Num;
    }

    public String getFileURL() {
        return fileURL;
    }

    public String getPathName() {
        return pathName;
    }

    public String getSbject() {
        return sbject;
    }

    public String getID() {
        return ID;
    }


}
