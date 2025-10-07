# Cập nhật logic hệ thống

Tài liệu này mô tả các điều chỉnh quan trọng nhằm đảm bảo tính đúng đắn dữ liệu cho hệ thống quản lý ký túc xá.

## Hợp đồng và phân bổ phòng
- Khi xóa hợp đồng, sinh viên liên quan sẽ được giải phóng khỏi phòng nhằm tránh trạng thái còn phòng nhưng không còn hợp đồng.

## Quản lý phòng
- Ngăn chặn thao tác xóa phòng khi vẫn còn sinh viên đang ở hoặc còn hợp đồng liên kết giúp bảo toàn dữ liệu và tránh mất tham chiếu.

## Quản lý tài khoản người dùng
- Kiểm tra ràng buộc email duy nhất và chuẩn hóa dữ liệu đầu vào khi cập nhật người dùng để đồng bộ với luồng cập nhật hồ sơ.

## Quản lý sinh viên
- Tự động tạo tài khoản đăng nhập cho sinh viên mới bằng chính email của sinh viên và mật khẩu mặc định 123, đồng thời
  đồng bộ lại tài khoản khi cập nhật thông tin liên hệ để đảm bảo sinh viên luôn đăng nhập được.

Những thay đổi này giúp hệ thống đồng nhất dữ liệu giữa sinh viên, phòng ở và tài khoản sử dụng.
