package me.plavikrug;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.plavikrug.db.DataBaseSource;

/**
 * Created by Vuk on 31.7.2017..
 */

public class ContentFragment extends Fragment {
    private Button btnAdd;
    private Button btnSeeJournal;
    private Button pomoc;
    private TextView lblLastMeasurementResult;
    private TextView lblLastMeasurementTime;
    private TextView lblLastMeasurementUnit;
    private TextView lblInsulinTherapyLast24hResult;
    private ImageView[] imgWeekDays;
    private DataBaseSource dbSource;
    int godina, mjesec, dan;
    String danStr, mjesecStr;
    String TAG = "AJMO_PLAVI";
    String measurementTime;
    float lastMeasurement;
    int lastTherapyTotal;
    int lastTherapy;
    int lastCorrection;
    ArrayList<Integer> weekMeasurementsCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Plavi Krug");
        btnAdd = (Button) view.findViewById(R.id.btn_add_measurement);
        btnSeeJournal = (Button) view.findViewById(R.id.btn_see_journal);
        lblLastMeasurementResult = (TextView) view.findViewById(R.id.lblLastMeasurementResult);
        lblLastMeasurementTime = (TextView) view.findViewById(R.id.lblLastMeasurementTime);
        lblLastMeasurementUnit = (TextView) view.findViewById(R.id.lblLastMeasurementUnit);
        lblInsulinTherapyLast24hResult = (TextView) view.findViewById(R.id.lblInsulinTherapyLast24hResulr);
        initializeWeekDays(view);

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

        // Odavdje krece iyvlacenje podataka u onCreate metodi
        try {
            extractLastMeasurementandTherapyFromDb();
        } catch (ParseException e) {
            e.printStackTrace();
            lblLastMeasurementUnit.setText("Nema podataka");
            lblInsulinTherapyLast24hResult.setText("Nema podataka");
        }

