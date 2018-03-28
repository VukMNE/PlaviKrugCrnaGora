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

import me.plavikrug.model.PodsjetnikKlasa;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Vuk on 10.8.2017..
 */

public class PodsjetnikAdapter extends ArrayAdapter<PodsjetnikKlasa> {
    private final Context context;
    private final ArrayList<PodsjetnikKlasa> listaPod;
    private int idClicked = 0;


    public PodsjetnikAdapter(Context context, ArrayList<PodsjetnikKlasa> listaPod){
        super(context,R.layout.list_item_podsjetnik,listaPod);
        this.context = context;
        this.listaPod = listaPod;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View podListItem = inflater.inflate(R.layout.list_item_podsjetnik,parent,false);
        TextView lblVrijeme = (TextView) podListItem.findViewById(R.id.lbl_pod_vrijeme);
        TextView lblDatum = (TextView) podListItem.findViewById(R.id.lbl_pod_datum);
        TextView lblNaslov = (TextView) podListItem.findViewById(R.id.lbl_pod_naslov);
        Button dltButton = (Button) podListItem.findViewById(R.id.deletePodsj);
        TextView sId = (TextView) podListItem.findViewById(R.id.secretID2);

        lblDatum.setText(listaPod.get(position).getDatum());
        lblVrijeme.setText(listaPod.get(position).getVrijeme());
        lblNaslov.setText(listaPod.get(position).getNaslov());
        sId.setText(listaPod.get(position).getId()+"");

        dltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dltIntent = new Intent(getContext(), DeletePopUpWindow.class);
                dltIntent.putExtra("tabela", "Podsjetnik");
                dltIntent.putExtra("id", ((TextView)((ViewGroup) v.getParent()).findViewById(R.id.secretID2)).getText());
                dltIntent.putExtra("activity", "PodsjetniciLista");
                dltIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(dltIntent);
            }
        });

        return  podListItem;
    }
}
