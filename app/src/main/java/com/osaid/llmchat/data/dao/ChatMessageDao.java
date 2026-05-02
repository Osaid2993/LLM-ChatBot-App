package com.osaid.llmchat.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.osaid.llmchat.data.model.ChatMessage;

import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    void insert(ChatMessage message);

    @Query("SELECT * FROM chat_messages WHERE username = :username ORDER BY timestamp ASC")
    List<ChatMessage> getMessagesForUser(String username);

    @Query("DELETE FROM chat_messages WHERE username = :username")
    void deleteMessagesForUser(String username);
}