package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Result {

    private static Sector sector ;
    private static Document document ;
    private static Type type ;
    private static Signer signer ;
    private static String ADA ;
    private static String protoc_Num ;
    private static String free_Text_str ;
    private static String fromDate ;
    private static String toDate ;

    public Result(Sector sector,Document document,Type type,Signer signer,String ADA,String protoc_Num,String free_Text_str,String fromDate,String toDate ){
        this.sector=sector;
        this.document=document;
        this.type=type;
        this.signer=signer;
        this.ADA=ADA;
        this.protoc_Num=protoc_Num;
        this.free_Text_str=free_Text_str;
        this.fromDate=fromDate;
        this.toDate=toDate;
    }

    public static Document getDocument() {
        return document;
    }

    public static Sector getSector() {
        return sector;
    }

    public static Type getType() {
        return type;
    }

    public static Signer getSigner() {
        return signer;
    }

    public static String getADA() {
        return ADA;
    }

    public static String getProtoc_Num() {
        return protoc_Num;
    }

    public static String getFree_Text_str() {
        return free_Text_str;
    }

    public static String getFromDate() {
        return fromDate;
    }

    public static String getToDate() {
        return toDate;
    }

}
