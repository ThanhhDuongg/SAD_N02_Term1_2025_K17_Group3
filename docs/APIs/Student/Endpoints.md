# API Endpoints - Student

*Phân hệ liên quan*: Quản lý hồ sơ sinh viên nội trú.

*Tài liệu liên quan*: [Use Cases](../../Domain/Student/UseCases.md) · [Domain Model](../../Domain/Student/DomainModel.mmd) · [Views](../../Domain/Student/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/students` | Liệt kê Students | Quản trị viên |
| GET | `/students/new` | Hiển thị Create Form | Quản trị viên |
| POST | `/students` | Tạo Student | Quản trị viên |
| GET | `/students/{id}` | Xem Student | Quản trị viên |
| GET | `/students/{id}/edit` | Hiển thị Update Form | Quản trị viên |
| POST | `/students/{id}` | Cập nhật Student | Quản trị viên |
| GET | `/students/{id}/delete` | Xóa Student | Quản trị viên |
| GET | `/students/search` | Autocomplete (JSON) | Quản trị viên |
