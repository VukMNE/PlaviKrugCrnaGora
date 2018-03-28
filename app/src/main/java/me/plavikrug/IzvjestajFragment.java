package me.plavikrug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.plavikrug.pdf.PDFReportGenerator;


/**
 * Created by Vuk on 31.7.2017..
 */

@SuppressLint("ValidFragment")
public class IzvjestajFragment extends Fragment {

    ViewPager mViewPager;
    String datumFragment;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_izvjestaj,container, false);
        // Setting ViewPager for each Tabs
        if(getArguments().containsKey("datumFragment")){
            String datum = getArguments().getString("datumFragment");
            if(datum.length()==0){
                datumFragment= "";
            }
            else{
                datumFragment = getArguments().getString("datumFragment");
            }
        }
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

    @SuppressLint("ValidFragment")
    public static final IzvjestajFragment newInstance(String datumPar) {
        Bundle args = new Bundle();
        args.putString("datumFragment", datumPar);
        IzvjestajFragment fragment = new IzvjestajFragment();
        fragment.setArguments(args);
        return fragment;
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        if(datumFragment.length()==0) {
            adapter.addFragment( ReportDnevni.newInstance(""), "Dnevni");
        }
        else{
            adapter.addFragment(ReportDnevni.newInstance(datumFragment), "Dnevni");
        }
        adapter.addFragment(new ReportNedeljni(), "Nedeljni");
        adapter.addFragment(new ReportMjesecni(), "Mjesečni");
        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           inflater.inflate(R.menu.izvjestaj_meni, menu);
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //Napravi izvještaj!
        if(item.getItemId() == R.id.men_print){
            Intent startReport = new Intent(getContext(), PDFReportPopUpWindow.class);
            startActivity(startReport);
        }
        return super.onOptionsItemSelected(item);
    }
}
