package kr.co.digitalanchor.studytime.purchase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.model.Item;
import kr.co.digitalanchor.studytime.view.ViewHolder;

/**
 * Created by Thomas on 2015-08-27.
 */
public class ItemAdapter extends ArrayAdapter<Item> {

    private Context context;

    private List<Item> items;

    public ItemAdapter(Context context, int resource, List<Item> items) {

        super(context, resource, items);

        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.layout_item_purchase, null);
        }

        Item item = items.get(position);

        TextView name = ViewHolder.get(convertView, R.id.name);
        TextView cost = ViewHolder.get(convertView, R.id.cost);

        name.setText(item.getName());
        cost.setText(item.getCost());

        return convertView;
    }
}
