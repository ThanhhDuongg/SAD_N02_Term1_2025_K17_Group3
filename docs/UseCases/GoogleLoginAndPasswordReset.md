# Đăng nhập Google & Quên mật khẩu

## 1. Đăng nhập bằng Google

### Luồng hoạt động
1. Người dùng mở trang đăng nhập và chọn nút **“Đăng nhập bằng Google”**.
2. Hệ thống chuyển hướng tới trang xác thực của Google theo cấu hình OAuth 2.0.
3. Sau khi xác thực thành công, Google trả về thông tin tài khoản (email, tên hiển thị).
4. Ứng dụng kiểm tra email:
   - Nếu đã tồn tại tài khoản Google: cập nhật thông tin mới nhất (tên, mã định danh nhà cung cấp) và đăng nhập.
   - Nếu email tồn tại dưới dạng tài khoản nội bộ: thông báo lỗi yêu cầu đăng nhập bằng mật khẩu.
   - Nếu email chưa tồn tại: tự động tạo tài khoản mới với vai trò `ROLE_STUDENT`, sinh tên đăng nhập từ phần đầu email.
5. Thành công đăng nhập -> hệ thống điều hướng theo vai trò (Sinh viên về `/student/dashboard`, Quản trị/Nhân viên về `/dashboard`).

### Cấu hình cần thiết
- Cập nhật biến môi trường `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` với thông tin ứng dụng trên Google Cloud.
- Tùy chọn: đặt `APP_BASE_URL` (mặc định `http://localhost:8080`) để tạo liên kết chính xác trong email/console.
- Các giá trị mẫu trong `application.properties` chỉ phục vụ phát triển, cần thay bằng thông tin thật khi triển khai.

### Ghi chú
- Tài khoản tạo mới từ Google được đánh dấu `auth_provider = GOOGLE`, lưu `provider_id` và thời điểm đăng nhập gần nhất.
- Người dùng Google không thể sử dụng chức năng đổi/đặt lại mật khẩu nội bộ.

## 2. Chức năng quên mật khẩu

### Luồng người dùng
1. Tại màn hình đăng nhập, chọn **“Quên mật khẩu?”** để mở form nhập email.
2. Sau khi nhập email và gửi:
   - Nếu email hợp lệ, hệ thống tạo token đặt lại mật khẩu có hạn dùng 30 phút.
   - Token cùng liên kết đặt lại được ghi log (mô phỏng việc gửi email) thông qua `PasswordResetNotificationService`.
   - Người dùng luôn nhận thông báo “Nếu email tồn tại…” nhằm tránh lộ thông tin tài khoản.
3. Người dùng mở liên kết `/reset-password?token=...`:
   - Hệ thống xác thực token chưa hết hạn/chưa sử dụng.
   - Hiển thị form tạo mật khẩu mới (yêu cầu tối thiểu 8 ký tự, nhập lại để xác nhận).
4. Sau khi đặt mật khẩu thành công, token bị vô hiệu (đánh dấu `consumed_at`) và người dùng được chuyển về trang đăng nhập.

### Ràng buộc kỹ thuật
- Token được lưu trong bảng `password_reset_tokens`, định danh bằng UUID, liên kết `user_id`.
- Jobs dọn rác: mỗi lần tạo token sẽ xóa các token hết hạn quá 12 giờ để tránh phình dữ liệu.
- Chỉ tài khoản đăng nhập nội bộ (`auth_provider = LOCAL`) được phép tạo token và đặt lại mật khẩu.
- Lịch sử `last_login_at` của người dùng được cập nhật sau mỗi lần đăng nhập thành công.

### Ghi chú triển khai
- Khi tích hợp dịch vụ email thật, có thể thay thế `PasswordResetNotificationService` bằng việc gửi email thực tế.
- Thông báo trên UI và log được viết bằng tiếng Việt để đồng nhất với ứng dụng.
