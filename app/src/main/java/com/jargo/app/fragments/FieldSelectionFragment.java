package com.jargo.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jargo.app.R;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * FieldSelectionFragment - Màn hình chọn chuyên ngành
 * Cho phép user chọn 1 trong 3 chuyên ngành: IT, Medical, Economics
 */
public class FieldSelectionFragment extends Fragment {

    private SharedPrefsManager prefsManager;
    private String selectedField = Constants.FIELD_IT; // Mặc định chọn IT

    public FieldSelectionFragment() {
        // Required empty public constructor
    }

    public static FieldSelectionFragment newInstance() {
        return new FieldSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_field_selection, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());

        // Khởi tạo views
        RadioGroup radioGroupFields = view.findViewById(R.id.radioGroupFields);

        // Mặc định chọn IT
        RadioButton rbIT = view.findViewById(R.id.rbIT);
        rbIT.setChecked(true);
        
        // Lưu giá trị mặc định ngay lập tức
        prefsManager.saveUserField(selectedField);

        // Lắng nghe thay đổi lựa chọn
        radioGroupFields.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIT) {
                selectedField = Constants.FIELD_IT;
            } else if (checkedId == R.id.rbMedical) {
                selectedField = Constants.FIELD_MEDICAL;
            } else if (checkedId == R.id.rbEconomics) {
                selectedField = Constants.FIELD_ECONOMICS;
            }

            // Lưu lựa chọn vào SharedPreferences
            prefsManager.saveUserField(selectedField);
        });

        return view;
    }
}
