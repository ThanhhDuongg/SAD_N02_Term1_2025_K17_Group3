# Use Case - Account

*Liên kết nhanh*: [Endpoints](../../APIs/Account/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Trang cá nhân cho người dùng đã đăng nhập (cập nhật thông tin và đổi mật khẩu).

## UC_Account_Showchangepasswordform

* **Mục tiêu**: Hiển thị Change Password Form.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ, Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/account/password`.
  2. Hệ thống thực thi handler `showChangePasswordForm` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Account_Showprofile

* **Mục tiêu**: Hiển thị Profile.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ, Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/account/profile`.
  2. Hệ thống thực thi handler `showProfile` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Account_Changepassword

* **Mục tiêu**: Thay đổi Password.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ, Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/account/password`.
  2. Hệ thống thực thi handler `changePassword` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.

## UC_Account_Updateprofile

* **Mục tiêu**: Cập nhật Profile.
* **Tác nhân chính**: Quản trị viên, Nhân viên hỗ trợ, Sinh viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu POST tới `/account/profile`.
  2. Hệ thống thực thi handler `updateProfile` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
