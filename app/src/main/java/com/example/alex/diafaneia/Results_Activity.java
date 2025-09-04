package com.example.alex.diafaneia;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    final String RESULTS_BASE_URL = "https://diafaneia.hellenicparliament.gr/api.ashx?q=documents&pageSize=50";
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
            // Use FileProvider for Android 7.0+ compatibility
            Uri contentUri = FileProvider.getUriForFile(context, 
                "com.alex.diafaneia.fileprovider", tempFile);
            openPDF(context, contentUri);
            return;
        }

        // Show progress dialog while downloading
//        final ProgressDialog progress = ProgressDialog.show(context, "Λήψη έκδοσης", "Περιμένετε να κατέβει το pdf.", true);

        // Create the download request
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(pdfUrl));
        r.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS+"/Αποφάσεις/", decoded);
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //Broadcast receiver for when downloading the PDF is complete
//        BroadcastReceiver onComplete = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
////                if (!progress.isShowing()) {
////                    return;
////                }
//                context.unregisterReceiver(this);
//                //Dismiss the progressDialog
////                progress.dismiss();
//                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                Cursor c = dm.query(new DownloadManager.Query().setFilterById(downloadId));
//                //if download was successful attempt to open the PDF
//                if (c.moveToFirst()) {
//                    @SuppressLint("Range") int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        // Use FileProvider for Android 7.0+ compatibility
//                        Uri contentUri = FileProvider.getUriForFile(context,
//                                "com.alex.diafaneia.fileprovider", tempFile);
//                        openPDF(context, contentUri);
//                    }
//                }
//                c.close();
//            }
//
//        };

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                ctx.unregisterReceiver(this);

                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Cursor c = dm.query(new DownloadManager.Query().setFilterById(downloadId));

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        if (progress.isShowing()) progress.dismiss();

                        Uri contentUri = FileProvider.getUriForFile(context,
                                "com.alex.diafaneia.fileprovider", tempFile);
                        openPDF(context, contentUri);
                    }
                }
                c.close();
            }
        };

        //Resister the receiver
