package com.example.alex.diafaneia.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user1 on 24/10/2016.
 */

public class Favourite extends RealmObject {

    @PrimaryKey
    private  String ID ;
    private  String sector ;
    private  String document ;
    private  String type ;
    private  String signer ;
    private  String ADA ;
    private  String protoc_Num ;
    private  String publishDate ;
    private  String fileURL ;
    private  String pathName;
    private  String sbject;

    public void setProtoc_Num(String protoc_Num) {
        this.protoc_Num = protoc_Num;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public void setADA(String ADA) {
        this.ADA = ADA;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void setSbject(String sbject) {
        this.sbject = sbject;
    }





    public String getSector() {
        return sector;
    }

    public String getID() {
        return ID;
    }

    public String getDocument() {
        return document;
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

    public String getPublishDate() {
        return publishDate;
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





}