        weekMeasurementsCount = getWeekMeasurements();
        for(int w = 0; w < weekMeasurementsCount.size(); w++) {
            if(weekMeasurementsCount.get(w).intValue() > 0) {
                imgWeekDays[w].setImageDrawable(getResources().getDrawable(R.drawable.ic_green_check));
            }
        }

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getActivity(), DodajMjerenje.class);
                startActivity(addIntent);
                getActivity().finish();

            }
        });

        btnSeeJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GlavnaAktivnost)getActivity()).pokaziOdabranuStvar(R.id.men_dnevnik);
            }
        });


    }

    private void initializeWeekDays(View view) {
        imgWeekDays = new ImageView[7];
        imgWeekDays[0] = (ImageView) view.findViewById(R.id.img_weekday1);
        imgWeekDays[1] = (ImageView) view.findViewById(R.id.img_weekday2);
        imgWeekDays[2] = (ImageView) view.findViewById(R.id.img_weekday3);
        imgWeekDays[3] = (ImageView) view.findViewById(R.id.img_weekday4);
        imgWeekDays[4] = (ImageView) view.findViewById(R.id.img_weekday5);
        imgWeekDays[5] = (ImageView) view.findViewById(R.id.img_weekday6);
        imgWeekDays[6] = (ImageView) view.findViewById(R.id.img_weekday7);
    }

    private void extractLastMeasurementandTherapyFromDb() throws ParseException {
        dbSource.open();
        Cursor cursor = dbSource.getLastMjerenje();
        boolean thereIsMeasurementData = cursor.moveToFirst();
        if(thereIsMeasurementData) {
            lastMeasurement = cursor.getFloat(3);
            measurementTime = cursor.getString(1);
            if(lastMeasurement < 4) {
                lblLastMeasurementResult.setTextColor(getActivity().getResources().getColor(R.color.hypoColor));
                lblLastMeasurementResult.setText(lastMeasurement + "");
            } else if(lastMeasurement >= 10) {
                lblLastMeasurementResult.setTextColor(getActivity().getResources().getColor(R.color.colorDanger));
                lblLastMeasurementResult.setText(lastMeasurement + "");
            } else {
                lblLastMeasurementResult.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                lblLastMeasurementResult.setText(lastMeasurement + "");
            }

            lblLastMeasurementUnit.setText(getString(R.string.lblBloodSugarMeasurementUnit));

            String strDate = measurementTime.substring(0, 11);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar today = Calendar.getInstance();


            boolean sameDay = today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
                    today.get(Calendar.YEAR) == cal.get(Calendar.YEAR);

            boolean yesterDay = today.get(Calendar.DAY_OF_YEAR) == (cal.get(Calendar.DAY_OF_YEAR) + 1) &&
                    today.get(Calendar.YEAR) == cal.get(Calendar.YEAR);
            if(sameDay) {
                lblLastMeasurementTime.setText("Danas u " + measurementTime.substring(11));
            } else if (yesterDay) {
                lblLastMeasurementTime.setText("Juče u " + measurementTime.substring(11));
            } else {
                lblLastMeasurementTime.setText(measurementTime);
            }

        } else {
            lblLastMeasurementResult.setTextColor(getActivity().getResources().getColor(R.color.normalText));
            lblLastMeasurementResult.setText("Nema podataka");
        }

        cursor = dbSource.getLastTerapija();
        boolean thereIsTherapyData = cursor.moveToFirst();
        if(thereIsTherapyData) {
            lastTherapy = cursor.getInt(0);
            lastCorrection = cursor.getInt(1);
            lastTherapyTotal = lastTherapy + lastCorrection;
            Spannable spannableTotal = new SpannableString(lastTherapyTotal + "");
            spannableTotal.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen)),
                    0, (lastTherapyTotal + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Spannable spannableRegular = new SpannableString("" + lastTherapy);
            spannableRegular.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen)),
                    0, (lastTherapy + "").length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Spannable spannableCorrection = new SpannableString("" + lastCorrection);
            spannableCorrection.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen)),
                    0, (lastCorrection + "").length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            lblInsulinTherapyLast24hResult.setText("");
            lblInsulinTherapyLast24hResult.append("Ukupno: ");
            lblInsulinTherapyLast24hResult.append(spannableTotal);
            lblInsulinTherapyLast24hResult.append(" (Regularno: ");
            lblInsulinTherapyLast24hResult.append(spannableRegular);
            lblInsulinTherapyLast24hResult.append(", Korekcija: ");
            lblInsulinTherapyLast24hResult.append(spannableCorrection);
            lblInsulinTherapyLast24hResult.append(")");
        } else {
            lblInsulinTherapyLast24hResult.setTextColor(getActivity().getResources().getColor(R.color.normalText));
            lblInsulinTherapyLast24hResult.setText("Nema podataka");
        }


        dbSource.close();
    }

    private ArrayList<Integer> getWeekMeasurements() {
        dbSource.open();
        ArrayList<Integer> measurementsCount = new ArrayList<Integer>();
        Calendar today = Calendar.getInstance();
        int currentWeekDay = today.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : today.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d(TAG, "Dan u nedljelji: " + currentWeekDay);
        String[] dates = new String[currentWeekDay];
        Cursor[] cursors = new Cursor[currentWeekDay];


        int i = currentWeekDay;
        int decreaser = 0;
        while (i > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Calendar someDayBefore = Calendar.getInstance();
            someDayBefore.add(Calendar.DAY_OF_MONTH, decreaser);
            dates[i-1] = simpleDateFormat.format(someDayBefore.getTime());
            decreaser--;
            i--;
        }

        for(int c = 0; c < dates.length; c++) {
            cursors[c] = dbSource.getGlMjerenja(dates[c]);
            if(cursors[c].moveToFirst()) {
                measurementsCount.add(1);
            } else {
                measurementsCount.add(0);
            }
        }
        dbSource.close();
        return measurementsCount;
    }
}