//        ContextCompat.registerReceiver(context, onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED);
        ContextCompat.registerReceiver(context, onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);


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

        String fileURL="https://diafaneia.hellenicparliament.gr" + jsonobject.getJSONObject("Attachment")
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

    /**
     * Checks if storage permission is granted
     */
    private static boolean hasStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, 
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests storage permission if not granted
     */
    private void requestStoragePermission() {
        if (!hasStoragePermission(this)) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                1001);
        }
    }

    /**
     * Converts a file URI to a content URI using FileProvider for Android 7.0+
     */
    private static Uri getUriForFile(Context context, Uri fileUri) {
        Log.d("Results_Activity", "Original URI: " + fileUri);
        Log.d("Results_Activity", "URI scheme: " + fileUri.getScheme());
        
        if (fileUri.getScheme() != null && fileUri.getScheme().equals("file")) {
            File file = new File(fileUri.getPath());
            Log.d("Results_Activity", "File path: " + file.getAbsolutePath());
            Log.d("Results_Activity", "File exists: " + file.exists());
            
            // Test FileProvider functionality
            testFileProvider(context, file);
            
            try {
                Uri contentUri = FileProvider.getUriForFile(context, 
                    "com.alex.diafaneia.fileprovider", file);
                Log.d("Results_Activity", "Content URI: " + contentUri);
                return contentUri;
            } catch (Exception e) {
                Log.e("Results_Activity", "Error getting FileProvider URI: " + e.getMessage(), e);
                return fileUri; // Fallback to original URI
            }
        }
        return fileUri;
    }

    public static final void openPDF(Context context, Uri localUri) {
        Log.d("Results_Activity", "openPDF called with URI: " + localUri);
        
//        // Check if we have storage permission (for Android 6.0+)
//        if (!hasStoragePermission(context)) {
//            Log.d("Results_Activity", "No storage permission");
//            Toast.makeText(context, "Χρειάζεται άδεια πρόσβασης στην αποθήκευση για να ανοίξετε το αρχείο.", Toast.LENGTH_SHORT).show();
//            // If this is called from an Activity, request permission
//            if (context instanceof Results_Activity) {
//                ((Results_Activity) context).requestStoragePermission();
//            }
//            return;
//        }
//
        Intent i = new Intent(Intent.ACTION_VIEW);
        
        // Convert file URI to content URI for Android 7.0+
        Uri contentUri = getUriForFile(context, localUri);
        Log.d("Results_Activity", "Final content URI: " + contentUri);
        
        i.setDataAndType(contentUri, "application/pdf");
        
        // Add flags for newer Android versions
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        Log.d("Results_Activity", "Starting activity with intent: " + i.toString());
        
        try {
            context.startActivity(i);
            Log.d("Results_Activity", "Activity started successfully");
        } catch (ActivityNotFoundException e) {
            // Try alternative approach
            Log.e("Results_Activity", "Primary approach failed, trying alternative", e);
            openPDFAlternative(context, localUri);
        } catch (SecurityException e) {
            // Handle security exceptions for newer Android versions
            Log.e("Results_Activity", "Security exception", e);
            Toast.makeText(context, "Δεν έχετε άδεια πρόσβασης στο αρχείο.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Log the error for debugging
            Log.e("Results_Activity", "Error opening PDF: " + e.getMessage(), e);
            Toast.makeText(context, "Σφάλμα κατά το άνοιγμα του αρχείου: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Alternative method to open PDF - tries different approaches
     */
    public static final void openPDFAlternative(Context context, Uri localUri) {
        Log.d("Results_Activity", "openPDFAlternative called with URI: " + localUri);
        
        // Try multiple approaches
        Intent[] intents = new Intent[5];
        
        // Approach 1: Direct file URI (for older Android versions)
        intents[0] = new Intent(Intent.ACTION_VIEW);
        intents[0].setDataAndType(localUri, "application/pdf");
        intents[0].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Approach 2: Content URI with FileProvider
        intents[1] = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = getUriForFile(context, localUri);
        intents[1].setDataAndType(contentUri, "application/pdf");
        intents[1].addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intents[1].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Approach 3: Generic intent with content URI
        intents[2] = new Intent(Intent.ACTION_VIEW);
        intents[2].setDataAndType(contentUri, "*/*");
        intents[2].addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intents[2].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Approach 4: Generic intent with file URI
        intents[3] = new Intent(Intent.ACTION_VIEW);
        intents[3].setDataAndType(localUri, "*/*");
        intents[3].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Approach 5: Send intent (for sharing)
        intents[4] = new Intent(Intent.ACTION_SEND);
        intents[4].setType("application/pdf");
        intents[4].putExtra(Intent.EXTRA_STREAM, contentUri);
        intents[4].addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intents[4].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        for (int i = 0; i < intents.length; i++) {
            try {
                Log.d("Results_Activity", "Trying approach " + (i + 1) + ": " + intents[i].toString());
                context.startActivity(intents[i]);
                Log.d("Results_Activity", "Success with approach " + (i + 1));
                return;
            } catch (Exception e) {
                Log.e("Results_Activity", "Approach " + (i + 1) + " failed: " + e.getMessage());
            }
        }
        
        // If all approaches fail, show chooser
        try {
            Intent chooser = Intent.createChooser(intents[1], "Επιλέξτε εφαρμογή για το άνοιγμα του PDF");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (Exception e) {
            Log.e("Results_Activity", "Chooser also failed: " + e.getMessage());
            Toast.makeText(context, "Δεν ήταν δυνατό το άνοιγμα του αρχείου PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if PDF viewer apps are available on the device
     */
    public static boolean isPDFViewerAvailable(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        boolean hasPdfViewer = intent.resolveActivity(context.getPackageManager()) != null;
        
        // Also check for generic file viewers
        Intent genericIntent = new Intent(Intent.ACTION_VIEW);
        genericIntent.setType("*/*");
        boolean hasGenericViewer = genericIntent.resolveActivity(context.getPackageManager()) != null;
        
        Log.d("Results_Activity", "PDF viewer available: " + hasPdfViewer);
        Log.d("Results_Activity", "Generic viewer available: " + hasGenericViewer);
        
        // Return true if either PDF-specific or generic viewer is available
        return hasPdfViewer || hasGenericViewer;
    }

    /**
     * Test FileProvider functionality
     */
    public static void testFileProvider(Context context, File file) {
        try {
            Uri contentUri = FileProvider.getUriForFile(context, "com.alex.diafaneia.fileprovider", file);
            Log.d("Results_Activity", "FileProvider test successful: " + contentUri);
            
            // Test if we can query the content URI
            try (Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null)) {
                if (cursor != null) {
                    Log.d("Results_Activity", "Content URI is queryable, row count: " + cursor.getCount());
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e("Results_Activity", "Content URI query failed: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e("Results_Activity", "FileProvider test failed: " + e.getMessage(), e);
        }
    }

}

