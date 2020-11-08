package com.example.vhaudioplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions extends AppCompatActivity implements View.OnClickListener {
    Button buttonYes;
    Button buttonReturn;
    private static final int REQUEST_PERMISSION_WRITE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        buttonYes = findViewById(R.id.buttonYes);
        buttonReturn = findViewById(R.id.buttonReturn);

        buttonYes.setOnClickListener(this);
        buttonReturn.setOnClickListener(this);
    }

    //////////check the permissions///////////////////////
    boolean checkPermissions(){

        if(!isExternalStorageReadable() || !isExternalStorageWriteable()){
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    // проверяем, доступно ли внешнее хранилище для чтения и записи
    public boolean isExternalStorageWriteable(){
        String state = Environment.getExternalStorageState();
        return  Environment.MEDIA_MOUNTED.equals(state);
    }
    // проверяем, доступно ли внешнее хранилище хотя бы только для чтения
    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return  (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonYes : {
                checkPermissions();
                savePreference();
                startActivity(new Intent(this, FileManager.class));
                break;
            }
            case R.id.buttonReturn : {
                //savePreference();
                startActivity(new Intent(this, Player.class));
                break;
            }
        }
    }
    ///////////////check the permissions/////////////////////////////

    void savePreference(){
        Player.sharedPreferences = getSharedPreferences(Player.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Player.sharedPreferences.edit();
        editor.putBoolean("boolean", false);
        editor.commit();
    }
}
