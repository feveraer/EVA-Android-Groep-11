package com.groep11.eva_app.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.interfaces.IColumnConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChallengeEntryAdapter extends CursorAdapter implements IColumnConstants {
    public static final String TAG = "ENTRY_ADAPTER";

    private static final float LEAF_DISABLED_OPACITY = 0.5f;

    public ChallengeEntryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "newView() called with: " + "context = [" + context + "], cursor = [" + cursor + "], parent = [" + parent + "]");
        Log.d(TAG, "Cursor position = " + cursor.getPosition());

        View view = LayoutInflater.from(context).inflate(R.layout.layout_timeline_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "bindView() called with: " + "view = [" + view + "], context = [" + context + "], cursor = [" + cursor + "]");
        ViewHolder holder = (ViewHolder) view.getTag();

        // Get values from cursor
        String title = cursor.getString(COL_CHALLENGE_TITLE);
        int diff = cursor.getInt(COL_CHALLENGE_DIFFICULTY);
        Log.d(TAG, "bindView: " + diff);

        // Set challenge title
        holder.entryTitle.setText(title);

        // Set opacity leaf #3
        holder.entryLeaves.get(2).setAlpha(diff < 3 ? LEAF_DISABLED_OPACITY : 1);
        // Set opacity leaf #2
        holder.entryLeaves.get(1).setAlpha(diff < 2 ? LEAF_DISABLED_OPACITY : 1);

        // Entry is the latest challenge, don't draw a top line (no challenge above it)
        if(cursor.getPosition() != 0)
            holder.entryLineTop.setBackgroundColor(Color.parseColor("#696b71"));

        // Entry is the last challenge, don't draw bottom line (no challenge below it)
        if(cursor.getPosition() != cursor.getCount()-1)
            holder.entryLineBottom.setBackgroundColor(Color.parseColor("#696b71"));
    }

    static class ViewHolder {
        @Bind(R.id.timeline_enty_title) TextView entryTitle;
        @Bind(R.id.timeline_entry_icon) ImageView entryCategoryIcon;
        @Bind(R.id.timeline_entry_line_top) ImageView entryLineTop;
        @Bind(R.id.timeline_entry_line_bottom) ImageView entryLineBottom;
        @Bind({R.id.timeline_entry_leaf_1, R.id.timeline_entry_leaf_2, R.id.timeline_entry_leaf_3}) List<ImageView> entryLeaves;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
