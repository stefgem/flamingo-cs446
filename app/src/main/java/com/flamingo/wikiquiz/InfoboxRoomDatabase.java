package com.flamingo.wikiquiz;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Infobox.class}, version = 1, exportSchema = false)
public abstract class InfoboxRoomDatabase extends RoomDatabase {

    public abstract InfoboxDao infoboxDao();

    // make the database a singleton so we don't have multiple instances of the same database
    private static volatile InfoboxRoomDatabase INSTANCE;

    static InfoboxRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (InfoboxRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            InfoboxRoomDatabase.class, "infobox_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final InfoboxDao _dao;

        PopulateDbAsync(InfoboxRoomDatabase db) {
            _dao = db.infoboxDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            _dao.deleteAll();
            Infobox infobox = new Infobox("Nikola Tesla", "Science & Math", 1856,  "no".getBytes());
            _dao.insert(infobox);
            //infobox = new Infobox(); //TODO
            //_dao.insert(infobox);
            return null;
        }
    }
}
