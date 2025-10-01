# Use Case - MaintenanceRequest

*Liên kết nhanh*: [Endpoints](../../APIs/MaintenanceRequest/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Tiếp nhận và xử lý yêu cầu bảo trì từ sinh viên.

## UC_MaintenanceRequest_Listrequests

* **Mục tiêu**: Liệt kê Requests.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/maintenance`.
  2. Hệ thống thực thi handler `listRequests` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_MaintenanceRequest_Viewrequest

* **Mục tiêu**: Xem Request.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/maintenance/{id}`.
  2. Hệ thống thực thi handler `viewRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_MaintenanceRequest_Updatestatus

* **Mục tiêu**: Cập nhật Status.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/maintenance/{id}/status`.
  2. Hệ thống thực thi handler `updateStatus` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_MaintenanceRequest_Assignrequest

* **Mục tiêu**: Phân công Request.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/maintenance/{id}/assign`.
  2. Hệ thống thực thi handler `assignRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_MaintenanceRequest_Acceptrequest

* **Mục tiêu**: Tiếp nhận Request.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/maintenance/{id}/accept`.
  2. Hệ thống thực thi handler `acceptRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_MaintenanceRequest_Unassignrequest

* **Mục tiêu**: Hủy phân công Request.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/maintenance/{id}/unassign`.
  2. Hệ thống thực thi handler `unassignRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
