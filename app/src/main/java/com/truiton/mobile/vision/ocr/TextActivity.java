package com.truiton.mobile.vision.ocr;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TextActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;

    //    private String names[] = {"Arun","Parkavi","Ishwarya","Dinesh","Sriram"};
    ArrayList<String> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        String state = "state";
        places = (ArrayList<String>)getIntent().getSerializableExtra("Data");
        if(places==null || (places!=null && places.size()>0))
        {
            ArrayList<String> actualList = new ArrayList<>();
            for (int i=0;i<places.size();i++)
            {
//                Log.e( "onCreate: ",places.get(i) );
            }
        }

        myAdapter = new MyAdapter(places,this);
        recyclerView.setAdapter(myAdapter);
    }
}