package kr.co.digitalanchor.studytime.control;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.captechconsulting.captechbuzz.model.images.ImageCacheManager;
import com.orhanobut.logger.Logger;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;
import java.util.Random;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.model.PackageElementForC;
import kr.co.digitalanchor.studytime.model.PackageElementForP;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.monitor.LockServiceHandler;
import kr.co.digitalanchor.studytime.monitor.LockServiceReceiver;
import kr.co.digitalanchor.utils.IntentFilteredData;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_CHANGED;
import static kr.co.digitalanchor.utils.Intents.EXTRA_PACKAGE;

/**
 * Created by Thomas on 2015-08-03.
 */
public class AppBlockGridAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater mInflater;

    private int mItemResId;

    private List<PackageElementForC> mItems;

    public AppBlockGridAdapter(Context context, int itemResId, List<PackageElementForC> items) {

        init(context, items, itemResId);
    }

    private void init(Context context, List<PackageElementForC> items, int itemResId) {

        this.mItems = items;

        this.mItemResId = itemResId;

        this.context = context;

        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {

        return mItems.size();
    }

    @Override
    public PackageElementForC getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = mInflater.inflate(mItemResId, parent, false);

            holder = new ViewHolder();

            holder.label = (TextView) convertView.findViewById(R.id.appLabelBlock);
            holder.appImg = (ImageView) convertView.findViewById(R.id.appIconBlock);
            holder.defalutImg = (ImageView) convertView.findViewById(R.id.appIcon2Block);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        PackageElementForC item = getItem(position);

        holder.label.setText(item.getName());


        holder.appImg.setVisibility(View.VISIBLE);
        holder.defalutImg.setVisibility(View.GONE);

        holder.appImg.setImageDrawable(item.getIconUrl());
            /*holder.appImg.setImageUrl(HttpHelper.getImageURL(item.getIconUrl()),
                    ImageCacheManager.getInstance().getImageLoader());*/

        if (item.getPackageName().equals("call")) {
            holder.appImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Context context = STApplication.applicationContext;
                        IntentFilteredData intentFilteredData = new IntentFilteredData();
                        Intent intent = intentFilteredData.INTENT_DIAL;
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_REORDER_TO_FRONT );
                        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                        Logger.i("onCall start");
                        PendingIntent.getActivity(context, 0, intent, flags).send();
                        Logger.i("OnCall end");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (item.getPackageName().equals("sms")) {
            holder.appImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Context context = STApplication.applicationContext;
                        Intent intent;
                        Logger.i("onSMS start");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            String pkg = Telephony.Sms.getDefaultSmsPackage(context);
                            intent = new Intent(IntentFilteredData.INTENT_SMS_KITKET_HIGH);

                            if (pkg != null) {
                                intent.setPackage(pkg);
                            }
                        } else {
                            intent = IntentFilteredData.INTENT_SMS_JB_LOW;
                        }
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP |FLAG_ACTIVITY_REORDER_TO_FRONT );
                        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                        PendingIntent.getActivity(context, 0, intent, flags).send();

                        //PendingIntent.getBroadcast(STApplication.applicationContext, 0, new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, item.getPackageName()), 0).send();
                        /*
                        Intent intent1 = new Intent(context, LockServiceReceiver.class);
                        intent1.setAction(ACTION_USAGE_CHANGED);
                        PendingIntent.getBroadcast(context, 1, intent1, flags).send();
*/

                        /*Logger.i("send Braodcast");*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            holder.appImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        PackageManager pm = STApplication.applicationContext.getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(item.getPackageName());
                        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                        //intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_REORDER_TO_FRONT );
                        Logger.i("packageName it clicked : " + item.getPackageName());
                        PendingIntent.getActivity(STApplication.applicationContext, 0, intent, flags).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return convertView;
    }

    protected class ViewHolder {

        public TextView label;

        public ImageView appImg;

        public ImageView defalutImg;

    }
}
