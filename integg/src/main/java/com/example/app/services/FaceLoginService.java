package com.example.app.services;

import com.example.app.dao.UserDAO;
import com.example.app.entities.User;
import org.bytedeco.opencv.opencv_core.Mat;

public class FaceLoginService {

    private final FaceRecognitionService recognition;
    private final UserDAO userDAO;

    public FaceLoginService() {
        recognition = new FaceRecognitionService();
        recognition.loadModel();
        userDAO = new UserDAO();
    }
    public User authenticate(Mat face) {
    try {
        int label = recognition.recognizeFace(face);

        System.out.println("FACE LABEL = " + label);

        if (label == -1) {
            return null;
        }

        User user = userDAO.findById(label);

        // 🔥 IMPORTANT : user existe mais pas de face enregistrée
        if (user == null) {
            return null;
        }

        if (!user.isFaceEnabled()) {
            System.out.println("❌ Face non activée pour user " + user.getUsername());
            return null;
        }

        return user;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

    }