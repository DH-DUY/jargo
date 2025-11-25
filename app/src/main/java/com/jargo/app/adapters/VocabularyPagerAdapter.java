package com.jargo.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.models.Vocabulary;
import java.util.List;

/**
 * VocabularyPagerAdapter - Adapter cho ViewPager2 hiển thị flashcards
 */
public class VocabularyPagerAdapter extends RecyclerView.Adapter<VocabularyPagerAdapter.VocabularyViewHolder> {

    private final List<Vocabulary> vocabularies;

    public VocabularyPagerAdapter(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_card, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularies.get(position);
        holder.bind(vocabulary);
    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardFront;
        private final CardView cardBack;
        private final TextView tvWord;
        private final TextView tvPronunciation;
        private final TextView tvMeaning;
        private final TextView tvDefinition;
        private final TextView tvExampleEn;
        private final TextView tvExampleVi;
        private final TextView tvPartOfSpeech;

        private boolean isFlipped = false;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFront = itemView.findViewById(R.id.cardFront);
            cardBack = itemView.findViewById(R.id.cardBack);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPronunciation = itemView.findViewById(R.id.tvPronunciation);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvDefinition = itemView.findViewById(R.id.tvDefinition);
            tvExampleEn = itemView.findViewById(R.id.tvExampleEn);
            tvExampleVi = itemView.findViewById(R.id.tvExampleVi);
            tvPartOfSpeech = itemView.findViewById(R.id.tvPartOfSpeech);

            // Click to flip
            itemView.setOnClickListener(v -> flipCard());
        }

        public void bind(Vocabulary vocabulary) {
            // Reset flip state
            isFlipped = false;
            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.GONE);

            // Front side
            tvWord.setText(vocabulary.getWord());
            tvPronunciation.setText(vocabulary.getPronunciation());
            tvPartOfSpeech.setText(vocabulary.getPartOfSpeech());

            // Back side
            tvMeaning.setText(vocabulary.getMeaning());
            tvDefinition.setText(vocabulary.getDefinition());
            tvExampleEn.setText("\"" + vocabulary.getExampleEn() + "\"");
            tvExampleVi.setText("\"" + vocabulary.getExampleVi() + "\"");
        }

        private void flipCard() {
            if (isFlipped) {
                // Flip to front
                cardBack.setVisibility(View.GONE);
                cardFront.setVisibility(View.VISIBLE);
            } else {
                // Flip to back
                cardFront.setVisibility(View.GONE);
                cardBack.setVisibility(View.VISIBLE);
            }
            isFlipped = !isFlipped;
        }
    }
}
