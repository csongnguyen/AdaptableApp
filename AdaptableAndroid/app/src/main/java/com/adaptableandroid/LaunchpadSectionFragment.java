package com.adaptableandroid;

/**
 * Created by Connie on 5/12/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.adaptableandroid.com.adaptableandroid.models.ChildItem;
import com.adaptableandroid.com.adaptableandroid.models.GroupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that launches other parts of the demo application.
 */
public class LaunchpadSectionFragment extends Fragment {
    public static final String ARG_DISASTER = "disaster_type";
    public static final String ARG_DISASTER_NUMBER = "disaster_number";
    public static final String ARG_TOTAL_DISASTERS = "total";
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    SharedPreferences sharedPreferences;
    ExpandableListAdapter explistAdapter;
    AnimatedExpandableListView expListView;
    List<GroupItem> groupItems;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_display_disasters, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.disasterTitle)).setText(
                args.getString(ARG_DISASTER));

        ((TextView) rootView.findViewById(R.id.disasterPageNumber)).setText(
                args.getInt(ARG_DISASTER_NUMBER) + " of " + args.getInt(ARG_TOTAL_DISASTERS));

        rootView.findViewById(R.id.disasterBackground).setBackgroundResource(R.drawable.drought_background);

//        expListView = (AnimatedExpandableListView) rootView.findViewById(R.id.impactList);
//
//        if(expListView == null){
//            Log.d("explistview", "NULL from launchpadSectionFragment");
//        }
//        if(expListView !=  null){
//            expListView.setGroupIndicator(null);
//            expListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {
//                int lastView = -1;
//
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
//                    // call collapseGroupWithAnimation(int) and expandGroupWithAnimation(int)
//                    // to animate group expansion/collapse.
//                    if(expListView.isGroupExpanded(groupPosition)){
//                        expListView.collapseGroupWithAnimation(groupPosition);
//                    }else{
//                        if(lastView != -1){
//                            expListView.collapseGroupWithAnimation(lastView);
//                        }
//
//                        expListView.expandGroupWithAnimation(groupPosition);
//                        lastView = groupPosition;
//                    }
//                    return true;
//                }
//
//            });
//            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//                @Override
//                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                    expListView.collapseGroupWithAnimation(groupPosition);
//                    return true;
//                }
//            });
//        }

//        // Demonstration of a collection-browsing activity.
//        rootView.findViewById(R.id.demo_collection_button)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//        // Demonstration of navigating to external activities.
//        rootView.findViewById(R.id.demo_external_activity)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // Create an intent that asks the user to pick a photo, but using
//                        // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
//                        // the application from the device home screen does not return
//                        // to the external activity.
//                        Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
//                        externalActivityIntent.setType("image/*");
//                        externalActivityIntent.addFlags(
//                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                        startActivity(externalActivityIntent);
//                    }
//                });

        return rootView;
    }



}

