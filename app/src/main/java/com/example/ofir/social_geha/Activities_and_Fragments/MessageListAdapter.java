package com.example.ofir.social_geha.Activities_and_Fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.R;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private List<Message> messageList;

    public MessageListAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        public TextView messageText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            messageText = mView.findViewById(R.id.message_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.messageText.setText(messageList.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
