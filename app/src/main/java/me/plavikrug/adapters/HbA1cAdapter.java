package me.plavikrug.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import me.plavikrug.DeletePopUpWindow;
import me.plavikrug.R;
import me.plavikrug.model.HbA1c;

/**
 * Created by Vuk on 10.8.2017..
 */

public class HbA1cAdapter extends ArrayAdapter<HbA1c> {
    private final Context context;
    private final ArrayList<HbA1c> lista;
    private EditButtonClickedListener editButtonClickedListener;
    private int idClicked = 0;


    public HbA1cAdapter(Context context, ArrayList<HbA1c> lista, EditButtonClickedListener editButtonClickedListener){
        super(context, R.layout.list_item_hba1c,lista);
        this.context = context;
        this.lista = lista;
        this.editButtonClickedListener = editButtonClickedListener;
    }

    public interface EditButtonClickedListener {
        public void editButtonClicked(int position);
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemList = inflater.inflate(R.layout.list_item_hba1c,parent,false);
        TextView lblDatum = (TextView) itemList.findViewById(R.id.lbl_hba1c_datum);
        TextView lblRez = (TextView) itemList.findViewById(R.id.lbl_hba1c_rez);
        TextView sId = (TextView) itemList.findViewById(R.id.secretID1);
        Button editButton = (Button) itemList.findViewById(R.id.li_hba1c_edit_btn);
        lblDatum.setText(lista.get(position).getDatum());
        lblRez.setText(lista.get(position).getRez()+"");

        sId.setText(lista.get(position).getId()+"");
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonClickedListener.editButtonClicked(position);
            }
        });

        return  itemList;
    }
}
