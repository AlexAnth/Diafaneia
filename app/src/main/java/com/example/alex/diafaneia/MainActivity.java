package com.example.alex.diafaneia;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button firstButton = (Button) findViewById(R.id.button1);
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityTwo.class);
                intent.putExtra("B","Υπηρεσία / Φορέας ");
                startActivity(intent);
            }

        });
        Button secondButton = (Button) findViewById(R.id.button2);
        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityTwo.class);
                intent.putExtra("B","Θεματική Ενότητα");
                startActivity(intent);
            }

        });
        Button thirdButton = (Button) findViewById(R.id.button3);
        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityTwo.class);
                intent.putExtra("B","Είδος Απόφασης");
                startActivity(intent);
            }

        });
        Button fourthButton = (Button) findViewById(R.id.button4);
        fourthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityTwo.class);
                intent.putExtra("B","Τελικός Υπογραφόντας");
                startActivity(intent);
            }

        });
    }

}