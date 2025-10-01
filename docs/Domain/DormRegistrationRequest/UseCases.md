# Use Case - DormRegistrationRequest

*Liên kết nhanh*: [Endpoints](../../APIs/DormRegistrationRequest/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Tiếp nhận và duyệt hồ sơ đăng ký ở ký túc xá.

## UC_DormRegistrationRequest_List

* **Mục tiêu**: Liệt kê.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/registrations`.
  2. Hệ thống thực thi handler `list` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_DormRegistrationRequest_Detail

* **Mục tiêu**: Xem chi tiết.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/registrations/{id}`.
  2. Hệ thống thực thi handler `detail` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_DormRegistrationRequest_Updaterequest

* **Mục tiêu**: Cập nhật Request.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/registrations/{id}/update`.
  2. Hệ thống thực thi handler `updateRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_DormRegistrationRequest_Updatestatus

* **Mục tiêu**: Cập nhật Status.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/registrations/{id}/status`.
  2. Hệ thống thực thi handler `updateStatus` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
