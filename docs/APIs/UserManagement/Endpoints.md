# API Endpoints - UserManagement

*Phân hệ liên quan*: Quản trị người dùng nội bộ và phân quyền.

*Tài liệu liên quan*: [Use Cases](../../Domain/UserManagement/UseCases.md) · [Domain Model](../../Domain/UserManagement/DomainModel.mmd) · [Views](../../Domain/UserManagement/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/users` | Liệt kê Users | Quản trị viên |
| GET | `/users/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/users` | Tạo User | Quản trị viên |
| POST | `/users/{id}/roles` | Cập nhật Roles | Quản trị viên |
