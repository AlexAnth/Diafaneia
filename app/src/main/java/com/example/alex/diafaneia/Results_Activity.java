package com.example.alex.diafaneia;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alex.diafaneia.Model.Result;
import com.example.alex.diafaneia.Model.Search;
import com.example.alex.diafaneia.Utils.RVAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.TimeZone;



/**
 * Created by Alex on 3/8/2016.
 */
public class Results_Activity extends AppCompatActivity {

    ArrayList <Search> JsonCollection= new ArrayList();

    final String RESULTS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=documents&pageSize=100";
    final String DOCUMENT_TYPE ="&type=";
    final String SIGNER ="&signer=";
    final String SECTOR ="&sector=";
    final String ADA ="&ada=";
    final String PROTOC_NUM ="&protocolnumber=";
    final String FREETEXT ="&freetext=";
    final String DATE_FROM ="&datefrom=";
    final String DATE_TO = "dateto";

    private RecyclerView mRecyclerView;
    private RVAdapter2 mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Result result;
    private TextView reco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);


        // Get the intent
        result = MainActivity.getR();

        reco = (TextView) findViewById(R.id.res_number);
        mRecyclerView = (RecyclerView) findViewById(R.id.result_card);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVAdapter2(JsonCollection);
        mRecyclerView.setAdapter(mAdapter);

        this.JsonCollection=downloadAPI(result);


        ImageView info_button=(ImageView)findViewById(R.id.info_btn);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Info.class);
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
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

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
                    reco.setText("Βρέθηκαν "+records+" αποφάσεις:");


                    JSONArray jsonarray  = jsonobj.getJSONArray("Data");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Search search =createResult(jsonobject);


                    }
                    mAdapter.notifyDataSetChanged();
                    for (int i = 0; i < JsonCollection.size(); i++) {
                        Search jsonobject = JsonCollection.get(i);
                        Log.v("Διαφάνεια", "Results :" + jsonobject.getDocument());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Διαφάνεια", "Err:" + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);

        return JsonCollection;
    }

    private String URLtoString(Result result) {
        String temp=RESULTS_BASE_URL;
        //Sectors
        if(result.getSector()!=null) {
            temp=temp+SECTOR+result.getSector().getSectorId();
        }
        //Signer
        if(result.getSigner()!=null) {
            temp=temp+SIGNER+result.getSigner().getSignerId();
        }

        if(result.getDocument()!=null) {
            temp=temp+DOCUMENT_TYPE+result.getDocument().getDocumentId();
        }
        if(result.getADA()!=null) {
            temp=temp+ADA+result.getADA();
        }
        if(result.getProtoc_Num()!=null) {
            temp=temp+PROTOC_NUM+result.getProtoc_Num();
        }
        if(result.getFree_Text_str()!=null) {
            temp=temp+FREETEXT+result.getFree_Text_str();
        }
        if(result.getFromDate()!=null) {
            temp=temp+DATE_FROM+result.getFromDate();
        }
        if(result.getToDate()!=null) {
            temp=temp+DATE_TO+result.getToDate();
        }
        Log.v("Διαφάνεια", "URL:" + temp);
        return temp;
    }

    private Search createResult(JSONObject jsonobject) throws JSONException {

        String sector= jsonobject.getJSONObject("Sector").getString("Title");
        String type= jsonobject.getJSONObject("DocumentType").getString("Title");
        String document =jsonobject.getJSONObject("DocumentType").getJSONArray("HierarchyPath").getJSONObject(0).getString("Title");
        String signer;
        try {
            signer = (jsonobject.getJSONObject("FinalSigner").getString("Info")) + " " +
                    jsonobject.getJSONObject("FinalSigner").getString("Fullname");
        }catch(Exception e){
            signer=" Δεν βρέθηκε υπογράφων.";
        }

        String ADA = jsonobject.getString("ADA");
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
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        f.setTimeZone(tz);
        PublishDate=f.format(cal.getTime());

        String fileURL="http://diafaneia.hellenicparliament.gr/" + jsonobject.getJSONObject("Attachment")
                .getString("FilePath");
        String pathName=jsonobject.getJSONObject("Attachment").getString("OriginalFileName");
        String sbject= jsonobject.getString("Subject");

        Search search = new Search(sector,document,type, signer, ADA, ProtocolNumber, fileURL, pathName,
                sbject,PublishDate);

        JsonCollection.add(search);
        return search;
    }




}
