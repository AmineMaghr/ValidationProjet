from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import numpy as np
import base64
import json
import os

app = Flask(__name__)
CORS(app)

KNOWN_FACES_FILE = "known_faces.json"
FACE_ENCODINGS = {}

def load_known_faces():
    global FACE_ENCODINGS
    if os.path.exists(KNOWN_FACES_FILE):
        with open(KNOWN_FACES_FILE, 'r') as f:
            FACE_ENCODINGS = json.load(f)
        print(f"✓ {len(FACE_ENCODINGS)} visages chargés")

def save_known_faces():
    with open(KNOWN_FACES_FILE, 'w') as f:
        json.dump(FACE_ENCODINGS, f)

def decode_image(base64_string):
    if ',' in base64_string:
        base64_string = base64_string.split(',')[1]
    img_data = base64.b64decode(base64_string)
    nparr = np.frombuffer(img_data, np.uint8)
    return cv2.imdecode(nparr, cv2.IMREAD_COLOR)

def extract_face_features(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    faces = face_cascade.detectMultiScale(gray, 1.1, 5)
    
    if len(faces) == 0:
        return None, None
    
    x, y, w, h = faces[0]
    face = gray[y:y+h, x:x+w]
    face_resized = cv2.resize(face, (100, 100))
    descriptor = face_resized.flatten() / 255.0
    return descriptor.tolist(), (x, y, w, h)

@app.route('/api/face/register', methods=['POST'])
def register_face():
    data = request.json
    user_id = data.get('userId')
    username = data.get('username')
    images = data.get('images', [])
    
    descriptors = []
    for img_base64 in images:
        image = decode_image(img_base64)
        descriptor, _ = extract_face_features(image)
        if descriptor:
            descriptors.append(descriptor)
    
    if len(descriptors) < 3:
        return jsonify({'success': False, 'message': f'{len(descriptors)}/3 captures'})
    
    avg_descriptor = np.mean(descriptors, axis=0).tolist()
    FACE_ENCODINGS[str(user_id)] = {'username': username, 'descriptor': avg_descriptor}
    save_known_faces()
    
    return jsonify({'success': True, 'message': f'Visage enregistré pour {username}'})

@app.route('/api/face/login', methods=['POST'])
def login_face():
    data = request.json
    image = decode_image(data.get('image'))
    descriptor, _ = extract_face_features(image)
    
    if not descriptor:
        return jsonify({'success': False, 'faceDetected': False, 'message': 'Aucun visage'})
    
    best_match = None
    best_distance = 999.0
    
    for user_id, info in FACE_ENCODINGS.items():
        distance = np.linalg.norm(np.array(descriptor) - np.array(info['descriptor']))
        if distance < best_distance:
            best_distance = distance
            best_match = user_id
    
    if best_match and best_distance < 0.6:
        return jsonify({
            'success': True,
            'faceDetected': True,
            'recognized': True,
            'userId': int(best_match),
            'username': FACE_ENCODINGS[best_match]['username']
        })
    
    return jsonify({'success': True, 'faceDetected': True, 'recognized': False})

@app.route('/api/face/detect', methods=['POST'])
def detect_face():
    data = request.json
    image = decode_image(data.get('image'))
    _, face_rect = extract_face_features(image)
    return jsonify({'success': True, 'faceDetected': face_rect is not None})

@app.route('/api/face/has-face/<int:user_id>', methods=['GET'])
def has_face(user_id):
    return jsonify({'success': True, 'hasFace': str(user_id) in FACE_ENCODINGS})

if __name__ == '__main__':
    load_known_faces()
    print("\n🚀 Serveur démarré sur http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True)