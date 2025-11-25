package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.R;
import com.jargo.app.adapters.LessonAdapter;
import com.jargo.app.models.Lesson;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * LessonListActivity - Hiển thị danh sách lessons trong 1 topic
 */
public class LessonListActivity extends AppCompatActivity implements LessonAdapter.OnLessonClickListener {

    private LessonAdapter lessonAdapter;
    private ProgressBar progressBar;
    private View emptyState;

    private FirebaseManager firebaseManager;
    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);

        firebaseManager = FirebaseManager.getInstance();

        // Get data from Intent
        topicId = getIntent().getStringExtra(Constants.EXTRA_TOPIC_ID);
        String topicName = getIntent().getStringExtra(Constants.EXTRA_TOPIC_NAME);

        // Bind views
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerViewLessons = findViewById(R.id.recyclerViewLessons);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(topicName);
        }

        // Setup RecyclerView
        recyclerViewLessons.setLayoutManager(new LinearLayoutManager(this));
        lessonAdapter = new LessonAdapter(new ArrayList<>(), this);
        recyclerViewLessons.setAdapter(lessonAdapter);

        // Load lessons
        loadLessons();
    }

    /**
     * Load danh sách lessons từ Firebase
     */
    private void loadLessons() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_LESSONS)
                .orderByChild("topicId")
                .equalTo(topicId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Lesson> lessons = new ArrayList<>();

                        for (DataSnapshot lessonSnapshot : snapshot.getChildren()) {
                            Lesson lesson = lessonSnapshot.getValue(Lesson.class);
                            if (lesson != null) {
                                lessons.add(lesson);
                            }
                        }

                        // Sort theo orderIndex
                        lessons.sort((l1, l2) -> Integer.compare(l1.getOrderIndex(), l2.getOrderIndex()));

                        // Update UI
                        progressBar.setVisibility(View.GONE);
                        if (lessons.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            lessonAdapter.updateLessons(lessons);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LessonListActivity.this,
                                "Lỗi: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onLessonClick(Lesson lesson) {
        // Mở LearningActivity
        Intent intent = new Intent(this, LearningActivity.class);
        intent.putExtra(Constants.EXTRA_LESSON_ID, lesson.getLessonId());
        intent.putExtra(Constants.EXTRA_LESSON_TITLE, lesson.getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
