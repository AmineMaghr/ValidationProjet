package com.example.app.models;

public class FaceDetectionResult {
    private boolean success;
    private boolean faceDetected;
    private String message;
    private float[] descriptor;
    private int captureCount;
    private double confidence;
    private int userId;
    private Object recognizedUser;
    
    public FaceDetectionResult() {
        this.success = false;
        this.faceDetected = false;
        this.captureCount = 0;
        this.confidence = 0;
        this.userId = -1;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public boolean isFaceDetected() { return faceDetected; }
    public void setFaceDetected(boolean faceDetected) { this.faceDetected = faceDetected; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public float[] getDescriptor() { return descriptor; }
    public void setDescriptor(float[] descriptor) { this.descriptor = descriptor; }
    
    public int getCaptureCount() { return captureCount; }
    public void setCaptureCount(int captureCount) { this.captureCount = captureCount; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Object getRecognizedUser() { return recognizedUser; }
    public void setRecognizedUser(Object recognizedUser) { this.recognizedUser = recognizedUser; }
}