package kr.co.digitalanchor.studytime.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.model.Child;
import kr.co.digitalanchor.studytime.view.ViewHolder;

/**
 * Created by Thomas on 2015-11-10.
 */
public class ChildListAdapter extends ArrayAdapter<Child> {

  private Context context;

  private List<Child> items;

  public ChildListAdapter(Context context, int resource, List<Child> items) {

    super(context, resource, items);

    this.context = context;

    this.items = items;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {

      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.layout_child_item_c, null);

    }

    Child child = items.get(position);

    View layoutEnable = ViewHolder.get(convertView, R.id.enableChild);
    View layoutDisable = ViewHolder.get(convertView, R.id.disableChild);

    if (child.getExpirationYN().equals("Y")) {

      layoutEnable.setVisibility(View.VISIBLE);
      layoutDisable.setVisibility(View.GONE);

      TextView name = ViewHolder.get(convertView, R.id.labelName);
      name.setText(child.getName());
      name.setSelected(true);

      TextView noti = ViewHolder.get(convertView, R.id.labelNotiCount);

      TextView labelDevice = ViewHolder.get(convertView, R.id.textDevice);
      labelDevice.setText(child.getDeviceModel());

      TextView labelExpired = ViewHolder.get(convertView, R.id.textExpired);
      labelExpired.setText(getContext().getString(R.string.payment_info,
          child.getExpirationDate(), child.getRemainingDays()));

    } else {

      layoutEnable.setVisibility(View.GONE);
      layoutDisable.setVisibility(View.VISIBLE);

      TextView name = ViewHolder.get(convertView, R.id.labelName2);
      name.setText(child.getName());
      name.setSelected(true);

      TextView labelDevice = ViewHolder.get(convertView, R.id.textDevice2);
      labelDevice.setText(child.getDeviceModel());

      ImageView profile = ViewHolder.get(convertView, R.id.imgProfileDisable);
    }

    return convertView;
  }
}
