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
                            .fallbackToDestructiveMigration() //destroy db when version# increased
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                // this gets called the first time the DB is created
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    // decided put Populate call in onOpen instead for now
                    // ideally would be here, we would do initial populating of the DB only once
                    // new PopulateDbAsync(INSTANCE).execute();
                }

                // this gets called every time the DB is opened
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
            _dao.deleteAll();  // this is only useful if calling Populate from an onOpen call

            Infobox infobox;

            // TODO hardcoded infoboxes go here
            // example insertion of 1 row - image blob is just a random byteArray for now
            // TODO make imageBlob actually be real
            infobox = new Infobox("Nikola Tesla", "Science & Math",
                    1856, "no".getBytes());
            _dao.insert(infobox);
            infobox = new Infobox("Alan Turing", "Science & Math", 1912, "no".getBytes());
            _dao.insert(infobox);
            infobox = new Infobox("Isaac Newton", "Science & Math", 1643, "no".getBytes());
            _dao.insert(infobox);
            infobox = new Infobox("Marie Curie", "Science & Math", 1867, "no".getBytes());
            _dao.insert(infobox);
            infobox = new Infobox("Albert Einstein", "Science & Math", 1879, "no".getBytes());
            _dao.insert(infobox);

            infobox = new Infobox("Elizabeth II", "Politics",
                    1926, "no".getBytes());
            _dao.insert(infobox);

            infobox = new Infobox("Robert Downey Jr.", "TV & Film",
                    1965, "no".getBytes());
            _dao.insert(infobox);

            //infobox = new Infobox(...);
            //_dao.insert(infobox);
            return null;
        }
    }
}
