package me.plavikrug;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.plavikrug.model.DanDnevnik;

/**
 * Created by Vuk on 10.8.2017..
 */

public class DnevnikFragment extends Fragment {

    ListView listaDana;
    DanDnevnikAdapter dnevnikAdapter;
    private DataBaseSource dbSource;
    Spinner spinMjesec;
    Spinner spinPeriod;
    TextView lblIli;
    String[] mjeseciUGodini;
    String[] mojiPeriodi;

    Calendar c;
    int godina;
    int mjesec;
    int dan;

    int width, height;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dnevnik, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dbSource = new DataBaseSource(getActivity());
        dnevnikAdapter = new DanDnevnikAdapter(getContext(),napraviDnevnik(""));
        getActivity().setTitle("Dnevnik");
        //Treba za kasnije;
         c = Calendar.getInstance();
         godina = c.get(Calendar.YEAR);
         mjesec = c.get(Calendar.MONTH);
         dan = c.get(Calendar.DAY_OF_MONTH);

        height = view.getHeight();
        width = view.getWidth();

        width =  (int) (0.8)*width;
        height =  (int) (0.2)*height;

        listaDana = (ListView) view.findViewById(R.id.list_dani_dnevnik);
        listaDana.setAdapter(dnevnikAdapter);
        listaDana.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DanDnevnik odabraniDan = dnevnikAdapter.getItem(position);
                IzvjestajFragment gr = IzvjestajFragment.newInstance(odabraniDan.getDatum());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_glavna_aktivnost,gr).addToBackStack("dnevnik");
                ft.commit();
            }
        });
        /*listaDana.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent nIntent = new Intent(getContext(), EditPopUpWindow.class);
                startActivity(nIntent);
                return true;
                /*LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.activity_edit_pop_up_window, null);
                PopupWindow popUp = new PopupWindow(container, width, height, true);
                popUp.showAtLocation(view.getRootView(), Gravity.CENTER_HORIZONTAL,100, 100);
                return true;
            }
        });*/
        lblIli = (TextView) view.findViewById(R.id.lbl_ili);
        spinMjesec = (Spinner) view.findViewById(R.id.spinnerMjesec);
        spinPeriod = (Spinner) view.findViewById(R.id.spinnerPeriod);
        popuniNizove();

        //Punjenje padajućeg menija iliti spinnera
        List<String> spinnerArrayM =  new ArrayList<String>();
        ArrayAdapter<String> adapterM = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArrayM);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> spinnerArrayP =  new ArrayList<String>();
        ArrayAdapter<String> adapterP = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArrayP);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        popuniSpinere(spinnerArrayM,spinnerArrayP);

        spinMjesec.setAdapter(adapterM);
        spinPeriod.setAdapter(adapterP);

        spinMjesec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > 0){
                        int mjesecNorm = mjesec + 1;
                        int godinaNorm = godina;
                        mjesecNorm = mjesecNorm - position + 1;
                        if (mjesecNorm < 0) {
                            godinaNorm--;
                            mjesecNorm = 13 + mjesecNorm;
                        }
                        String mjesecStr = mjesecNorm + "";
                        if (mjesecNorm < 10) {
                            mjesecStr = "0" + mjesecStr;
                        }
                        dnevnikAdapter.clear();
                        dnevnikAdapter.addAll(napraviDnevnik(mjesecStr + "." + godinaNorm));
                        //napraviDnevnik(mjesecStr + "." + godinaNorm);
                        dnevnikAdapter.notifyDataSetChanged();
                    }
                    else{
                        dnevnikAdapter.clear();
                        dnevnikAdapter.addAll(napraviDnevnik(""));
                    }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               
            }
        });


        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {

                        dnevnikAdapter.clear();
                        dnevnikAdapter.addAll(napraviDnevnik(position + ""));

                    } else {
                        dnevnikAdapter.clear();
                        dnevnikAdapter.addAll(napraviDnevnik(""));
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private ArrayList<DanDnevnik> napraviDnevnik(String param){
       ArrayList<DanDnevnik> daniDnevnik = new ArrayList<DanDnevnik>();
        dbSource.open();
        Cursor daniCursor = null;

        if(param.length()==0){daniCursor = dbSource.getDnevnik("");}
        else {daniCursor = dbSource.getDnevnik(param);}

        Cursor[] kursori = new  Cursor[daniCursor.getCount()];
        daniCursor.moveToFirst();
        int i = 0;
        while(!daniCursor.isAfterLast()){

            String bilKomentar = "";
            Cursor bilCursor = dbSource.getBiljeskeZaDan(daniCursor.getString(0));
            if(bilCursor != null && bilCursor.getCount()>0){
                bilCursor.moveToFirst();
                bilKomentar = bilCursor.getString(0);
            }

            kursori[i] = dbSource.getMjerenja(daniCursor.getString(0));
            kursori[i].moveToFirst();
            int j = 0;
            String vremenaPoDanima[] = new String[kursori[i].getCount()];
            float mjerenjaPoDanima[] = new float[kursori[i].getCount()];
            while(!kursori[i].isAfterLast()){
                vremenaPoDanima[j] = kursori[i].getString(0);
                mjerenjaPoDanima[j] = kursori[i].getFloat(1);
                j++;
                kursori[i].moveToNext();
            }
            int mjesecZaListu = Integer.parseInt(daniCursor.getString(0).substring(3,5));
            int danZaListu = Integer.parseInt(daniCursor.getString(0).substring(0,2));
            int god = Integer.parseInt(daniCursor.getString(0).substring(6,10));
            String mjStr = "";
            switch(mjesecZaListu){
                case 1:
                    mjStr = getString(R.string.Jan);
                    break;
                case 2:
                    mjStr = getString(R.string.Feb);
                    break;
                case 3:
                    mjStr = getString(R.string.Mar);
                    break;
                case 4:
                    mjStr = getString(R.string.Apr);
                    break;
                case 5:
                    mjStr =  getString(R.string.Maj);
                    break;
                case 6:
                    mjStr =  getString(R.string.Jun);
                    break;
                case 7:
                    mjStr =  getString(R.string.Jul);
                    break;
                case 8:
                    mjStr =  getString(R.string.Avg);
                    break;
                case 9:
                    mjStr =  getString(R.string.Sep);
                    break;
                case 10:
                    mjStr =  getString(R.string.Okt);
                    break;
                case 11:
                    mjStr =  getString(R.string.Nov);
                    break;
                case 12:
                    mjStr =  getString(R.string.Dec);
                    break;
            }
            daniDnevnik.add(new DanDnevnik(mjStr,danZaListu,mjesecZaListu,god,vremenaPoDanima,mjerenjaPoDanima,bilKomentar));
            i++;
            daniCursor.moveToNext();
        }
        dbSource.close();
        return daniDnevnik;
    }

    public void popuniNizove(){
        mjeseciUGodini = new String[12];
        mjeseciUGodini[0] = "Januar";
        mjeseciUGodini[1] = "Februar";
        mjeseciUGodini[2] = "Mart";
        mjeseciUGodini[3] = "April";
        mjeseciUGodini[4] = "Maj";
        mjeseciUGodini[5] = "Jun";
        mjeseciUGodini[6] = "Jul";
        mjeseciUGodini[7] = "Avgust";
        mjeseciUGodini[8] = "Septembar";
        mjeseciUGodini[9] = "Oktobar";
        mjeseciUGodini[10] = "Novembar";
        mjeseciUGodini[11] = "Decembar";

        mojiPeriodi = new String[3];
        mojiPeriodi[0] = "Posljednjih 7 dana";
        mojiPeriodi[1] = "Posljednje dvije nedelje";
        mojiPeriodi[2] = "Zadnjih mjesec dana";
    }

    public void popuniSpinere(List<String> spinNiz,List<String> spinNiz2){
        int brojac = 10;
        int currentMonth = mjesec;
        int currentGodina = godina;

        spinNiz.add("-Odaberite mjesec-");
        spinNiz2.add("-Odaberite period-");
        while(brojac>0){
            spinNiz.add(mjeseciUGodini[currentMonth] + " " + currentGodina);
            if(currentMonth==0){
                currentMonth = 11;
                currentGodina--;
            }
            else{
                currentMonth--;
            }
            brojac--;
        }

        for(int i = 0; i < mojiPeriodi.length; i++){
            spinNiz2.add(mojiPeriodi[i]);
        }

    }
}
