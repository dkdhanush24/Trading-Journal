from flask import Blueprint, request, jsonify
from groq import Groq
import os

parse_bp = Blueprint('parse', __name__)

SYSTEM_PROMPT = """You are a trading journal parser. Convert spoken or written trade descriptions into STRICT JSON format.

CONVERSION RULES:
1. entry_timeframe: Extract the timeframe entered (5m, 15m, 1h, 4h, Daily, etc.)
2. htf_bias: Determine higher timeframe bias from context (Bullish, Bearish, Neutral)
3. setup: Extract the specific setup type (Breakout, Pullback, MSS, FVG, OTE, ICT Killzone, etc.)
4. confluences: Extract all supporting factors (FVG, Order Blocks, Liquidity Sweeps, etc.)
5. summary: Create a 1-2 sentence professional summary of the trade

CRITICAL RULES:
- Use ONLY the information provided. Do NOT assume or invent data.
- If a field cannot be determined, use null
- confluences must be an array (can be empty array [])
- Output ONLY valid JSON, no markdown, no explanations, no apologies

EXAMPLE:
Input: "5 min broke daily low, MSS plus FVG, expecting lower on 4H lower low"
Output: {"entry_timeframe":"5m","htf_bias":"Bearish","setup":"MSS","confluences":["FVG","Daily Low Break","Lower Low Structure"],"summary":"Bearish continuation setup on 5m with MSS and FVG confirmation."}

"""

@parse_bp.route('/parse', methods=['POST'])
def parse():
    data = request.get_json()
    
    if not data or 'text' not in data:
        return jsonify({'error': 'No text provided'}), 400
    
    text = data['text']
    
    if not text or len(text.strip()) < 3:
        return jsonify({'error': 'Text too short to parse'}), 400
    
    try:
        api_key = os.getenv('GROQ_API_KEY')
        if not api_key:
            return jsonify({'error': 'GROQ_API_KEY not configured'}), 500
        
        client = Groq(api_key=api_key)
        
        response = client.chat.completions.create(
            model='llama-3.3-70b-versatile',
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": text}
            ],
            temperature=0.1,
            max_tokens=500
        )
        
        result_text = response.choices[0].message.content.strip()
        
        import json
        result_text = result_text.strip()
        if result_text.startswith('```json'):
            result_text = result_text[7:]
        if result_text.startswith('```'):
            result_text = result_text[3:]
        if result_text.endswith('```'):
            result_text = result_text[:-3]
        result_text = result_text.strip()
        
        parsed = json.loads(result_text)
        
        return jsonify(parsed)
        
    except json.JSONDecodeError as e:
        return jsonify({
            'entry_timeframe': None,
            'htf_bias': None,
            'setup': None,
            'confluences': [],
            'summary': text,
            'parse_error': 'Could not parse structured data from text'
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500
