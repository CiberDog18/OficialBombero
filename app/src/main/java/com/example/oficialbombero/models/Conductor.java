package com.example.oficialbombero.models;

public class Conductor {
    String id;
    String cedula;
    String name;
    String email;
    String ape;
    String address;
    String sexo;
    String image;
    String phone;
    String ciuidad;
    String dateborn;
    String contrato;
    String token;
    private boolean online;

    public Conductor() {
    }

    public Conductor(String id, String cedula, String name, String email, String ape, String address, String sexo, String image, String phone, String ciuidad, String dateborn, String contrato, String token, boolean online) {
        this.id = id;
        this.cedula = cedula;
        this.name = name;
        this.email = email;
        this.ape = ape;
        this.address = address;
        this.sexo = sexo;
        this.image = image;
        this.phone = phone;
        this.ciuidad = ciuidad;
        this.dateborn = dateborn;
        this.contrato = contrato;
        this.token = token;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApe() {
        return ape;
    }

    public void setApe(String ape) {
        this.ape = ape;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCiuidad() {
        return ciuidad;
    }

    public void setCiuidad(String ciuidad) {
        this.ciuidad = ciuidad;
    }

    public String getDateborn() {
        return dateborn;
    }

    public void setDateborn(String dateborn) {
        this.dateborn = dateborn;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
