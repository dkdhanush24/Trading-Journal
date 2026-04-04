# Voice Trading Journal

> Speak your trade reasoning. AI structures it. Review it as flashcards.

A personal Android app built to eliminate the friction of logging trades manually. Record your analysis by voice, let Whisper transcribe it and Llama 3 extract the structure, then review past trades as flashcard-style journal entries.

Built for personal use — because typing out trade notes after every session is tedious and I actually needed this.

---

## Screenshots

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/6a941b6f-649a-4a1d-a08f-30321795d3b6" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/e4fb0072-bafe-45da-97b8-d2b827a47ef6" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/0c4a640f-01bb-4424-acd0-a3a28d69d7cf" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/8a30961b-d4af-467a-b0d1-910c81fd0ed7" width="200"/></td>
  </tr>
  <tr>
    <td align="center">Home</td>
    <td align="center">Voice Recording</td>
    <td align="center">Parsed Result</td>
    <td align="center">Journal Flashcard</td>
  </tr>
</table>

---

## How It Works

```
Tap mic → Speak trade reasoning
        ↓
Groq Whisper transcribes audio → plain text
        ↓
Llama 3 70B extracts structured trade data → JSON
        ↓
Saved locally as flashcard journal entry
        ↓
Review past trades anytime
```

---

## What Gets Extracted

From a single voice note, Llama 3 pulls out:

- **Timeframe** — what timeframe the setup was on
- **High Timeframe Bias** — directional bias (bullish/bearish)
- **Setup Elements** — order blocks, FVGs, liquidity levels etc.
- **Confluences** — supporting factors for the trade
- **Full Transcription** — raw text always saved alongside structured data

---

## Tech Stack

### Android App
- **Language** — Kotlin
- **UI** — Jetpack Compose (Material 3)
- **Architecture** — MVVM (ViewModel + Coroutines)
- **Local Storage** — Room Database (SQLite)
- **Networking** — OkHttp + Gson
- **Target SDK** — Android API Level 35

### Backend
- **Framework** — Python Flask
- **Transcription** — Groq Whisper
- **Parsing** — Groq Llama 3 70B (structured JSON extraction)
- **CORS** — flask-cors

---

## Project Structure

```
voice-trading-journal/
├── app/                        # Android application (Kotlin)
│   └── src/main/java/
│       ├── model/              # Trade, Strategy, API models
│       ├── data/               # Room DB, DAOs, Repository, ApiService
│       └── ui/
│           ├── screens/        # HomeScreen, RecordScreen, JournalScreen, ReviewScreen
│           ├── components/     # FlashCard component
│           └── viewmodel/      # ViewModels per screen
│
└── backend/                    # Python Flask API
    ├── app.py                  # Main Flask app
    └── .env                    # GROQ_API_KEY (not committed)
```

---

## API Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/api/transcribe` | POST | Upload audio → returns transcription text |
| `/api/parse` | POST | Send transcription → returns structured JSON |
| `/api/health` | GET | Health check |

---

## Setup

### Backend
```bash
cd backend
pip install -r requirements.txt

# Create .env file
echo "GROQ_API_KEY=your_key_here" > .env

python app.py
```

### Android App
- Open `app/` in Android Studio
- Update backend URL in `ApiService.kt`
  - Emulator: `http://10.0.2.2:5000`
  - Physical device: `http://your-local-ip:5000`
- Build and run

---

## Why I Built This

I trade XAUUSD and keeping a proper journal is critical for improving. But typing detailed notes after every trade — bias, setup, confluences, reasoning — is slow enough that I'd skip it.

This app lets me just talk. The AI handles the structure. I handle the trading.

---

## Status

| Feature | Status |
|---|---|
| Voice recording + transcription | ✅ Complete |
| Llama 3 structured extraction | ✅ Complete |
| Flashcard journal view | ✅ Complete |
| Local Room DB persistence | ✅ Complete |


---

**Author: Dhanush S**
*Personal project — built to solve a real problem.*
