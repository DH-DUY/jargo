package com.jargo.app.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.DebugChallenge;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DebugChallengeRepository - Quản lý data cho Debug Challenge
 */
public class DebugChallengeRepository {

    private static final String TAG = "DebugChallengeRepo";
    private static DebugChallengeRepository instance;
    private final DatabaseReference databaseRef;

    private DebugChallengeRepository() {
        databaseRef = FirebaseManager.getInstance().getDatabaseReference();
    }

    public static synchronized DebugChallengeRepository getInstance() {
        if (instance == null) {
            instance = new DebugChallengeRepository();
        }
        return instance;
    }

    /**
     * Load all debug challenges
     */
    public LiveData<List<DebugChallenge>> getAllChallenges() {
        MutableLiveData<List<DebugChallenge>> challengesLiveData = new MutableLiveData<>();

        databaseRef.child(Constants.DB_DEBUG_CHALLENGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<DebugChallenge> challenges = new ArrayList<>();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            DebugChallenge challenge = childSnapshot.getValue(DebugChallenge.class);
                            if (challenge != null) {
                                challenges.add(challenge);
                            }
                        }
                        // Sort by orderIndex
                        Collections.sort(challenges, (c1, c2) -> 
                                Integer.compare(c1.getOrderIndex(), c2.getOrderIndex()));
                        
                        Log.d(TAG, "Loaded " + challenges.size() + " challenges");
                        challengesLiveData.setValue(challenges);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading challenges: " + error.getMessage());
                        challengesLiveData.setValue(new ArrayList<>());
                    }
                });

        return challengesLiveData;
    }

    /**
     * Load challenges by topic
     */
    public LiveData<List<DebugChallenge>> getChallengesByTopic(String topicId) {
        MutableLiveData<List<DebugChallenge>> challengesLiveData = new MutableLiveData<>();

        Query query = databaseRef.child(Constants.DB_DEBUG_CHALLENGES)
                .orderByChild("topicId")
                .equalTo(topicId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DebugChallenge> challenges = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    DebugChallenge challenge = childSnapshot.getValue(DebugChallenge.class);
                    if (challenge != null && !challenge.isLocked()) {
                        challenges.add(challenge);
                    }
                }
                // Sort by orderIndex
                Collections.sort(challenges, (c1, c2) -> 
                        Integer.compare(c1.getOrderIndex(), c2.getOrderIndex()));
                
                Log.d(TAG, "Loaded " + challenges.size() + " challenges for topic: " + topicId);
                challengesLiveData.setValue(challenges);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading challenges by topic: " + error.getMessage());
                challengesLiveData.setValue(new ArrayList<>());
            }
        });

        return challengesLiveData;
    }

    /**
     * Load challenges by difficulty
     */
    public LiveData<List<DebugChallenge>> getChallengesByDifficulty(String difficulty) {
        MutableLiveData<List<DebugChallenge>> challengesLiveData = new MutableLiveData<>();

        Query query = databaseRef.child(Constants.DB_DEBUG_CHALLENGES)
                .orderByChild("difficulty")
                .equalTo(difficulty);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DebugChallenge> challenges = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    DebugChallenge challenge = childSnapshot.getValue(DebugChallenge.class);
                    if (challenge != null && !challenge.isLocked()) {
                        challenges.add(challenge);
                    }
                }
                Collections.sort(challenges, (c1, c2) -> 
                        Integer.compare(c1.getOrderIndex(), c2.getOrderIndex()));
                
                challengesLiveData.setValue(challenges);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading challenges by difficulty: " + error.getMessage());
                challengesLiveData.setValue(new ArrayList<>());
            }
        });

        return challengesLiveData;
    }

    /**
     * Get single challenge by ID
     */
    public LiveData<DebugChallenge> getChallengeById(String challengeId) {
        MutableLiveData<DebugChallenge> challengeLiveData = new MutableLiveData<>();

        databaseRef.child(Constants.DB_DEBUG_CHALLENGES)
                .child(challengeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DebugChallenge challenge = snapshot.getValue(DebugChallenge.class);
                        challengeLiveData.setValue(challenge);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading challenge: " + error.getMessage());
                        challengeLiveData.setValue(null);
                    }
                });

        return challengeLiveData;
    }

    /**
     * Save user's challenge completion
     */
    public void saveChallengeCompletion(String userId, String challengeId, boolean isCorrect, int xpEarned) {
        if (userId == null || challengeId == null) return;

        long timestamp = System.currentTimeMillis();

        databaseRef.child(Constants.DB_USER_PROGRESS)
                .child(userId)
                .child("debugChallenges")
                .child(challengeId)
                .child("completed")
                .setValue(true);

        databaseRef.child(Constants.DB_USER_PROGRESS)
                .child(userId)
                .child("debugChallenges")
                .child(challengeId)
                .child("isCorrect")
                .setValue(isCorrect);

        databaseRef.child(Constants.DB_USER_PROGRESS)
                .child(userId)
                .child("debugChallenges")
                .child(challengeId)
                .child("xpEarned")
                .setValue(xpEarned);

        databaseRef.child(Constants.DB_USER_PROGRESS)
                .child(userId)
                .child("debugChallenges")
                .child(challengeId)
                .child("completedAt")
                .setValue(timestamp);

        Log.d(TAG, "Saved challenge completion: " + challengeId + ", correct: " + isCorrect);
    }

    /**
     * Update user's total XP
     */
    public void updateUserXP(String userId, int xpToAdd, XPUpdateCallback callback) {
        if (userId == null) {
            if (callback != null) callback.onError("User not logged in");
            return;
        }

        DatabaseReference userRef = databaseRef.child(Constants.DB_USERS).child(userId);

        userRef.child("totalXP").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentXP = 0;
                if (snapshot.exists() && snapshot.getValue() != null) {
                    currentXP = snapshot.getValue(Integer.class);
                }

                final int currentXpValue = currentXP;
                final int newXpValue = currentXP + xpToAdd;
                userRef.child("totalXP").setValue(newXpValue)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "XP updated: " + currentXpValue + " -> " + newXpValue);
                            if (callback != null) callback.onSuccess(newXpValue);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update XP: " + e.getMessage());
                            if (callback != null) callback.onError(e.getMessage());
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error reading XP: " + error.getMessage());
                if (callback != null) callback.onError(error.getMessage());
            }
        });
    }

    /**
     * Callback for XP update
     */
    public interface XPUpdateCallback {
        void onSuccess(int newTotalXP);
        void onError(String error);
    }
}
