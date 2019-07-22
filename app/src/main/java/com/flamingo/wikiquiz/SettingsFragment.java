package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final QuestionViewModel questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button deleteDbButton = view.findViewById(R.id.delete_db_button);

        deleteDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionViewModel.deleteAllDatabaseRows();
                String toastMsg = "Database wipe initiated.";
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
