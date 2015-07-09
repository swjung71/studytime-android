package kr.co.digitalanchor.studytime.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.view.ViewHolder;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ChildListAdapter extends ArrayAdapter<Child> {

    private Context context;

    private ArrayList<Child> items;

    private DBHelper mHelper;

    public ChildListAdapter(Context context, int resId, ArrayList<Child> items) {

        super(context, resId, items);

        this.items = items;
        this.context = context;

        this.mHelper = new DBHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_child_item, null);
        }

        Child child = items.get(position);

        TextView name = ViewHolder.get(convertView, R.id.labelName);
        name.setText(child.getName());
        name.setSelected(true);

        TextView noti = ViewHolder.get(convertView, R.id.labelNotiCount);

        int count = child.getNewMessageCount();

        if (count > 0) {

            noti.setVisibility(View.VISIBLE);

            noti.setText(count > 10 ? "10+" : String.valueOf(count));

        } else {

            noti.setVisibility(View.GONE);
        }

        ImageView profile = ViewHolder.get(convertView, R.id.imgProfile);

        return convertView;
    }
}
