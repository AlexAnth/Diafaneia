package com.example.alex.diafaneia;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alex.diafaneia.Model.Document;
import com.example.alex.diafaneia.Model.Result;
import com.example.alex.diafaneia.Model.Search;
import com.example.alex.diafaneia.Model.Sector;
import com.example.alex.diafaneia.Model.Signer;
import com.example.alex.diafaneia.Model.Type;
import com.example.alex.diafaneia.Utils.Constants;
import com.example.alex.diafaneia.Utils.RVAdapter;
import com.example.alex.diafaneia.Utils.RVAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.alex.diafaneia.MainActivity.getDocument;

/**
 * Created by Alex on 3/8/2016.
 */
public class Results_Activity extends AppCompatActivity {

    ArrayList <Object> JsonCollection= new ArrayList();

    final String RESULTS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=documents";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);


        // Get the intent
        result = MainActivity.getR();


        mRecyclerView = (RecyclerView) findViewById(R.id.result_card);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVAdapter2(JsonCollection);
        mRecyclerView.setAdapter(mAdapter);

        JsonCollection=downloadAPI(result);

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
        ( mAdapter).setOnItemClickListener(new RVAdapter2
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

                    JSONArray jsonarray  = jsonobj.getJSONArray("Data");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Search search =createResult(jsonobject);

                        JsonCollection.add(search);

                    }
                    mAdapter.notifyDataSetChanged();
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
        if(result.getSector()!=null) {
            temp=temp+result.getSector().getSectorId();
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
        return temp;
    }

    private Search createResult(JSONObject jsonobject) throws JSONException {

        String sector= jsonobject.getJSONObject("Sector").getString("Title");
        Log.v("Διαφάνεια", "test:" + sector);
        String document= jsonobject.getJSONObject("DocumentType").getString("Title");
        String type =jsonobject.getJSONObject("DocumentType").getJSONArray("HierarchyPath").getJSONObject(1).getString("Title");
        String signer = jsonobject.getJSONObject("FinalSigner").getString("Fullname")+ " " +
                (jsonobject.getJSONObject("FinalSigner").getString("Info"));
        String ADA = jsonobject.getString("ADA");
        String ProtocolNumber = jsonobject.getString("ProtocolNumber");
        String PublishDate = jsonobject.getString("PublishDate");

        String fileURL="http://diafaneia.hellenicparliament.gr/" + jsonobject.getJSONObject("Attachment")
                .getString("FilePath");
        String pathName=jsonobject.getJSONObject("Attachment").getString("OriginalFileName");
        String sbject= jsonobject.getString("Subject");

        Search search = new Search(sector,document,type, signer, ADA, ProtocolNumber, fileURL, pathName,
                sbject,PublishDate);

        return search;
    }




}
