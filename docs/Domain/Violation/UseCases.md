# Use Case - Violation

*Liên kết nhanh*: [Endpoints](../../APIs/Violation/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Ghi nhận và thống kê vi phạm nội quy của sinh viên.

## UC_Violation_Listviolations

* **Mục tiêu**: Liệt kê Violations.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/violations`.
  2. Hệ thống thực thi handler `listViolations` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Violation_Newviolationform

* **Mục tiêu**: Hiển thị form Violation Form.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/violations/new`.
  2. Hệ thống thực thi handler `newViolationForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Violation_Createviolation

* **Mục tiêu**: Tạo Violation.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/violations`.
  2. Hệ thống thực thi handler `createViolation` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
