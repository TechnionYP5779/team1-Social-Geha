package com.example.ofir.social_geha.Activities_and_Fragments;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private List<Message> messageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessageListAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    private static String getHourStringFromDate(Date date){
        if(date == null)
            return "NULL DATE";
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        StringBuilder sb = new StringBuilder();
        if(hour < 10)
            sb.append(0);
        sb.append(hour);
        sb.append(":");
        if(minute < 10)
            sb.append(0);
        sb.append(minute);
        return sb.toString();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        public TextView messageText;
        public TextView messageTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            messageText = mView.findViewById(R.id.message_text_view);
            messageTime = mView.findViewById(R.id.text_message_time);
        }

        public void bind(Message msg) {
            messageText.setText(msg.getMessage());
            messageTime.setText(getHourStringFromDate(msg.getMessageDate()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getFromPersonID().equals(Database.getInstance().getLoggedInUserID())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_layout, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_layout_recieved, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder vh = (ViewHolder)viewHolder;
        vh.bind(messageList.get(i));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
