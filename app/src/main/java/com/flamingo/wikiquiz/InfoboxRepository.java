package com.flamingo.wikiquiz;

import android.app.Application;
import android.icu.text.IDNA;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InfoboxRepository {

    private InfoboxDao _infoboxDao;
    private InfoboxRoomDatabase _DB;
    private List<Infobox> _allInfoboxes;
    private LiveData<List<Integer>> _infoboxIdList;

    InfoboxRepository(Application application) {
        InfoboxRoomDatabase db = InfoboxRoomDatabase.getDatabase(application);
        _DB = db;
        _infoboxDao = db.infoboxDao();
        _allInfoboxes = _infoboxDao.getAllInfoboxes();
        _infoboxIdList = _infoboxDao.getInfoboxIdList();
    }

    void resyncInfoboxesWithDb() {
        _allInfoboxes = _infoboxDao.getAllInfoboxes();
    }

    List<Infobox> getAllInfoboxes() {
        return _allInfoboxes;
    }

    LiveData<List<Infobox>> getInfoboxesInCategory(String category) {
        return _infoboxDao.getInfoboxesInCategory(category);
    }

    public void fetchDataFromWikipedia() {
        new concurrentFetchTask(this).execute();
    }

    public void insert(Infobox infobox) {
        new insertAsyncTask(_infoboxDao).execute(infobox);
    }

    public void deleteAll() {
        _infoboxDao.deleteAll();
    }

    @Nullable // Note that this method may return null!!
    public Infobox getInfoboxById(int id) {

        return _infoboxDao.getInfoboxById(id);
    }

    public LiveData<List<Integer>> getInfoboxIdList() {
        return _infoboxIdList;
    }

    private void populateDbWithAllCategs() {
        populateDbSingers();
        populateDbActors();
        populateDbAthletes();
    }

    private void populateDbSingers() {
        String[] singers_urls = {
                "Michael_Jackson", "Eminem", "Lady_Gaga", "Justin_Bieber",
                "Lil_Wayne", "Miley_Cyrus", "Rihanna", "Selena_Gomez", "Taylor_Swift",
                "Kanye_West", "Tupac_Shakur", "Freddie_Mercury", "Nicki_Minaj",
                "John_Cena", "Katy_Perry", "Ariana_Grande", "Adele", "Jay-Z",
                "Prince_(musician)", "Elvis_Presley", "David_Bowie", "Britney_Spears",
                "Demi_Lovato", "Madonna_(entertainer)", "Bob_Marley", "John_Lennon",
                "Bruno_Mars", "Jennifer_Lopez", "Mariah_Carey", "Snoop_Dogg"};

        for (String url : singers_urls) {
            Infobox infobox = fetchData(url, "Singers");
            _infoboxDao.insert(infobox);
        }

    }

    private void populateDbActors() {
        String[] actor_urls = {
                "Kim_Kardashian", "Dwayne_Johnson", "Johnny_Depp", "Angelina_Jolie",
                "Leonardo_DiCaprio", "Mila_Kunis", "Tom_Cruise", "Arnold_Schwarzenegger",
                "Will_Smith", "Bruce_Lee", "Marilyn_Monroe", "Scarlett_Johansson",
                "Jennifer_Aniston", "Jennifer_Lawrence", "Brad_Pitt", "Sylvester_Stallone",
                "Heath_Ledger", "The_Undertaker", "Emma_Watson", "Sunny_Leone", "Megan_Fox",
                "Mark_Wahlberg", "Ryan_Reynolds", "Robin_Williams", "Natalie_Portman",
                "Clint_Eastwood", "Kristen_Stewart", "Tom_Hardy", "Charlie_Sheen",
                "Meghan_Markle", "Salman_Khan"};

        for (String url : actor_urls) {
            Infobox infobox = fetchData(url, "Actors");
            _infoboxDao.insert(infobox);
        }
    }

    private void populateDbAthletes() {
        String[] athlete_urls = {"Cristiano_Ronaldo", "Lionel_Messi", "Michael_Jordan",
                "LeBron_James", "Muhammad_Ali", "Roger_Federer", "Kobe_Bryant", "O._J._Simpson",
                "Tom_Brady", "Mike_Tyson", "Brock_Lesnar", "Shaquille_O%27Neal",
                "Michael_Phelps", "Conor_McGregor", "Neymar", "Manny_Pacquiao", "Rafael_Nadal",
                "Serena_Williams", "David_Beckham", "Zlatan_Ibrahimović", "Tiger_Woods",
                "Peyton_Manning", "Sachin_Tendulkar", "Ronda_Rousey", "Usain_Bolt",
                "Stephen_Curry", "Ronaldinho", "Wayne_Rooney", "Novak_Djokovic",
                "Floyd_Mayweather,_Jr."};

        for (String url : athlete_urls) {
            Infobox infobox = fetchData(url, "Athletes");
            _infoboxDao.insert(infobox);
        }
    }

    private Infobox fetchData(final String page, String category) {
        String url = "https://en.wikipedia.org/wiki/" + page;
        String name = page.replace("_", " ");
        int year = 0;
        byte[] imageBlob = new byte[4096];
        try {
            Connection.Response res
                    = Jsoup.connect(url)
                    .execute();
            String html = res.body();
            html = html.substring(0, html.length() / 2);
            Document doc2 = Jsoup.parseBodyFragment(html);

            Element body = doc2.body();
            Elements tables = body.getElementsByTag("table");
            String imageUrl = "";
            for (Element table : tables) {
                if (table.className().contains("infobox")) {
                    String bday = table.select("span[class=bday]").first().text();
                    year = Integer.parseInt(bday.split("-")[0]);

                    Element imageElement = table.select("img").first();
                    imageUrl = "https:" + imageElement.attr("src");
                    //System.out.println(table.outerHtml());
                    //builder.append(table.outerHtml());
                    break;
                }
            }


            // The following downloads images into a byte[]
            URL urlImage = new URL(imageUrl);
            InputStream in = urlImage.openStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = -1;
            while ((n = in.read(imageBlob)) > 0) {
                baos.write(imageBlob, 0, n);
            }
            imageBlob = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Infobox(name, category,
                year, imageBlob);
    }


    private static class insertAsyncTask extends AsyncTask<Infobox, Void, Void> {
        private InfoboxDao _AsyncTaskDao;

        insertAsyncTask(InfoboxDao dao) {
            _AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Infobox... params) {
            _AsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public static class concurrentFetchTask extends AsyncTask<Void, Void, Void> {
        private InfoboxRepository _repo;

        concurrentFetchTask(InfoboxRepository repository) {
            _repo = repository;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            _repo.populateDbWithAllCategs();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _repo.resyncInfoboxesWithDb();
        }
    }

    /* private static class getInfoboxByIdAsyncTask extends AsyncTask<Integer, Void, LiveData<Infobox>> {
        private InfoboxDao _AsyncTaskDao;

        getInfoboxByIdAsyncTask(InfoboxDao dao) {
            _AsyncTaskDao = dao;
        }
        @Override
        protected LiveData<Infobox> doInBackground(final Integer... params) {
            return _AsyncTaskDao.getInfoboxById(params[0]);
        }
    } */
}
