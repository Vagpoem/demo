package com.example.demo.bean.entity;

/**
 * 在消息队列中暂存的消息实体，用于在生产者和消费者之间传输数据
 */
public class JobMessage {

    // 在请求端生成的唯一标识任务的id
    private String job_id;
    // 任务对应的类型
    private String type;
    // 任务对应的数据（可以是base64编码、图片链接和验证码链接或是手机号码四者之一）
    private String data;
    // 验证码分类的结果
    private String jobType;
    // 图片保存后的地址
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public JobMessage(String job_id, String type, String data, String jobType){
        this.job_id = job_id;
        this.type = type;
        this.data = data;
        this.jobType = jobType;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "JobMessage{" +
                "job_id='" + job_id + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
