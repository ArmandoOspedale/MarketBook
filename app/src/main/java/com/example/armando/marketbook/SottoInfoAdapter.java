package com.example.armando.marketbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SottoInfoAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context mContext;
    private List<HashMap<String,Object>> sottoinfo;

    SottoInfoAdapter(Context context, List<HashMap<String,Object>> info) {
        this.mContext = context;
        this.sottoinfo = info;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sottoinfo.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        HashMap<String,String> Oggetto = (HashMap<String,String>) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.info,parent,false);
        }
        TextView text = convertView.findViewById(R.id.eventsListEventRowText);
        text.setText(Oggetto.get("Testo"));
        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(400);
        text.startAnimation(in);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        HashMap<String,HashMap<String,String>> Oggetto = (HashMap<String,HashMap<String, String>>) getGroup(groupPosition);
        return Oggetto.size()-1;
    }

    /**@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        HashMap<String,String> Oggetto = (HashMap<String,String>) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.info,parent,false);
        }
        TextView text = convertView.findViewById(R.id.eventsListEventRowText);
        text.setText(Oggetto.get("Testo"));
        return convertView;
    }*/

    /**@Override
    public int getChildrenCount(int groupPosition) {
        HashMap<String,HashMap<String,String>> Oggetto = (HashMap<String,HashMap<String, String>>) getGroup(groupPosition);
        return Oggetto.size()-1;
    }*/

    @Override
    public Object getGroup(int groupPosition) {
        return sottoinfo.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        try{
            return sottoinfo.size();
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HashMap<String,String> Oggetto = (HashMap<String,String>) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.sub_info_row,parent,false);
        }
        TextView text = convertView.findViewById(R.id.eventsListEventRowText);
        text.setText(Oggetto.get("Sottotitolo"));
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

}

