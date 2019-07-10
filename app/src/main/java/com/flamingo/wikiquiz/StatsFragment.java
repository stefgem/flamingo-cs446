package com.flamingo.wikiquiz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {


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
        result = (WebView) view.findViewById(R.id.result);
        //result = (TextView) view.findViewById(R.id.result);
        getBtn = (Button) view.findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button Click","Entering button click event");
                System.out.println("Entering button click event");
                getWebsite();
            }
        });
        return view;
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                Element desiredTbl = new Element(Tag.valueOf("table"), "");

                try {
                    Connection.Response res = Jsoup.connect("http://en.wikipedia.org/wiki/Linus_Torvalds").execute();
                    String html = res.body();
                    Document doc2 = Jsoup.parseBodyFragment(html);
                    Element body = doc2.body();
                    Elements tables = body.getElementsByTag("table");
                    for (Element table : tables) {
                        if (table.className().contains("infobox") == true) {
                            System.out.println(table.outerHtml());
                            builder.append(table.outerHtml());
                            break;
                        }
                    }
                    final Element infobox = desiredTbl;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getParentFragment().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.loadDataWithBaseURL(null, builder.toString(), "text/html", "utf-8", null);
                        //result.setText(builder.toString());
                    }
                });
            }
        }).start();
    }

}
