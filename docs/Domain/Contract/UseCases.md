# Use Case - Contract

*Liên kết nhanh*: [Endpoints](../../APIs/Contract/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Quy trình lập và cập nhật hợp đồng ở ký túc xá.

## UC_Contract_Listcontracts

* **Mục tiêu**: Liệt kê Contracts.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts`.
  2. Hệ thống thực thi handler `listContracts` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Showcreateform

* **Mục tiêu**: Hiển thị Create Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/new`.
  2. Hệ thống thực thi handler `showCreateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Createcontract

* **Mục tiêu**: Tạo Contract.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/contracts`.
  2. Hệ thống thực thi handler `createContract` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Viewcontract

* **Mục tiêu**: Xem Contract.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/{id}`.
  2. Hệ thống thực thi handler `viewContract` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Showupdateform

* **Mục tiêu**: Hiển thị Update Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/{id}/edit`.
  2. Hệ thống thực thi handler `showUpdateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Updatecontract

* **Mục tiêu**: Cập nhật Contract.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/contracts/{id}`.
  2. Hệ thống thực thi handler `updateContract` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Deletecontract

* **Mục tiêu**: Xóa Contract.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/{id}/delete`.
  2. Hệ thống thực thi handler `deleteContract` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Autocomplete

* **Mục tiêu**: Autocomplete (JSON).
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/search`.
  2. Hệ thống thực thi handler `autocomplete` và áp dụng nghiệp vụ tương ứng.
  3. Trả về JSON.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Contract_Contractsbyroom

* **Mục tiêu**: Contracts By Room (JSON).
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/contracts/by-room/{roomId}`.
  2. Hệ thống thực thi handler `contractsByRoom` và áp dụng nghiệp vụ tương ứng.
  3. Trả về JSON.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
