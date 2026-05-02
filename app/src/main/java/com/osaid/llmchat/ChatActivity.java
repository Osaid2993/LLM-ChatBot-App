package com.osaid.llmchat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.osaid.llmchat.adapter.MessageAdapter;
import com.osaid.llmchat.api.GeminiHelper;
import com.osaid.llmchat.data.ChatDatabase;
import com.osaid.llmchat.data.dao.ChatMessageDao;
import com.osaid.llmchat.data.model.ChatMessage;
import com.osaid.llmchat.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private MessageAdapter adapter;
    private List<ChatMessage> messageList;
    private String username;
    private GeminiHelper geminiHelper;
    private ChatMessageDao dao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = getIntent().getStringExtra("username");

        geminiHelper = new GeminiHelper();
        dao = ChatDatabase.getInstance(this).chatMessageDao();
        executor = Executors.newSingleThreadExecutor();

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, username);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.setAdapter(adapter);

        loadChatHistory();

        binding.sendButton.setOnClickListener(v -> {
            String text = binding.messageInput.getText().toString().trim();
            if (text.isEmpty()) return;

            binding.messageInput.setText("");
            sendMessage(text);
        });
    }

    private void loadChatHistory() {
        executor.execute(() -> {
            List<ChatMessage> history = dao.getMessagesForUser(username);
            runOnUiThread(() -> {
                if (history.isEmpty()) {
                    ChatMessage welcome = new ChatMessage("bot", "Welcome " + username + "!", username, System.currentTimeMillis());
                    addMessageToChat(welcome);
                    executor.execute(() -> dao.insert(welcome));
                } else {
                    messageList.addAll(history);
                    adapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            });
        });
    }

    private void sendMessage(String text) {
        ChatMessage userMessage = new ChatMessage("user", text, username, System.currentTimeMillis());
        addMessageToChat(userMessage);

        executor.execute(() -> dao.insert(userMessage));

        geminiHelper.sendMessage(text, new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                ChatMessage botMessage = new ChatMessage("bot", response, username, System.currentTimeMillis());
                executor.execute(() -> dao.insert(botMessage));
                runOnUiThread(() -> addMessageToChat(botMessage));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                    Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void addMessageToChat(ChatMessage message) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (!messageList.isEmpty()) {
            binding.chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}