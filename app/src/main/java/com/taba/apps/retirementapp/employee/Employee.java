package com.taba.apps.retirementapp.employee;

import com.taba.apps.retirementapp.api.Api;

public class Employee {

    private int id;
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String photo;
    private String workPlace;

    public Employee() {

    }

    public Employee(int id) {
        this();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public int getId() {
        return id;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return (Api.BASE_URL + this.photo).replace(" ", "%20");
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public String getFullName() {
        return this.getSalutation() + "." + this.getFirstName() + " " + this.getLastName();
    }
}
