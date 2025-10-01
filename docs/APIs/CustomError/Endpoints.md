# API Endpoints - CustomError

*Phân hệ liên quan*: Trang/thông điệp lỗi tùy biến cho toàn hệ thống.

*Tài liệu liên quan*: [Use Cases](../../Domain/CustomError/UseCases.md) · [Domain Model](../../Domain/CustomError/DomainModel.mmd) · [Views](../../Domain/CustomError/Views.md)

| HTTP | Đường dẫn | Mô tả | Quyền truy cập |
| --- | --- | --- | --- |
| ANY | `${server.error.path:${error.path:/error}}` | Error Html | Công khai |
| ANY | `${server.error.path:${error.path:/error}}` | Error (JSON) | Công khai |
