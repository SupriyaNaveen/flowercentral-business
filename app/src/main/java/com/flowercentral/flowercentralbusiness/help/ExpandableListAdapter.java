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

import java.util.ArrayList;
import java.util.List;

/**
 * ExpandableListAdapter provides view to each item of help list.
 */
class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;
    private Activity context;
    private List<HelpDetails> helpDetailsList;
    private int viewType;

    /**
     * Init the adapter by setting the help list data.
     *
     * @param context         context
     * @param helpDetailsList list of questioner
     */
    ExpandableListAdapter(Activity context, List<HelpDetails> helpDetailsList) {
        this.context = context;
        this.helpDetailsList = helpDetailsList;
    }

    /**
     * Get the answer(child position) for question(group position).
     *
     * @param groupPosition of question
     * @param childPosition of answer
     * @return Model object
     */
    public Object getChild(int groupPosition, int childPosition) {
        return helpDetailsList.get(groupPosition).getAnswer();
    }

    /**
     * Get the child element id.
     *
     * @param groupPosition of question
     * @param childPosition of answer
     * @return index of answer
     */
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    /**
     * Inflate the child view. i.e. answer of each question.
     *
     * @param groupPosition of question
     * @param childPosition of answer
     * @param isLastChild   isLastChild
     * @param convertView   convertView
     * @param parent        parent
     * @return view
     */
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

    /**
     * Get the number of answers for each question.
     *
     * @param groupPosition of question
     * @return count
     */
    public int getChildrenCount(int groupPosition) {

        String answer = helpDetailsList.get(groupPosition).getAnswer();
        if (null != answer && answer.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Get the question model
     *
     * @param groupPosition of question
     * @return Model class
     */
    public Object getGroup(int groupPosition) {
        return helpDetailsList.get(groupPosition).getQuestion();
    }

    /**
     * Number of questions in the list.
     *
     * @return count
     */
    public int getGroupCount() {
        int size;
        if (helpDetailsList != null && helpDetailsList.size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
            size = helpDetailsList.size();
        } else {
            //To show empty view
            viewType = VIEW_TYPE_EMPTY_LIST;
            size = 1;
        }
        return size;
    }

    /**
     * Get the position of question.
     *
     * @param groupPosition of question
     * @return position
     */
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * View type is empty means show empty view.
     * Else inflate the question view
     *
     * @param groupPosition of question
     * @param isExpanded    isExpanded
     * @param convertView   convertView
     * @param parent        parent
     * @return view
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_order_item, parent, false);
                }
                TextView msg = (TextView) convertView.findViewById(R.id.txt_msg_no_item_found);
                msg.setText(context.getString(R.string.empty_help_items));
                break;

            case VIEW_TYPE_NON_EMPTY_LIST:
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
                break;
        }
        return convertView;
    }

    /**
     * Has stable id's.
     *
     * @return true
     */
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Answer view if each question is non selectable.
     *
     * @param groupPosition of question
     * @param childPosition of answer
     * @return false
     */
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * Replace the help item list with new set of data.
     * Refresh the view accordingly.
     *
     * @param _list list
     */
    void replaceAll(List<HelpDetails> _list) {
        if (null == helpDetailsList) {
            helpDetailsList = new ArrayList<>();
        }
        if (helpDetailsList.size() > 0) {
            helpDetailsList.clear();
        }
        helpDetailsList.addAll(_list);
        this.notifyDataSetChanged();
    }
}
