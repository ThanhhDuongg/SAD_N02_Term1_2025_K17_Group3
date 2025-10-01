# Use Case - Room

*Liên kết nhanh*: [Endpoints](../../APIs/Room/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Quản trị thông tin phòng và phân bổ sức chứa.

## UC_Room_Detail

* **Mục tiêu**: Xem chi tiết.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms/{id}`.
  2. Hệ thống thực thi handler `detail` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Listrooms

* **Mục tiêu**: Liệt kê Rooms.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms`.
  2. Hệ thống thực thi handler `listRooms` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Roomsbybuilding

* **Mục tiêu**: Rooms By Building (JSON).
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms/by-building/{buildingId}`.
  2. Hệ thống thực thi handler `roomsByBuilding` và áp dụng nghiệp vụ tương ứng.
  3. Trả về JSON.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Showcreateform

* **Mục tiêu**: Hiển thị Create Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms/new`.
  2. Hệ thống thực thi handler `showCreateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Createroom

* **Mục tiêu**: Tạo Room.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/rooms`.
  2. Hệ thống thực thi handler `createRoom` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Showupdateform

* **Mục tiêu**: Hiển thị Update Form.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms/{id}/edit`.
  2. Hệ thống thực thi handler `showUpdateForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Updateroom

* **Mục tiêu**: Cập nhật Room.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/rooms/{id}`.
  2. Hệ thống thực thi handler `updateRoom` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Room_Deleteroom

* **Mục tiêu**: Xóa Room.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/rooms/{id}/delete`.
  2. Hệ thống thực thi handler `deleteRoom` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
