package com.example.alex.diafaneia;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alex.diafaneia.Model.Favourite;
import com.example.alex.diafaneia.Model.Result;
import com.example.alex.diafaneia.Model.Search;
import com.example.alex.diafaneia.Utils.RVAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by Alex on 3/8/2016.
 */
public class Results_Activity extends AppCompatActivity {

    ArrayList <Search> JsonCollection= new ArrayList();
    private ProgressBar mProgressBar;
    final String RESULTS_BASE_URL = "http://diafaneia.hellenicparliament.gr/api.ashx?q=documents&pageSize=50";
    final String DOCUMENT_TYPE ="&type=";
    final String SIGNER ="&signer=";
    final String SECTOR ="&sector=";
    final String ADA ="&ada=";
    final String PROTOC_NUM ="&protocolnumber=";
    final String FREETEXT ="&freetext=";
    final String DATE_FROM ="&datefrom=";
    final String DATE_TO = "&dateto=";

    private RecyclerView mRecyclerView;
    private RVAdapter2 mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Result result;
    private TextView reco;
    private ImageView dwnl_button;
    private ImageView bookmark_button;
    Realm realm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        // Initialize Realm
        Realm.init(getApplicationContext());

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        // Get the intent
        result = MainActivity.getR();
        bookmark_button=(ImageView)findViewById(R.id.bookmark_btn);
        mProgressBar= (ProgressBar)this.findViewById(R.id.progress_bar);
        reco = (TextView) findViewById(R.id.res_number);
        mRecyclerView = (RecyclerView) findViewById(R.id.result_card);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVAdapter2(JsonCollection,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);


        if(!internetConnection()) {
            mProgressBar.setVisibility(View.GONE);
            new AlertDialog.Builder(this)
                    .setTitle("Σύνδεση στο Διαδίκτυο")
                    .setMessage("Ελέγξτε την σύνδεσή σας και ξαναπροσπαθήστε.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        JsonCollection = downloadAPI(result);



        ImageView info_button=(ImageView)findViewById(R.id.info_btn);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Info.class);
                startActivity(intent);
            }

        });
        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Bookmark.class);
                startActivity(intent);
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        (mAdapter).setOnItemClickListener(new RVAdapter2
                .MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                final String filename = JsonCollection.get(position).getPathName();
                String url = JsonCollection.get(position).getFileURL();

                CopyPDF(filename,url);
                dwnl_button=(ImageView)v.findViewById(R.id.download_btn);
                final RealmResults<Favourite> favs = realm.where(Favourite.class).findAll();
                dwnl_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Change icon
                        dwnl_button.setImageResource(R.drawable.bookmark_icon_selected);

                        Favourite fav = createFavourite(JsonCollection.get(position));

                        if(favs!=null) {

                            if (realm.where(Favourite.class).equalTo("ID",fav.getID()).findFirst()==null) {
                                Context context = getApplicationContext();
                                CharSequence text = "Το έγγραφο προστέθηκε στις Αποθηκεύμένες Αποφάσεις";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(fav);
                                realm.commitTransaction();
                            } else {
                                Context context = getApplicationContext();
                                CharSequence text = "Το έγγραφο υπάρχει ήδη στις Αποθηκεύμένες Αποφάσεις";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }else{
                            realm.beginTransaction();
                            realm.copyToRealm(fav);
                            realm.commitTransaction();
                        }
                    }



                });



            }
        });
    }

    //method to write the PDFs file to sd card under a PDF directory.
    private void CopyPDF(String filename, String url) {
        try {
            downloadAndOpenPDF(this, url,filename);
        } catch (Exception e) {
            Log.d("Downloader", e.getMessage());
        }
    }

    public void downloadAndOpenPDF(final Context context, final String pdfUrl, String filename) {

        final String decoded = Uri.decode(filename); //handles spaces at filename
        // The place where the downloaded PDF file will be put
        final File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS +"/Αποφάσεις/"), decoded);
        // If we have downloaded the file before, just go ahead and show it(if its cached)
        if (tempFile.exists()) {
            openPDF(context, Uri.fromFile(tempFile));
            return;
        }

