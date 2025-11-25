package com.jargo.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * FirebaseManager - Singleton class để quản lý Firebase instances
 */
public class FirebaseManager {
    
    private static FirebaseManager instance;
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    
    // Phương thức khởi tạo riêng (mẫu Singleton)
    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    
    // Lấy thể hiện duy nhất (mẫu Singleton)
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }
    
    // Lấy tham chiếu cơ sở dữ liệu
    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }
    
    public DatabaseReference getDatabaseReference(String path) {
        return database.getReference(path);
    }
    
    // Lấy thể hiện xác thực
    public FirebaseAuth getAuth() {
        return auth;
    }
    
    // Lấy thông tin người dùng hiện tại
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    // Kiểm tra người dùng đã đăng nhập hay chưa
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // Lấy mã số người dùng
    public String getUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    // Lấy tham chiếu kho lưu trữ
    public StorageReference getStorageReference() {
        return storage.getReference();
    }
    
    public StorageReference getStorageReference(String path) {
        return storage.getReference(path);
    }
    
    // Tham chiếu cơ sở dữ liệu theo loại
    public DatabaseReference getUsersRef() {
        return getDatabaseReference(Constants.DB_USERS);
    }
    
    public DatabaseReference getFieldsRef() {
        return getDatabaseReference(Constants.DB_FIELDS);
    }
    
    public DatabaseReference getTopicsRef() {
        return getDatabaseReference(Constants.DB_TOPICS);
    }
    
    public DatabaseReference getLessonsRef() {
        return getDatabaseReference(Constants.DB_LESSONS);
    }
    
    public DatabaseReference getQuizzesRef() {
        return getDatabaseReference(Constants.DB_QUIZZES);
    }
    
    // Tham chiếu riêng cho người dùng
    public DatabaseReference getUserRef(String userId) {
        return getUsersRef().child(userId);
    }
    
    public DatabaseReference getUserProgressRef(String userId) {
        return getUserRef(userId).child(Constants.DB_PROGRESS);
    }
    
    // Phương thức đăng xuất
    public void signOut() {
        auth.signOut();
    }
}
