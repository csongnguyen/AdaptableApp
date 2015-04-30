package com.adaptableandroid;

import android.content.Context;
import android.graphics.Typeface;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.adaptableandroid.com.adaptableandroid.models.ChildHolder;
import com.adaptableandroid.com.adaptableandroid.models.ChildItem;
import com.adaptableandroid.com.adaptableandroid.models.GroupHolder;
import com.adaptableandroid.com.adaptableandroid.models.GroupItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.adaptableandroid.AnimatedExpandableListView.AnimatedExpandableListAdapter;

/**
 * Created by Connie on 4/28/2015.
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class ExpandableListAdapter extends AnimatedExpandableListAdapter {
    private Context context;
    private List<GroupItem> groupItems;
    private List<String> listDataHeader; //header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataChild;
    private static final int DIVIDER = 30;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listDataChild){
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public ExpandableListAdapter(Context context, List<GroupItem> groupItems){
        this.context = context;
        this.groupItems = groupItems;
    }

    @Override
//    public Object getChild(int groupPosition, int childPosition){
    public ChildItem getChild(int groupPosition, int childPosition){
//        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
        return groupItems.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent){
//        final String childText = (String) getChild(groupPosition, childPosition);
        ChildItem childItem = getChild(groupPosition, childPosition);
        ChildHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_child, parent, false);
            holder = new ChildHolder();
            holder.setTopic((TextView) convertView.findViewById(R.id.lblListItem));
            convertView.setTag(holder);
        }else{
            holder = (ChildHolder) convertView.getTag();
        }

//        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
//        txtListChild.setText(childText);
//        txtListChild.setText(childItem.getTopic());
        holder.getTopic().setText(childItem.getTopic());

//        int thisGroupSize = this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
        int thisGroupSize = this.groupItems.get(groupPosition).getChildren().size();
        // groups.get(groupPosition).getChilds().size() - 1
        int lastElementInGroup = thisGroupSize - 1;
        if (childPosition == lastElementInGroup) {
            convertView.setPadding(0, 0, 0, DIVIDER);
        } else
            convertView.setPadding(0, 0, 0, 0);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition){
//        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
        return groupItems.get(groupPosition).getChildren().size();
    }

    @Override
//    public Object getGroup(int groupPosition){
    public GroupItem getGroup(int groupPosition){
//        return this.listDataHeader.get(groupPosition);
        return groupItems.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
//        return this.listDataHeader.size();
        return groupItems.size();
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent){
        GroupItem groupItem =  getGroup(groupPosition);
        GroupHolder holder;
//        String headerTitle = (String) getGroup(groupPosition); // this one is used with listDataHeader
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group, parent, false);
            holder = new GroupHolder();
            holder.setTitle((TextView) convertView.findViewById(R.id.lblListHeader));
            convertView.setTag(holder);
        }else{
            holder = (GroupHolder) convertView.getTag();
        }

//        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
////        lblListHeader.setText(headerTitle); // used with listDataHeader
//        lblListHeader.setText(groupItem.getTitle());

        holder.getTitle().setTypeface(null, Typeface.BOLD);
        holder.getTitle().setText(groupItem.getTitle());

        if (isExpanded) {
            convertView.setPadding(0, 0, 0, 0);
//            lblListHeader.setPadding(lblListHeader.getPaddingLeft(), lblListHeader.getPaddingTop(), lblListHeader.getPaddingRight(), 10);
        }
        else {
            convertView.setPadding(0, 0, 0, DIVIDER);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }


}



