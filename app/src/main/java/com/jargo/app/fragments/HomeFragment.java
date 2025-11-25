package com.jargo.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.R;
import com.jargo.app.activities.LessonListActivity;
import com.jargo.app.adapters.TopicAdapter;
import com.jargo.app.models.Topic;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment - Màn hình chính hiển thị danh sách topics
 */
public class HomeFragment extends Fragment implements TopicAdapter.OnTopicClickListener {

    private RecyclerView recyclerViewTopics;
    private TopicAdapter topicAdapter;
    private TextView tvFieldName;
    private TextView tvUserName;
    private TextView tvXP;
    private TextView tvStreak;
    private View progressBar;
    private View emptyState;

    private SharedPrefsManager prefsManager;
    private FirebaseManager firebaseManager;
    private String currentField;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo
        prefsManager = SharedPrefsManager.getInstance(requireContext());
        firebaseManager = FirebaseManager.getInstance();
        currentField = prefsManager.getUserField();

        // Bind views
        tvFieldName = view.findViewById(R.id.tvFieldName);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvXP = view.findViewById(R.id.tvXP);
        tvStreak = view.findViewById(R.id.tvStreak);
        recyclerViewTopics = view.findViewById(R.id.recyclerViewTopics);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);

        // Setup RecyclerView
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(requireContext()));
        topicAdapter = new TopicAdapter(new ArrayList<>(), this);
        recyclerViewTopics.setAdapter(topicAdapter);

        // Load data
        loadUserInfo();
        loadTopics();

        return view;
    }

    /**
     * Load thông tin user từ SharedPreferences
     */
    private void loadUserInfo() {
        String userName = prefsManager.getUserName();
        String fieldName = getFieldDisplayName(currentField);

        tvUserName.setText(userName != null ? userName : "User");
        tvFieldName.setText(fieldName);
        tvXP.setText("0"); // TODO: Load từ Firebase
        tvStreak.setText("0"); // TODO: Load từ Firebase
    }

    /**
     * Load danh sách topics từ Firebase theo field
     */
    private void loadTopics() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_TOPICS)
                .orderByChild("fieldId")
                .equalTo(currentField)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Topic> topics = new ArrayList<>();

                        for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                            Topic topic = topicSnapshot.getValue(Topic.class);
                            if (topic != null) {
                                topics.add(topic);
                            }
                        }

                        // Sort theo orderIndex
                        topics.sort((t1, t2) -> Integer.compare(t1.getOrderIndex(), t2.getOrderIndex()));

                        // Update UI
                        progressBar.setVisibility(View.GONE);
                        if (topics.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            topicAdapter.updateTopics(topics);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),
                                "Lỗi: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Lấy tên hiển thị của field
     */
    private String getFieldDisplayName(String fieldId) {
        switch (fieldId) {
            case Constants.FIELD_IT:
                return getString(R.string.field_it);
            case Constants.FIELD_MEDICAL:
                return getString(R.string.field_medical);
            case Constants.FIELD_ECONOMICS:
                return getString(R.string.field_economics);
            default:
                return "";
        }
    }

    @Override
    public void onTopicClick(Topic topic) {
        if (topic.isLocked()) {
            Toast.makeText(requireContext(),
                    "Chủ đề này đang bị khóa. Hoàn thành chủ đề trước đó để mở khóa!",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Mở LessonListActivity
            Intent intent = new Intent(requireContext(), LessonListActivity.class);
            intent.putExtra(Constants.EXTRA_TOPIC_ID, topic.getTopicId());
            intent.putExtra(Constants.EXTRA_TOPIC_NAME, topic.getName());
            startActivity(intent);
        }
    }
}
