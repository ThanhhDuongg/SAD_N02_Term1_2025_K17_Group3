# Use Case - StudentPortal

*Liên kết nhanh*: [Endpoints](../../APIs/StudentPortal/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Cổng dịch vụ tự phục vụ cho sinh viên (dashboard, phí, hợp đồng, đăng ký).

## UC_StudentPortal_Studentdashboard

* **Mục tiêu**: Student Dashboard.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/dashboard`.
  2. Hệ thống thực thi handler `studentDashboard` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewprofile

* **Mục tiêu**: Xem Profile.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/profile`.
  2. Hệ thống thực thi handler `viewProfile` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewcontracts

* **Mục tiêu**: Xem Contracts.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/contracts`.
  2. Hệ thống thực thi handler `viewContracts` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewfees

* **Mục tiêu**: Xem Fees.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/fees`.
  2. Hệ thống thực thi handler `viewFees` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Newmaintenancerequest

* **Mục tiêu**: Hiển thị form Maintenance Request.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/maintenance/new`.
  2. Hệ thống thực thi handler `newMaintenanceRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewrequests

* **Mục tiêu**: Xem Requests.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/requests`.
  2. Hệ thống thực thi handler `viewRequests` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewregistrationrequests

* **Mục tiêu**: Xem Registration Requests.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/registrations`.
  2. Hệ thống thực thi handler `viewRegistrationRequests` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Newregistrationrequest

* **Mục tiêu**: Hiển thị form Registration Request.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/registrations/new`.
  2. Hệ thống thực thi handler `newRegistrationRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Createregistrationrequest

* **Mục tiêu**: Tạo Registration Request.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/student/registrations`.
  2. Hệ thống thực thi handler `createRegistrationRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Createmaintenancerequest

* **Mục tiêu**: Tạo Maintenance Request.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/student/maintenance`.
  2. Hệ thống thực thi handler `createMaintenanceRequest` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_StudentPortal_Viewviolations

* **Mục tiêu**: Xem Violations.
* **Tác nhân chính**: Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/student/violations`.
  2. Hệ thống thực thi handler `viewViolations` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
