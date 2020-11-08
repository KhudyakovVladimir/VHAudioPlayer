package com.example.vhaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;

public class ListItemAdapter extends ArrayAdapter<File> implements View.OnClickListener {
    LayoutInflater layoutInflater;
    File[] listOfFiles;
    int layout;
    File fileForOnClick;
    String fileSize;

    public ListItemAdapter(Context context, int resource, File[] listOfFiles) {
        super(context, resource, listOfFiles);
        context = getContext();
        this.layout = resource;
        this.listOfFiles = listOfFiles;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listOfFiles.length;
    }

    @Override
    public File getItem(int position) {
        return listOfFiles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    File getFile(int position) {
        return (getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.list_item, parent, false);
        }

        File file = getItem(position);
        assert file != null;

        fileForOnClick = file;

        //find metadata//////////////
        fileSize = fileSize(file.getPath());

        TextView textViewItem = view.findViewById(R.id.textViewItem);
        TextView textViewItem2 = view.findViewById(R.id.textViewItem2);

        textViewItem.setText(file.toString());
        textViewItem2.setText(fileSize);
        textViewItem.setTag(fileForOnClick.toString());

        textViewItem.setOnClickListener(this);

        CheckBox checkBox = view.findViewById(R.id.checkBoxItem);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox.setTag(file.toString());

        return view;
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //getFile((Integer) buttonView.getTag()).flag = isChecked;
            Model.selectedFile = (String) buttonView.getTag();
        }
    };

    @Override
    public void onClick(View v) {
        Model.mainFileName = (String) v.getTag();
        File file = new File(Model.mainFileName);

        if(file.isDirectory()){
            show(getContext());
        }
        if(file.isFile()){
            show(getContext());
        }

        Model.heap.add(Model.mainFileName);
        Model.count++;

        FileManager.textView.setText(Model.mainFileName);
    }

    //вызывает активити из статического контекста
    public static void show(Context context) {
        Intent intent = new Intent(context, FileManager.class);
        context.startActivity(intent);
    }

    private static String getFileSizeMegaBytes(File file) {
        return (double) file.length()/(1024*1024)+" mb";
    }

    public String fileSize(String pathName){
        File file = new File(pathName);
        double megaBytes = (double) file.length() / (1024 * 1024);
        String str = String.format("%.2f", megaBytes);
        String result = str + " mb";
        return result;
    }
}

