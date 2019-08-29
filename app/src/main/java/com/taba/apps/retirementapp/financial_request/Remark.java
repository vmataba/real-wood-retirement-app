package com.taba.apps.retirementapp.financial_request;

import com.taba.apps.retirementapp.api.Api;

public class Remark extends FinancialRequest {



    private String userPhoto;
    private String userFullName;
    private String userTitle;
    private int status;
    private String remark;
    private String createdAt;


    public void setStatus(int status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public String getUserPhoto() {
        return (Api.BASE_URL + userPhoto).replace(" ","%20");
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
