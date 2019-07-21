package com.flamingo.wikiquiz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userQuizStat_table")

public class UserQuizStat {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="rowId")
    private int rowId;

    @NonNull
    private int numQuestions;

    @NonNull
    private int score;

    public UserQuizStat(@NonNull int numQuestions, @NonNull int score) {
        rowId = 0;
        setNumQuestions(numQuestions);
        setScore(score);
    }

    public UserQuizStat getUserQuizStat() {
        return this;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public int getScore() {
        return score;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }
}
