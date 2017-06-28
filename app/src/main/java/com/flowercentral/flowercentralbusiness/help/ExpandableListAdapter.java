package com.flowercentral.flowercentralbusiness.help;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;

import java.util.List;

class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<HelpDetails> helpDetailsList;

    ExpandableListAdapter(Activity context, List<HelpDetails> helpDetailsList) {
        this.context = context;
        this.helpDetailsList = helpDetailsList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return helpDetailsList.get(groupPosition).getAnswer();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String answer = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandile_child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.answer);
        item.setText(answer);

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {

        String answer = helpDetailsList.get(groupPosition).getAnswer();
        if (null != answer && answer.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    public Object getGroup(int groupPosition) {
        return helpDetailsList.get(groupPosition).getQuestion();
    }

    public int getGroupCount() {
        return helpDetailsList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandible_group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.question);
        item.setText(question);

        ImageView indicator = (ImageView) convertView.findViewById(R.id.help_group_indicator);
        if (isExpanded) {
            indicator.setImageResource(R.drawable.ic_remove);
            convertView.findViewById(R.id.group_divider_blank).setVisibility(View.GONE);
        } else {
            indicator.setImageResource(R.drawable.ic_add);
            convertView.findViewById(R.id.group_divider_blank).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
