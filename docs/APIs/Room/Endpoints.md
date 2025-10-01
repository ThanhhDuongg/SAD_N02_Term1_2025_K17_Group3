# API Endpoints - Room

*Phân hệ liên quan*: Quản trị thông tin phòng và phân bổ sức chứa.

*Tài liệu liên quan*: [Use Cases](../../Domain/Room/UseCases.md) · [Domain Model](../../Domain/Room/DomainModel.mmd) · [Views](../../Domain/Room/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/rooms/{id}` | Xem chi tiết | Quản trị viên |
| GET | `/rooms` | Liệt kê Rooms | Quản trị viên |
| GET | `/rooms/by-building/{buildingId}` | Rooms By Building (JSON) | Quản trị viên |
| GET | `/rooms/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/rooms` | Tạo Room | Quản trị viên |
| GET | `/rooms/{id}/edit` | Hiển thị Update Form | Quản trị viên |
| POST | `/rooms/{id}` | Cập nhật Room | Quản trị viên |
| GET | `/rooms/{id}/delete` | Xóa Room | Quản trị viên |
