package com.example.dugarolo;
import android.app.Application;

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

    boolean checkIfAlreadyAssignedBert = false;
    boolean checkIfAlreadyAssignedFerr = false;

    ArrayList<FarmColor> farmColors = new ArrayList<FarmColor>();

    private Globals() {

    }

    public void updateList(FarmColor farmColor){
        farmColors.add(farmColor);
    }

    public ArrayList<FarmColor> getFarmColors(){
        return this.farmColors;
    }

    public void setCheckIfAlreadyAssignedBert() {
        checkIfAlreadyAssignedBert = true;
    }

    public boolean isCheckIfAlreadyAssignedBert() {
        return checkIfAlreadyAssignedBert;
    }

    public void setCheckIfAlreadyAssignedFerr() {
        this.checkIfAlreadyAssignedFerr = true;
    }

    public boolean isCheckIfAlreadyAssignedFerr() {
        return checkIfAlreadyAssignedFerr;
    }

}
