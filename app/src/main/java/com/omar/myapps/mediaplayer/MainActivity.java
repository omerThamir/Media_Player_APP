package com.omar.myapps.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toolbar;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private EditText searchET;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private List<Song> songsList;
    private ImageButton searchBTN;

    public void startPlayingActivity(String songUri, String songName, int songIndex) {
        Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
        intent.putExtra("songUri", songUri);
        intent.putExtra("songName", songName);
        intent.putExtra("songIndex", songIndex);
        intent.putExtra("songsList", (Serializable) songsList);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestThePermissions();

        init();

        getAllAudioFromDevice();


        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if the user wrote something
                String searchFor = searchET.getText().toString().trim();
                if (searchFor.length() > 0) {
                    ArrayList<Song> resArrayList = new ArrayList<>();    // create new array list to save search result in it
                    for (Song song : songsList) {                    // class song contains Name,path,id
                        if (song.name.contains(searchFor)) {
                            resArrayList.add(song);
                        }
                    }
                    myRecyclerAdapter = new MyRecyclerAdapter(MainActivity.this, resArrayList);
                    recyclerView.setAdapter(myRecyclerAdapter);
                } else {
                    // if the user did not write any ting or removed his search -> set original list
                    myRecyclerAdapter = new MyRecyclerAdapter(MainActivity.this, songsList);
                    recyclerView.setAdapter(myRecyclerAdapter);
                }
            }
        });


        // inside on create
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // if the user wrote something
                if (charSequence.length() <= 0) {
                    // if the user did not write any ting or removed his search -> set original list
                    myRecyclerAdapter = new MyRecyclerAdapter(MainActivity.this, songsList);
                    recyclerView.setAdapter(myRecyclerAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    private void requestThePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
                    WAKE_LOCK}, 1);
        }
    }


    void init() {

        initRecyclerView();
        searchET = findViewById(R.id.searchET);
        searchBTN = findViewById(R.id.searchBTN);
    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.reclerView);
        songsList = new ArrayList<>();
        myRecyclerAdapter = new MyRecyclerAdapter(MainActivity.this, songsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerAdapter);
    }


    // Method to read all the audio/MP3 files.
    public void getAllAudioFromDevice() {

        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AlbumColumns.ARTIST};

        Cursor c = cr.query(uri, projection, selection, null, sortOrder);

        if (c != null) {

            while (c.moveToNext()) {
                // Create a model object.
                String path = c.getString(0);   // Retrieve path.
                String name = c.getString(1);   // Retrieve name.
                String artist = c.getString(2);   // Retrieve name.

                // String album = c.getString(2);  // Retrieve album name.
                //   String artist = c.getString(3); // Retrieve artist name.

                // Add the model object to the list .
                songsList.add(new Song(name, path, artist));
            }
            c.close();
        }
    }

    void getFuckingAudio() {
        ContentResolver cr = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    String songName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String songPath = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String auther = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.AUTHOR));
                    // Add code to get more column here
                    songsList.add(new Song(songName, songPath, auther));
                    // Save to your list here
                }
            }

            cur.close();
        }
    }
}
