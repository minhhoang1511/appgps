package edu.tnut.appgps;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class showsavelocation extends AppCompatActivity {

    ListView lv_savelocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showsavelocation);
        lv_savelocations = findViewById(R.id.lv_waypoint);
        myclass Myclass = (myclass)getApplicationContext();
        List<Location> savelocation = Myclass.getMyLocation();
        lv_savelocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savelocation));
    }
}