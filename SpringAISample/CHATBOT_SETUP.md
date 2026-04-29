# Complete AI Chatbot Setup Guide

## 🎉 What You Have

A fully functional **AI Chatbot System** consisting of:
1. **Spring Boot Backend** - REST API with exception handling
2. **React Frontend UI** - Beautiful chat interface
3. **Documentation** - Complete setup and usage guides

---

## 🚀 Quick Start (5 minutes)

### 1. Start the Backend
```bash
cd D:\PRACTICE\TELUSKO\SpringAISample
mvn spring-boot:run
```
✅ Backend runs on: http://localhost:8080

### 2. Start the Frontend
```bash
cd D:\PRACTICE\TELUSKO\SpringAISample\src\main\resources\static
setup.bat
```
OR manually:
```bash
npm install
npm start
```
✅ Frontend runs on: http://localhost:3000

### 3. Access the UI
Open your browser to: **http://localhost:3000**

---

## ✨ Features Implemented

### Backend
✅ **Exception Handling**
  - Empty message validation
  - Message length validation (max 5000 chars)
  - Type checking
  - HTTP status codes (400, 500)
  - Error responses with timestamps

✅ **RESTful Endpoints**
  - GET `/api/{message}` - Single message
  - GET `/api/stream/{message}` - Streaming response
  - POST `/api/chat` - JSON request (recommended)

✅ **CORS Support**
  - All endpoints accessible from React UI
  - Cross-origin requests enabled

### Frontend (React)
✅ **User Interface**
  - Beautiful gradient design
  - Input validation on client side
  - Clear error messages
  - Loading states with typing indicator
  - Message history display

✅ **Functionality**
  - Send messages via POST request
  - Real-time response display
  - Clear chat history
  - Auto-scroll to latest message
  - Responsive mobile design

✅ **Error Handling**
  - Network error handling
  - Server error display
  - Validation feedback
  - Retry capability

---

## 📊 How It Works

```
User Interface (React)
        ↓
    axios POST request with message
        ↓
Backend (Spring Boot)
        ↓
Validation checks
        ↓
ChatClient (Spring AI)
        ↓
Ollama Model
        ↓
Response generation
        ↓
Return JSON response
        ↓
Display in UI
```

---

## 🔧 Configuration

### Backend (application.properties)
```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=smallthinker:latest
spring.ai.ollama.chat.options.temperature=0.0
spring.ai.ollama.chat.options.max-tokens=100
```

### Frontend (Chatbot.jsx)
- API endpoint: `http://localhost:8080/api/chat`
- Max message length: 5000 characters
- Auto-retry on error: Yes
- Timeout: Default axios timeout

---

## 📁 Project Structure

```
SpringAISample/
├── src/
│   ├── main/
│   │   ├── java/com/springaisample/
│   │   │   ├── AIController.java          ← Backend API with exception handling
│   │   │   └── SpringAiSampleApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/                    ← React UI
│   │       │   ├── package.json
│   │       │   ├── setup.bat
│   │       │   ├── public/
│   │       │   │   └── index.html
│   │       │   └── src/
│   │       │       ├── index.jsx
│   │       │       ├── App.jsx
│   │       │       ├── Chatbot.jsx        ← Main chat component
│   │       │       ├── App.css
│   │       │       └── Chatbot.css        ← Beautiful styling
│   │       ├── templates/
│   │       └── static/
│   └── test/
├── pom.xml
├── HELP.md                                ← Updated with UI info
└── CHATBOT_SETUP.md                       ← This file
```

---

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use?**
```bash
# Set different port in application.properties
server.port=8081
```

**Ollama connection error?**
```bash
# Ensure Ollama is running
# Windows: Start Ollama application or run: ollama serve
# Mac/Linux: brew services start ollama
```

**Compilation error?**
```bash
mvn clean compile
```

### Frontend Issues

**npm: command not found**
- Install Node.js from https://nodejs.org/

**Port 3000 already in use?**
```bash
# On Windows PowerShell
$env:PORT=3001; npm start

# Or use different port
set PORT=3001 && npm start
```

**CORS error?**
- Ensure `@CrossOrigin(origins = "*")` is in AIController.java
- Restart Spring Boot backend

**Module not found?**
```bash
npm install
```

### API Issues

**"Failed to get response from chatbot"**
1. Check backend is running: http://localhost:8080/api/chat
2. Ensure Ollama is running
3. Check browser console for specific error

**Empty response**
- Check model is active in Ollama
- Verify message is not too long

---

## 🎓 How to Use

### From React UI
1. Type your question in the chat box
2. Click "📤 Send" or press Enter
3. Wait for AI response
4. Continue chatting

### From External App (Spring Integration)
```java
// Using RestTemplate or WebClient
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);

String json = "{\"message\": \"Hello\"}";
HttpEntity<String> request = new HttpEntity<>(json, headers);

ResponseEntity<String> response = restTemplate.postForEntity(
    "http://localhost:8080/api/chat",
    request,
    String.class
);
```

### From cURL
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"What is AI?\"}"
```

### From JavaScript/Frontend
```javascript
const response = await fetch('http://localhost:8080/api/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ message: 'Hello!' })
});
const data = await response.json();
console.log(data.response);
```

---

## 🚢 Deployment

### Build for Production

**Backend:**
```bash
mvn clean package
# Creates JAR in target/
java -jar target/SpringAISample-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd src/main/resources/static
npm run build
# Creates optimized build in build/
```

---

## 📝 API Response Examples

### Success Response
```json
{
  "response": "The response from the AI model",
  "timestamp": 1234567890
}
```

### Error Response
```json
{
  "message": "Message cannot be empty",
  "status": 400,
  "timestamp": 1234567890
}
```

---

## 🔐 Security Considerations

### Current Implementation
- ✅ Input validation
- ✅ Message length limits
- ✅ Error handling
- ❌ Authentication (not implemented)
- ❌ Rate limiting (not implemented)

### For Production
Add:
- JWT authentication
- Rate limiting per user
- HTTPS/TLS encryption
- Input sanitization
- CSRF protection
- API key management

---

## 📞 Support

For issues or questions:
1. Check HELP.md in project root
2. Review src/main/resources/static/README.md
3. Check browser console for errors
4. Verify all services are running

---

## ✅ Verification Checklist

- [ ] Ollama running with smallthinker:latest
- [ ] Spring Boot backend started (port 8080)
- [ ] React frontend started (port 3000)
- [ ] Browser can access http://localhost:3000
- [ ] Can send messages and get responses
- [ ] Error messages display properly
- [ ] No CORS errors in console

---

## 🎯 Next Steps

1. **Customize the Model**
   - Change model in application.properties
   - Adjust temperature for different responses
   - Modify system prompt in AIController.java

2. **Add Features**
   - User authentication
   - Chat history persistence
   - Streaming responses
   - Voice input/output

3. **Deploy**
   - Build production JAR
   - Host on cloud (AWS, Azure, GCP)
   - Use Docker for containerization

4. **Integrate**
   - Use as service in other apps
   - Embed in websites
   - Mobile app integration

---

**Congratulations! Your AI Chatbot is ready to use! 🎉**

