package com.example.alex.diafaneia;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Alex on 3/8/2016.
 */
public class ActivityTwo extends AppCompatActivity {

    final String BASE_URL = "http://diafaneia.hellenicparliament.gr//api.ashx?q=sectors";
    private ArrayList<Sector> sectorCollection = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        // Get the right Label
        String text = getIntent().getStringExtra("B");
        TextView textUi= (TextView)findViewById(R.id.TextInput);
        textUi.setText(text);

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {

            public void onResponse(JSONArray response) {
                try {
                    JSONArray jsonarray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        String name = jsonobject.getString("Title");
                        String id = jsonobject.getString("ID");
                        String CleanUrl = jsonobject.getString("CleanUrl");

                        Sector sector = new Sector(name,id,CleanUrl);
                        sectorCollection.add(sector);
                        Log.v("Διαφάνεια","JSON:"+ sector.getSectorTitle());
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

    public class JsonAdapter extends RecyclerView.Adapter<SectorViewHolder>
    {

        private ArrayList<Sector> secList;

        public JsonAdapter (ArrayList<Sector> secList){
            this.secList=secList;
        }
        @Override
        public SectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_json,parent,false);
            return new SectorViewHolder(card);
        }

        @Override
        public void onBindViewHolder(SectorViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


    public class SectorViewHolder extends RecyclerView.ViewHolder{

        private TextView text;

        public SectorViewHolder(View itemView){
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.card_text);
        }

        public void updateUI(Sector sector){
            text.setText(sector.getSectorTitle());

        }

    }
}
