package com.example.alex.diafaneia;


/**
 * Created by Alex on 4/8/2016.
 */
public class Sector {


    private String sectorTitle;
    private String sectorCleanUrl;
    private String sectorId;
    private String parentSector;
    private String sector_url = "(URL_DIEFTHINSEIS_TMIMATA)";
    private String  base_url = "(URL_BASE)";


    public Sector(String sectorTitle ,String sectorCleanUrl ,String sectorId ) {
        this.sectorTitle = sectorTitle;
        this.sectorCleanUrl = sectorCleanUrl;
        this.sectorId = sectorId;
    }

    public Sector(String sectorTitle) {
        this.sectorTitle = sectorTitle;
    }

    public String getSectorCleanUrl() {
        return sectorCleanUrl;
    }

    public String getSectorTitle() {
        return sectorTitle;
    }

    public String getSectorId() {
        return sectorId;
    }




}
