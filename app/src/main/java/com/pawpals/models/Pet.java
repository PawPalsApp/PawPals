package com.pawpals.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Pet")
public class Pet extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_AGE = "age";
    public static final String KEY_BIO = "bio";
    public static final String KEY_BREED = "breed";
    public static final String KEY_PET_IMAGE = "petImage";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";


    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser owner) {
        put(KEY_OWNER, owner);
    }

    public String getGender() {
        return getString(KEY_GENDER);
    }

    public void setGender(String gender) {
        put(KEY_GENDER, gender);
    }

    public String getAge() {
        return getString(KEY_GENDER);
    }

    public void setAge(String age) {
        put(KEY_AGE, age);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public String getBreed() {
        return getString(KEY_BREED);
    }

    public void setBreed(String breed) {
        put(KEY_BREED, breed);
    }

    public ParseFile getPetImage() {
        return getParseFile(KEY_PET_IMAGE);
    }

    public void setPetImage(ParseFile parseFile) {
        put(KEY_PET_IMAGE, parseFile);
    }
}
