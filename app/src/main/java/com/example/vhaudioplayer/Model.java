package com.example.vhaudioplayer;

import java.io.File;
import java.util.ArrayList;

public class Model {
    static String ROOT_PATH = "/";
    //static final String ROOT_PATH_2 = "/sdcard";
    public static String mainFileName;
    //папка с музыкой
    static String musicFolder;
    ////////////////////
    //текущая папка для воспроизведения
    static File currentMusicFolder;
    //////////////////
    //текущие треки папки для воспроизведения/
    static File[] currentAlbumTracks;
    /////////////////////////////////
    //текущий трек
    static File currentTrack;
    /////////////////
    static String selectedFile;

    static boolean flagFirstCreated = true;

    static int codeForMenu = 0;
    static int musicFoldersCount = 0;
    static int musicTracksCount = 0;
    static int musicTracksAmount = 0;
    static int seekBarCurrentPosition = 0;

    static double trackTime;
    //список музыкальных папок
    static File[] listOfMusicFolders;
    //////////////////////////
    static ArrayList<String> heap = new ArrayList<>();
    static int count = 0;

    public static File setFileName(String fileName){
        File file = new File(fileName);
        return file;
    }
}
