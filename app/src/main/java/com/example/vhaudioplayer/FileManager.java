package com.example.vhaudioplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FileManager extends AppCompatActivity {
    static TextView textView;
    static ListView listView;
    ListItemAdapter listItemAdapter;
    private static final int REQUEST_PERMISSION_WRITE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        textView.setText(Model.mainFileName);
        checkPermissions();

        if(checkPermissions()){
            //Model.ROOT_PATH = "/";
            isMountSdCard();
        }

        //code from isMountSdCard();

        if (Model.flagFirstCreated){
            //получаем корневую папку и сортируем список
            Model.listOfMusicFolders = Model.setFileName(Model.ROOT_PATH).listFiles();
            assert Model.listOfMusicFolders != null;
            Arrays.sort(Model.listOfMusicFolders);

            //передаём его адаптеру и добавляем в список
            listItemAdapter = new ListItemAdapter(this, R.layout.list_item, Model.listOfMusicFolders);
            listView.setAdapter(listItemAdapter);

            Model.heap.add(Model.ROOT_PATH);
            Model.flagFirstCreated = false;
        }

        else {
            Model.listOfMusicFolders = Model.setFileName(Model.mainFileName).listFiles();

            if(Model.listOfMusicFolders != null) {
                Arrays.sort(Model.listOfMusicFolders);
                listItemAdapter = new ListItemAdapter(this, R.layout.list_item, Model.listOfMusicFolders);
                listView.setAdapter(listItemAdapter);
            }else {
                Toast.makeText(this, "No files here.", Toast.LENGTH_SHORT).show();
            }
        }

        if(Model.mainFileName != null){
            File file = new File(Model.mainFileName);
            if(file.isFile()){
                openFile(file.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(Model.heap.size() > 1){
            Model.count--;
            Model.heap.remove(Model.heap.size() - 1);
            int position = Model.heap.size();
            Model.mainFileName = Model.heap.get(position - 1);
            openDirectory(Model.mainFileName);
        }
    }


    //метод определяет MIME-Type и возвращает его в String
    public static String getMimeType(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    private boolean checkPermissions(){
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

    void openDirectory(String folderName){
        recreate();
    }

    public void openFile(String localUri){
        File file = new File(localUri);
        Uri contentUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
        openFileIntent.setDataAndTypeAndNormalize(contentUri, getMimeType(Model.mainFileName));
        openFileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(openFileIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Model.codeForMenu == 0) {
            getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        }
        return true;
    }
    //обработчик нажатия на пунк actionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.chooseMusicFolder: {
                if(hasFoldersInside(Model.selectedFile)){
                    Player.flagForSingleMusicFolder = true;
                    //Model.codeForMenu = 1;
                    String openDirectoryPath = Model.heap.get(Model.heap.size() - 1);
                    Model.musicFolder = Model.selectedFile;

                    //Model.currentTrack =

                    Model.mainFileName = openDirectoryPath;
                    Model.listOfMusicFolders = Model.setFileName(Model.musicFolder).listFiles();

                    if(Model.listOfMusicFolders != null) {
                        Model.currentMusicFolder = Model.listOfMusicFolders[0];
                        Model.currentAlbumTracks = Model.currentMusicFolder.listFiles();
                        if(Model.currentAlbumTracks != null){
                            Model.currentTrack = Model.currentAlbumTracks[0];
                        }
                        Arrays.sort(Model.listOfMusicFolders);
                        listItemAdapter = new ListItemAdapter(this, R.layout.list_item, Model.listOfMusicFolders);
                        listView.setAdapter(listItemAdapter);
                    }else {
                        Toast.makeText(this, "No files here.", Toast.LENGTH_SHORT).show();
                    }

                    Player.sharedPreferences = getSharedPreferences(Player.APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = Player.sharedPreferences.edit();
                    editor.putString("musicFolder", Model.musicFolder);
                    editor.putString("currentMusicFolder", String.valueOf(Model.currentMusicFolder));
                    editor.putString("currentTrack", String.valueOf(Model.currentTrack));
                    editor.putInt("musicFoldersCount", 0);
                    editor.commit();

                    startActivity(new Intent(this, Player.class));
                }else {
                    Toast.makeText(this, "Select another folder", Toast.LENGTH_SHORT).show();
                    Model.musicFolder = Model.selectedFile;
                    File file = new File(Model.musicFolder);
                    Model.currentAlbumTracks = file.listFiles();
                    Model.currentTrack = Model.currentAlbumTracks[0];

                    Player.sharedPreferences = getSharedPreferences(Player.APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = Player.sharedPreferences.edit();
                    editor.putString("musicFolder", Model.musicFolder);
                    editor.putString("currentMusicFolder", String.valueOf(Model.musicFolder));
                    editor.putString("currentTrack", String.valueOf(Model.currentTrack));
                    editor.putInt("musicFoldersCount", 0);
                    editor.commit();

                    Player.flagForSingleMusicFolder = false;
                    startActivity(new Intent(this, Player.class));
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasFoldersInside(String musicFolder){
        int countOfFolders = 0;
        File file = new File(musicFolder);
        File[] listOfMusicFolders = file.listFiles();
        //System.out.println("FILE MANAGER - listOfMusicFolders = " + listOfMusicFolders.length);
        for (int i = 0; i < Objects.requireNonNull(listOfMusicFolders).length; i++) {
            if(listOfMusicFolders[i].isDirectory()){
                countOfFolders++;
            }
        }
        //System.out.println("FILE MANAGER - hasFoldersInside = " + countOfFolders);
        if(countOfFolders > 0){
            return true;
        }else {
            return false;
        }
    }

    boolean isMountSdCard(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Model.ROOT_PATH = "/";
            System.out.println("SD-карта не доступна: " + Environment.getExternalStorageState());
            return false;
        }else {
            //File sdPath = Environment.getExternalStorageDirectory();
            Model.ROOT_PATH = "/sdcard";
            return true;
        }
    }
}
