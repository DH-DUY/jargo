package com.jargo.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.jargo.app.R;
import com.jargo.app.models.LoginHistory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LoginHistoryAdapter - Adapter cho RecyclerView hiển thị lịch sử đăng nhập
 */
public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {

    private List<LoginHistory> historyList;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    public LoginHistoryAdapter(List<LoginHistory> historyList) {
        this.historyList = historyList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_login_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginHistory history = historyList.get(position);
        
        // Hiển thị thông tin
        Date date = new Date(history.getTimestamp());
        holder.tvDate.setText(dateFormat.format(date));
        holder.tvTime.setText(timeFormat.format(date));
        holder.tvDevice.setText(history.getDeviceInfo());
        holder.tvMethod.setText("Phương thức: " + getLoginMethodText(history.getLoginMethod()));
        holder.tvVersion.setText("App v" + history.getAppVersion());
        
        // Hiển thị trạng thái
        if (history.isSuccess()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
            holder.tvStatus.setText("Thành công");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_error);
            holder.tvStatus.setText("Thất bại");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * Update dữ liệu
     */
    public void updateData(List<LoginHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    /**
     * Chuyển đổi login method sang text tiếng Việt
     */
    private String getLoginMethodText(String method) {
        switch (method) {
            case "email":
                return "Email";
            case "google":
                return "Google";
            case "auto":
                return "Tự động";
            default:
                return method;
        }
    }

    /**
     * ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvTime;
        TextView tvDevice;
        TextView tvMethod;
        TextView tvVersion;
        TextView tvStatus;
        ImageView ivStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDevice = itemView.findViewById(R.id.tvDevice);
            tvMethod = itemView.findViewById(R.id.tvMethod);
            tvVersion = itemView.findViewById(R.id.tvVersion);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }
}
