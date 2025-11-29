package com.jargo.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.adapters.LoginHistoryAdapter;
import com.jargo.app.models.LoginHistory;
import com.jargo.app.repositories.LoginHistoryRepository;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;

/**
 * LoginHistoryActivity - Hiển thị lịch sử đăng nhập của user
 */
public class LoginHistoryActivity extends AppCompatActivity {

    private static final String TAG = "Jargo_LoginHistory";
    
    private RecyclerView recyclerView;
    private LoginHistoryAdapter adapter;
    private View loadingView;
    private TextView tvEmpty;
    
    private LoginHistoryRepository loginHistoryRepository;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_history);

        prefsManager = SharedPrefsManager.getInstance(this);
        loginHistoryRepository = LoginHistoryRepository.getInstance();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lịch sử đăng nhập");
        }

        // Bind views
        recyclerView = findViewById(R.id.recyclerViewHistory);
        loadingView = findViewById(R.id.loadingView);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LoginHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Load data
        loadLoginHistory();
    }

    /**
     * Load lịch sử đăng nhập từ Firebase
     */
    private void loadLoginHistory() {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            NotificationHelper.showError(this, "Lỗi", "User chưa đăng nhập");
            finish();
            return;
        }

        loadingView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        Log.d(TAG, "Loading login history for user: " + userId);

        loginHistoryRepository.getLoginHistory(userId, new LoginHistoryRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<LoginHistory> historyList) {
                loadingView.setVisibility(View.GONE);

                if (historyList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.d(TAG, "No login history found");
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.updateData(historyList);
                    Log.i(TAG, "Loaded " + historyList.size() + " login records");
                }
            }

            @Override
            public void onError(String error) {
                loadingView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Không thể tải lịch sử đăng nhập");
                Log.e(TAG, "Error loading history: " + error);
                NotificationHelper.showError(LoginHistoryActivity.this, "Lỗi", error);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
