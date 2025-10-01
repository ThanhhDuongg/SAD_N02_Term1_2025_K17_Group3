# Use Case - Auth

*Liên kết nhanh*: [Endpoints](../../APIs/Auth/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Luồng đăng nhập và tự đăng ký tài khoản cho sinh viên.

## UC_Auth_Login

* **Mục tiêu**: Login.
* **Tác nhân chính**: Công khai.
* **Điều kiện tiên quyết**: Người dùng truy cập công khai.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/login`.
  2. Hệ thống thực thi handler `login` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Auth_Showregistrationform

* **Mục tiêu**: Hiển thị Registration Form.
* **Tác nhân chính**: Công khai.
* **Điều kiện tiên quyết**: Người dùng truy cập công khai.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/register`.
  2. Hệ thống thực thi handler `showRegistrationForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Auth_Registerstudent

* **Mục tiêu**: Register Student.
* **Tác nhân chính**: Công khai.
* **Điều kiện tiên quyết**: Người dùng truy cập công khai.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/register`.
  2. Hệ thống thực thi handler `registerStudent` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
