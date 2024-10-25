package com.ahdevelopment.callhandlingapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // Set up RecyclerView with summary data from the server or local storage
    }
}
