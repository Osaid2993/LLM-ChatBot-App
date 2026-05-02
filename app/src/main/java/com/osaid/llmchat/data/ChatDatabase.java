package com.osaid.llmchat.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.osaid.llmchat.data.dao.ChatMessageDao;
import com.osaid.llmchat.data.model.ChatMessage;

@Database(entities = {ChatMessage.class}, version = 1, exportSchema = false)
public abstract class ChatDatabase extends RoomDatabase {

    private static ChatDatabase instance;

    public abstract ChatMessageDao chatMessageDao();

    public static synchronized ChatDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ChatDatabase.class,
                    "chat_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}