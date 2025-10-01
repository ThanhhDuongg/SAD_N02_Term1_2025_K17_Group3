# Use Case - UserManagement

*Liên kết nhanh*: [Endpoints](../../APIs/UserManagement/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Quản trị người dùng nội bộ và phân quyền.

## UC_UserManagement_Listusers

* **Mục tiêu**: Liệt kê Users.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/users`.
  2. Hệ thống thực thi handler `listUsers` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_UserManagement_Showcreateform

* **Mục tiêu**: Hiển thị Create Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/users/new`.
  2. Hệ thống thực thi handler `showCreateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_UserManagement_Createuser

* **Mục tiêu**: Tạo User.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/users`.
  2. Hệ thống thực thi handler `createUser` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_UserManagement_Updateroles

* **Mục tiêu**: Cập nhật Roles.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/users/{id}/roles`.
  2. Hệ thống thực thi handler `updateRoles` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
