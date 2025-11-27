package com.jargo.app.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * ViewModelFactory - Factory để tạo ViewModels với custom constructors
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final SharedPrefsManager prefsManager;

    public ViewModelFactory(SharedPrefsManager prefsManager) {
        this.prefsManager = prefsManager;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(prefsManager);
        } else if (modelClass.isAssignableFrom(QuizViewModel.class)) {
            return (T) new QuizViewModel(prefsManager);
        } else if (modelClass.isAssignableFrom(LearningViewModel.class)) {
            return (T) new LearningViewModel();
        }
        
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
