# Dormitory Management System

Ứng dụng quản lý ký túc xá cho phép ban quản lý và sinh viên theo dõi thông tin phòng ở, hợp đồng, phí dịch vụ, yêu cầu bảo trì và vi phạm nội quy. Dự án được phát triển bởi nhóm K17 Group 3 trong học phần Phân tích và thiết kế phần mềm.

## 🎯 Tính năng chính
- Quản lý sinh viên, phòng, hợp đồng và phí với đầy đủ thao tác CRUD.
- Cổng thông tin sinh viên hiển thị hợp đồng, phí chưa thanh toán, yêu cầu bảo trì và thông báo mới nhất.
- Bảng điều khiển cho cán bộ với thống kê nhanh về sinh viên, phòng, phí, yêu cầu bảo trì và vi phạm.
- Quy trình xử lý yêu cầu bảo trì, ghi nhận vi phạm có phân quyền theo vai trò.
- Tích hợp sẵn dữ liệu mẫu và cơ chế chuẩn hóa tài khoản khi import từ tập dữ liệu lớn.

## 🛠️ Công nghệ sử dụng
- **Java 21**, **Spring Boot 3**, **Spring MVC**, **Spring Security**, **Spring Data JPA**
- **Thymeleaf**, **Bootstrap 5** cho giao diện người dùng
- **H2** (mặc định) hoặc **MySQL** với profile `mysql`
- **Maven** quản lý phụ thuộc và build

## 🧱 Kiến trúc & cấu trúc thư mục
```
src/main/java/com/example/dorm
├── config          # Cấu hình bảo mật và khởi tạo dữ liệu
├── controller      # Xử lý request, điều hướng view
├── dto             # Đối tượng truyền dữ liệu cho form
├── exception       # GlobalExceptionHandler
├── model           # Entity JPA
├── repository      # Lớp truy vấn cơ sở dữ liệu
└── service         # Xử lý nghiệp vụ và kiểm tra ràng buộc
```

Các controller chỉ nhận request và chuyển tiếp tới service. Service thực hiện kiểm tra nghiệp vụ (ví dụ: giới hạn số giường phòng, ràng buộc duy nhất của mã sinh viên, chuẩn hóa mật khẩu dataset) trước khi gọi repository. Lỗi nghiệp vụ sẽ được bắt bởi `GlobalExceptionHandler` và trả về trang lỗi thân thiện.

👉 Tham khảo thêm tài liệu **[Thiết kế luồng đăng ký KTX](docs/ktx-registration-flow.md)** để xem chi tiết quy trình mở đợt, sinh viên nộp đơn và đóng đợt đăng ký trong hệ thống.

## 🚀 Hướng dẫn chạy ứng dụng
1. Cài đặt **JDK 21** và **Maven 3.9+**.
2. (Tuỳ chọn) Đặt biến môi trường `SPRING_PROFILES_ACTIVE=mysql` nếu muốn kết nối MySQL. Cấu hình kết nối nằm trong `application-mysql.properties`.
3. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```
   > Lưu ý: môi trường chấm tự động không có quyền truy cập Internet nên Maven có thể không tải được phụ thuộc nếu chưa có sẵn trong cache.
4. Truy cập `http://localhost:8080`. Tài khoản mẫu:
   | Vai trò | Tài khoản | Mật khẩu |
   |---------|-----------|----------|
   | Quản trị | `admin` | `password` |
   | Nhân viên | `staff` | `password` |
   | Sinh viên | `sv01` | `password` |

Hồ sơ sinh viên và dữ liệu demo được tạo trong `DemoDataInitializer` giúp nhóm kiểm thử giao diện và luồng nghiệp vụ mà không cần chuẩn bị dữ liệu thủ công.

## 🧪 Kiểm thử
- Chạy kiểm thử: `mvn test`
- Có thể dùng `-DskipTests` khi cần build nhanh.

## 📄 Giấy phép
Mã nguồn phục vụ mục đích học tập và có thể tái sử dụng nội bộ trong môn học. Vui lòng ghi nguồn nếu trích dẫn.
