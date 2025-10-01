# API Endpoints - Fee

*Phân hệ liên quan*: Quản lý các khoản phí và phân bổ theo hợp đồng/phòng.

*Tài liệu liên quan*: [Use Cases](../../Domain/Fee/UseCases.md) · [Domain Model](../../Domain/Fee/DomainModel.mmd) · [Views](../../Domain/Fee/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/fees` | Liệt kê Fees | Quản trị viên |
| GET | `/fees/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/fees` | Tạo Fee | Quản trị viên |
| GET | `/fees/{id}` | Xem Fee | Quản trị viên |
| GET | `/fees/{id}/edit` | Hiển thị Update Form | Quản trị viên |
| POST | `/fees/{id}` | Cập nhật Fee | Quản trị viên |
| GET | `/fees/{id}/delete` | Xóa Fee | Quản trị viên |
