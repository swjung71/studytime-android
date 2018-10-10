package kr.co.digitalanchor.studytime.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.model.db.Child2;
import kr.co.digitalanchor.studytime.view.ViewHolder;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ChildListAdapter extends ArrayAdapter<Child2> {

  private Context context;

  private ArrayList<Child2> items;

  private DBHelper mHelper;

  /*public ChildListAdapter(Context context, int resId, ArrayList<Child> items) {

    super(context, resId, items);

    this.items = items;
    this.context = context;

    //this.mHelper = new DBHelper(context);
    this.mHelper = DBHelper.getInstance(context);
  }*/

  public ChildListAdapter(Context context, int resId, ArrayList<Child2> items) {

    super(context, resId, items);

    this.items = items;
    this.context = context;

    //this.mHelper = new DBHelper(context);
    this.mHelper = DBHelper.getInstance(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {

      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.layout_child_item, null);
    }

    Child2 child = items.get(position);

    View layoutEnable = ViewHolder.get(convertView, R.id.enableChild);

    View layoutDisable = ViewHolder.get(convertView, R.id.disableChild);

    if (child.getIsExpired().equals("N")) {

      layoutEnable.setVisibility(View.VISIBLE);
      layoutDisable.setVisibility(View.GONE);

      TextView name = ViewHolder.get(convertView, R.id.labelName);
      name.setText(child.getName());
      name.setSelected(true);

      TextView noti = ViewHolder.get(convertView, R.id.labelNotiCount);

      TextView labelDevice = ViewHolder.get(convertView, R.id.textDevice);
      labelDevice.setText(child.getDeviceModel());

      Logger.d(child.getRemainingDays() + " days");

      TextView labelExpired = ViewHolder.get(convertView, R.id.textExpired);
      labelExpired.setText(context.getResources().getString(R.string.payment_info,
          child.getExpirationDate(),
          String.valueOf(child.getRemainingDays())));

      int count = child.getNewMessageCount();

      if (count > 0) {

        noti.setVisibility(View.VISIBLE);

        noti.setText(count > 10 ? "10+" : String.valueOf(count));

      } else {

        noti.setVisibility(View.GONE);
      }

      ImageView profile = ViewHolder.get(convertView, R.id.imgProfile);

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
