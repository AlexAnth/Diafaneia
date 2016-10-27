package com.example.alex.diafaneia;

import android.app.DownloadManager;
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
        mProgressBar= (ProgressBar)findViewById(R.id.progress_bar);
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
                final File file = new File(Environment.getExternalStorageDirectory() + "/Αποφάσεις/" + filename);
                if (!file.exists()) {
                    file_download(url, filename);
                }
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

                dwnl_button=(ImageView)v.findViewById(R.id.download_btn);
                final RealmResults<Favourite> favs = realm.where(Favourite.class).findAll();
                dwnl_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Change icon
                        dwnl_button.setImageResource(R.drawable.bookmark_icon_selected);

                        Favourite fav = createFavourite(JsonCollection.get(position));


                        if(favs!=null) {

                            if (!favs.contains(fav)) {
                                Context context = getApplicationContext();
                                CharSequence text = "Το έγγραφο αποθηκεύτηκε στο φάκελο Αποφάσεις";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                realm.beginTransaction();
                                realm.copyToRealm(fav);
                                realm.commitTransaction();
                            } else {
                                Context context = getApplicationContext();
                                CharSequence text = "Το έγγραφο υπάρχει ήδη στο φάκελο Αποφάσεις";
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

    private ArrayList downloadAPI(Result result) {

        String temp =URLtoString(result);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, temp, null, new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonobj = new JSONObject(response.toString());

                    String records = jsonobj.getString("TotalRecords");
                    if(records=="1"){
                        reco.setText("Βρέθηκε "+records+" απόφαση");
                    }else if(records=="0") {
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
                .setDestinationInExternalPublicDir("/Αποφάσεις/", filename);

        mgr.enqueue(request);

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

}

