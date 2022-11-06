package com.example.oficialbombero.models;

public class Cliente {

    String id;
    String cedula;
    String name;
    String ape;
    String address;
    String sexo;
    String email;
    String image;
    String token;
    String ciuidad;
    String phone;
    String dateborn;
    String tokenimage;
    private boolean online;

    public Cliente() {
    }

    public Cliente(String id, String cedula, String name, String ape, String address, String sexo, String email, String image, String token, String ciuidad, String phone, String dateborn, String tokenimage, boolean online) {
        this.id = id;
        this.cedula = cedula;
        this.name = name;
        this.ape = ape;
        this.address = address;
        this.sexo = sexo;
        this.email = email;
        this.image = image;
        this.token = token;
        this.ciuidad = ciuidad;
        this.phone = phone;
        this.dateborn = dateborn;
        this.tokenimage = tokenimage;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCiuidad() {
        return ciuidad;
    }

    public void setCiuidad(String ciuidad) {
        this.ciuidad = ciuidad;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateborn() {
        return dateborn;
    }

    public void setDateborn(String dateborn) {
        this.dateborn = dateborn;
    }

    public String getTokenimage() {
        return tokenimage;
    }

    public void setTokenimage(String tokenimage) {
        this.tokenimage = tokenimage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
