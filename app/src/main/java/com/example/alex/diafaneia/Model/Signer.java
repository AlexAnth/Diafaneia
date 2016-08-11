package com.example.alex.diafaneia.Model;

/**
 * Created by user1 on 10/8/2016.
 */
public class Signer {


    private String signerId;
    private String signerFullName;
    private String signerInfo;
    private String signerCleanUrl;

    private String signer_url = "(URL_TELIKOI_IPOGRAFONTES)";
    private String  base_url = "(URL_BASE)";


    // constructor
    public Signer(String signerFullName, String signerId, String signerCleanUrl) {
        this.signerId = signerId;
        this.signerFullName = signerFullName;
        this.signerCleanUrl = signerCleanUrl;

    }


    // constructor default
    public Signer(String signerFullName ) {
        this.signerFullName = signerFullName;
    }

    //Getters
    public String getSignerId() {
        return signerId;
    }

    public String getSignerFullName() {
        return signerFullName;
    }

    public String getSignerInfo() {
        return signerInfo;
    }

    public String getSignerCleanUrl() {
        return signerCleanUrl;
    }


}
