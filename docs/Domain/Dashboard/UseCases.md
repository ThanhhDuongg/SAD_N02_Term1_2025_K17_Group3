# Use Case - Dashboard

*Liên kết nhanh*: [Endpoints](../../APIs/Dashboard/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Bảng điều khiển dành cho quản trị viên/nhân viên với các chỉ số nhanh.

## UC_Dashboard_Home

* **Mục tiêu**: Home.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/`.
  2. Hệ thống thực thi handler `home` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Dashboard_Showdashboard

* **Mục tiêu**: Hiển thị Dashboard.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/dashboard`.
  2. Hệ thống thực thi handler `showDashboard` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
