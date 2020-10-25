package com.example.queuemanager.model;

public class Patient {
    private int patientimage;
    private int instanceid;
    private int patientid;
    private String queuetype;
    private String TimeStamp;
    private String Status;
    private String Priority;
    private String QueueNumber;

    public Patient(int patientimage, int instanceid, int patientid, String queuetype, String timeStamp, String status, String priority, String queueNumber) {
        this.patientimage = patientimage;
        this.instanceid = instanceid;
        this.patientid = patientid;
        this.queuetype = queuetype;
        TimeStamp = timeStamp;
        Status = status;
        Priority = priority;
        QueueNumber = queueNumber;
    }

    public int getPatientimage() {
        return patientimage;
    }

    public void setPatientimage(int patientimage) {
        this.patientimage = patientimage;
    }

    public int getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(int instanceid) {
        this.instanceid = instanceid;
    }

    public int getPatientid() {
        return patientid;
    }

    public void setPatientid(int patientid) {
        this.patientid = patientid;
    }

    public String getQueuetype() {
        return queuetype;
    }

    public void setQueuetype(String queuetype) {
        this.queuetype = queuetype;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getQueueNumber() {
        return QueueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        QueueNumber = queueNumber;
    }
}
