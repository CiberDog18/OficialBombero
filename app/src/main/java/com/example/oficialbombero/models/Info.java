package com.example.oficialbombero.models;

public class Info {

    double km;
    double min;
    double tarifaMin;

    public Info() {
    }

    public Info(double km, double min, double tarifaMin) {
        this.km = km;
        this.min = min;
        this.tarifaMin = tarifaMin;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getTarifaMin() {
        return tarifaMin;
    }

    public void setTarifaMin(double tarifaMin) {
        this.tarifaMin = tarifaMin;
    }
}
