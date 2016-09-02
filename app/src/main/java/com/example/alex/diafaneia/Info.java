package com.example.alex.diafaneia;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
    }
}
