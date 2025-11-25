# 🔥 HƯỚNG DẪN SETUP FIREBASE REALTIME DATABASE

## 📋 BƯỚC 1: MỞ FIREBASE CONSOLE

1. Truy cập: https://console.firebase.google.com/
2. Chọn project: **jargo-5a26d**
3. Sidebar: Click **Realtime Database**

---

## 🗄️ BƯỚC 2: IMPORT DATABASE STRUCTURE

### **Cách 1: Import JSON (Khuyến nghị)**

1. Trong Firebase Console, click tab **"Data"**
2. Click vào dấu **"⋮"** (3 chấm dọc) bên phải
3. Chọn **"Import JSON"**
4. Upload file: `database-structure.json`
5. Click **"Import"**

### **Cách 2: Tạo thủ công**

Nếu import không được, tạo thủ công:

1. Click vào root node (tên database)
2. Click dấu **"+"** để thêm child
3. Tạo các nodes sau:
   - `fields`
   - `topics`
   - `lessons`
   - `vocabularies`
   - `quizzes`
   - `users`
   - `progress`

---

## 📦 BƯỚC 3: IMPORT SAMPLE DATA

### **Import data IT:**

1. Click vào node `topics`
2. Click **"⋮"** → **"Import JSON"**
3. Mở file `sample-data-it.json`
4. Copy nội dung của object `topics`
5. Paste và import

**Lặp lại với:**
- `lessons` node → import `lessons` từ `sample-data-it.json`
- `vocabularies` node → import `vocabularies`
- `quizzes` node → import `quizzes`

---

## 🔒 BƯỚC 4: CÀI ĐẶT SECURITY RULES

1. Click tab **"Rules"**
2. Xóa rules cũ
3. Copy toàn bộ nội dung từ `database.rules.json`
4. Paste vào editor
5. Click **"Publish"**

### **Giải thích Rules:**

```json
{
  "fields": {
    ".read": true,              // Ai cũng đọc được
    ".write": "auth && isAdmin" // Chỉ admin mới sửa được
  },
  "users": {
    "$uid": {
      ".read": "auth.uid == $uid",  // Chỉ đọc data của mình
      ".write": "auth.uid == $uid"   // Chỉ sửa data của mình
    }
  }
}
```

---

## ✅ BƯỚC 5: KIỂM TRA

### **Test trong Console:**

1. Click vào node `fields/it`
2. Xem có data không?
3. Click `topics/it_topic_01`
4. Xem có data không?

### **Test trong App:**

Chạy code này trong `MainActivity`:

```java
DatabaseReference ref = FirebaseManager.getInstance()
    .getDatabaseReference()
    .child("fields")
    .child("it");

ref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Field field = snapshot.getValue(Field.class);
        Log.d("FIREBASE", "Field: " + field.getName());
        Toast.makeText(MainActivity.this, 
            "Firebase OK: " + field.getName(), 
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.e("FIREBASE", "Error: " + error.getMessage());
    }
});
```

---

## 📊 CẤU TRÚC DATABASE

```
jargo-5a26d-default-rtdb
├── fields/
│   ├── it/
│   ├── medical/
│   └── economics/
├── topics/
│   ├── it_topic_01/
│   ├── it_topic_02/
│   └── it_topic_03/
├── lessons/
│   ├── it_lesson_01/
│   ├── it_lesson_02/
│   └── it_lesson_03/
├── vocabularies/
│   ├── it_vocab_001/
│   ├── it_vocab_002/
│   └── ... (15 từ)
├── quizzes/
│   ├── it_quiz_001/
│   └── ... (5 câu hỏi)
├── users/
│   └── {userId}/
│       ├── userId
│       ├── name
│       ├── email
│       ├── field
│       ├── level
│       └── ...
└── progress/
    └── {userId}/
        ├── {lessonId}/
        │   ├── lessonId
        │   ├── isCompleted
        │   └── completedAt
        └── ...
```

---

## 🚀 BƯỚC 6: THÊM DATA CHO MEDICAL & ECONOMICS

Sau khi test IT thành công, bạn có thể:

1. Tạo file `sample-data-medical.json` tương tự
2. Tạo file `sample-data-economics.json`
3. Import vào database

**Hoặc để tôi tạo cho bạn!**

---

## 🐛 TROUBLESHOOTING

### **Lỗi: "Permission denied"**
→ Check Security Rules, đảm bảo `.read: true` cho public data

### **Lỗi: "Invalid JSON"**
→ Validate JSON tại: https://jsonlint.com/

### **Data không hiển thị trong app**
→ Check Firebase URL trong `google-services.json`
→ URL phải là: `https://jargo-5a26d-default-rtdb.asia-southeast1.firebasedatabase.app`

### **Connection timeout**
→ Check internet connection
→ Check Firebase project settings

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề, báo cho tôi:
1. Screenshot lỗi từ Logcat
2. Screenshot Firebase Console
3. Mô tả chi tiết

---

**✅ Sau khi hoàn thành, bạn sẽ có database đầy đủ để test app!**
