package com.getrent.timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.getrent.timer.database.DBConnection;
import com.getrent.timer.model.TimerList;

import java.util.ArrayList;
import java.util.List;

public class TimerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DBConnection sqliteDB;
    private ArrayList<TimerList> lists;
    private TimerListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lists = new ArrayList<>();
        sqliteDB = new DBConnection(this);
        lists = sqliteDB.checkTimerList();
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new TimerListAdapter(this, lists);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
