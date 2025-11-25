# 🔥 FIREBASE DATABASE FILES

## 📁 CÁC FILE TRONG THƯ MỤC NÀY

### **1. jargo-database-full.json** ⭐ KHUYẾN NGHỊ
File JSON hoàn chỉnh chứa TẤT CẢ dữ liệu:
- ✅ 3 Fields (IT, Medical, Economics)
- ✅ 3 Topics (IT: Lập trình cơ bản, Database, Mạng)
- ✅ 3 Lessons (Biến & Kiểu dữ liệu, Vòng lặp, Hàm)
- ✅ 15 Vocabularies (5 từ/lesson)
- ✅ 5 Quizzes (câu hỏi trắc nghiệm và điền từ)

**Cách dùng:**
```
1. Mở Firebase Console
2. Realtime Database → Data tab
3. Click "⋮" (3 chấm) → Import JSON
4. Chọn file này → Import
5. DONE! ✅
```

---

### **2. database-structure.json**
Cấu trúc database cơ bản (rỗng):
- Chỉ có skeleton của các nodes
- Dùng để khởi tạo database ban đầu

---

### **3. sample-data-it.json**
Dữ liệu mẫu cho chuyên ngành IT:
- Topics, Lessons, Vocabularies, Quizzes
- Có thể import riêng từng phần

---

### **4. database.rules.json**
Firebase Security Rules:
```json
{
  "fields": { ".read": true },           // Public read
  "users": { ".read": "auth.uid == $uid" }, // Private user data
  "progress": { ".read": "auth.uid == $uid" } // Private progress
}
```

**Cách áp dụng:**
```
1. Firebase Console → Realtime Database
2. Tab "Rules"
3. Copy nội dung file này
4. Paste vào editor
5. Click "Publish"
```

---

### **5. SETUP_GUIDE.md**
Hướng dẫn chi tiết từng bước:
- ✅ Cách mở Firebase Console
- ✅ Cách import JSON
- ✅ Cách setup Security Rules
- ✅ Cách test kết nối
- ✅ Troubleshooting

---

## 🚀 HƯỚNG DẪN NHANH

### **CÁCH 1: Import All-in-One (Nhanh nhất)** ⭐

```bash
1. Firebase Console → Realtime Database
2. Click "⋮" → Import JSON
3. Chọn: jargo-database-full.json
4. Click Import
5. Tab "Rules" → Copy nội dung database.rules.json → Publish
```

✅ **DONE trong 2 phút!**

---

### **CÁCH 2: Import từng phần**

1. Import `database-structure.json` (structure)
2. Import `sample-data-it.json` vào từng node
3. Apply rules từ `database.rules.json`

---

## 📊 DỮ LIỆU ĐÃ TẠO

### **Fields: 3**
| ID | Tên | Icon | Topics |
|----|-----|------|--------|
| it | Công nghệ thông tin | 💻 | 3 |
| medical | Y tế | 🏥 | 0 |
| economics | Kinh tế | 💼 | 0 |

### **Topics (IT): 3**
1. **Lập trình cơ bản** (Beginner) - 3 lessons, 15 từ
2. **Cơ sở dữ liệu** (Intermediate) - LOCKED
3. **Mạng máy tính** (Intermediate) - LOCKED

### **Lessons: 3**
1. Biến và Kiểu dữ liệu (5 từ)
2. Vòng lặp và Điều kiện (5 từ)
3. Hàm và Tham số (5 từ)

### **Vocabularies: 15 từ**
```
variable, integer, string, boolean, constant,
loop, condition, iterate, break, continue,
function, parameter, return, argument, invoke
```

### **Quizzes: 5 câu**
- 4 câu trắc nghiệm (multiple choice)
- 1 câu điền từ (fill blank)

---

## 🔗 DATABASE URL

```
https://jargo-5a26d-default-rtdb.asia-southeast1.firebasedatabase.app
```

**Region:** Singapore (asia-southeast1)

---

## ✅ SAU KHI IMPORT XONG

### **Kiểm tra trong Console:**
```
jargo-5a26d-default-rtdb/
├── fields/
│   └── it/ ✅ Có data
├── topics/
│   └── it_topic_01/ ✅ Có data
├── lessons/
│   └── it_lesson_01/ ✅ Có data
├── vocabularies/
│   └── it_vocab_001/ ✅ Có data
└── quizzes/
    └── it_quiz_001/ ✅ Có data
```

### **Test trong App:**

Thêm code này vào `MainActivity.onCreate()`:

```java
FirebaseDatabase.getInstance()
    .getReference("fields/it")
    .addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Field field = snapshot.getValue(Field.class);
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

**Kết quả mong đợi:**
```
Toast: "Firebase OK: Công nghệ thông tin"
```

---

## 📈 KẾ HOẠCH MỞ RỘNG

### **Sắp tới:**
- [ ] Thêm data cho Medical (Y tế)
- [ ] Thêm data cho Economics (Kinh tế)
- [ ] Thêm audio cho pronunciations
- [ ] Thêm images cho vocabularies
- [ ] Thêm more topics cho IT

---

## 🐛 LỖI THƯỜNG GẶP

### **1. "Permission denied"**
→ Check Rules, đảm bảo `.read: true` cho public data

### **2. "Invalid JSON"**
→ Validate tại https://jsonlint.com/

### **3. "Data not showing in app"**
→ Check `google-services.json` có đúng project không

### **4. "Connection timeout"**
→ Check internet, check Firebase region

---

## 📞 CẦN HỖ TRỢ?

Nếu gặp vấn đề:
1. Đọc `SETUP_GUIDE.md` chi tiết
2. Check Logcat để xem lỗi cụ thể
3. Screenshot Firebase Console
4. Báo lại để được hỗ trợ

---

**🎉 Chúc bạn setup thành công!**
