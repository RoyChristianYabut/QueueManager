package com.example.queuemanager.model;

public class Doctor {
    private int doctorimage;
    private int doctorid;
    private String doctorfirstname;
    private String doctorlastname;

    public Doctor(int doctorimage, int doctorid, String doctorfirstname, String doctorlastname) {
        this.doctorimage = doctorimage;
        this.doctorid = doctorid;
        this.doctorfirstname = doctorfirstname;
        this.doctorlastname = doctorlastname;
    }

    public int getDoctorimage() {
        return doctorimage;
    }

    public void setDoctorimage(int doctorimage) {
        this.doctorimage = doctorimage;
    }

    public int getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(int doctorid) {
        this.doctorid = doctorid;
    }

    public String getDoctorfirstname() {
        return doctorfirstname;
    }

    public void setDoctorfirstname(String doctorfirstname) {
        this.doctorfirstname = doctorfirstname;
    }

    public String getDoctorlastname() {
        return doctorlastname;
    }

    public void setDoctorlastname(String doctorlastname) {
        this.doctorlastname = doctorlastname;
    }
}
