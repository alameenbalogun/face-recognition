package com.example.facerec.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class FaceService {

    private CascadeClassifier faceDetector;
    private final String facesDir = "faces";

    @PostConstruct
    public void init() {
        // load native lib
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Ensure faces dir exists
        try {
            Files.createDirectories(Paths.get(facesDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create faces directory", e);
        }

        // Copy cascade from resources to temp file to ensure CascadeClassifier can load it (works on Windows)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("haarcascade_frontalface_default.xml")) {
            if (is == null) {
                throw new RuntimeException("Cascade resource not found in classpath. Place haarcascade_frontalface_default.xml into src/main/resources/");
            }
            Path tmp = Files.createTempFile("cascade-", ".xml");
            Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
            faceDetector = new CascadeClassifier(tmp.toAbsolutePath().toString());
            if (faceDetector.empty()) {
                throw new RuntimeException("Failed to load cascade classifier from temp file: " + tmp.toAbsolutePath());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String detectCropAndSave(byte[] imageBytes, String username) throws IOException {
        Path tmp = Files.createTempFile("upload-", ".jpg");
        Files.write(tmp, imageBytes);

        Mat image = Imgcodecs.imread(tmp.toAbsolutePath().toString());
        if (image.empty()) {
            Files.deleteIfExists(tmp);
            return null;
        }

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(image, faces);

        if (faces.empty()) {
            Files.deleteIfExists(tmp);
            return null;
        }

        Rect rect = faces.toArray()[0];
        Mat face = new Mat(image, rect);
        Imgproc.resize(face, face, new Size(200, 200));

        String filename = username.replaceAll("\\s+","_") + "_" + UUID.randomUUID() + ".jpg";
        Path out = Paths.get(facesDir, filename);
        Imgcodecs.imwrite(out.toAbsolutePath().toString(), face);

        Files.deleteIfExists(tmp);
        return out.toString().replace("\\","/");
    }

    public String detectCropAndSaveFromBase64(String base64, String username) throws IOException {
        String b64 = base64;
        if (b64.contains(",")) b64 = b64.split(",")[1];
        byte[] decoded = Base64.getDecoder().decode(b64);
        return detectCropAndSave(decoded, username);
    }

    public boolean deleteImage(String imagePath) {
        try {
            Path p = Paths.get(imagePath);
            return Files.deleteIfExists(p);
        } catch (Exception e) {
            return false;
        }
    }
}
