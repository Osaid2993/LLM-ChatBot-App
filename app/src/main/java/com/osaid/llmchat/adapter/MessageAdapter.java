package com.osaid.llmchat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.osaid.llmchat.R;
import com.osaid.llmchat.data.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private final List<ChatMessage> messages;
    private final String username;

    public MessageAdapter(List<ChatMessage> messages, String username) {
        this.messages = messages;
        this.username = username;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().equals("user") ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        String time = formatTimestamp(message.getTimestamp());

        if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.messageText.setText(message.getMessage());
            userHolder.timestamp.setText(time);
            userHolder.avatarLetter.setText(username.substring(0, 1).toUpperCase());
        } else {
            BotViewHolder botHolder = (BotViewHolder) holder;
            botHolder.messageText.setText(message.getMessage());
            botHolder.timestamp.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp, avatarLetter;

        UserViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.userMessageText);
            timestamp = itemView.findViewById(R.id.userTimestamp);
            avatarLetter = itemView.findViewById(R.id.userAvatarLetter);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;

        BotViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.botMessageText);
            timestamp = itemView.findViewById(R.id.botTimestamp);
        }
    }
}