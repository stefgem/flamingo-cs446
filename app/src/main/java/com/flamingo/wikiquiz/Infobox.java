package com.flamingo.wikiquiz;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "infobox_table")

public class Infobox {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="rowid")
    private int rowId;

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
        rowId = 0; // setting to 0 will make the ID autoGenerate annotation work
        setName(name);
        setCategory(category);
        setBirthYear(birthYear);
        setImageBlob(imageBlob);
    }

    public Infobox getInfobox() {
        return this;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int row_id) {
        this.rowId = row_id;
    }

    @NonNull
    public String getName() {
        return _name;
    }

    public void setName(@NonNull String name) {
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
