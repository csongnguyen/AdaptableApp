package com.adaptableandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Connie on 5/29/2015.
 */
public class FragmentTwo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_page_top_risk_level, container, false);
        LinearLayout layout = (LinearLayout)v.findViewById(R.id.home_risk_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayDisastersActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public static FragmentTwo newInstance(String text) {

        FragmentTwo f = new FragmentTwo();
//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }
}