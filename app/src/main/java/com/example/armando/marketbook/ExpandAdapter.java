package com.example.armando.marketbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter implements AnimatedExpandableListView.myInterface {

    private Context mContext;
    private List<Object> Info;
    private List<HashMap<String,Object>> sottoinfo;
    private ArrayList<Integer> Stato;
    private List<View> SpecificView;

    ExpandAdapter(Context context, List<Object> info) {
        this.mContext = context;
        this.Info = info;
        this.Stato = new ArrayList<>();
        this.SpecificView = new ArrayList<>();
        try{
            for(int i=0;i<Info.size();i++){
                Stato.add(0);
            }
        }catch (Exception e){}

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        HashMap <String,Object> Oggetto =  (HashMap<String, Object>) getGroup(groupPosition);
        try {
            HashMap <String,List<HashMap<String,Object>>> x = (HashMap<String, List<HashMap<String,Object>>>) Oggetto.get("SottoInfo");
            final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(this.mContext);
            sottoinfo = (List<HashMap<String,Object>>) x.get("Informazioni");
            secondLevelELV.setAdapter(new SottoInfoAdapter(parent.getContext(),sottoinfo));
            secondLevelELV.setDividerHeight(0);
            secondLevelELV.setGroupIndicator(null);
            secondLevelELV.setFocusable(false);
            secondLevelELV.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            return secondLevelELV;
        } catch (Exception e){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.info, null);
            TextView text = (TextView) convertView.findViewById(R.id.eventsListEventRowText);
            text.setText( (String) Oggetto.get("Testo"));
            return convertView;
        }
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Info.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        try{
            return Info.size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HashMap<String,String> Oggetto = (HashMap<String,String>) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.titoli_row,parent,false);
            TextView text = convertView.findViewById(R.id.eventsListEventRowText);
            SpecificView.add(convertView);
            text.setText(Oggetto.get("Titolo"));
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void arrowAnimationStart(int groupPosition){
        View specific = SpecificView.get(groupPosition);
        ImageView arrow = specific.findViewById(R.id.arrow);
        if(Stato.get(groupPosition)==0){
            arrow.animate().rotation(180).setDuration(500).start();
            Stato.set(groupPosition,180);
        } else{
            arrow.animate().rotation(0).setDuration(500).start();
            Stato.set(groupPosition,0);
        }
    }

}