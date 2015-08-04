package kr.co.digitalanchor.studytime.control;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.captechconsulting.captechbuzz.model.images.ImageCacheManager;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.model.PackageElementForP;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;

/**
 * Created by Thomas on 2015-08-03.
 */
public class AppGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    private Context context;

    private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private List<PackageElementForP> mItems;

    public AppGridAdapter(Context context, int headerResId, int itemResId, List<PackageElementForP> items) {

        init(context, items, headerResId, itemResId);
    }

    private void init(Context context, List<PackageElementForP> items, int headerResId, int itemResId) {

        this.mItems = items;

        this.mHeaderResId = headerResId;

        this.mItemResId = itemResId;

        this.context = context;

        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {

        return mItems.size();
    }

    @Override
    public long getHeaderId(int position) {

        PackageElementForP model = getItem(position);

        return model.getExcepted();
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder holder;

        if (convertView == null) {

            convertView = mInflater.inflate(mHeaderResId, parent, false);

            holder = new HeaderViewHolder();

            holder.textView = (TextView) convertView.findViewById(R.id.text1);

            convertView.setTag(holder);

        } else {

            holder = (HeaderViewHolder) convertView.getTag();
        }

        PackageElementForP item = getItem(position);

        String text;

        if (item.getExcepted() == 0) {

            text = context.getString(R.string.unexceptedAppListHeader);

        } else {

            text = context.getString(R.string.exceptedAppListHeader);
        }

        holder.textView.setText(text);

        return convertView;
    }

    @Override
    public PackageElementForP getItem(int position) {
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

            holder.label = (TextView) convertView.findViewById(R.id.appLabel);
            holder.appImg = (NetworkImageView) convertView.findViewById(R.id.appIcon);
            holder.lockImg = (ImageView) convertView.findViewById(R.id.lockImg);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        PackageElementForP item = getItem(position);

        if (item.getExcepted_ex() == 0) {

            holder.lockImg.setImageResource(R.drawable.icon_lock);

        } else {

            holder.lockImg.setImageResource(R.drawable.icon_unlock);
        }

        holder.label.setText(item.getName());

        if (TextUtils.isEmpty(item.getIconUrl())) {

            holder.appImg.setImageResource(R.drawable.app_icon);
            holder.appImg.setDefaultImageResId(R.drawable.app_icon);
            holder.appImg.setErrorImageResId(R.drawable.app_icon);

        } else {

            holder.appImg.setImageUrl(HttpHelper.getImageURL(item.getIconUrl()), ImageCacheManager.getInstance().getImageLoader());
            holder.appImg.setDefaultImageResId(R.drawable.app_icon);
            holder.appImg.setErrorImageResId(R.drawable.app_icon);

        }

        return convertView;
    }

    protected class HeaderViewHolder {

        public TextView textView;
    }

    protected class ViewHolder {

        public TextView label;

        public ImageView lockImg;

        public NetworkImageView appImg;

    }
}
