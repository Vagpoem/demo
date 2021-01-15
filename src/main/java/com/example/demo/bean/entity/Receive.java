package com.example.demo.bean.entity;

public class Receive {
    private String jobId;
    private String receiverId;
    private String isSuccess;
    private int timeLoss;

    public Receive(String jobId, String receiverId, String isSuccess) {
        this.jobId = jobId;
        this.receiverId = receiverId;
        this.isSuccess = isSuccess;
    }

    public int getTimeLoss() {
        return timeLoss;
    }

    public void setTimeLoss(int timeLoss) {
        this.timeLoss = timeLoss;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return "Receive{" +
                "jobId='" + jobId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", isSuccess='" + isSuccess + '\'' +
                ", timeLoss=" + timeLoss +
                '}';
    }
}
