package com.flamingo.wikiquiz;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;


public class StatsFragment extends Fragment {
    private StatsViewModel statsViewModel;
    private List<String> statsArray;
    private ArrayAdapter arrayAdapter;
    private ListView listView;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        statsViewModel = ViewModelProviders.of(getActivity()).get(StatsViewModel.class);

        statsArray = statsViewModel.getAllScoresStrings();
        arrayAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.stats_listview, statsArray);
        listView = view.findViewById(R.id.statsList);
        listView.setAdapter(arrayAdapter);
        NavHostFragment.findNavController(this).addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination == controller.getGraph().findNode(R.id.statsFragment)) {
                    statsArray.clear();
                    statsArray.addAll(statsViewModel.getAllScoresStrings());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

}