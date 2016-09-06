package com.pubnub.example.android.fabric.pnfabricchat.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pubnub.example.android.fabric.pnfabricchat.R;
import com.pubnub.example.android.fabric.pnfabricchat.speech.TextHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatPojo> {
    private final Context context;
    private final LayoutInflater inflater;
    private final List<ChatPojo> values = new ArrayList<>();
    private TextHandler extra;
    private String selfUuid;

    public ChatListAdapter(Context context, String selfUuid, TextHandler extra) {
        super(context, R.layout.list_row_chat);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.extra = extra;
        this.selfUuid = selfUuid;
    }

    @Override
    public void add(final ChatPojo message) {
        this.values.add(0, message);

        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();

                if (ChatListAdapter.this.extra != null && ChatListAdapter.this.selfUuid != null && !message.getSender().equals(ChatListAdapter.this.selfUuid)) {
                    extra.onText(message.getMessage());
                }
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChatPojo dsMsg = this.values.get(position);
        ChatListRowUi msgView;

        if (convertView == null) {
            msgView = new ChatListRowUi();

            convertView = inflater.inflate(R.layout.list_row_chat, parent, false);

            msgView.sender = (TextView) convertView.findViewById(R.id.sender);
            msgView.message = (TextView) convertView.findViewById(R.id.message);
            msgView.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(msgView);
        } else {
            msgView = (ChatListRowUi) convertView.getTag();
        }

        msgView.sender.setText(dsMsg.getSender());
        msgView.message.setText(dsMsg.getMessage());
        msgView.timestamp.setText(dsMsg.getTimestamp());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    public void clear() {
        this.values.clear();
        notifyDataSetChanged();
    }
}
