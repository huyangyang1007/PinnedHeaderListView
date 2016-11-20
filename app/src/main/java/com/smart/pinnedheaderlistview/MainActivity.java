package com.smart.pinnedheaderlistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.smart.library.PinnedHeaderListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PinnedHeaderListView mPinnedHeaderListView = (PinnedHeaderListView) findViewById(R.id.list_view);

        for (int i = 0; i <= 10; i++) {
            ArrayList<String> list = new ArrayList<>();
            for (int j = 0; j <= 10; j++) {
                list.add(j, "group : " + i + " index: " + j);
            }
            mList.add(i, list);
        }

        PinnedHeaderAdapter adapter = new PinnedHeaderAdapter(getApplicationContext(), mList);
        mPinnedHeaderListView.setAdapter(adapter);
        mPinnedHeaderListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int groupIndex, int itemIndexInGroup, long id) {
                Toast.makeText(getApplicationContext(), "group " + groupIndex + " index " + itemIndexInGroup, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHeaderClick(AdapterView<?> adapterView, View view, int groupIndex, long id) {
                Toast.makeText(getApplicationContext(), "header " + groupIndex, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
