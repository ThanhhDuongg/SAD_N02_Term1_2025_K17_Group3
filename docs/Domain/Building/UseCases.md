# Use Case - Building

*Liên kết nhanh*: [Endpoints](../../APIs/Building/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Quản lý danh mục tòa nhà ký túc xá.

## UC_Building_Listbuildings

* **Mục tiêu**: Liệt kê Buildings.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/buildings`.
  2. Hệ thống thực thi handler `listBuildings` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Buildingoptions

* **Mục tiêu**: Building Options (JSON).
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/buildings/options`.
  2. Hệ thống thực thi handler `buildingOptions` và áp dụng nghiệp vụ tương ứng.
  3. Trả về JSON.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Showcreateform

* **Mục tiêu**: Hiển thị Create Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/buildings/new`.
  2. Hệ thống thực thi handler `showCreateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Createbuilding

* **Mục tiêu**: Tạo Building.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/buildings`.
  2. Hệ thống thực thi handler `createBuilding` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Showeditform

* **Mục tiêu**: Hiển thị Edit Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/buildings/{id}/edit`.
  2. Hệ thống thực thi handler `showEditForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Updatebuilding

* **Mục tiêu**: Cập nhật Building.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/buildings/{id}`.
  2. Hệ thống thực thi handler `updateBuilding` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Building_Deletebuilding

* **Mục tiêu**: Xóa Building.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/buildings/{id}/delete`.
  2. Hệ thống thực thi handler `deleteBuilding` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
