# Use Case - DormRegistrationPeriod

*Liên kết nhanh*: [Endpoints](../../APIs/DormRegistrationPeriod/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Thiết lập, mở/đóng các đợt đăng ký KTX.

## UC_DormRegistrationPeriod_List

* **Mục tiêu**: Liệt kê.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/registrations/periods`.
  2. Hệ thống thực thi handler `list` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_DormRegistrationPeriod_Openperiod

* **Mục tiêu**: Mở Period.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/registrations/periods`.
  2. Hệ thống thực thi handler `openPeriod` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_DormRegistrationPeriod_Closeperiod

* **Mục tiêu**: Đóng Period.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/registrations/periods/{id}/close`.
  2. Hệ thống thực thi handler `closePeriod` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
