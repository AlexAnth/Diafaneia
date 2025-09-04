package com.example.alex.diafaneia;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
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

// import io.realm.Realm;
// import io.realm.RealmResults;

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

        // Add home button functionality
        ImageView home_button = (ImageView) findViewById(R.id.home_btn);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will return to the previous activity (like swiping back)
            }
        });

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

                final String decoded = Uri.decode(filename); //handles spaces at filename
                // The place where the downloaded PDF file will be put
                final File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS +"/Αποφάσεις/"), decoded);

                if (!file.exists()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Το έγγραφο δεν βρέθηκε.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    
                    // Use FileProvider for Android 7.0+ compatibility
                    Uri contentUri = getUriForFile(file);
                    target.setDataAndType(contentUri, "application/pdf");
                    
                    // Add flags for newer Android versions
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        startActivity(target);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(Bookmark.this, "Δέν βρέθηκε εφαρμογή προβολής αρχείων PDF.\nΚατεβάστε μία απο το διαδίκτυο.", Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        // Handle security exceptions for newer Android versions
                        Toast.makeText(Bookmark.this, "Δεν έχετε άδεια πρόσβασης στο αρχείο.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // Generic error handling
                        Toast.makeText(Bookmark.this, "Σφάλμα κατά το άνοιγμα του αρχείου.", Toast.LENGTH_SHORT).show();
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


    /**
     * Converts a file to a content URI using FileProvider for Android 7.0+
     */
    private Uri getUriForFile(File file) {
        try {
            return FileProvider.getUriForFile(this, "com.alex.diafaneia.fileprovider", file);
        } catch (Exception e) {
            // Fallback to file URI if FileProvider fails
            return Uri.fromFile(file);
        }
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

