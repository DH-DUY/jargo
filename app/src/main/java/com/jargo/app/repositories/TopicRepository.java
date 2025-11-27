package com.jargo.app.repositories;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.Topic;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * TopicRepository - Quản lý dữ liệu topics từ Firebase
 */
public class TopicRepository {

    private static TopicRepository instance;
    private final FirebaseManager firebaseManager;

    private TopicRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized TopicRepository getInstance() {
        if (instance == null) {
            instance = new TopicRepository();
        }
        return instance;
    }

    /**
     * Load tất cả topics
     */
    public void getAllTopics(TopicCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_TOPICS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Topic> topics = new ArrayList<>();
                        
                        for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                            Topic topic = topicSnapshot.getValue(Topic.class);
                            if (topic != null) {
                                topics.add(topic);
                            }
                        }
                        
                        callback.onSuccess(topics);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load topics theo field
     */
    public void getTopicsByField(String fieldId, TopicCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_TOPICS)
                .orderByChild("fieldId")
                .equalTo(fieldId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Topic> topics = new ArrayList<>();
                        
                        for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                            Topic topic = topicSnapshot.getValue(Topic.class);
                            if (topic != null) {
                                topics.add(topic);
                            }
                        }
                        
                        callback.onSuccess(topics);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load 1 topic theo ID
     */
    public void getTopicById(String topicId, SingleTopicCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_TOPICS)
                .child(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Topic topic = snapshot.getValue(Topic.class);
                        if (topic != null) {
                            callback.onSuccess(topic);
                        } else {
                            callback.onError("Topic not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Callback cho danh sách topics
     */
    public interface TopicCallback {
        void onSuccess(List<Topic> topics);
        void onError(String error);
    }

    /**
     * Callback cho 1 topic
     */
    public interface SingleTopicCallback {
        void onSuccess(Topic topic);
        void onError(String error);
    }
}
