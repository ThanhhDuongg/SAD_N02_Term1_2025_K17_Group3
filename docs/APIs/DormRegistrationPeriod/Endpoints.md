# API Endpoints - DormRegistrationPeriod

*Phân hệ liên quan*: Thiết lập, mở/đóng các đợt đăng ký KTX.

*Tài liệu liên quan*: [Use Cases](../../Domain/DormRegistrationPeriod/UseCases.md) · [Domain Model](../../Domain/DormRegistrationPeriod/DomainModel.mmd) · [Views](../../Domain/DormRegistrationPeriod/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/registrations/periods` | Liệt kê | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/registrations/periods` | Mở Period | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/registrations/periods/{id}/close` | Đóng Period | Quản trị viên, Nhân viên hỗ trợ |
