package com.example.queuemanager.dbutility;

public interface DBUtility {
//    String jdbcDriverName = "vxcd9lOiVlb9DcyuaKAzLr5qD7AQB+5gr7zwfl1MXhY=";
//    String jdbcUrl ="Fcr4UFEAihYbgy0ONZrH0yOieNzuRPvdibFJ3A1Rh9s=";
//    String dbUserName = "ZtZog8cq99D+yewURdclvw==";
//    String dbPassword = "Z+pHcz0oWkDRL90KPQ5jQA";

/*    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl ="jdbc:mysql://10.70.0.17/keruxdbupdate";
    String dbUserName = "KeruxAdmin";
    String dbPassword = "admin";*/
    String jdbcDriverName = "com.mysql.jdbc.Driver";//vxcd9lOiVlb9DcyuaKAzLr5qD7AQB+5gr7zwfl1MXhY=
    String jdbcUrl ="jdbc:mysql://192.168.1.13/kerux";//jdbc:mysql://192.168.1.1/keruxdb
    String dbUserName = "user";//user//o9gPQILs8mlgWTtuaBMBFA==
    String dbPassword = "admin";//admin//oCeOPEBYh4uhgDL4d2Q/8g==

    String LOGIN_CRED="select queuemanager_id, clinic_id, username, firstname, lastname, email from queuemanager where username=? and password =?";


    String SELECT_DEPARTMENT_LIST="SELECT Department_ID, Name from department WHERE Status='Active' and clinic_id=? ";
    String SELECT_DOCTOR_LIST="SELECT * from doctor WHERE Status='Active' and clinic_id=? "; //WHERE DEPARTMENT ID IS ALSO INCLUDED; NOT YET COMPLETE
    String SELECT_PATIENT_LIST="SELECT i.instance_id, i.patient_id, i.queuetype, i.timestamp, i.status, i.priority, i.queuenumber from instance i inner join queuelist ql on ql.instance_id=i.instance_id where ql.queue_id = ? AND ql.status!='Served'";
    String SELECT_QUEUE="SELECT Queue_ID from queue WHERE Doctor_ID=? and Department_ID=? and Status='Active'";
    String SELECT_ACTIVE_QUEUE="SELECT queue.Queue_ID, queue.QueueName from queue INNER JOIN queueconnector ON queueconnector.Queue_ID = queue.Queue_ID WHERE queueconnector.QueueManager_ID=? and queue.Status='Active'";
    String SELECT_ACTIVE_QUEUE_NUMBER="SELECT COUNT(Queue_ID) from queuelist WHERE Status='Active' and Queue_ID=?";
    String SELECT_REPORT_QUEUE_NUMBER="SELECT COUNT(Queue_ID) from queuelist WHERE Queue_ID=?";
    String SELECT_REPORT_QUEUE_LIST="SELECT i.queuenumber, i.status from instance i inner join queuelist ql on ql.instance_id=i.instance_id where ql.queue_id = ?";
    String SELECT_REPORT_QUEUE_CANCELLED="SELECT COUNT(i.queuenumber) from instance i inner join queuelist ql on ql.instance_id=i.instance_id where ql.queue_id = ? and ql.status='Cancelled'";
    String SELECT_REPORT_QUEUE_SERVED="SELECT COUNT(i.queuenumber) from instance i inner join queuelist ql on ql.instance_id=i.instance_id where ql.queue_id = ? and ql.status='Served'";

    String GET_NEW_QUEUE_ID="SELECT MAX(queue_id) from queue";

    String INSERT_INTO_QUEUE="INSERT INTO queue (QueueName, TimeStamp, Doctor_ID, Department_ID, Status) values (?, ?, ?, ?, 'Active')";

    String UPDATE_MARK_PATIENT_AS_SERVED="UPDATE instance set status = 'Served' where instance_id= ?";
    String UPDATE_MARK_PATIENT_AS_NO_SHOW="UPDATE instance set status = 'Cancelled' where instance_id= ?";
    String UPDATE_QUEUELIST_AS_SERVED="UPDATE queuelist set status = 'Served' where instance_id= ? and queue_id=?";
    String UPDATE_QUEUELIST_AS_NO_SHOW="UPDATE queuelist set status = 'Cancelled' where instance_id= ? and queue_id=?";
    String UPDATE_MARK_PATIENT_AS_CALLED="UPDATE instance set status = 'Called' where instance_id= ?";
    String UPDATE_QUEUELIST_AS_CALLED="UPDATE queuelist set status = 'Called' where instance_id= ? and queue_id=?";
    String UPDATE_QUEUE_ENDQUEUE="UPDATE queue set EndTime = CURRENT_TIMESTAMP, Status='Closed' where queue_id=?";


}
