package com.example.dugarolo;

import android.util.Log;

import androidx.annotation.NonNull;

public class FarmColor {

    String nameFarm;
    int idColor;
    boolean usedColor;

    public FarmColor(int idColor){
        this.idColor = idColor;
        this.usedColor = false;
        this.nameFarm = "";
    }

    @NonNull
    @Override
    public String toString() {
        String toReturn = "NameFarm : "+this.nameFarm+", IdColor : "+this.idColor+"";
        Log.d("FarmColorStampa", toReturn);
        return toReturn;
    }
}
