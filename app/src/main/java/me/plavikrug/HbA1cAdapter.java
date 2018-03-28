package me.plavikrug;

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

import me.plavikrug.model.HbA1c;

/**
 * Created by Vuk on 10.8.2017..
 */

public class HbA1cAdapter extends ArrayAdapter<HbA1c> {
    private final Context context;
    private final ArrayList<HbA1c> lista;
    private int idClicked = 0;


    public HbA1cAdapter(Context context, ArrayList<HbA1c> lista){
        super(context,R.layout.list_item_hba1c,lista);
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemList = inflater.inflate(R.layout.list_item_hba1c,parent,false);
        TextView lblDatum = (TextView) itemList.findViewById(R.id.lbl_hba1c_datum);
        TextView lblRez = (TextView) itemList.findViewById(R.id.lbl_hba1c_rez);
        TextView sId = (TextView) itemList.findViewById(R.id.secretID1);
        Button dltButton = (Button) itemList.findViewById(R.id.deleteHbA1c);
        lblDatum.setText(lista.get(position).getDatum());
        lblRez.setText(lista.get(position).getRez()+"");

        sId.setText(lista.get(position).getId()+"");
        dltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dltIntent = new Intent(getContext(), DeletePopUpWindow.class);
                dltIntent.putExtra("tabela", "HbA1c");
                dltIntent.putExtra("id", ((TextView)((ViewGroup) v.getParent()).findViewById(R.id.secretID1)).getText());
                dltIntent.putExtra("activity", "HbA1cLista");
                getContext().startActivity(dltIntent);
            }
        });

        return  itemList;
    }
}
