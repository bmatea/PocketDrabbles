package com.example.pocketdrabbles;

public class Character {

    private String name;
    private String surname;
    private int age;
    private String sex;
    private String description;
    private String story;

    public Character() {
    }

    public Character(String name, String surname, int age, String sex, String description, String story) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.sex = sex;
        this.description = description;
        this.story = story;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public String getStory() {
        return story;
    }

    public String getSex() {
        return sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
