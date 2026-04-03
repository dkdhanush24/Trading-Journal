# Voice Trading Journal Backend

## Setup
```bash
cd backend
pip install -r requirements.txt
cp .env.example .env
# Edit .env and add your GROQ_API_KEY
```

## Run
```bash
flask run --host=0.0.0.0 --port=5000
```

## API Endpoints

### POST /api/transcribe
Upload audio file for transcription.
- Content-Type: multipart/form-data
- Body: file (audio/m4a, wav, mp3)
- Response: `{"text": "transcribed text"}`

### POST /api/parse
Parse trading text into structured data.
- Content-Type: application/json
- Body: `{"text": "raw trading description"}`
- Response:
```json
{
  "entry_timeframe": "5m",
  "htf_bias": "Bearish",
  "setup": "MSS",
  "confluences": ["FVG", "Daily Low Break"],
  "summary": "Clean breakdown"
}
```

### GET /api/health
Health check endpoint.
