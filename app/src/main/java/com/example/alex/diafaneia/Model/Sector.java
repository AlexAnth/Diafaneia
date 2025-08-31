package com.example.alex.diafaneia.Model;


/**
 * Created by Alex on 4/8/2016.
 */
public class Sector {


    private String sectorTitle;
    private String sectorCleanUrl;
    private String sectorId;

    public Sector(String sectorTitle, String sectorCleanUrl, String sectorId) {
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
