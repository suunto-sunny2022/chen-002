package com.chen.ocsw.entity;

public interface OcswRecord {
    Long getId();
    void setBusinessNo(String businessNo);
    String getBusinessNo();
    void setWindowId(Long windowId);
    void setStatus(String status);
    String getStatus();
    void setRemarks(String remarks);
}
