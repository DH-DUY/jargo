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
    
    // Private constructor
    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    
    // Get singleton instance
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }
    
    // Get Database Reference
    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }
    
    public DatabaseReference getDatabaseReference(String path) {
        return database.getReference(path);
    }
    
    // Get Auth instance
    public FirebaseAuth getAuth() {
        return auth;
    }
    
    // Get current user
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // Get user ID
    public String getUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    // Get Storage Reference
    public StorageReference getStorageReference() {
        return storage.getReference();
    }
    
    public StorageReference getStorageReference(String path) {
        return storage.getReference(path);
    }
    
    // Database references by type
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
    
    // User-specific references
    public DatabaseReference getUserRef(String userId) {
        return getUsersRef().child(userId);
    }
    
    public DatabaseReference getUserProgressRef(String userId) {
        return getUserRef(userId).child(Constants.DB_PROGRESS);
    }
    
    // Sign out
    public void signOut() {
        auth.signOut();
    }
}
