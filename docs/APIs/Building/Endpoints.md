# API Endpoints - Building

*Phân hệ liên quan*: Quản lý danh mục tòa nhà ký túc xá.

*Tài liệu liên quan*: [Use Cases](../../Domain/Building/UseCases.md) · [Domain Model](../../Domain/Building/DomainModel.mmd) · [Views](../../Domain/Building/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/buildings` | Liệt kê Buildings | Quản trị viên |
| GET | `/buildings/options` | Building Options (JSON) | Quản trị viên |
| GET | `/buildings/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/buildings` | Tạo Building | Quản trị viên |
| GET | `/buildings/{id}/edit` | Hiển thị Edit Form | Quản trị viên |
| POST | `/buildings/{id}` | Cập nhật Building | Quản trị viên |
| GET | `/buildings/{id}/delete` | Xóa Building | Quản trị viên |
