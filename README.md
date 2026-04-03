# Voice Trading Journal

A personal trading journal app that lets you speak your trade reasoning naturally and converts it into structured data.

## Features

- **Voice Recording** - Tap mic, speak naturally, done
- **AI Parsing** - Whisper + Llama extract trade details automatically
- **Review & Edit** - Always editable before saving
- **Flashcard Journal** - Tap to flip between summary and details
- **Date Navigation** - Browse your trading history by day

## Project Structure

```
voice-trading-journal/
├── backend/                    # Flask API
│   ├── app.py                 # Main Flask app
│   ├── routes/
│   │   ├── transcription.py   # POST /api/transcribe
│   │   └── parse.py           # POST /api/parse
│   └── requirements.txt
│
├── app/                       # Android app (Kotlin)
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/tradingjournal/
│   │       │   ├── model/        # Data models
│   │       │   ├── data/         # Room DB, API service
│   │       │   └── ui/           # Compose screens
│   │       └── res/
│   └── build.gradle.kts
│
└── README.md
```

## Setup Instructions

### 1. Backend Setup

```bash
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Configure API key
cp .env.example .env
# Edit .env and add your GROQ_API_KEY from https://console.groq.com

# Run server
flask run --host=0.0.0.0 --port=5000
```

### 2. Android App Setup

1. Open the `app/` folder in Android Studio
2. Wait for Gradle sync to complete
3. Set your Android SDK path in `local.properties`
4. Build and run on device/emulator

**API URL Configuration:**
- For emulator: `http://10.0.2.2:5000` (built into Android)
- For physical device: Use your computer's local IP address

## API Endpoints

### POST /api/transcribe
Upload audio for transcription.

```bash
curl -X POST http://localhost:5000/api/transcribe \
  -F "file=@recording.m4a"
```

### POST /api/parse
Parse trading text into structured JSON.

```bash
curl -X POST http://localhost:5000/api/parse \
  -H "Content-Type: application/json" \
  -d '{"text": "5 min MSS with FVG on 4H lower low"}'
```

### GET /api/health
Health check endpoint.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Mobile | Kotlin + Jetpack Compose |
| Backend | Flask (Python) |
| Speech-to-Text | Groq Whisper API |
| LLM Parsing | Groq Llama 3.3 70B |
| Database | Room (SQLite) |
| API Client | OkHttp + Gson |

## Future Enhancements

- Pattern detection across trades
- Trade frequency tracking
- CSV export
- Cloud sync

## License

Personal use only.
