package com.example.queuemanager.model;

public class ActiveQueue {
    private int queue_image;
    private int queue_id;
    private String QueueName;
    private String PatientCount;

    public ActiveQueue(int queue_image, int queue_id, String queueName, String patientCount) {
        this.queue_image = queue_image;
        this.queue_id = queue_id;
        QueueName = queueName;
        PatientCount = patientCount;
    }

    public int getQueue_image() {
        return queue_image;
    }

    public void setQueue_image(int queue_image) {
        this.queue_image = queue_image;
    }

    public int getQueue_id() {
        return queue_id;
    }

    public void setQueue_id(int queue_id) {
        this.queue_id = queue_id;
    }

    public String getQueueName() {
        return QueueName;
    }

    public void setQueueName(String queueName) {
        QueueName = queueName;
    }

    public String getPatientCount() {
        return PatientCount;
    }

    public void setPatientCount(String patientCount) {
        PatientCount = patientCount;
    }
}
