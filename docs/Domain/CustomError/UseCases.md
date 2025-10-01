# Use Case - CustomError

*Liên kết nhanh*: [Endpoints](../../APIs/CustomError/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Trang/thông điệp lỗi tùy biến cho toàn hệ thống.

## UC_CustomError_Errorhtml

* **Mục tiêu**: Error Html.
* **Tác nhân chính**: Công khai.
* **Điều kiện tiên quyết**: Người dùng truy cập công khai.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu ANY tới `${server.error.path:${error.path:/error}}`.
  2. Hệ thống thực thi handler `errorHtml` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_CustomError_Error

* **Mục tiêu**: Error (JSON).
* **Tác nhân chính**: Công khai.
* **Điều kiện tiên quyết**: Người dùng truy cập công khai.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu ANY tới `${server.error.path:${error.path:/error}}`.
  2. Hệ thống thực thi handler `error` và áp dụng nghiệp vụ tương ứng.
  3. Trả về JSON.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
