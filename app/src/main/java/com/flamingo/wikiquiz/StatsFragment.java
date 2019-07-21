package com.flamingo.wikiquiz;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class StatsFragment extends Fragment {
    private StatsViewModel statsViewModel;

    public StatsFragment() {
        // Required empty public constructor
    }


    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }*/

    WebView result;
    //TextView result;
    Button getBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
//        result = (WebView) view.findViewById(R.id.result);
//        getBtn = (Button) view.findViewById(R.id.getBtn);
//        getBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // getWebsite();
//            }
//        });

        statsViewModel = ViewModelProviders.of(getActivity()).get(StatsViewModel.class);

        List<String> statsArray = statsViewModel.getAllScoresStrings();
        ArrayAdapter adapter = new ArrayAdapter<>(this.getActivity(), R.layout.stats_listview, statsArray);
        ListView listView = view.findViewById(R.id.statsList);
        listView.setAdapter(adapter);
        return view;
    }

//    private void getWebsite() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final StringBuilder builder = new StringBuilder();
//
//                try {
//                    Connection.Response res
//                            = Jsoup.connect("https://en.wikipedia.org/wiki/Linus_Torvalds")
//                            .execute();
//                    String html = res.body();
//                    Document doc2 = Jsoup.parseBodyFragment(html);
//                    Element body = doc2.body();
//                    Elements tables = body.getElementsByTag("table");
//                    for (Element table : tables) {
//                        if (table.className().contains("infobox") == true) {
//                            System.out.println(table.outerHtml());
//                            builder.append(table.outerHtml());
//                            break;
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                getParentFragment().getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        result.loadDataWithBaseURL(null, builder.toString(),
//                                "text/html", "utf-8", null);
//                    }
//                });
//            }
//        }).start();
//    }

}