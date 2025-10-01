# API Endpoints - Contract

*Phân hệ liên quan*: Quy trình lập và cập nhật hợp đồng ở ký túc xá.

*Tài liệu liên quan*: [Use Cases](../../Domain/Contract/UseCases.md) · [Domain Model](../../Domain/Contract/DomainModel.mmd) · [Views](../../Domain/Contract/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/contracts` | Liệt kê Contracts | Quản trị viên |
| GET | `/contracts/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/contracts` | Tạo Contract | Quản trị viên |
| GET | `/contracts/{id}` | Xem Contract | Quản trị viên |
| GET | `/contracts/{id}/edit` | Hiển thị Update Form | Quản trị viên |
| POST | `/contracts/{id}` | Cập nhật Contract | Quản trị viên |
| GET | `/contracts/{id}/delete` | Xóa Contract | Quản trị viên |
| GET | `/contracts/search` | Autocomplete (JSON) | Quản trị viên |
| GET | `/contracts/by-room/{roomId}` | Contracts By Room (JSON) | Quản trị viên |
