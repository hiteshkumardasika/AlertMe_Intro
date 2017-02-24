package com.example.root.alertme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class Settings extends AppCompatActivity {

    Toolbar settingsToolbar;
    SharedPreferences preference;
    SharedPreferences.Editor editor;

    Spinner bufferSpinner;
    String bufferValue;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preference = getApplicationContext().getSharedPreferences("myapp",MODE_PRIVATE);
        editor = preference.edit();

        initToolBar();
        initWidgets();
    }

    public void initWidgets() {
        EditText userName = (EditText) findViewById(R.id.userName);
        final String userNameVal = preference.getString("userName",null);
        if( userNameVal != null && !userNameVal.isEmpty()) {
            userName.setText(userNameVal);
        }

        userName = (EditText) findViewById(R.id.userName);
        bufferSpinner = (Spinner) findViewById(R.id.bufferSpinner);
        List<String> bufferData = new ArrayList<String>();
        bufferData.add("500");
        bufferData.add("1000");
        bufferData.add("1500");
        bufferData.add("2000");
        bufferData.add("2500");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bufferData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bufferSpinner.setAdapter(dataAdapter);
        bufferSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                bufferValue = adapterView.getItemAtPosition(i).toString();
                editor.putString("bufferSpinner",i+"");
                System.out.println("bufferSpinner:: " + value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button save = (Button) findViewById(R.id.saveSettings);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userName = (EditText) findViewById(R.id.userName);
                name = userName.getText().toString();
                System.out.println("userName:: " + name);
                editor.putString("userName",userNameVal);
                editor.commit();

                Intent intentResult = new Intent();
                intentResult.putExtra("bufferValue", bufferValue);
                intentResult.putExtra("name", name);
                setResult(Activity.RESULT_OK, intentResult);
                finish();
            }
        });
        Button cancel = (Button) findViewById(R.id.cancelSettings);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear().commit();

            }
        });
    }

    public void initToolBar() {
        settingsToolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        settingsToolbar.setTitle("Settings");
//        setSupportActionBar(settingsToolbar);


//        settingsToolbar.setNavigationIcon(R.drawable.back);
//        How to add Navigation Icon
        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Back to Main");
            }
        });

    }

}