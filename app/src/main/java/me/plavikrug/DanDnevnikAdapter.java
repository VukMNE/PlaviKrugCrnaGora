package me.plavikrug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.plavikrug.model.DanDnevnik;

/**
 * Created by Vuk on 10.8.2017..
 */

public class DanDnevnikAdapter extends ArrayAdapter<DanDnevnik> {
    private final Context context;
    private final ArrayList<DanDnevnik> listaDana;
    int pozicija;
    int ink;


    public DanDnevnikAdapter(Context context, ArrayList<DanDnevnik> listaDana){
        super(context,R.layout.list_item_dan_u_dnevniku,listaDana);
        this.context = context;
        this.listaDana = listaDana;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View danDnevnikListItem = inflater.inflate(R.layout.list_item_dan_u_dnevniku,parent,false);
        TextView lblMjesec = (TextView) danDnevnikListItem.findViewById(R.id.lbl_mjesec);
        TextView lblDan = (TextView) danDnevnikListItem.findViewById(R.id.lbl_dan);
        TextView lblGodina = new TextView(getContext());
        lblGodina.setText(listaDana.get(position).getGodina()+"");
        lblGodina.setVisibility(View.INVISIBLE);

        lblDan.setText(listaDana.get(position).getDan()+"");
        lblMjesec.setText(listaDana.get(position).getMjesec());
        RelativeLayout ddan = (RelativeLayout) danDnevnikListItem.findViewById(R.id.ddan);
        ddan.addView(lblGodina);
        LinearLayout desniDan = (LinearLayout) danDnevnikListItem.findViewById(R.id.desniDan);
        LinearLayout linLayVrijemeMjerenja = (LinearLayout) danDnevnikListItem.findViewById(R.id.lin_lay_vrijeme_mjerenja);
        RelativeLayout[] sadrzaci = new RelativeLayout[listaDana.get(position).getVremenaMjerenja().length];
        float brojPuta = (float)listaDana.get(position).getVremenaMjerenja().length/(float)5;
        int brojLinova = (int) brojPuta;
        LinearLayout[] noviLinovi = new LinearLayout[brojLinova];
        pozicija = position;

        TextView[] vremena = new TextView[sadrzaci.length];
        TextView[] rezultati = new TextView[sadrzaci.length];

        for(int k = 0; k < noviLinovi.length; k++){
            noviLinovi[k] = new LinearLayout(getContext());
            noviLinovi[k].setId(-100 + k + 1);
            ViewGroup.LayoutParams ll = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            noviLinovi[k].setLayoutParams(ll);
            noviLinovi[k].setOrientation(LinearLayout.HORIZONTAL);

        }


        int linCount = 0;

        for(int k = 0; k < sadrzaci.length; k++){
            sadrzaci[k] = new RelativeLayout(context);
            sadrzaci[k].setId(1000+k+1);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            sadrzaci[k].setLayoutParams(rlParams);
            sadrzaci[k].setPadding(5,2,0,1);
            if(k+1>5){
                if((k+1)%5==1) {
                    desniDan.addView(noviLinovi[linCount]);
                }
                noviLinovi[linCount].addView(sadrzaci[k]);
                if((k+1)%5==0) {
                    linCount++;
                }
            }
            else{
                linLayVrijemeMjerenja.addView(sadrzaci[k]);
            }


        }

        for(int i = 0; i < listaDana.get(position).getVremenaMjerenja().length; i++){
            vremena[i] = new TextView(getContext());
            vremena[i].setId(i+1);
            vremena[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            vremena[i].setTextSize(15);
            vremena[i].setTextColor(Color.BLACK);
            vremena[i].setPadding(0,3,0,0);
            vremena[i].setText(listaDana.get(position).getVremenaMjerenja()[i]);
            sadrzaci[i].addView(vremena[i]);
        }

        for(int j = 0; j < listaDana.get(position).getRezultatiMjerenja().length; j++){
            rezultati[j] = new TextView(getContext());
            RelativeLayout.LayoutParams rezParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            rezParams.addRule(RelativeLayout.BELOW, vremena[j].getId()+1);
            rezParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rezParams.setMargins(0,37,0,8);
            ink = j;
            rezultati[j].setLayoutParams(rezParams);
            rezultati[j].setTextSize(22);
            if(listaDana.get(position).getRezultatiMjerenja()[j]<4){
                rezultati[j].setTextColor(Color.rgb(255,108,26));
            }
            else if(listaDana.get(position).getRezultatiMjerenja()[j]>10){
                rezultati[j].setTextColor(Color.RED);
            }
            else{
                rezultati[j].setTextColor(Color.BLUE);
            }

            rezultati[j].setText(listaDana.get(position).getRezultatiMjerenja()[j]+"");
            rezultati[j].setBackgroundColor(Color.TRANSPARENT);
            rezultati[j].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setBackgroundColor(Color.GRAY);
                    Intent nIntent = new Intent(getContext(), EditPopUpWindow.class);
                    ViewGroup container = ((ViewGroup) v.getParent().getParent().getParent().getParent());
                    String dan = ((TextView)((ViewGroup)container.getChildAt(0)).getChildAt(1)).getText().toString();// Ne postoji bolji na;in od prolaska kroz xml stablo
                    if(dan.length()==1){
                        dan = "0"+dan;
                    }
                    String mjesec = ((TextView)((ViewGroup)container.getChildAt(0)).getChildAt(0)).getText().toString();
                    String godina = ((TextView)((ViewGroup)container.getChildAt(0)).getChildAt(2)).getText().toString();
                    nIntent.putExtra("full_datum", (dan+"."+mjesec +"."+godina+" "+ ((TextView)((ViewGroup) v.getParent()).getChildAt(0)).getText()));
                    getContext().startActivity(nIntent);
                    return true;
                }
            });
            sadrzaci[j].addView(rezultati[j]);
        }

        if(listaDana.get(position).getBiljeska() != null && listaDana.get(position).getBiljeska().length() > 0){
            Log.d("EJ","eve me");
            TextView txtBiljeska = new TextView(getContext());
            ViewGroup.LayoutParams biljeskaParams = new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtBiljeska.setLayoutParams(biljeskaParams);
            txtBiljeska.setBackgroundResource(R.drawable.biljeska);
            txtBiljeska.setTextColor(Color.BLACK);
            txtBiljeska.setText(Html.fromHtml("<i><b>Moj komentar</b></i>: " + listaDana.get(position).getBiljeska()));
            txtBiljeska.setPadding(15,15,15,15);
            desniDan.addView(txtBiljeska);
        }
        /*lblMjesec.setPadding(0,(desniDan.getHeight() - ddan.getHeight())/2,0,0);
        lblDan.setPadding(0,0,0,(desniDan.getHeight() - ddan.getHeight())/2);*/

        return  danDnevnikListItem;
    }
}
