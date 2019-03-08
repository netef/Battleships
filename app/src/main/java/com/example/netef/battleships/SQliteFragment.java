package com.example.netef.battleships;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;


public class SQliteFragment extends Fragment {

    private final String EASY_TABLE = "Easy";
    private final String NORMAL_TABLE = "Normal";
    private final String HARD_TABLE = "Hard";
    private final int TOP_TEN = 10;

    private Player tempPlayer;
    private ArrayList<Player> allPlayers;

    private DatabaseHelper easyDatabaseHelper;
    private DatabaseHelper normalDatabaseHelper;
    private DatabaseHelper hardDatabaseHelper;

    private TextView EasyLevelText;
    private TextView NormalLevelText;
    private TextView HardLevelText;

    private ListView easyListView;
    private ListView hardListView;
    private ListView normalListView;

    private String name;
    private int score;
    private String city;
    private Double lat;
    private Double lon;

    private Cursor cursor;


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_sqlite, parent, false);
        easyListView = v.findViewById(R.id.listViewSQLEasy);
        normalListView = v.findViewById(R.id.listViewSQLNormal);
        hardListView = v.findViewById(R.id.listViewSQLHard);

        easyDatabaseHelper = new DatabaseHelper(getActivity());
        normalDatabaseHelper = new DatabaseHelper(getActivity());
        hardDatabaseHelper = new DatabaseHelper(getActivity());

        EasyLevelText = v.findViewById(R.id.EasyLevelText);
        NormalLevelText = v.findViewById(R.id.NormalLevelText);
        HardLevelText = v.findViewById(R.id.HardLevelText);

        populateListViewEasy();
        populateListViewNormal();
        populateListViewHard();
        return v;
    }

    private void populateListViewEasy() {
        EasyLevelText.setText("Easy");
        EasyLevelText.setTextColor(Color.LTGRAY);

        cursor = easyDatabaseHelper.getAllData(EASY_TABLE);
        cursor.moveToFirst();
        allPlayers = new ArrayList<>();
        for (int i = 0; i < TOP_TEN && cursor.moveToNext(); ++i) {
            name = cursor.getString(0);
            score = cursor.getInt(1);
            city = cursor.getString(2);
            lat = cursor.getDouble(3);
            lon = cursor.getDouble(4);
            tempPlayer = new Player(name, score, city, lat, lon);
            allPlayers.add(tempPlayer);
        }
        cursor.close();
        allPlayers.sort(Comparator.comparing(Player::getScore).reversed().thenComparing(Player::getName));

        ListAdapter adapterEasy = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, allPlayers);
        easyListView.setAdapter(adapterEasy);
        easyListView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = easyListView.getItemAtPosition(position);
            Player temp = (Player) o; //As you are using Default String Adapter
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewside, new MapFragment(temp)).commit();

        });

    }

    public void populateListViewNormal() {
        NormalLevelText.setText("Normal");
        NormalLevelText.setTextColor(Color.LTGRAY);


        cursor = normalDatabaseHelper.getAllData(NORMAL_TABLE);
        allPlayers = new ArrayList<>();
        for (int i = 0; i < TOP_TEN && cursor.moveToNext(); ++i) {
            name = cursor.getString(0);
            score = cursor.getInt(1);
            city = cursor.getString(2);
            lat = cursor.getDouble(3);
            lon = cursor.getDouble(4);
            tempPlayer = new Player(name, score, city, lat, lon);
            allPlayers.add(tempPlayer);
        }
        cursor.close();
        allPlayers.sort(Comparator.comparing(Player::getScore).reversed().thenComparing(Player::getName));

        ListAdapter adapterNormal = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, allPlayers);
        normalListView.setAdapter(adapterNormal);
        normalListView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = normalListView.getItemAtPosition(position);
            Player temp = (Player) o; //As you are using Default String Adapter
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewside, new MapFragment(temp)).commit();

        });

    }

    public void populateListViewHard() {
        HardLevelText.setText("Hard");
        HardLevelText.setTextColor(Color.LTGRAY);

        cursor = hardDatabaseHelper.getAllData(HARD_TABLE);
        allPlayers = new ArrayList<>();
        for (int i = 0; i < TOP_TEN && cursor.moveToNext(); ++i) {
            name = cursor.getString(0);
            score = cursor.getInt(1);
            city = cursor.getString(2);
            lat = cursor.getDouble(3);
            lon = cursor.getDouble(4);
            tempPlayer = new Player(name, score, city, lat, lon);
            allPlayers.add(tempPlayer);
        }
        cursor.close();

        allPlayers.sort(Comparator.comparing(Player::getScore).reversed().thenComparing(Player::getName));

        ListAdapter adapterHard = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, allPlayers);

        hardListView.setAdapter(adapterHard);

        hardListView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = hardListView.getItemAtPosition(position);
            Player temp = (Player) o; //As you are using Default String Adapter
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewside, new MapFragment(temp)).commit();

        });

    }
}