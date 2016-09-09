package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Search {

    private static String sector ;
    private static String document ;
    private static String type ;
    private static String signer ;
    private static String ADA ;
    private static String protoc_Num ;
    private static String publishDate ;
    private static String fileURL ;
    private static String pathName;
    private  static  String sbject;


    public Search(String sector, String document, String type, String signer, String ADA, String protoc_Num,String fileURL, String pathName,
                  String sbject,String publishDate){
        this.sector=sector;
        this.document=document;
        this.type=type;
        this.signer=signer;
        this.ADA=ADA;
        this.protoc_Num=protoc_Num;
        this.fileURL=fileURL;
        this.pathName=pathName;
        this.sbject=sbject;
        this.fileURL=fileURL;
        this.pathName=pathName;
        this.sbject=sbject;
        this.publishDate=publishDate ;
    }

    public static String getDocument() {
        return document;
    }

    public static String getPublishDate() {
        return publishDate;
    }

    public static String getSector() {
        return sector;
    }

    public static String getType() {
        return type;
    }

    public static String getSigner() {
        return signer;
    }

    public static String getADA() {
        return ADA;
    }

    public static String getProtoc_Num() {
        return protoc_Num;
    }

    public static String getFileURL() {
        return fileURL;
    }

    public static String getPathName() {
        return pathName;
    }

    public static String getSbject() {
        return sbject;
    }

}
