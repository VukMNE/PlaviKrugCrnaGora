package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import me.plavikrug.adapters.HbA1cAdapter;
import me.plavikrug.db.DataBaseSource;
import me.plavikrug.model.HbA1c;

public class HbA1cLista extends Fragment implements
        HbA1cAdapter.EditButtonClickedListener {

    Button btnAddHbA1c;
    Button btnCrudConfirm;
    Button btnCancel;
    TextView lblTitle;
    TextView lblNoData;
    DataBaseSource dbSource;
    ListView listH;
    HbA1cAdapter adapter;
    LinearLayout layInsertForm;
    Animation fadeOutFirstAnimation;
    Animation fadeOutSecondAnimation;
    Animation fadeInFirstAnimation;
    Animation fadeInSecondAnimation;
    HbA1c chosenHbA1c;
    Button btnDate;
    EditText txtTestResult;
    private DatePickerDialog.OnDateSetListener datumListener;
    int godina, mjesec, dan;
    String danStr, mjesecStr;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hba1c_lista, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lblTitle = (TextView) view.findViewById(R.id.lbl_hba1c_list_title);
        lblNoData = (TextView) view.findViewById(R.id.lblNoDataHba1C);
        setUpAnimations();

        btnAddHbA1c = (Button) view.findViewById(R.id.addHbA1c);
        btnCrudConfirm = (Button) view.findViewById(R.id.btn_hba1c_confirm);
        btnCancel = (Button) view.findViewById(R.id.btn_hba1c_cancel);
        layInsertForm = (LinearLayout) view.findViewById(R.id.hba1c_crud_box);
        listH = (ListView) view.findViewById(R.id.HbA1c_lista);

        btnDate = (Button) view.findViewById(R.id.btn_date_hba1c);


        txtTestResult = (EditText) view.findViewById(R.id.txt_hba1c_crud_result);

        syncData(true);

        listH.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editButtonClicked(i);
            }
        });


        btnAddHbA1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), HbA1cInsert.class);
//                startActivity(intent);
//                getActivity().finish();
                chosenHbA1c = null;
                Calendar cal = Calendar.getInstance();
                godina = cal.get(Calendar.YEAR);
                mjesec = cal.get(Calendar.MONTH) + 1;
                dan = cal.get(Calendar.DAY_OF_MONTH);
                danStr = dan + "";
                mjesecStr = mjesec + "";

                if(mjesec<10){
                    mjesecStr = "0" + mjesec;
                }
                if(dan<10){
                    danStr = "0" + dan;
                }

                btnDate.setText(danStr+"."+mjesecStr+"."+godina);
                txtTestResult.setText("");

                if(listH.getVisibility() == View.VISIBLE) {
                    listH.startAnimation(fadeOutFirstAnimation);
                } else {
                    lblNoData.startAnimation(fadeOutFirstAnimation);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layInsertForm.startAnimation(fadeOutSecondAnimation);
            }
        });

        btnCrudConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chosenHbA1c == null) {
                    insertTestResult();
                } else {
                    updateTestResult(chosenHbA1c.getId());
                }

            }
        });

        Calendar cal = Calendar.getInstance();
        godina = cal.get(Calendar.YEAR);
        mjesec = cal.get(Calendar.MONTH) + 1;
        dan = cal.get(Calendar.DAY_OF_MONTH);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datumDialog = new DatePickerDialog(getContext(),
                        R.style.Theme_DelegateWindow,
                        datumListener,
                        godina,mjesec-1,dan);
                // datumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datumDialog.setTitle("Datum");
                datumDialog.setButton(datumDialog.BUTTON_POSITIVE,"U redu",datumDialog);
                datumDialog.setButton(datumDialog.BUTTON_NEGATIVE,"Otkaži",datumDialog);
                datumDialog.show();
            }
        });

        datumListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina= year;
                mjesec = month+1;
                dan = dayOfMonth;
                danStr = dan + "";
                mjesecStr = mjesec + "";

                if(dan < 10){
                    danStr = "0" + dan;
                }
                if(mjesec < 10){
                    mjesecStr = "0" + mjesec;
                }
                btnDate.setText(danStr+"."+mjesecStr+"."+year);
            }
        };
    }


    private void syncData(boolean doVisibilityStuff) {
        adapter =  new HbA1cAdapter(getContext(), getHbA1cLista(), this);
        listH.setAdapter(adapter);

        if(doVisibilityStuff) {
            if (adapter.getCount() == 0) {
                listH.setVisibility(View.GONE);
                lblNoData.setVisibility(View.VISIBLE);
            } else {
                listH.setVisibility(View.VISIBLE);
                lblNoData.setVisibility(View.GONE);
            }
        }
    }

    private void setUpAnimations() {
        fadeOutFirstAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeOutFirstAnimation.setStartOffset(200);
        fadeOutFirstAnimation.setDuration(400);

        fadeOutFirstAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listH.setVisibility(View.GONE);
                lblNoData.setVisibility(View.GONE);
                layInsertForm.setVisibility(View.VISIBLE);
                lblTitle.setText(getText(R.string.lbl_hba1c_list_title2));
                layInsertForm.startAnimation(fadeInFirstAnimation);
                btnAddHbA1c.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInFirstAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeInFirstAnimation.setStartOffset(200);
        fadeInFirstAnimation.setDuration(400);

        fadeOutSecondAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeOutSecondAnimation.setStartOffset(200);
        fadeOutSecondAnimation.setDuration(400);

        fadeOutSecondAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layInsertForm.setVisibility(View.GONE);
                listH.setVisibility(View.VISIBLE);
                listH.startAnimation(fadeInSecondAnimation);
                lblTitle.setText(getText(R.string.lbl_hba1c_list_title1));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInSecondAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeInSecondAnimation.setStartOffset(200);
        fadeInSecondAnimation.setDuration(400);
        fadeInSecondAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnAddHbA1c.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
        if(cursor.moveToFirst()) {

            String datum;
            float rez;

            while (!cursor.isAfterLast()) {
                datum = cursor.getString(0);
                rez = cursor.getFloat(1);
                id = cursor.getInt(2);
                hba1c.add(new HbA1c(datum, rez, id));
                cursor.moveToNext();
            }
        }
        return hba1c;
    }

    @Override
    public void editButtonClicked(int position) {
        chosenHbA1c = adapter.getItem(position);
        btnDate.setText(chosenHbA1c.getDatum());
        txtTestResult.setText(chosenHbA1c.getRez() + "");

        godina = Integer.parseInt(chosenHbA1c.getDatum().substring(6,10));
        mjesec = Integer.parseInt(chosenHbA1c.getDatum().substring(3,5));
        dan = Integer.parseInt(chosenHbA1c.getDatum().substring(0,2));

        listH.startAnimation(fadeOutFirstAnimation);
    }

    private void insertTestResult() {
        dbSource.open();
        dbSource.insertHbA1c(btnDate.getText().toString(), Float.parseFloat(txtTestResult.getText().toString()));
        dbSource.close();

        syncData(false);
        layInsertForm.startAnimation(fadeOutSecondAnimation);

    }

    private void updateTestResult(int id) {
        dbSource.open();
        dbSource.updateHbA1c(btnDate.getText().toString(), Float.parseFloat(txtTestResult.getText().toString()), id);
        dbSource.close();

        syncData(false);

        layInsertForm.startAnimation(fadeOutSecondAnimation);
    }
}
