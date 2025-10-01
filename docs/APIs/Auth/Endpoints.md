# API Endpoints - Auth

*Phân hệ liên quan*: Luồng đăng nhập và tự đăng ký tài khoản cho sinh viên.

*Tài liệu liên quan*: [Use Cases](../../Domain/Auth/UseCases.md) · [Domain Model](../../Domain/Auth/DomainModel.mmd) · [Views](../../Domain/Auth/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| GET | `/login` | Login | Công khai |
| GET | `/register` | Hiển thị Registration Form | Công khai |
| POST | `/register` | Register Student | Công khai |
