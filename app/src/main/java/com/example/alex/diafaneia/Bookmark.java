package com.example.alex.diafaneia;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alex.diafaneia.Model.Favourite;
import com.example.alex.diafaneia.Model.Search;

import com.example.alex.diafaneia.Utils.RVAdapter2;
import com.example.alex.diafaneia.Utils.RVAdapter3;


import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by Alex on 3/8/2016.
 */
public class Bookmark extends AppCompatActivity {

    ArrayList <Search> Favourite_Collection= new ArrayList();

    private RecyclerView mRecyclerView;
    private RVAdapter3 mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize Realm
        Realm.init(getApplicationContext());

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);
        mRecyclerView = (RecyclerView) findViewById(R.id.content);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVAdapter3(Favourite_Collection);
        mRecyclerView.setAdapter(mAdapter);

        Favourite_Collection.addAll(convertFavsToSearch());
        mAdapter.notifyDataSetChanged();


        ImageView info_button=(ImageView)findViewById(R.id.info_btn);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Info.class);
                startActivity(intent);
                finish();

            }

        });

        ImageView bookmark_button=(ImageView)findViewById(R.id.bookmark_btn);
        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setOnItemClickListener(new RVAdapter3
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                final String filename = Favourite_Collection.get(position).getPathName();
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                RealmResults<Favourite> favs = realm.where(Favourite.class).findAll();
                realm.beginTransaction();
                favs.get(viewHolder.getAdapterPosition()).deleteFromRealm();
                realm.commitTransaction();
                mAdapter.remove(viewHolder.getAdapterPosition());
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(mRecyclerView);


    }


    private ArrayList<Search> convertFavsToSearch() {

        ArrayList<Search> s = new ArrayList<>();
        // Get favourites
        RealmResults<Favourite> favs = realm.where(Favourite.class).findAll();
        for (int i = 0; i < favs.size(); i++) {
            s.add(new Search(favs.get(i).getSector(), favs.get(i).getDocument(), favs.get(i).getType(), favs.get(i).getSigner(), favs.get(i).getADA(), favs.get(i).getProtoc_Num(),
                    favs.get(i).getFileURL(), favs.get(i).getPathName(), favs.get(i).getSbject(), favs.get(i).getPublishDate(), favs.get(i).getID()));
        }
        return s;
    }
}

