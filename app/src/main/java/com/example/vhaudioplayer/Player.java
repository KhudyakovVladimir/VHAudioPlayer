package com.example.vhaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    public static final String APP_PREFERENCES = "mysettings";
    static SharedPreferences sharedPreferences;
    static boolean permissionsCheck = true;
    static boolean flagForSingleMusicFolder = true;
    static boolean flagVolume = true;
    static boolean flagLooping = false;

    final String LOG_TAG = "myLogs";

    //double trackTime;

    static MediaPlayer mediaPlayer;
    AudioManager audioManager;

    Button buttonPlay;
    Button buttonPause;
    Button buttonResume;
    Button buttonStop;
    Button buttonPrevFolder;
    Button buttonNextFolder;
    Button buttonSoundOn;
    Button buttonSoundOff;
    Button buttonRepeatOn;
    Button buttonRepeatOff;
    SeekBar seekBar;

    ImageButton imageButton;

    TextView textViewFolderName;
    TextView textViewBandName;
    TextView textViewTrackName;
    TextView textViewCurrentTrackTime;
    TextView textViewTrackTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pleer);

        //sharedPreferences = getSharedPreferences(Player.APP_PREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear();
        //editor.commit();
        //System.out.println("ON_CREATE");
        //System.out.println("Model.currentTrack - " + Model.currentTrack);
        //System.out.println("Model.musicTracksCount - " + Model.musicTracksCount );
        //System.out.println("Model.trackTime - " + Model.trackTime);
        //System.out.println("Model.seekBarCurrentPosition - " + Model.seekBarCurrentPosition);

        //check the permissions///////////////////////////////////////////////////////////////
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.contains("boolean")){
            permissionsCheck = sharedPreferences.getBoolean("boolean", Boolean.parseBoolean(""));
        }else {
            startActivity(new Intent(this, Permissions.class));
        }
        //check the permissions_end/////////////////////////////////////////////////////////////////

        if(sharedPreferences.contains("musicFolder")){
            Model.musicFolder = sharedPreferences.getString("musicFolder", "");
        }

        if(sharedPreferences.contains("currentMusicFolder")){
            Model.currentMusicFolder = new File(sharedPreferences.getString("currentMusicFolder", ""));
        }

        if(sharedPreferences.contains("currentTrack")){
            Model.currentTrack = new File(sharedPreferences.getString("currentTrack", ""));
            Model.musicTracksCount = sharedPreferences.getInt("musicTacksCount", 0);
            Model.trackTime = Double.parseDouble(sharedPreferences.getString("trackTime", String.valueOf(Model.trackTime)));
            Model.seekBarCurrentPosition = sharedPreferences.getInt("seekbarCurrentPosition", 0);
            //System.out.println("CURRENT_TRACK");
            //System.out.println("Model.currentTrack - " + Model.currentTrack);
            //System.out.println("Model.musicTracksCount - " + Model.musicTracksCount );
            //System.out.println("Model.trackTime - " + Model.trackTime);
            //System.out.println("Model.seekBarCurrentPosition - " + Model.seekBarCurrentPosition);
        }else {

        }

        if(sharedPreferences.contains("musicFoldersCount")){
            Model.musicFoldersCount = sharedPreferences.getInt("musicFoldersCount", 0);
        }

        if(Model.musicFolder != null){
            Model.listOfMusicFolders = Model.setFileName(Model.musicFolder).listFiles();
        }

        if (Model.listOfMusicFolders != null) {
            Model.currentMusicFolder = Model.listOfMusicFolders[Model.musicFoldersCount];
        }

        if(Model.currentMusicFolder != null){
            Model.currentAlbumTracks = Model.currentMusicFolder.listFiles();
            //Model.currentTrack = Model.currentAlbumTracks[0];
        }

        //System.out.println("END");
        //System.out.println("Model.currentTrack - " + Model.currentTrack);
        //System.out.println("Model.musicTracksCount - " + Model.musicTracksCount );
        //System.out.println("Model.trackTime - " + Model.trackTime);
        //System.out.println("Model.seekBarCurrentPosition - " + Model.seekBarCurrentPosition);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPause = findViewById(R.id.buttonPause);
        buttonResume = findViewById(R.id.buttonResume);
        buttonStop = findViewById(R.id.buttonStop);
        buttonPrevFolder = findViewById(R.id.buttonPrevFolder);
        buttonNextFolder = findViewById(R.id.buttonNextFolder);
        buttonSoundOn = findViewById(R.id.buttonSoundOn);
        buttonSoundOff = findViewById(R.id.buttonSoundOff);
        buttonRepeatOn = findViewById(R.id.buttonRepeatOn);
        buttonRepeatOff = findViewById(R.id.buttonRepeatOff);

        if(!flagForSingleMusicFolder){
           buttonPrevFolder.setEnabled(false);
           buttonPrevFolder.setVisibility(View.INVISIBLE);
           buttonNextFolder.setEnabled(false);
           buttonNextFolder.setVisibility(View.INVISIBLE);
        }else {
            buttonPrevFolder.setEnabled(true);
            buttonPrevFolder.setVisibility(View.VISIBLE);
            buttonNextFolder.setEnabled(true);
            buttonNextFolder.setVisibility(View.VISIBLE);
        }

        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax((int) Model.trackTime);
        seekBar.setProgress(Model.seekBarCurrentPosition);

        buttonPause.setVisibility(View.INVISIBLE);
        buttonPause.setEnabled(false);
        buttonResume.setVisibility(View.INVISIBLE);
        buttonResume.setEnabled(false);
        buttonSoundOn.setVisibility(View.INVISIBLE);
        buttonSoundOn.setEnabled(false);
        buttonRepeatOff.setVisibility(View.INVISIBLE);
        buttonRepeatOff.setEnabled(false);


        textViewFolderName = findViewById(R.id.textViewFolderName);
        textViewBandName = findViewById(R.id.textViewBandName);
        textViewTrackName = findViewById(R.id.textViewTrackName);
        textViewCurrentTrackTime = findViewById(R.id.textViewCurrentTrackTime);
        textViewTrackTime = findViewById(R.id.textViewTrackTime);

        int trackTimeMillis = ((int) Model.trackTime) * 1000;
        int currentTrackTimeMillis = Model.seekBarCurrentPosition * 1000;
        String currentTrackTime = String.format("%d:%02d", (currentTrackTimeMillis % (1000 * 60 * 60)) / (1000 * 60), ((currentTrackTimeMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        textViewCurrentTrackTime.setText(currentTrackTime);
        String trackTime = String.format("%d:%02d", (trackTimeMillis % (1000 * 60 * 60)) / (1000 * 60), ((trackTimeMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        textViewTrackTime.setText(trackTime);

        imageButton = findViewById(R.id.imageView);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        setMetaData();
        if(Model.currentAlbumTracks != null){
            findCover(Model.currentAlbumTracks);
        }

        final Handler mHandler = new Handler();
        Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    Model.trackTime = mediaPlayer.getDuration() / 1000;
                    seekBar.setMax((int) Model.trackTime);

                    Model.seekBarCurrentPosition =  mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(Model.seekBarCurrentPosition);
                    //////////set time///////
                    String currentTrackTime = String.format("%d:%02d", (mediaPlayer.getCurrentPosition() % (1000 * 60 * 60)) / (1000 * 60), ((mediaPlayer.getCurrentPosition() % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                    textViewCurrentTrackTime.setText(currentTrackTime);
                    String trackTime = String.format("%d:%02d", (mediaPlayer.getDuration() % (1000 * 60 * 60)) / (1000 * 60), ((mediaPlayer.getDuration() % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                    textViewTrackTime.setText(trackTime);
                    ////////////////////////

                    if(Model.seekBarCurrentPosition == Model.trackTime){
                        seekBar.setProgress(0);
                        textViewCurrentTrackTime.setText("0:00");
                        buttonPause.setVisibility(View.INVISIBLE);
                        buttonPause.setEnabled(false);
                        buttonResume.setVisibility(View.INVISIBLE);
                        buttonResume.setEnabled(false);
                        buttonPlay.setVisibility(View.VISIBLE);
                        buttonPlay.setEnabled(true);
                        mediaPlayer.stop();

                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonPrevFolder : {
                buttonPause.setVisibility(View.VISIBLE);
                buttonPause.setEnabled(true);
                buttonResume.setVisibility(View.INVISIBLE);
                buttonResume.setEnabled(false);

                Model.musicTracksCount = 0;
                if(Model.listOfMusicFolders != null){
                    if(Model.musicFoldersCount > 0){
                        Model.musicFoldersCount--;
                        Model.currentMusicFolder = Model.listOfMusicFolders[Model.musicFoldersCount];
                        Model.currentAlbumTracks = Model.currentMusicFolder.listFiles();

                        if(Model.currentAlbumTracks != null){
                            Model.currentTrack = Model.currentAlbumTracks[0];
                        }
                    }
                }
                setMetaData();
                findCover(Model.currentAlbumTracks);
                releaseMediaPleer();
                play();
                break;
            }
            case R.id.buttonNextFolder : {
                buttonPause.setVisibility(View.VISIBLE);
                buttonPause.setEnabled(true);
                buttonResume.setVisibility(View.INVISIBLE);
                buttonResume.setEnabled(false);

                Model.musicTracksCount = 0;
                if(Model.listOfMusicFolders != null){
                    if(Model.musicFoldersCount >= 0 && Model.musicFoldersCount < Model.listOfMusicFolders.length - 1){
                        Model.musicFoldersCount++;
                        Model.currentMusicFolder = Model.listOfMusicFolders[Model.musicFoldersCount];
                        Model.currentAlbumTracks = Model.currentMusicFolder.listFiles();
                        if(Model.currentAlbumTracks != null){
                            Model.currentTrack = Model.currentAlbumTracks[0];
                        }
                    }
                }
                setMetaData();
                findCover(Model.currentAlbumTracks);
                releaseMediaPleer();
                play();
                break;
            }
            case R.id.buttonPrevTrack : {
                if(Model.musicTracksCount > 0){
                    buttonPause.setVisibility(View.VISIBLE);
                    buttonPause.setEnabled(true);
                    buttonResume.setVisibility(View.INVISIBLE);
                    buttonResume.setEnabled(false);

                    Model.musicTracksCount--;
                    Model.currentTrack = Model.currentAlbumTracks[Model.musicTracksCount];
                    setMetaData();
                    releaseMediaPleer();
                    play();
                }
                break;
            }
            case R.id.buttonNextTrack : {
                buttonPause.setVisibility(View.VISIBLE);
                buttonPause.setEnabled(true);
                buttonResume.setVisibility(View.INVISIBLE);
                buttonResume.setEnabled(false);
                if(Model.musicTracksCount < tracksAmount(Model.currentAlbumTracks) - 1){
                    Model.musicTracksCount++;
                    Model.currentTrack = Model.currentAlbumTracks[Model.musicTracksCount];
                    setMetaData();
                    releaseMediaPleer();
                    play();
                }
                break;
            }
            case R.id.buttonPlay : {
                play();
                mediaPlayer.seekTo(Model.seekBarCurrentPosition * 1000);
                Model.seekBarCurrentPosition = 0;
                //System.out.println("PLAY");
                //System.out.println("Model.currentTrack - " + Model.currentTrack);
                //System.out.println("Model.musicTracksCount - " + Model.musicTracksCount );
                //System.out.println("Model.trackTime - " + Model.trackTime);
                //System.out.println("Model.seekBarCurrentPosition - " + Model.seekBarCurrentPosition);
                break;
            }
            case R.id.buttonPause : {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    buttonPause.setVisibility(View.INVISIBLE);
                    buttonPause.setEnabled(false);
                    buttonResume.setVisibility(View.VISIBLE);
                    buttonResume.setEnabled(true);
                }
                break;
            }

            case R.id.buttonResume:{
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                buttonResume.setVisibility(View.INVISIBLE);
                buttonResume.setEnabled(false);
                buttonPause.setVisibility(View.VISIBLE);
                buttonPause.setEnabled(true);
                break;
            }

            case R.id.buttonStop : {
                //Log.d(LOG_TAG, "STOP");
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                }
                buttonPause.setVisibility(View.INVISIBLE);
                buttonPause.setEnabled(false);
                buttonResume.setVisibility(View.INVISIBLE);
                buttonResume.setEnabled(false);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonPlay.setEnabled(true);
                seekBar.setProgress(0);
                Model.seekBarCurrentPosition = 0;
                textViewCurrentTrackTime.setText("0:00");
                releaseMediaPleer();
                break;
            }
        }
    }
    private void releaseMediaPleer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        System.out.println("ON_COMPLETION!!!");
        buttonPause.setVisibility(View.VISIBLE);
        buttonPause.setEnabled(true);
        buttonResume.setVisibility(View.INVISIBLE);
        buttonResume.setEnabled(false);
        mediaPlayer.stop();
        mediaPlayer.reset();
        if(Model.musicTracksCount < tracksAmount(Model.currentAlbumTracks) - 1){
            Model.musicTracksCount++;
            Model.currentTrack = Model.currentAlbumTracks[Model.musicTracksCount];
            setMetaData();
            releaseMediaPleer();
            play();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mediaPlayer != null){
            mediaPlayer.seekTo(seekBar.getProgress() * 1000);
        }
    }

    public void imageButton(View view) {
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        startActivity(new Intent(this, FileManager.class));
    }

    public void setMetaData(){
        if(Model.currentTrack != null){
            if(Model.currentTrack.isFile()){
                if(Model.currentTrack != null){
                    if(getMimeType(String.valueOf(Model.currentTrack)).equals("audio/mpeg")){
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(String.valueOf(Model.currentTrack));
                        String artistName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        String trackName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String bitrateDisplay = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                        int bitrate = Integer.parseInt(bitrateDisplay) / 1000;

                        textViewFolderName.setText(Model.currentMusicFolder + " : "+ bitrate + " kbps");
                        textViewBandName.setText(artistName);
                        textViewTrackName.setText(trackName);
                    }
                }
            }
            if(Model.currentTrack.isDirectory()){

            }
        }
    }

    public void play(){
        if(Model.currentTrack == null){
            return;
        }
        buttonPlay.setVisibility(View.INVISIBLE);
        buttonPlay.setEnabled(false);
        buttonPause.setVisibility(View.VISIBLE);
        buttonPause.setEnabled(true);

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(String.valueOf(Model.currentTrack));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            //equalizer();
            //looping(flagLooping);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
            volume(flagVolume);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int tracksAmount(File[] currentAlbumTracks){
        if(currentAlbumTracks != null){
            for (int i = 0; i < currentAlbumTracks.length; i++) {
                if(!currentAlbumTracks[i].isDirectory()){
                    String s = getMimeType(String.valueOf(currentAlbumTracks[i]));
                    if(s.equals("audio/mpeg")){
                        Model.musicTracksAmount++;
                    }
                }
            }
        }
        int result = Model.musicTracksAmount;
        Model.musicTracksAmount = 0;
        return result;
    }

    public static String getMimeType(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    public int findCover(File[] currentAlbumTracks){
        for (int i = 0; i < currentAlbumTracks.length; i++) {
            if(!currentAlbumTracks[i].isDirectory()){
                String s = getMimeType(String.valueOf(currentAlbumTracks[i]));
                if(s.equals("image/jpeg")){
                    String pathName = currentAlbumTracks[i].toString();
                    Drawable d = Drawable.createFromPath(pathName);
                    imageButton.setBackground(d);
                }
                else {
                    imageButton.setBackground(ContextCompat.getDrawable(this, R.drawable.label));
                }
            }
        }
        int result = Model.musicTracksAmount;
        Model.musicTracksAmount = 0;
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentMusicFolder", String.valueOf(Model.currentMusicFolder));
        editor.putString("currentTrack", String.valueOf(Model.currentTrack));
        editor.putInt("musicTacksCount", Model.musicTracksCount);
        editor.putInt("musicFoldersCount", Model.musicFoldersCount);
        editor.putInt("seekbarCurrentPosition", seekBar.getProgress());

        if(mediaPlayer != null){
            editor.putString("trackTime", String.valueOf(mediaPlayer.getDuration() / 1000));
        }
        editor.commit();
        System.out.println("ON_PAUSE");
        System.out.println("Model.currentTrack - " + Model.currentTrack);
        System.out.println("Model.musicTracksCount - " + Model.musicTracksCount );
        System.out.println("Model.trackTime - " + Model.trackTime);
        System.out.println("Model.seekBarCurrentPosition - " + Model.seekBarCurrentPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("about");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("about")){
            Toast.makeText(this, "Copyright by Khudyakov Vladimir 2020 v1.6", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void equalizer() {
        Equalizer equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        short s = equalizer.getNumberOfBands();

        short ss = equalizer.getCurrentPreset();
        equalizer.setBandLevel(equalizer.getBand(10000), (short) 1500);
        System.out.println(Arrays.toString(equalizer.getBandLevelRange()));
        System.out.println("EQUALIZER = " + ss);
    }

    public void soundOff(View view) {
        flagVolume = false;
        volume(flagVolume);
    }
    public void soundOn(View view) {
        flagVolume = true;
        volume(flagVolume);
    }

    public void repeatOff(View view) {
        flagLooping = false;
        looping(flagLooping);
    }
    public void repeatOn(View view) {
        flagLooping = true;
        looping(flagLooping);
    }

    void volume(boolean flagVolume){
        if(flagVolume){
            if(mediaPlayer != null){
                mediaPlayer.setVolume(1.0f,1.0f);
                buttonSoundOn.setVisibility(View.INVISIBLE);
                buttonSoundOn.setEnabled(false);
                buttonSoundOff.setVisibility(View.VISIBLE);
                buttonSoundOff.setEnabled(true);
            }
        }else {
            if(mediaPlayer != null){
                mediaPlayer.setVolume(0.0f,0.0f);
                buttonSoundOff.setVisibility(View.INVISIBLE);
                buttonSoundOff.setEnabled(false);
                buttonSoundOn.setVisibility(View.VISIBLE);
                buttonSoundOn.setEnabled(true);
            }
        }
    }

    void looping(boolean flagLooping){
        if(flagLooping){
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(this);
            buttonRepeatOn.setVisibility(View.INVISIBLE);
            buttonRepeatOn.setEnabled(false);
            buttonRepeatOff.setVisibility(View.VISIBLE);
            buttonRepeatOff.setEnabled(true);

        }else {
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(this);
            buttonRepeatOff.setVisibility(View.INVISIBLE);
            buttonRepeatOff.setEnabled(false);
            buttonRepeatOn.setVisibility(View.VISIBLE);
            buttonRepeatOn.setEnabled(true);
        }
    }

    public void equalizer(View view) {
        startActivity(new Intent(this, com.example.vhaudioplayer.Equalizer.class));
    }
}