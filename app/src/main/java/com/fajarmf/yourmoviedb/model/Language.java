package com.fajarmf.yourmoviedb.model;

/**
 * Created by znaznazna on 12/5/2016.
 */

public enum Language {
    LANGUAGE_EN("en"),
    LANGUAGE_Hi("hi");

    private String value;

    Language(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

}
