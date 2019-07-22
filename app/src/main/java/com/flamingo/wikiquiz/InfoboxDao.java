package com.flamingo.wikiquiz;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InfoboxDao {

    @Query("SELECT * FROM infobox_table ORDER BY _name ASC")
    List<Infobox> getAllInfoboxes();

    @Query("SELECT * FROM infobox_table WHERE category = :category")
    LiveData<List<Infobox>> getInfoboxesInCategory(String category);

    @Query("SELECT * FROM infobox_table WHERE rowid = :id LIMIT 1")
    Infobox getInfoboxById(int id);

    @Query("SELECT rowid FROM infobox_table ORDER BY rowid ASC")
    LiveData<List<Integer>> getInfoboxIdList();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Infobox infobox);

    @Query("DELETE FROM infobox_table")
    void deleteAll();
}
