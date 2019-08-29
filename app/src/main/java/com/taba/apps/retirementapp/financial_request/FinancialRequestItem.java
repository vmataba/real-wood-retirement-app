package com.taba.apps.retirementapp.financial_request;

public class FinancialRequestItem {

    private String description;
    private double amount;

    public FinancialRequestItem(){

    }

    public FinancialRequestItem(String description, double amount){
        this();
        this.description = description;
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
