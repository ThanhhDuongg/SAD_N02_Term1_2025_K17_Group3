# API Endpoints - MaintenanceRequest

*Phân hệ liên quan*: Tiếp nhận và xử lý yêu cầu bảo trì từ sinh viên.

*Tài liệu liên quan*: [Use Cases](../../Domain/MaintenanceRequest/UseCases.md) · [Domain Model](../../Domain/MaintenanceRequest/DomainModel.mmd) · [Views](../../Domain/MaintenanceRequest/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/maintenance` | Liệt kê Requests | Quản trị viên, Nhân viên hỗ trợ |
| GET | `/maintenance/{id}` | Xem Request | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/maintenance/{id}/status` | Cập nhật Status | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/maintenance/{id}/assign` | Phân công Request | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/maintenance/{id}/accept` | Tiếp nhận Request | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/maintenance/{id}/unassign` | Hủy phân công Request | Quản trị viên, Nhân viên hỗ trợ |
