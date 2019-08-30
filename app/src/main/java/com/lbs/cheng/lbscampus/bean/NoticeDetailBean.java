package com.lbs.cheng.lbscampus.bean;

import android.nfc.Tag;

import java.util.Date;
import java.util.List;

/** *
 * Created by LT on 2019/5/11.
 */

public class NoticeDetailBean {

    private int noticeId;

    private String title;

    private String content;

    private UserBean admin;

    private UserBean publisher;

    private UserBean assessor;

    private Date publishTime;

    private Date createTime;

    private String picturePath;

    private int type;

    private int priority;

    private int status;

    private List<TagBean> tagList;

    private List<NoticeImage> imageList;

    private int buildingId;

    private Date startTime;

    private Date endTime;

    private Date updateTime;

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
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

    public UserBean getAdmin() {
        return admin;
    }

    public void setAdmin(UserBean admin) {
        this.admin = admin;
    }

    public UserBean getPublisher() {
        return publisher;
    }

    public void setPublisher(UserBean publisher) {
        this.publisher = publisher;
    }

    public UserBean getAssessor() {
        return assessor;
    }

    public void setAssessor(UserBean assessor) {
        this.assessor = assessor;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
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

    public List<TagBean> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagBean> tagList) {
        this.tagList = tagList;
    }

    public List<NoticeImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<NoticeImage> imageList) {
        this.imageList = imageList;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
