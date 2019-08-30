package com.lbs.cheng.lbscampus.bean;

import android.nfc.Tag;

import java.util.Date;
import java.util.List;

/**
 * Created by cheng on 2019/1/20.
 */

public class NoticeBean {

    private String noticeId;

    private String title;

    private String content;

    private String publisher;

    private String assessor;

    private String publishTime;

    private String startTime;

    private String endTime;

    private String updateTime;

    private String picturePath;

    private Integer buildingId;

    private int type;

    private int priority;

    private int status;

    private List<NoticeImage> imageList;

    private List<TagBean> tagList;


    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAssessor() {
        return assessor;
    }

    public void setAssessor(String assessor) {
        this.assessor = assessor;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<NoticeImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<NoticeImage> imageList) {
        this.imageList = imageList;
    }

    public List<TagBean> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagBean> tagList) {
        this.tagList = tagList;
    }
}