// Show progress dialog while downloading
//        final ProgressDialog progress = ProgressDialog.show(context, "Λήψη έκδοσης", "Περιμένετε να κατέβει το pdf.", true);

        // Create the download request
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(pdfUrl));
        r.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS+"/Αποφάσεις/", decoded);
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //Broadcast receiver for when downloading the PDF is complete
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                if (!progress.isShowing()) {
//                    return;
//                }
                context.unregisterReceiver(this);
                //Dismiss the progressDialog
//                progress.dismiss();
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Cursor c = dm.query(new DownloadManager.Query().setFilterById(downloadId));
                //if download was successful attempt to open the PDF
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        openPDF(context, Uri.fromFile(tempFile));
                    }
                }
                c.close();
            }
        };
        //Resister the receiver
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Enqueue the request
        dm.enqueue(r);
    }


    private ArrayList downloadAPI(Result result) {

        String temp =URLtoString(result);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, temp, null, new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonobj = new JSONObject(response.toString());

                    String records = jsonobj.getString("TotalRecords");
                    if(records.equalsIgnoreCase("1")){
                        reco.setText("Βρέθηκε "+records+" απόφαση");
                    }else if(records.equalsIgnoreCase("0")) {
                        reco.setText("Δεν βρέθηκαν απόφασεις");
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }else{
                        reco.setText("Βρέθηκαν "+records+" αποφάσεις");


                    }


                    JSONArray jsonarray  = jsonobj.getJSONArray("Data");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        createResult(jsonobject);


                    }
                    mAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);

        return JsonCollection;
    }

    private String URLtoString(Result result) {
        String temp=RESULTS_BASE_URL;
        //Sectors
        try {
            if (result.getSector() != null) {
                temp = temp + SECTOR + result.getSector().getSectorId();
            }
            //Signer
            if (result.getSigner() != null) {
                temp = temp + SIGNER + result.getSigner().getSignerId();
            }

            if (result.getType() != null) {
                temp = temp + DOCUMENT_TYPE + result.getType().getTypeId();
            } else {
                if (result.getDocument() != null) {
                    temp = temp + DOCUMENT_TYPE + result.getDocument().getDocumentId();
                }

            }


            if (result.getADA() != null) {
                temp = temp + ADA + result.getADA();
            }
            if (result.getProtoc_Num() != null) {
                temp = temp + PROTOC_NUM + result.getProtoc_Num();
            }
            if (result.getFree_Text_str() != null) {
                temp = temp + FREETEXT + result.getFree_Text_str();
            }
            if (result.getFromDate() != null) {
                temp = temp + DATE_FROM + result.getFromDate();
            }
            if (result.getToDate() != null) {
                temp = temp + DATE_TO + result.getToDate();
            }
        }catch (Exception e){

        }
        return temp;
    }

    private void createResult(JSONObject jsonobject) throws JSONException {

        String sector;
        try {
            sector = jsonobject.getJSONObject("Sector").getString("Title");
        }catch(Exception e){
            sector=" Δεν βρέθηκε τομέας ";
        }

        String type;
        try {
            type  = jsonobject.getJSONObject("DocumentType").getString("Title");
        }catch(Exception e){
            type=" Δεν βρέθηκε είδος απόφασης.";
        }

        String document =jsonobject.getJSONObject("DocumentType").getJSONArray("HierarchyPath").getJSONObject(0).getString("Title");
        String signer;
        try {
            signer = (jsonobject.getJSONObject("FinalSigner").getString("Info")) + " " +
                    jsonobject.getJSONObject("FinalSigner").getString("Fullname");
        }catch(Exception e){
            signer=" Δεν βρέθηκε υπογράφων.";
        }

        String ADA = jsonobject.getString("ADA");
        String ID = jsonobject.getString("ID");
        String ProtocolNumber = jsonobject.getString("ProtocolNumber");
        if (ProtocolNumber.equals("null") ) ProtocolNumber="";

        String PublishDate = jsonobject.getString("PublishDate");

        //Date formating
        PublishDate = PublishDate.replaceAll("\\D+","");
        long x=Long.parseLong(PublishDate);
        x=x/10000;

        TimeZone tz = TimeZone.getTimeZone("GMT+0200");
        Calendar cal = Calendar.getInstance(tz);
        cal.setTimeInMillis(x);
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        f.setTimeZone(tz);
        PublishDate=f.format(cal.getTime());

        String fileURL="http://diafaneia.hellenicparliament.gr" + jsonobject.getJSONObject("Attachment")
                .getString("FilePath");
        String pathName=jsonobject.getJSONObject("Attachment").getString("OriginalFileName");
        String sbject= jsonobject.getString("Subject");

        Search search = new Search(sector,document,type, signer, ADA, ProtocolNumber, fileURL, pathName,
                sbject,PublishDate,ID);

        JsonCollection.add(search);

    }

    public boolean internetConnection() {

        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null)
            return info.isConnected(); // WIFI connected
        else
            return false; // no info object implies no connectivity
    }

    private Favourite createFavourite(Search search) {

        final Favourite fav = new Favourite();
        fav.setADA(search.getADA());
        fav.setDocument(search.getDocument());
        fav.setType(search.getType());
        fav.setFileURL(search.getFileURL());
        fav.setPathName(search.getPathName());
        fav.setProtoc_Num(search.getProtoc_Num());
        fav.setPublishDate(search.getPublishDate());
        fav.setSbject(search.getSbject());
        fav.setSector(search.getSector());
        fav.setSigner(search.getSigner());
        fav.setID(search.getID());
        return fav;
    }

    public static final void openPDF(Context context, Uri localUri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(localUri, "application/pdf");
        try {
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(context, "Δέν βρέθηκε εφαρμογή προβολής αρχείων PDF.\\nΚατεβάστε μία απο το διαδίκτυο.", Toast.LENGTH_SHORT).show();
        }
    }

}

