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

import com.android.volley.toolbox.Volley;
import com.example.alex.diafaneia.Model.Document;
import com.example.alex.diafaneia.Model.Sector;
import com.example.alex.diafaneia.Model.Signer;
import com.example.alex.diafaneia.Model.Type;
import com.example.alex.diafaneia.Utils.Constants;
import com.example.alex.diafaneia.Utils.RVAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alex on 3/8/2016.
 */
public class ActivityTwo extends AppCompatActivity {

    ArrayList <Object> JsonCollection= new ArrayList();

    final String SECTORS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=sectors";
    final String SIGNERS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=final-signers";
    final String DOCUMENT_TYPES_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=document-types";

    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);


        // Get the right Label
        text = getIntent().getStringExtra("B");
        Log.v("Διαφάνεια", "JSON:" + text);
        TextView textUi = (TextView) findViewById(R.id.TextInput);
        textUi.setText(text);
        mRecyclerView = (RecyclerView) findViewById(R.id.content);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVAdapter(JsonCollection);
        mRecyclerView.setAdapter(mAdapter);

        JsonCollection=downloadAPI(text);

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
        ((RVAdapter) mAdapter).setOnItemClickListener(new RVAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.v("Hi!", " Clicked on Item " + (JsonCollection.get(position)));

                // Check the Category selected
                if (text.equalsIgnoreCase(Constants.SECTOR_TITLE)){
                    Log.v("Hi!", " Clicked on Item " + ((Sector) JsonCollection.get(position)).getSectorTitle());
                    Sector s = (Sector) JsonCollection.get(position);
                    MainActivity.setSector(s);
                }else if (text.equalsIgnoreCase(Constants.DOCUMENT_TITLE)){
                    Log.v("Hi!", " Clicked on Item " + ((Document) JsonCollection.get(position)).getDocumentTitle());
                    Document d = (Document) JsonCollection.get(position);
                    MainActivity.setDocument(d);
                }else if (text.equalsIgnoreCase(Constants.TYPE_TITLE)){
                    Log.v("Hi!", " Clicked on Item " + ((Type) JsonCollection.get(position)).getTypeTitle());
                    Type t = (Type) JsonCollection.get(position);
                    MainActivity.setType(t);
                }else if (text.equalsIgnoreCase(Constants.SIGNER_TITLE)){
                    Log.v("Hi!", " Clicked on Item " + ((Signer) JsonCollection.get(position)).getSignerFullName());
                    Signer s = (Signer) JsonCollection.get(position);
                    MainActivity.setSigner(s);
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

    private ArrayList downloadAPI(String text) {

        if (text.equalsIgnoreCase(Constants.SECTOR_TITLE)) {            // if sectors

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, SECTORS_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("Title");
                            String id = jsonobject.getString("ID");
                            String CleanUrl = jsonobject.getString("CleanUrl");

                            Sector sector = new Sector(name, id, CleanUrl);
                            JsonCollection.add(sector);
                            Log.v("Διαφάνεια", "SECTORS:" + sector.getSectorTitle());
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

        } else if (text.equalsIgnoreCase(Constants.SIGNER_TITLE)) {      // if signers

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, SIGNERS_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("Fullname");
                            String id = jsonobject.getString("ID");
                            String CleanUrl = jsonobject.getString("CleanUrl");

                            Signer signer = new Signer(name, id, CleanUrl);
                            JsonCollection.add(signer);
                            Log.v("Διαφάνεια", "SIGNERS:" + signer.getSignerFullName());
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

        } else if (text.equalsIgnoreCase(Constants.DOCUMENT_TITLE)) {    // if documents

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, DOCUMENT_TYPES_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            JSONArray jsonArray = jsonobject.getJSONArray("HierarchyPath");

                            String title = jsonArray.getJSONObject(0).getString("Title");
                            String id = jsonArray.getJSONObject(0).getString("ID");

                            Document document = new Document(title, id);
                            if (jsonArray.length() == 1) {          // Prevents duplicates
                                JsonCollection.add(document);
                                Log.v("Διαφάνεια", "DOCUMENTS:" + document.getDocumentTitle());
                            }

                        } mAdapter.notifyDataSetChanged();

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

        } else if (text.equalsIgnoreCase(Constants.TYPE_TITLE)) {        // if types

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, DOCUMENT_TYPES_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            JSONArray jsonArray = jsonobject.getJSONArray("HierarchyPath");
                            if (jsonArray.length() == 2) {
                                String title = jsonArray.getJSONObject(1).getString("Title");
                                String id = jsonArray.getJSONObject(1).getString("ID");

                                Type type = new Type(title, id);
                                JsonCollection.add(type);
                                Log.v("Διαφάνεια", "TYPES:" + type.getTypeTitle());
                            }

                        } mAdapter.notifyDataSetChanged();

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

        }


    return JsonCollection;}

}
