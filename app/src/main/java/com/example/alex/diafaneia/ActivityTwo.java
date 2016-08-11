package com.example.alex.diafaneia;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by Alex on 3/8/2016.
 */
public class ActivityTwo extends AppCompatActivity {

    final String SECTORS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=sectors";
    final String SIGNERS_BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=final-signers";
    final String DOCUMENT_TYPES_BASE_URL ="http://diafaneia.hellenicparliament.gr//api.ashx?q=document-types";
    private JsonAdapter adapter;
    private TextView text ;
    private ArrayList<Object> JsonCollection = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        text = (TextView)findViewById(R.id.card_text);
        // Get the right Label
        String text = getIntent().getStringExtra("B");
        Log.v("Διαφάνεια","JSON:"+ text);
        TextView textUi= (TextView)findViewById(R.id.TextInput);
        textUi.setText(text);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content);

        adapter =  new JsonAdapter(JsonCollection);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);



        switch (text) {
            case Constants.SECTOR_TITLE :
                downloadAPI(Constants.SECTOR_TITLE);
                break;
            case Constants.SIGNER_TITLE :
                downloadAPI(Constants.SIGNER_TITLE);

                break;
            case Constants.TYPE_TITLE :
                downloadAPI(Constants.TYPE_TITLE);

                break;
            case Constants.DOCUMENT_TITLE :
                downloadAPI(Constants.DOCUMENT_TITLE);

                break;
        }
        updateUI();




      }
    public void updateUI(){
        if(JsonCollection.size()>0){
            Object object = JsonCollection.get(0);
            if (object.getClass().equals(Sector.class)){
                text.setText(((Sector) object).getSectorTitle());
            }else if (object.getClass().equals(Signer.class)){
                 text.setText(((Signer) object).getSignerFullName());
            }else if (object.getClass().equals(Document.class)){
                  text.setText(((Document) object).getDocumentTitle());
            }else if (object.getClass().equals(Type.class)){
                   text.setText(((Type) object).getTypeTitle());
            }
        }
    }
    public void downloadAPI(String text){


        if(text.equalsIgnoreCase(Constants.SECTOR_TITLE)){            // if sectors

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, SECTORS_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("Title");
                            String id = jsonobject.getString("ID");
                            String CleanUrl = jsonobject.getString("CleanUrl");

                            Sector sector = new Sector(name,id,CleanUrl);
                            JsonCollection.add(sector);
                            Log.v("Διαφάνεια","SECTORS:"+ sector.getSectorTitle());
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Διαφάνεια","Err:"+ error.getLocalizedMessage());
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }else if(text.equalsIgnoreCase(Constants.SIGNER_TITLE)){      // if signers

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,SIGNERS_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("Fullname");
                            String id = jsonobject.getString("ID");
                            String CleanUrl = jsonobject.getString("CleanUrl");

                            Signer signer = new Signer(name,id,CleanUrl);
                            JsonCollection.add(signer);
                            Log.v("Διαφάνεια","SIGNERS:"+ signer.getSignerFullName());
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Διαφάνεια","Err:"+ error.getLocalizedMessage());
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }else if(text.equalsIgnoreCase(Constants.DOCUMENT_TITLE)){    // if documents

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,DOCUMENT_TYPES_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            JSONArray jsonArray = jsonobject.getJSONArray("HierarchyPath");

                            String title = jsonArray.getJSONObject(0).getString("Title");
                            String id = jsonArray.getJSONObject(0).getString("ID");

                            Document document = new Document(title,id);
                            if (jsonArray.length() == 1) {          // Prevents duplicates
                                JsonCollection.add(document);
                                Log.v("Διαφάνεια","DOCUMENTS:"+ document.getDocumentTitle());
                            }

                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Διαφάνεια","Err:"+ error.getLocalizedMessage());
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }else if(text.equalsIgnoreCase(Constants.TYPE_TITLE)){        // if types

            final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,DOCUMENT_TYPES_BASE_URL, null, new Response.Listener<JSONArray>() {

                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonarray = new JSONArray(response.toString());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            JSONArray jsonArray = jsonobject.getJSONArray("HierarchyPath");
                            if (jsonArray.length()==2){
                            String title = jsonArray.getJSONObject(1).getString("Title");
                            String id = jsonArray.getJSONObject(1).getString("ID");

                            Type type = new Type(title,id);
                            JsonCollection.add(type);
                            Log.v("Διαφάνεια","TYPES:"+ type.getTypeTitle());
                            }

                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Διαφάνεια","Err:"+ error.getLocalizedMessage());
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }

    }

    public class JsonAdapter extends RecyclerView.Adapter<ObjectViewHolder>
    {

        private ArrayList<Object> JsonList;

        public JsonAdapter (ArrayList<Object> JsonList){
            this.JsonList=JsonList;
        }
        @Override
        public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_json,parent,false);
            return new ObjectViewHolder(card);
        }

        @Override
        public void onBindViewHolder(ObjectViewHolder holder, int position) {
            Object obj = JsonList.get(position);
            Log.v("Διαφάνεια","UpdateUI:"+ "Got Here!");
            holder.updateUI(obj);
        }

        @Override
        public int getItemCount() {
            return JsonList.size();
        }
    }


    public class ObjectViewHolder extends RecyclerView.ViewHolder{

        private TextView text;

        public ObjectViewHolder(View itemView){
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.card_text);
        }

        public  void updateUI(Object object){
            Log.v("Διαφάνεια","UpdateUI:"+ "Got Here!");

            if (object.getClass().equals(Sector.class)){
                text.setText(((Sector) object).getSectorTitle());
            }else if (object.getClass().equals(Signer.class)){
                 text.setText(((Signer) object).getSignerFullName());
            }else if (object.getClass().equals(Document.class)){
                text.setText(((Document) object).getDocumentTitle());
            }else if (object.getClass().equals(Type.class)){
                text.setText(((Type) object).getTypeTitle());
}







        }

    }
}
