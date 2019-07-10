package com.flamingo.wikiquiz;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "infobox_table")

public class Infobox {

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @NonNull
    private String _name;

    @NonNull
    private String category;

    @Nullable
    private int birthYear;

    @NonNull
    private byte[] imageBlob;


    public Infobox(@NonNull String name,
                   @NonNull String category, int birthYear,
                   @NonNull byte[] imageBlob) {
        _id = 0; // setting to 0 will make the ID autoGenerate annotation work
        set_name(name);
        setCategory(category);
        setBirthYear(birthYear);
        setImageBlob(imageBlob);
    }

    public Infobox getInfobox() {
        return this;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @NonNull
    public String get_name() {
        return _name;
    }

    public void set_name(@NonNull String name) {
        this._name = name;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    @NonNull
    public byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(@NonNull byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }
}
