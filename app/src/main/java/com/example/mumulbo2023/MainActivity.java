package com.example.mumulbo2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_ask =  (Button) findViewById(R.id.button_ask);
        Button button_act =  (Button) findViewById(R.id.button_act);
        Button button_please =  (Button) findViewById(R.id.button_please);

        button_ask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), AskMmbActivity.class);
                startActivity(intent);
            }
        });

        button_act.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ActMmbActivity.class);
                startActivity(intent);
            }
        });

        button_please.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PleaseActivity.class);
                startActivity(intent);
            }
        });

    }

}