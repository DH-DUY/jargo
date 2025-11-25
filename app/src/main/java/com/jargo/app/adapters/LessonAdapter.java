package com.jargo.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.models.Lesson;
import java.util.List;

/**
 * LessonAdapter - Adapter cho RecyclerView hiển thị danh sách lessons
 */
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson, listener, position);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    /**
     * Update danh sách lessons
     */
    public void updateLessons(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLessonNumber;
        private TextView tvLessonTitle;
        private TextView tvLessonTitleEn;
        private TextView tvVocabCount;
        private TextView tvCompletedIndicator;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLessonNumber = itemView.findViewById(R.id.tvLessonNumber);
            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvLessonTitleEn = itemView.findViewById(R.id.tvLessonTitleEn);
            tvVocabCount = itemView.findViewById(R.id.tvVocabCount);
            tvCompletedIndicator = itemView.findViewById(R.id.tvCompletedIndicator);
        }

        public void bind(Lesson lesson, OnLessonClickListener listener, int position) {
            tvLessonNumber.setText(String.valueOf(position + 1));
            tvLessonTitle.setText(lesson.getTitle());
            tvLessonTitleEn.setText(lesson.getTitleEn());
            tvVocabCount.setText(lesson.getVocabularyCount() + " từ vựng");

            // Completed indicator
            if (lesson.isCompleted()) {
                tvCompletedIndicator.setVisibility(View.VISIBLE);
                tvCompletedIndicator.setText("✅");
            } else {
                tvCompletedIndicator.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> listener.onLessonClick(lesson));
        }
    }
}
