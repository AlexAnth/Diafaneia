package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Result {

    private Signer signer;
    private Sector sector;
    private Document document;
    private Type type;
    private String ADA;
    private String protoc_Num;
    private String free_Text_str;
    private String fromDate;
    private String toDate;

    public Result(Sector sector, Document document, Type type, Signer signer, String ADA, String protoc_Num, String free_Text_str, String fromDate, String toDate) {
        this.sector = sector;
        this.document = document;
        this.type = type;
        this.signer = signer;
        this.ADA = ADA;
        this.protoc_Num = protoc_Num;
        this.free_Text_str = free_Text_str;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Document getDocument() {
        return document;
    }

    public Sector getSector() {
        return sector;
    }

    public Type getType() {
        return type;
    }

    public Signer getSigner() {
        return signer;
    }

    public String getADA() {
        return ADA;
    }

    public String getProtoc_Num() {
        return protoc_Num;
    }

    public String getFree_Text_str() {
        return free_Text_str;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

}
