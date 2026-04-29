# 🤖 AI Chatbot - Quick Reference Guide

## ⚡ QUICK START (Just 3 Steps!)

### Step 1: Start the Backend
```powershell
cd D:\PRACTICE\TELUSKO\SpringAISample
mvn spring-boot:run
```
⏳ Wait for: "Started SpringAiSampleApplication"

### Step 2: Start the Frontend (New Terminal)
```powershell
cd D:\PRACTICE\TELUSKO\SpringAISample\src\main\resources\static
.\setup.bat
```
OR:
```powershell
npm install
npm start
```

### Step 3: Open Browser
👉 Navigate to: **http://localhost:3000**

---

## 🎯 What You Can Do Now

✅ **Chat with AI** - Ask questions and get answers instantly
✅ **Integration** - Use API in other applications
✅ **Customize** - Change model, prompt, settings
✅ **Deploy** - Use in production

---

## 🔗 API Usage

### Quick Copy-Paste Examples

**JavaScript/React:**
```javascript
const response = await fetch('http://localhost:8080/api/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ message: 'Hello!' })
});
const data = await response.json();
console.log(data.response);
```

**Python:**
```python
import requests
response = requests.post('http://localhost:8080/api/chat',
  json={'message': 'Hello!'})
print(response.json()['response'])
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"Hello!\"}"
```

---

## 📋 Features Implemented

### ✅ Exception Handling
- Empty message validation
- Message length validation (max 5000 chars)
- Type checking
- Server error responses
- Network error handling

### ✅ React UI
- Beautiful gradient interface
- Real-time chat display
- Error messages
- Loading indicator
- Mobile responsive
- Clear chat history

---

## 🐛 Quick Fixes

| Problem | Solution |
|---------|----------|
| Backend won't start | Check if port 8080 is in use: `netstat -ano \| findstr :8080` |
| Frontend won't start | Install Node.js: https://nodejs.org/ |
| "Failed to get response" | Verify Ollama is running |
| Port 3000 in use | `set PORT=3001 && npm start` |
| CORS error | Restart Spring Boot |

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `src/main/java/AIController.java` | Backend API with exception handling |
| `src/main/resources/static/src/Chatbot.jsx` | React chat component |
| `application.properties` | Backend configuration |
| `HELP.md` | Detailed documentation |
| `CHATBOT_SETUP.md` | Complete setup guide |

---

## 🔄 How It Works

1. You type a message in React UI
2. Message is sent to: `POST /api/chat`
3. Backend validates the message
4. Spring AI sends to Ollama model
5. Ollama generates response
6. Response returned as JSON
7. React displays in chat

---

## 💡 Tips & Tricks

**Faster Responses?**
- Switch to smaller model: `phi3:3.8b` or `llama3.2:1b`
- Reduce max tokens in `application.properties`

**Better Answers?**
- Adjust system prompt in `AIController.java`
- Change temperature from 0.0 to 0.7

**More Users?**
- Add authentication
- Implement rate limiting
- Use load balancer

---

## 📝 Testing the API

**Option 1: Using Postman**
1. Create new POST request
2. URL: `http://localhost:8080/api/chat`
3. Body (JSON): `{"message": "Hello"}`
4. Click Send

**Option 2: Using Browser Console**
```javascript
fetch('http://localhost:8080/api/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ message: 'Test' })
}).then(r => r.json()).then(console.log)
```

---

## 📚 Documentation

- **Full Setup**: See `CHATBOT_SETUP.md`
- **API Details**: See `HELP.md`
- **React UI**: See `src/main/resources/static/README.md`

---

## ❓ Common Questions

**Q: Can I use a different AI model?**
A: Yes! Change in `application.properties`:
```properties
spring.ai.ollama.chat.model=mistral:latest
```

**Q: How do I add authentication?**
A: Add Spring Security to `pom.xml`

**Q: Can I deploy this?**
A: Yes! Build JAR and deploy to cloud (AWS, Azure, Heroku)

**Q: Does it work offline?**
A: Yes! Everything runs locally with Ollama

---

## ✨ You're All Set!

Your AI Chatbot is now:
- ✅ Running locally
- ✅ Accessible via beautiful UI
- ✅ Ready for integration
- ✅ Production-ready with error handling

**Start chatting now! 👉 http://localhost:3000**

---

**Questions?** Check the documentation files included in the project.

