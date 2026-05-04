package com.example.app.services;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.List;

public class FaceRecognitionService {

    private final LBPHFaceRecognizer recognizer;
    private static final String MODEL_PATH = "face-model.xml";

    public FaceRecognitionService() {
        recognizer = LBPHFaceRecognizer.create();
    }

    // ========== LOAD MODEL ==========
    public void loadModel() {
        try {
            recognizer.read(MODEL_PATH);
            System.out.println("✔ Model chargé");
        } catch (Exception e) {
            System.out.println("⚠ Aucun model existant");
        }
    }

    // ========== SAVE MODEL ==========
    public void saveModel() {
        recognizer.save(MODEL_PATH);
        System.out.println("✔ Model sauvegardé");
    }

    // ========== PREPROCESS ==========
    private Mat preprocess(Mat img) {
        Mat gray = new Mat();
        opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);

        Mat resized = new Mat();
        opencv_imgproc.resize(gray, resized, new Size(200, 200));

        return resized;
    }

    // ========== TRAIN ==========
    public void train(List<Mat> faces, List<Integer> labels) {

        org.bytedeco.opencv.opencv_core.MatVector images =
                new org.bytedeco.opencv.opencv_core.MatVector(faces.size());

        org.bytedeco.opencv.opencv_core.Mat labelsMat =
                new org.bytedeco.opencv.opencv_core.Mat(faces.size(), 1,
                        org.bytedeco.opencv.global.opencv_core.CV_32SC1);

        for (int i = 0; i < faces.size(); i++) {
            Mat face = preprocess(faces.get(i));
            images.put(i, face);
            labelsMat.ptr(i).putInt(labels.get(i));
        }

        recognizer.train(images, labelsMat);

        saveModel(); // 🔥 CRUCIAL
    }

    // ========== PREDICT ==========
    public int recognizeFace(Mat face) {

        Mat processed = preprocess(face);

        int[] label = new int[1];
        double[] confidence = new double[1];

        recognizer.predict(processed, label, confidence);

        System.out.println("label=" + label[0] + " conf=" + confidence[0]);

        // 🔥 seuil important
        return (confidence[0] < 70) ? label[0] : -1;
    }
}