# API Endpoints - Account

*Phân hệ liên quan*: Trang cá nhân cho người dùng đã đăng nhập (cập nhật thông tin và đổi mật khẩu).

*Tài liệu liên quan*: [Use Cases](../../Domain/Account/UseCases.md) · [Domain Model](../../Domain/Account/DomainModel.mmd) · [Views](../../Domain/Account/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/account/password` | Hiển thị Change Password Form | Quản trị viên, Nhân viên hỗ trợ, Sinh viên |
| GET | `/account/profile` | Hiển thị Profile | Quản trị viên, Nhân viên hỗ trợ, Sinh viên |
| POST | `/account/password` | Thay đổi Password | Quản trị viên, Nhân viên hỗ trợ, Sinh viên |
| POST | `/account/profile` | Cập nhật Profile | Quản trị viên, Nhân viên hỗ trợ, Sinh viên |
