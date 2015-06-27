package kr.co.digitalanchor.studytime.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.model.db.ChatMessage;
import kr.co.digitalanchor.studytime.view.ViewHolder;

/**
 * Created by Thomas on 2015-06-27.
 */
public class ChildChatAdapter extends BaseAdapter {

    final private LayoutInflater inflater;

    final private List<ChatMessage> rows;

    final private String hostId;

    private MessageItemListener itemListener;


    public ChildChatAdapter(Context context, List<ChatMessage> rows, String hostId) {

        inflater = LayoutInflater.from(context);

        this.rows = rows;

        this.hostId = hostId;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int position) {

        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            v = inflater.inflate(R.layout.layout_chat_item_c, null);
        }

        ChatMessage model = rows.get(position);

        TextView textDate = ViewHolder.get(v, R.id.textDate);

        View layoutHost = ViewHolder.get(v, R.id.layoutHost);

        TextView textHostMsg = ViewHolder.get(v, R.id.textHostMsg);

        ImageButton buttonRetry = ViewHolder.get(v, R.id.buttonRetry);

        ImageButton buttonDelete = ViewHolder.get(v, R.id.buttonDelete);

        TextView textReadCnt = ViewHolder.get(v, R.id.textReadCount);

        View layoutGuest = ViewHolder.get(v, R.id.layoutGuest);

        TextView textGuestMsg = ViewHolder.get(v, R.id.textGuestMessage);

        TextView textReadCountGuest = ViewHolder.get(v, R.id.textReadCountGuest);

        textDate.setText(model.getTimeStamp());

        if (model.isMine()) {

            layoutHost.setVisibility(View.VISIBLE);
            layoutGuest.setVisibility(View.GONE);

            textHostMsg.setText(model.getMessage());

        } else {

            layoutHost.setVisibility(View.GONE);
            layoutGuest.setVisibility(View.VISIBLE);

            textGuestMsg.setText(model.getMessage());
        }

        return v;
    }

    public void setMessageListener(MessageItemListener listener) {

        itemListener = listener;
    }
}
