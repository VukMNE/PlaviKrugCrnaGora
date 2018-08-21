package me.plavikrug;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Vuk on 31.7.2017..
 */

public class ContentFragment extends Fragment {
    private Button btnAdd;
    private SeekArc seekArc;
    private TextView seekArcProgress;
    private TextView expl;
    private Button pomoc;
    private DataBaseSource dbSource;
    Cursor podaciMea;
    int godina, mjesec, dan;
    String danStr, mjesecStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Plavi Krug");
        btnAdd = (Button) view.findViewById(R.id.add_button);
        seekArc = (SeekArc) view.findViewById(R.id.seekArc);
        seekArcProgress = (TextView) view.findViewById(R.id.seekArcProgress);
        expl = (TextView) view.findViewById(R.id.probaj);
        pomoc = (Button) view.findViewById(R.id.pomoc);

        //Dio za dobijanje danasnjeg dana
        Calendar c = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        godina = c.get(Calendar.YEAR);
        mjesec = c.get(Calendar.MONTH)+1;
        dan = c.get(Calendar.DAY_OF_MONTH);

        mjesecStr = mjesec + "";
        danStr = dan + "";
        if(mjesec<10){
            mjesecStr = "0" + mjesec;
        }
        if(dan<10){
            danStr = "0" + dan;
        }
        //Dio za dobijanje danasnjeg dana
        dbSource = new DataBaseSource(getActivity());
        dbSource.open();
        podaciMea = dbSource.getMjerenja(danStr + "." + mjesecStr + "." + godina);

        double[] nivoiGl = new double[podaciMea.getCount()];
        podaciMea.moveToFirst();

        int j = 0;
        while(!podaciMea.isAfterLast()){
            nivoiGl[j] = podaciMea.getDouble(1);
            j++;
            podaciMea.moveToNext();
        }

        izracunajPoene(nivoiGl);


        ValueAnimator anim = ValueAnimator.ofInt(0, seekArc.getProgress());
        anim.setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekArc.setProgress(animProgress);
                seekArcProgress.setText(animProgress+"");
            }
        });
        anim.start();

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getActivity(), DodajMjerenje.class);
                startActivity(addIntent);
                getActivity().finish();

            }
        });


        pomoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpWindow = new Intent(getActivity(), PomocActivity.class);
                startActivity(helpWindow);
            }
        });
    }

    public void izracunajPoene( double[] mjerenja){
        int suma = 0;
        for(int u = 0; u < mjerenja.length; u++){
            if(mjerenja[u]>= 4 && mjerenja[u] <= 10){
                suma += 20;
            }
            else if(mjerenja[u] > 10 && mjerenja[u] <= 15){
                suma += 5;
            }
            else if(mjerenja[u] > 15){
                suma -= 5;
            }
            else if (mjerenja[u] < 4 && mjerenja[u] >= 2.5){
                suma += 5;
            }
            else if (mjerenja[u]< 2.5){
                suma -= 5;
            }
        }

        seekArcProgress.setText(suma+"");
        seekArc.setProgress(suma);
    }
}
