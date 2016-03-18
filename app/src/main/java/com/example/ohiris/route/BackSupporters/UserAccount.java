package com.example.ohiris.route.BackSupporters;


import android.util.Log;

public class UserAccount {
    private static final String TAG = "UserAccount";

    private long id = 0;
    private String name;
    private String email;
    private String password;

    private String gender;
    private int age;
    private double height;
    private int weight;
    private int activeLevel;

    private double bmi=0;

    public  UserAccount(){

    }

    public UserAccount(long id, String name, String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;

    }

    public double calBMI(){
        double inch = Math.floor(height);
        Log.d(TAG, "1: " + inch);
        double inches = inch*12 + (height - inch)*10;
        Log.d(TAG, "2: " + inches);

        double heightM = inches * 0.025;
        Log.d(TAG, "height after conversion: " + heightM);

        double weightKG = weight*0.45;

        double suqare_height = heightM*heightM;

        bmi = weightKG / suqare_height;
        Log.d(TAG, "bmi: " + bmi);

        return bmi;
    }

    public boolean checkHealthy(){
        boolean res = false;

        if (bmi > 19 && bmi < 25){
            res = true;
        }

        return res;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getHeight() {
        return height;
    }

    public int getActiveLevel() {
        return activeLevel;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public String getGender() {
        return gender;
    }

    public void setActiveLevel(int activeLevel) {
        this.activeLevel = activeLevel;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

