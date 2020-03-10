package com.example.dugarolo;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class FarmColor {

    String nameFarm;
    int idColor;

    ArrayList<FarmColor> farmColors = new ArrayList<FarmColor>();

    public FarmColor(String nameFarm, int idColor){
        this.idColor = idColor;
        this.nameFarm =  nameFarm;
    }

    public int getIdColor() {
        return idColor;
    }

    public String getNameFarm() {
        return nameFarm;
    }

    public void updateList(ArrayList<FarmColor> farmColors){
        farmColors = this.farmColors;
    }

    public ArrayList<FarmColor> getFarmColors(){
        return this.farmColors;
    }

    
    @NonNull
    @Override
    public String toString() {
        String toReturn = "NameFarm : "+this.nameFarm+", IdColor : "+this.idColor+"";
        //Log.d("FarmColorStampa", toReturn);
        return toReturn;
    }
}
