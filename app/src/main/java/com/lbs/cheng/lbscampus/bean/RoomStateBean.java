package com.lbs.cheng.lbscampus.bean;

public class RoomStateBean {

    String roomName;
    int state;

    public RoomStateBean() {
    }

    public RoomStateBean(String roomName, int state) {
        this.roomName = roomName;
        this.state = state;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
