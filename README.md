# Face Recognition Spring Boot (Windows + PostgreSQL)

This is a Spring Boot project that:
- Accepts webcam captures (client-side) or image uploads
- Detects faces using OpenCV Haar cascade on the backend
- Saves cropped face images into the local `faces/` folder
- Stores user metadata (name + image path) in PostgreSQL
- Provides REST endpoints for CRUD operations

## What I included
- Full Maven project
- Static frontend at `http://localhost:8080/` for webcam capture
- Face detection service that copies `haarcascade_frontalface_default.xml` from classpath to a temp file (works on Windows)

## Important setup steps (Windows)
1. Install PostgreSQL and create database:
   ```sql
   CREATE DATABASE face_recognition_db;
   ```
   Update `src/main/resources/application.properties` with your PostgreSQL username/password.

2. OpenCV native libs:
   - The Maven `org.openpnp:opencv` dependency bundles native libs for many platforms, but on Windows you might need to set `-Djava.library.path` when running if you get `UnsatisfiedLinkError`.
   - Alternatively download official OpenCV for Windows, locate `opencv_java*.dll`, and add its folder to your PATH, or run:
     ```
     mvn spring-boot:run -Djava.library.path="C:\path\to\opencv\build\java\x64"
     ```

3. Haar cascade:
   - Download `haarcascade_frontalface_default.xml` from OpenCV repository and place it in `src/main/resources/`.
   - Example download link: https://github.com/opencv/opencv/blob/master/data/haarcascades/haarcascade_frontalface_default.xml

4. Build and run:
   ```
   mvn clean package
   mvn spring-boot:run
   ```
   or run the generated jar.

5. Use the web UI:
   - Open `http://localhost:8080/` to capture a face with your webcam and save to DB.

## API endpoints
- `POST /api/users/upload` — multipart upload (`name`, `file`)
- `POST /api/users/capture` — JSON `{ name, imageBase64 }`
- `GET /api/users` — list users
- `PUT /api/users/{id}` — update name (JSON `{ name }`)
- `DELETE /api/users/{id}` — delete user & image file

## Notes
- This project *detects* faces and saves a cropped face image. It does not perform recognition (matching) yet.
- If you want recognition (LBPH or neural embeddings), I can add training and recognition endpoints next.

Enjoy! If you want, I can now:
- produce a ZIP file for download (included below), and
- optionally add LBPH recognition.

