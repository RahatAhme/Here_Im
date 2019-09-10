package com.soft_sketch.hereim.POJO;

public class ParentInfo {
    private String parentID;
    private String parentName;
    private String parentNumber;


    public ParentInfo() {
    }

    public ParentInfo(String parentID, String parentName, String parentNumber) {
        this.parentID = parentID;
        this.parentName = parentName;
        this.parentNumber = parentNumber;
    }

    public String getParentID() {
        return parentID;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentNumber() {
        return parentNumber;
    }
}
