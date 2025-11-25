package com.jargo.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.models.Topic;
import java.util.List;

/**
 * TopicAdapter - Adapter cho RecyclerView hiển thị danh sách topics
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics;
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
        holder.bind(topic, listener);
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

        public void bind(Topic topic, OnTopicClickListener listener) {
            tvTopicName.setText(topic.getName());
            tvTopicNameEn.setText(topic.getNameEn());
            tvDescription.setText(topic.getDescription());
            tvLessonCount.setText(topic.getLessonCount() + " bài học");
            tvVocabCount.setText(topic.getTotalVocabularies() + " từ vựng");

            // Progress (TODO: Tính toán từ Firebase)
            int progress = topic.isLocked() ? 0 : 0; // Placeholder
            progressBar.setProgress(progress);
            tvProgress.setText(progress + "%");

            // Lock indicator
            lockIndicator.setVisibility(topic.isLocked() ? View.VISIBLE : View.GONE);

            // Alpha cho locked items
            itemView.setAlpha(topic.isLocked() ? 0.5f : 1.0f);

            // Click listener
            itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        }
    }
}
