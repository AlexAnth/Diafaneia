package com.example.alex.diafaneia;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alex.diafaneia.Utils.RVAdapter;
import com.example.alex.diafaneia.Utils.SharedPreference;


import java.io.File;
import java.util.ArrayList;


/**
 * Created by Alex on 3/8/2016.
 */
public class Bookmark extends AppCompatActivity {

    ArrayList <Object> Favourite_Collection= new ArrayList();

    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);
        mRecyclerView = (RecyclerView) findViewById(R.id.content);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVAdapter(Favourite_Collection);
        mRecyclerView.setAdapter(mAdapter);

        Favourite_Collection = loadFavourites();



        ImageView info_button=(ImageView)findViewById(R.id.info_btn);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Info.class);
                startActivity(intent);
            }

        });


    }

    private ArrayList loadFavourites() {

        // Get favourites
        ArrayList temp=   new SharedPreference().getFavorites(getApplicationContext());
        // Convert to ArrayList Object
        ArrayList<Object> strings = temp;
        Favourite_Collection.addAll(strings);
//        setUiProg();
        mAdapter.notifyDataSetChanged();
        return strings;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setOnItemClickListener(new RVAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                final String filename = Favourite_Collection.get(position).toString();
                final File file = new File(Environment.getExternalStorageDirectory() + "/Αποφάσεις/" + filename);
                if (!file.exists()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Το έγγραφο δεν βρέθηκε.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Context context = getApplicationContext();
                        CharSequence text = "Δέν βρέθηκε εφαρμογή προβολής αρχείων PDF.\nΚατεβάστε μία απο το διαδίκτυο.";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                }


            }
        });



    }

}

