package me.plavikrug;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import me.plavikrug.model.HbA1c;

public class HbA1cLista extends Fragment {

    Button addHbA1c;
    DataBaseSource dbSource;
    ListView listH;
    HbA1cAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hba1c_lista, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("HbA1c");
        adapter =  new HbA1cAdapter(getContext(), getHbA1cLista());

        addHbA1c = (Button) view.findViewById(R.id.addHbA1c);
        listH = (ListView) view.findViewById(R.id.HbA1c_lista);
        listH.setAdapter(adapter);
        listH.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent hIntent = new Intent(getContext(), HbA1cInsert.class);
                hIntent.putExtra("datum", adapter.getItem(position).getDatum());
                hIntent.putExtra("rez", adapter.getItem(position).getRez());
                hIntent.putExtra("id", adapter.getItem(position).getId());
                startActivity(hIntent);
                getActivity().finish();

            }
        });

        addHbA1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HbA1cInsert.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private ArrayList<HbA1c> getHbA1cLista(){
        ArrayList<HbA1c> hba1c = new ArrayList<HbA1c>();
        dbSource = new DataBaseSource(getContext());
        dbSource.open();
        Calendar c = Calendar.getInstance();
        int godina = c.get(Calendar.YEAR);
        int mjesec = c.get(Calendar.MONTH)+1;
        int dan = c.get(Calendar.DAY_OF_MONTH);
        int id = 0;


        String mjesecStr = mjesec + "";
        String danStr = dan + "";
        if(mjesec<10){
            mjesecStr = "0" + mjesec;
        }
        if(dan<10){
            danStr = "0" + dan;
        }

        Cursor cursor = dbSource.getHbA1c();
        cursor.moveToFirst();
        String datum;
        float rez;

        while(!cursor.isAfterLast()){
            datum = cursor.getString(0);
            rez = cursor.getFloat(1);
            id = cursor.getInt(2);
            hba1c.add(new HbA1c(datum,rez, id));
            cursor.moveToNext();
        }
        return hba1c;
    }
}
