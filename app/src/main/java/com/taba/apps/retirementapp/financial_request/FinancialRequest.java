package com.taba.apps.retirementapp.financial_request;

import com.taba.apps.retirementapp.R;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.employee.Employee;

import java.io.Serializable;

public class FinancialRequest implements Serializable {


    public static final int STATUS_INITIATE = 1;
    public static final int STATUS_INPROGRESS = 2;
    public static final int STATUS_DENIED = 3;
    public static final int STATUS_APPROVED = 4;
    public static final int STATUS_OFFERED = 5;
    public static final int STATUS_RETIRED = 6;

    private int id;
    private double amount;
    private String requestRemarks;
    private int status;
    private Employee employee;
    private boolean isApproved;
    private boolean isOffered;
    private boolean isRetired;
    private double issuedAmount;
    private String requestedAt;
    private String approvedAt;
    private int approvedBy;
    private String updatedAt;
    private String offerAttachment;
    private double pendingAmount;
    private String textPendingAmount;
    private double retiredAmount;
    private String textRetiredAmount;


    private String textAmount;
    private String textIssuedAmount;


    public FinancialRequest() {

    }

    public FinancialRequest(int id) {
        this();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setRequestRemarks(String requestRemarks) {
        this.requestRemarks = requestRemarks;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setOffered(boolean offered) {
        isOffered = offered;
    }

    public void setRetired(boolean retired){
        isRetired = retired;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setIssuedAmount(double issuedAmount) {
        this.issuedAmount = issuedAmount;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTextAmount(String textAmount) {
        this.textAmount = textAmount;
    }

    public void setTextIssuedAmount(String textIssuedAmount) {
        this.textIssuedAmount = textIssuedAmount;
    }

    public void setOfferAttachment(String offerAttachment) {
        this.offerAttachment = offerAttachment;
    }

    public void setPendingAmount(double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public void setTextPendingAmount(String textPendingAmount) {
        this.textPendingAmount = textPendingAmount;
    }

    public void setRetiredAmount(double retiredAmount) {
        this.retiredAmount = retiredAmount;
    }

    public void setTextRetiredAmount(String textRetiredAmount) {
        this.textRetiredAmount = textRetiredAmount;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public String getRequestRemarks() {
        return requestRemarks;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isOffered() {
        return isOffered;
    }

    public boolean isRetired(){
        return isRetired;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public double getIssuedAmount() {
        return issuedAmount;
    }

    public String getTextAmount() {
        return textAmount;
    }

    public String getTextIssuedAmount() {
        return textIssuedAmount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getOfferAttachment() {
        return (Api.BASE_URL + this.offerAttachment).replace(" ", "%20");
    }

    public double getPendingAmount() {
        return pendingAmount;
    }

    public String getTextPendingAmount() {
        return textPendingAmount;
    }

    public double getRetiredAmount() {
        return retiredAmount;
    }

    public String getTextRetiredAmount() {
        return textRetiredAmount;
    }

    public String getNamedStatus() {
        switch (this.getStatus()) {
            case STATUS_INITIATE:
                return "Initiated";
            case STATUS_INPROGRESS:
                return "In progress";
            case STATUS_APPROVED:
                return "Approved";
            case STATUS_DENIED:
                return "Denied";
            case STATUS_OFFERED:
                return "Offered";
            case STATUS_RETIRED:
                return "Retired";
            default:
                return "Initiated";
        }
    }

    public int getStatusColor() {
        switch (this.getStatus()) {
            case STATUS_INITIATE:
                return R.color.info;
            case STATUS_INPROGRESS:
                return R.color.info;
            case STATUS_APPROVED:
            case STATUS_OFFERED:
            case STATUS_RETIRED:
                return R.color.success;
            case STATUS_DENIED:
                return R.color.danger;
            default:
                return R.color.info;
        }
    }



    public boolean hasAmountDifference(){
        return this.getIssuedAmount() != this.getAmount();
    }
}
