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
public class FragmentThree extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_page_checklist, container, false);
        LinearLayout layout = (LinearLayout)v.findViewById(R.id.home_checklist_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayChecklistActivityWithFragment.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public static FragmentThree newInstance(String text) {

        FragmentThree f = new FragmentThree();
//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }
}