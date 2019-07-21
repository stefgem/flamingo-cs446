package com.flamingo.wikiquiz;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Database(entities = {Infobox.class, UserQuizStat.class}, version = 1, exportSchema = false)
public abstract class InfoboxRoomDatabase extends RoomDatabase {

    public abstract InfoboxDao infoboxDao();
    public abstract UserQuizStatDao userQuizStatDao();

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
                            .allowMainThreadQueries() // a necessary evil
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
                    //new PopulateDbAsync(INSTANCE).execute();
                }

                // this gets called every time the DB is opened
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    // new PopulateDbAsync(INSTANCE).execute();
                }
            };
}

//    public static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final InfoboxDao _dao;
//
//        PopulateDbAsync(InfoboxRoomDatabase db) {
//            _dao = db.infoboxDao();
//        }

//        @Override
//        protected Void doInBackground(final Void... params) {
           // _dao.deleteAll();  // this is only useful if calling Populate from an onOpen call
//            Infobox infobox;
            // TODO hardcoded infoboxes go here
            // example insertion of 1 row - image blob is just a random byteArray for now
            // TODO make imageBlob actually be real
//            infobox = new Infobox("Nikola Tesla", "Science & Math",
//                    1856, "no".getBytes());
//            _dao.insert(infobox);
//            infobox = new Infobox("Alan Turing", "Science & Math", 1912, "no".getBytes());
//            _dao.insert(infobox);
//            infobox = new Infobox("Isaac Newton", "Science & Math", 1643, "no".getBytes());
//            _dao.insert(infobox);
//            infobox = new Infobox("Marie Curie", "Science & Math", 1867, "no".getBytes());
//            _dao.insert(infobox);
//            infobox = new Infobox("Albert Einstein", "Science & Math", 1879, "no".getBytes());
//            _dao.insert(infobox);
//
//            infobox = new Infobox("Elizabeth II", "Politics",
//                    1926, "no".getBytes());
//            _dao.insert(infobox);
//
//            infobox = new Infobox("Robert Downey Jr.", "TV & Film",
//                    1965, "no".getBytes());
//            _dao.insert(infobox);

            //populateDbAll();
            //infobox = new Infobox(...);
            //_dao.insert(infobox);
//            return null;
//        }

//        private void populateDbAll(){
//            populateDbSingers();
//            populateDbActors();
//            populateDbAthletes();
//        }
//
//        private void populateDbSingers() {
//            String[] singers_urls = {
//                    "Michael_Jackson", "Eminem", "Lady_Gaga", "Justin_Bieber",
//                    "Lil_Wayne", "Miley_Cyrus", "Rihanna", "Selena_Gomez", "Taylor_Swift",
//                    "Kanye_West", "Tupac_Shakur", "Freddie_Mercury", "Nicki_Minaj",
//                    "John_Cena", "Katy_Perry", "Ariana_Grande", "Adele", "Jay-Z",
//                    "Prince_(musician)", "Elvis_Presley", "David_Bowie", "Britney_Spears",
//                    "Demi_Lovato", "Madonna_(entertainer)", "Bob_Marley", "John_Lennon",
//                    "Bruno_Mars", "Jennifer_Lopez", "Mariah_Carey", "Snoop_Dogg"};
//
//            for (String url: singers_urls) {
//                Infobox infobox = fetchData(url, "Singers");
//                _dao.insert(infobox);
//            }
//
//        }
//
//        private void populateDbActors() {
//            String[] actor_urls = {
//                    "Kim_Kardashian", "Dwayne_Johnson", "Johnny_Depp", "Angelina_Jolie",
//                    "Leonardo_DiCaprio", "Mila_Kunis", "Tom_Cruise", "Arnold_Schwarzenegger",
//                    "Will_Smith", "Bruce_Lee", "Marilyn_Monroe", "Scarlett_Johansson",
//                    "Jennifer_Aniston", "Jennifer_Lawrence", "Brad_Pitt", "Sylvester_Stallone",
//                    "Heath_Ledger", "The_Undertaker", "Emma_Watson", "Sunny_Leone", "Megan_Fox",
//                    "Mark_Wahlberg", "Ryan_Reynolds", "Robin_Williams", "Natalie_Portman",
//                    "Clint_Eastwood", "Kristen_Stewart", "Tom_Hardy", "Charlie_Sheen",
//                    "Meghan_Markle", "Salman_Khan"};
//
//            for (String url: actor_urls) {
//                Infobox infobox = fetchData(url, "Actors");
//                _dao.insert(infobox);
//            }
//        }
//
//        private void populateDbAthletes() {
//            String[] athlete_urls = {"Cristiano_Ronaldo", "Lionel_Messi", "Michael_Jordan",
//                    "LeBron_James", "Muhammad_Ali", "Roger_Federer", "Kobe_Bryant", "O._J._Simpson",
//                    "Tom_Brady", "Mike_Tyson", "Brock_Lesnar", "Shaquille_O%27Neal",
//                    "Michael_Phelps", "Conor_McGregor", "Neymar", "Manny_Pacquiao", "Rafael_Nadal",
//                    "Serena_Williams", "David_Beckham", "Zlatan_Ibrahimović", "Tiger_Woods",
//                    "Peyton_Manning", "Sachin_Tendulkar", "Ronda_Rousey", "Usain_Bolt",
//                    "Stephen_Curry", "Ronaldinho", "Wayne_Rooney", "Novak_Djokovic",
//                    "Floyd_Mayweather,_Jr."};
//
//            for (String url: athlete_urls) {
//                Infobox infobox = fetchData(url, "Athletes");
//                _dao.insert(infobox);
//            }
//        }
//
//        private Infobox fetchData(final String page, String category) {
//            String url = "https://en.wikipedia.org/wiki/" + page;
//            String name = page.replace("_", " ");
//            int year = 0;
//            byte[] imageBlob = new byte[4096];
//            try {
//                Connection.Response res
//                        = Jsoup.connect(url)
//                        .execute();
//                String html = res.body();
//                html = html.substring(0, html.length() / 2);
//                Document doc2 = Jsoup.parseBodyFragment(html);
//
//                Element body = doc2.body();
//                Elements tables = body.getElementsByTag("table");
//                String imageUrl = "";
//                for (Element table : tables) {
//                    if (table.className().contains("infobox")) {
//                        String bday = table.select("span[class=bday]").first().text();
//                        year = Integer.parseInt(bday.split("-")[0]);
//
//                        Element imageElement = table.select("img").first();
//                        imageUrl = "https:" + imageElement.attr("src");
//                        //System.out.println(table.outerHtml());
//                        //builder.append(table.outerHtml());
//                        break;
//                    }
//                }
//
//
//                // The following downloads images into a byte[]
//                URL urlImage = new URL(imageUrl);
//                InputStream in = urlImage.openStream();
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                int n = -1;
//                while ((n = in.read(imageBlob)) > 0) {
//                    baos.write(imageBlob, 0, n);
//                }
//                imageBlob = baos.toByteArray();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return new Infobox(name, category,
//                    year, imageBlob);
//        }
//   }
//}
