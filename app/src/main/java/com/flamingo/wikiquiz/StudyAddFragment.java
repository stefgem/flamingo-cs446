package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;


public class StudyAddFragment extends Fragment {

    private QuestionViewModel questionViewModel;
    private TextView textView;

    public StudyAddFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);
        questionViewModel
                .getAllInfoBoxes()
                .observe(getViewLifecycleOwner(), new Observer<List<Infobox>>() {
            @Override
            public void onChanged(@Nullable final List<Infobox> infoboxes) {
                // Update the cached copy of the words in the adapter.

                if (textView != null) {
                    textView.setText("");
                    for (Infobox infobox : infoboxes) {
                        String line = infobox.get_id() + " , " + infobox.get_name() + " , "
                                + infobox.getCategory() + " , " + infobox.getBirthYear();
                        textView.append(line + "\n");

                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_add, container, false);
        textView = view.findViewById(R.id.database_textview);


        //Button button = view.findViewById(R.id.launchQuizButton);

        //button.setOnClickListener(Navigation.createNavigateOnClickListener(
        //      R.id.action_launchQuizFragment_to_questionFragment, null));

        return view;
    }
}
