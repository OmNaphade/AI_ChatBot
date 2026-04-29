import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import './Chatbot.css';

const Chatbot = () => {
  const [messages, setMessages] = useState([
    { id: 1, text: 'Hello! I am your AI Chatbot. How can I help you today?', sender: 'bot' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const sendMessage = async () => {
    // Validate input
    if (!input.trim()) {
      setError('Message cannot be empty');
      return;
    }

    if (input.length > 5000) {
      setError('Message exceeds maximum length of 5000 characters');
      return;
    }

    // Add user message to chat
    const userMessage = {
      id: messages.length + 1,
      text: input,
      sender: 'user'
    };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setError('');
    setLoading(true);

    try {
      // Send to backend
      const response = await axios.post('http://localhost:8080/api/chat', {
        message: input,
        userId: 'user-' + Date.now()
      });

      // Add bot response
      const botMessage = {
        id: messages.length + 2,
        text: response.data.response,
        sender: 'bot'
      };
      setMessages(prev => [...prev, botMessage]);
    } catch (err) {
      // Handle error from backend
      let errorMessage = 'Failed to get response from chatbot';
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.message) {
        errorMessage = err.message;
      }
      setError(errorMessage);

      // Add error message to chat
      const errorBotMessage = {
        id: messages.length + 2,
        text: `Error: ${errorMessage}`,
        sender: 'error'
      };
      setMessages(prev => [...prev, errorBotMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const clearChat = () => {
    setMessages([
      { id: 1, text: 'Hello! I am your AI Chatbot. How can I help you today?', sender: 'bot' }
    ]);
    setError('');
  };

  return (
    <div className="chatbot-container">
      <div className="chatbot-header">
        <h1>🤖 AI Chatbot</h1>
        <button className="clear-btn" onClick={clearChat}>Clear Chat</button>
      </div>

      <div className="messages-container">
        {messages.map((msg) => (
          <div key={msg.id} className={`message ${msg.sender}`}>
            <div className="message-content">
              {msg.sender === 'bot' && <span className="bot-icon">🤖</span>}
              {msg.sender === 'user' && <span className="user-icon">👤</span>}
              {msg.sender === 'error' && <span className="error-icon">⚠️</span>}
              <p>{msg.text}</p>
            </div>
          </div>
        ))}
        {loading && (
          <div className="message bot">
            <div className="message-content">
              <span className="bot-icon">🤖</span>
              <div className="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {error && (
        <div className="error-banner">
          ⚠️ {error}
        </div>
      )}

      <div className="input-container">
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Type your message... (Max 5000 characters)"
          rows="3"
          disabled={loading}
          className="message-input"
        />
        <button
          onClick={sendMessage}
          disabled={loading}
          className="send-btn"
        >
          {loading ? '⏳ Sending...' : '📤 Send'}
        </button>
      </div>
    </div>
  );
};

export default Chatbot;

