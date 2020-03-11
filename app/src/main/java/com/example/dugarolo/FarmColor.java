package com.example.dugarolo;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class FarmColor implements Serializable {

    String nameFarm;
    int idColor;

    Globals globals = Globals.getInstance();

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

    @NonNull
    @Override
    public String toString() {
        String toReturn = "NameFarm : "+this.nameFarm+", IdColor : "+this.idColor+"";
        //Log.d("FarmColorStampa", toReturn);
        return toReturn;
    }
}
