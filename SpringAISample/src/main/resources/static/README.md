# React Chatbot UI Setup Guide

## Overview
This is a React-based UI for the Spring AI Chatbot service. It provides a beautiful, interactive chat interface to communicate with your AI model.

## Features
✅ Real-time chat interface
✅ Error handling and validation
✅ Loading states with typing indicator
✅ Message history
✅ Responsive design (works on mobile)
✅ Beautiful gradient UI
✅ Auto-scroll to latest messages

## Setup Instructions

### Prerequisites
- Node.js (v14 or higher)
- npm (comes with Node.js)
- Spring Boot application running on http://localhost:8080

### Installation Steps

1. Navigate to the UI directory:
   ```bash
   cd D:\PRACTICE\TELUSKO\SpringAISample\src\main\resources\static
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Ensure your Spring Boot backend is running:
   ```bash
   # In another terminal, from SpringAISample root directory
   mvn spring-boot:run
   ```

4. Start the React development server:
   ```bash
   npm start
   ```

5. Open your browser and navigate to:
   ```
   http://localhost:3000
   ```

## Usage

1. **Type a Message**: Enter your question or message in the input field
2. **Send Message**: Click "📤 Send" button or press Enter (Shift+Enter for new line)
3. **View Response**: Wait for the AI to respond with an answer
4. **Clear Chat**: Click "Clear Chat" button to start a new conversation

## Features in Detail

### Error Handling
- Empty message validation
- Message length validation (max 5000 characters)
- Backend error responses displayed
- Network error handling

### Input Validation
- Prevents sending empty messages
- Validates message length
- Disables input while waiting for response

### UI Features
- Smooth animations for messages
- Typing indicator while waiting for response
- Auto-scroll to latest message
- Message timestamps
- Clear error messages
- Responsive design for mobile devices

## API Integration

The UI connects to the following endpoint:
```
POST http://localhost:8080/api/chat
Content-Type: application/json

Request:
{
  "message": "Your question here"
}

Response:
{
  "response": "AI's answer",
  "timestamp": 1234567890
}
```

## Build for Production

To create a production build:
```bash
npm run build
```

This creates an optimized build in the `build` folder that you can deploy.

## Troubleshooting

### Error: "Failed to get response from chatbot"
- Ensure Spring Boot application is running on http://localhost:8080
- Check that Ollama is running with the correct model

### Error: "Cannot find module 'axios'"
```bash
npm install axios
```

### Port 3000 already in use?
Set a different port:
```bash
set PORT=3001 && npm start
```
(On Linux/Mac: `PORT=3001 npm start`)

### CORS Error
- Check that `@CrossOrigin(origins = "*")` is present in AIController.java
- Ensure Spring Boot application is restarted after code changes

## File Structure
```
static/
├── package.json              # Dependencies
├── public/
│   └── index.html           # Main HTML file
├── src/
│   ├── index.jsx            # Entry point
│   ├── index.css            # Global styles
│   ├── App.jsx              # Main app component
│   ├── App.css              # App styles
│   ├── Chatbot.jsx          # Chatbot component
│   └── Chatbot.css          # Chatbot styles
└── README.md                # This file
```

## Technology Stack
- React 18.2
- Axios (for HTTP requests)
- CSS3 (for styling and animations)
- ES6+ JavaScript

## Future Enhancements
- User authentication
- Chat history persistence
- Streaming responses
- Voice input/output
- File upload support
- Multi-user support
- Dark/Light theme toggle

