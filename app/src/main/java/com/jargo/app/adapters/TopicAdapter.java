package com.jargo.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.models.Progress;
import com.jargo.app.models.Topic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TopicAdapter - Adapter cho RecyclerView hiển thị danh sách topics
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private Map<String, Progress> progressMap;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.progressMap = new HashMap<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.bind(topic, progressMap, listener);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    /**
     * Update danh sách topics
     */
    public void updateTopics(List<Topic> newTopics) {
        this.topics = newTopics;
        notifyDataSetChanged();
    }

    /**
     * Update progress map
     */
    public void updateProgress(Map<String, Progress> progressMap) {
        this.progressMap = progressMap != null ? progressMap : new HashMap<>();
        notifyDataSetChanged();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTopicName;
        private TextView tvTopicNameEn;
        private TextView tvDescription;
        private TextView tvLessonCount;
        private TextView tvVocabCount;
        private ProgressBar progressBar;
        private TextView tvProgress;
        private View lockIndicator;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvTopicNameEn = itemView.findViewById(R.id.tvTopicNameEn);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLessonCount = itemView.findViewById(R.id.tvLessonCount);
            tvVocabCount = itemView.findViewById(R.id.tvVocabCount);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            lockIndicator = itemView.findViewById(R.id.lockIndicator);
        }

        public void bind(Topic topic, Map<String, Progress> progressMap, OnTopicClickListener listener) {
            tvTopicName.setText(topic.getName());
            tvTopicNameEn.setText(topic.getNameEn());
            tvDescription.setText(topic.getDescription());
            tvLessonCount.setText(topic.getLessonCount() + " bài học");
            tvVocabCount.setText(topic.getTotalVocabularies() + " từ vựng");

            // Calculate progress from completed lessons
            int progress = calculateTopicProgress(topic, progressMap);
            progressBar.setProgress(progress);
            tvProgress.setText(progress + "%");

            // Lock indicator
            lockIndicator.setVisibility(topic.isLocked() ? View.VISIBLE : View.GONE);

            // Alpha cho locked items
            itemView.setAlpha(topic.isLocked() ? 0.5f : 1.0f);

            // Click listener
            itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        }

        /**
         * Calculate progress percentage for a topic based on completed lessons
         */
        private int calculateTopicProgress(Topic topic, Map<String, Progress> progressMap) {
            if (topic.getLessonCount() == 0) {
                return 0;
            }

            // Count completed lessons for this topic
            int completedLessons = 0;
            for (Map.Entry<String, Progress> entry : progressMap.entrySet()) {
                Progress progress = entry.getValue();
                String lessonId = entry.getKey(); // lessonId from map key
                
                // Filter by topicId if available
                if (progress.getTopicId() != null && topic.getTopicId() != null) {
                    if (progress.getTopicId().equals(topic.getTopicId()) && progress.isCompleted()) {
                        completedLessons++;
                    }
                } 
                // Fallback: Use lessonId pattern matching (e.g., "lesson_it_001" belongs to IT topic)
                else if (lessonId != null && progress.isCompleted()) {
                    // Extract topic from lessonId pattern: "lesson_{topicId}_{number}"
                    String topicFromLesson = extractTopicFromLessonId(lessonId);
                    if (topicFromLesson != null && topic.getTopicId() != null 
                            && topic.getTopicId().contains(topicFromLesson)) {
                        completedLessons++;
                    }
                }
            }

            // Calculate percentage
            return Math.min(100, (completedLessons * 100) / topic.getLessonCount());
        }
        
        /**
         * Extract topic ID from lessonId pattern
         * Example: "lesson_it_001" -> "it"
         */
        private String extractTopicFromLessonId(String lessonId) {
            if (lessonId != null && lessonId.startsWith("lesson_")) {
                String[] parts = lessonId.split("_");
                if (parts.length >= 2) {
                    return parts[1]; // Return topic part (e.g., "it", "medical")
                }
            }
            return null;
        }
    }
}
