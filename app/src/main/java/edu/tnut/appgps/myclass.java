package edu.tnut.appgps;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class myclass extends Application {
    private  static myclass singleton;

    private List<Location> myLocations;
    public myclass getSingleton(){
        return singleton;
    }

    public List<Location> getMyLocation() {
        return myLocations;
    }

    public void setMyLocation(List<Location> myLocation) {
        this.myLocations = myLocation;
    }

    public void onCreate(){
        super.onCreate();
        singleton=this;
        myLocations = new ArrayList<>();
    }


}
