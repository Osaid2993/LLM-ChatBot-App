# LLM ChatBot App

An Android chatbot application that connects to Google's Gemini API to deliver a real-time AI-powered chat experience. Users log in with a username and interact with a generative AI model through a clean, message-based interface. All conversations are persisted locally using Room so chat history carries across sessions.

## Features

- Username-based login screen
- Real-time chat with Google's Gemini 2.5 Flash model
- Bot sends an automatic welcome message on first login
- Full chat history persisted with Room (SQLite)
- Messages tied to each username, so different users get separate histories
- Timestamps displayed on every message bubble
- Distinct message bubbles for user and bot with avatar icons

## Tech Stack

- Java
- Room (SQLite)
- OkHttp for Gemini API calls
- Material Design
- View Binding

## Project Structure

- `LoginActivity.java` - Login screen where the user enters a username to proceed
- `ChatActivity.java` - Main chat screen handling message sending, receiving, and history loading
- `GeminiHelper.java` - Handles HTTP requests to the Gemini API and parses responses
- `MessageAdapter.java` - RecyclerView adapter with separate view types for user and bot messages
- `ChatMessage.java` - Room entity representing a single chat message
- `ChatMessageDao.java` - DAO interface for inserting and querying messages by username
- `ChatDatabase.java` - Room database singleton providing access to the DAO

## Layouts

- `activity_login.xml` - Login screen with username field and Go button
- `activity_chat.xml` - Chat screen with RecyclerView and message input bar
- `item_message_user.xml` - User message bubble with avatar and timestamp
- `item_message_bot.xml` - Bot message bubble with sparkle icon and timestamp
