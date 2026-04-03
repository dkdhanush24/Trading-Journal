from flask import Flask, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import os

load_dotenv()

def create_app():
    app = Flask(__name__)
    CORS(app)
    
    app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024
    
    from routes.transcription import transcription_bp
    from routes.parse import parse_bp
    
    app.register_blueprint(transcription_bp, url_prefix='/api')
    app.register_blueprint(parse_bp, url_prefix='/api')
    
    @app.route('/api/health', methods=['GET'])
    def health():
        return jsonify({
            'status': 'healthy',
            'service': 'Voice Trading Journal API'
        })
    
    return app

app = create_app()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
