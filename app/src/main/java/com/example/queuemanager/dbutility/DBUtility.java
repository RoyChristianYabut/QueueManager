package com.example.queuemanager.dbutility;

public interface DBUtility {
//    String jdbcDriverName = "vxcd9lOiVlb9DcyuaKAzLr5qD7AQB+5gr7zwfl1MXhY=";
//    String jdbcUrl ="Fcr4UFEAihYbgy0ONZrH0yOieNzuRPvdibFJ3A1Rh9s=";
//    String dbUserName = "ZtZog8cq99D+yewURdclvw==";
//    String dbPassword = "Z+pHcz0oWkDRL90KPQ5jQA";

    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl ="jdbc:mysql://10.70.0.17/keruxdbupdate";
    String dbUserName = "KeruxAdmin";
    String dbPassword = "admin";

    String SELECT_LIST_PATIENT ="SELECT * from department ";
    String LOGIN_CRED="select queuemanager_id, clinic_id, username, firstname, lastname, email from queuemanager where username=? and password =?";


    String SELECT_DEPARTMENT_LIST="SELECT Department_ID, Name from department WHERE Status='Active' and clinic_id=? ";
    String SELECT_DOCTOR_LIST="SELECT * from doctor WHERE Status='Active' and clinic_id=? "; //WHERE DEPARTMENT ID IS ALSO INCLUDED; NOT YET COMPLETE
    String SELECT_PATIENT_LIST="SELECT i.instance_id, i.patient_id, i.queuetype, i.timestamp, i.status, i.priority, i.queuenumber from instance i inner join queuelist ql on ql.instance_id=i.instance_id where ql.queue_id = ?";
    String SELECT_QUEUE="SELECT Queue_ID from queue WHERE Doctor_ID=? and Department_ID=? and Status='Active'";

    String GET_NEW_QUEUE_ID="SELECT MAX(queue_id) from queue";

    String INSERT_INTO_QUEUE="INSERT INTO queue (QueueName, TimeStamp, Doctor_ID, Department_ID, Status) values (?, ?, ?, ?, 'Active')";

}
