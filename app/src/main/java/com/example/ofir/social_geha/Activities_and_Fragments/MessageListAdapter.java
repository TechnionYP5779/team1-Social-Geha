package com.example.ofir.social_geha.Activities_and_Fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    private List<Message> messageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    // ==================================
    //             FUNCTIONS
    // ==================================
    //             C'TOR
    // ==================================
    public MessageListAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    // ==================================
    // This functions gets a date parameter (Date)
    // and returns a string matching that date in the format of:
    // HH:MM
    // ==================================
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

    // ==================================
    //            INNER CLASS
    // this class represents an item in message list
    // ==================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        TextView messageText;
        TextView messageTime;


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

    // ==================================
    // this function gets a position of a message in list, and returns:
    // VIEW_TYPE_MESSAGE_SENT - if the message was sent by the user.
    // VIEW_TYPE_MESSAGE_RECEIVED - if the message was received by the user.
    // ==================================
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getFromPersonID().equals(Database.getInstance().getLoggedInUserID())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // ==================================
    // Inflates the appropriate layout according to the ViewType.
    // ==================================
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
