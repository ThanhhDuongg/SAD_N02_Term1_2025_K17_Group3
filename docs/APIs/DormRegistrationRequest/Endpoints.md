# API Endpoints - DormRegistrationRequest

*Phân hệ liên quan*: Tiếp nhận và duyệt hồ sơ đăng ký ở ký túc xá.

*Tài liệu liên quan*: [Use Cases](../../Domain/DormRegistrationRequest/UseCases.md) · [Domain Model](../../Domain/DormRegistrationRequest/DomainModel.mmd) · [Views](../../Domain/DormRegistrationRequest/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/registrations` | Liệt kê | Quản trị viên, Nhân viên hỗ trợ |
| GET | `/registrations/{id}` | Xem chi tiết | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/registrations/{id}/update` | Cập nhật Request | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/registrations/{id}/status` | Cập nhật Status | Quản trị viên, Nhân viên hỗ trợ |
