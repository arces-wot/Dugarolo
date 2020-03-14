package com.example.dugarolo;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class Globals {


    private static Globals instance = new Globals();


    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    MyMapView myMapView = null;

    ArrayList<FarmColor> farmColors = new ArrayList<FarmColor>();

    private Globals() {}

    public void updateList(FarmColor farmColor){
        farmColors.add(farmColor);
    }

    public ArrayList<FarmColor> getFarmColors(){
        return this.farmColors;
    }

    public boolean searchForAssignedFarm(String nameFarm) {

        for(int i=0; i<farmColors.size(); i++)
        {
            if(nameFarm.equals(farmColors.get(i).nameFarm))
                return true;
        }
        return false;
    }
}
