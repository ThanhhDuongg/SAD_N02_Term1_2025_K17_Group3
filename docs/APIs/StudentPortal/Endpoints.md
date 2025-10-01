# API Endpoints - StudentPortal

*Phân hệ liên quan*: Cổng dịch vụ tự phục vụ cho sinh viên (dashboard, phí, hợp đồng, đăng ký).

*Tài liệu liên quan*: [Use Cases](../../Domain/StudentPortal/UseCases.md) · [Domain Model](../../Domain/StudentPortal/DomainModel.mmd) · [Views](../../Domain/StudentPortal/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/student/dashboard` | Student Dashboard | Sinh viên |
| GET | `/student/profile` | Xem Profile | Sinh viên |
| GET | `/student/contracts` | Xem Contracts | Sinh viên |
| GET | `/student/fees` | Xem Fees | Sinh viên |
| GET | `/student/maintenance/new` | Hiển thị form Maintenance Request | Sinh viên |
| GET | `/student/requests` | Xem Requests | Sinh viên |
| GET | `/student/registrations` | Xem Registration Requests | Sinh viên |
| GET | `/student/registrations/new` | Hiển thị form Registration Request | Sinh viên |
| POST | `/student/registrations` | Tạo Registration Request | Sinh viên |
| POST | `/student/maintenance` | Tạo Maintenance Request | Sinh viên |
| GET | `/student/violations` | Xem Violations | Sinh viên |
