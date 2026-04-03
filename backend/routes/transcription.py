from flask import Blueprint, request, jsonify
from groq import Groq
import os
from werkzeug.utils import secure_filename

transcription_bp = Blueprint('transcription', __name__)

ALLOWED_EXTENSIONS = {'m4a', 'wav', 'mp3', 'webm', 'ogg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@transcription_bp.route('/transcribe', methods=['POST'])
def transcribe():
    if 'file' not in request.files:
        return jsonify({'error': 'No file provided'}), 400
    
    file = request.files['file']
    
    if file.filename == '':
        return jsonify({'error': 'No file selected'}), 400
    
    if not allowed_file(file.filename):
        return jsonify({'error': 'Invalid file type'}), 400
    
    try:
        api_key = os.getenv('GROQ_API_KEY')
        if not api_key:
            return jsonify({'error': 'GROQ_API_KEY not configured'}), 500
        
        client = Groq(api_key=api_key)
        
        audio_bytes = file.read()
        file_name = secure_filename(file.filename) or 'audio.m4a'
        
        transcription = client.audio.transcriptions.create(
            file=(file_name, audio_bytes),
            model='whisper-large-v3',
            response_format='text',
            language='en'
        )
        
        # response_format='text' returns a string directly
        text = transcription if isinstance(transcription, str) else transcription.text
        
        return jsonify({
            'text': text
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
