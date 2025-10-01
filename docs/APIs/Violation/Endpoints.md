# API Endpoints - Violation

*Phân hệ liên quan*: Ghi nhận và thống kê vi phạm nội quy của sinh viên.

*Tài liệu liên quan*: [Use Cases](../../Domain/Violation/UseCases.md) · [Domain Model](../../Domain/Violation/DomainModel.mmd) · [Views](../../Domain/Violation/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/violations` | Liệt kê Violations | Quản trị viên, Nhân viên hỗ trợ |
| GET | `/violations/new` | Hiển thị form Violation Form | Quản trị viên, Nhân viên hỗ trợ |
| POST | `/violations` | Tạo Violation | Quản trị viên, Nhân viên hỗ trợ |
