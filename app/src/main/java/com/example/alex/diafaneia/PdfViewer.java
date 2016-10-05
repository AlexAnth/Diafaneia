package com.example.alex.diafaneia;

/**
 * Created by Alex on 30/9/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuItem;

import com.example.alex.diafaneia.Utils.PdfRendererBasicFragment;

import java.io.File;




public class PdfViewer extends Activity {
    public  File file;


    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Intent intent = getIntent();
        String filename = intent.getStringExtra("filename");
        String url = intent.getStringExtra("url");

        File file = new File(Environment.getExternalStorageDirectory() + "/Αποφάσεις/" + filename);
        if (!file.exists()) {
            file_download(url, filename);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PdfRendererBasicFragment(),
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new AlertDialog.Builder(this)
//                        .setMessage(R.string.intro_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }













    public void file_download(String uRl,String filename) {
        File direct = new File(Environment.DIRECTORY_DOWNLOADS
                + "/Αποφάσεις");

        if (!direct.exists()) {
            direct.mkdirs();
        }


        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir("/Αποφάσεις", filename);

        mgr.enqueue(request);

    }








}




