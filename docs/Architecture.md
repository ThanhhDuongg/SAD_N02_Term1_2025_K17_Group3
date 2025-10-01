# Kiến trúc hệ thống

Ứng dụng được xây dựng trên Spring Boot theo mô hình nhiều tầng (Controller → Service → Repository) với giao diện Thymeleaf.

## Sơ đồ lớp kiến trúc

```mermaid
graph TD
    Browser[Người dùng/Browser] -->|HTTP| Controller[Controllers (Spring MVC)]
    Controller -->|Gọi nghiệp vụ| Service[Dịch vụ ứng dụng]
    Service -->|Truy vấn| Repository[JPA Repositories]
    Repository -->|ORM| Database[(CSDL Quan hệ)]
    Service --> Security[SecurityConfig & CustomUserDetailsService]
    Controller --> View[Thymeleaf Views]
    Security --> Auth[Authentication Manager]
```

## Các thành phần chính
- **Controller**: định nghĩa endpoint và ghép dữ liệu view (ví dụ `RoomController`, `StudentController`).
- **Service**: hiện thực nghiệp vụ, kiểm tra ràng buộc (`RoomService`, `DormRegistrationRequestService`, ...).
- **Repository**: giao tiếp cơ sở dữ liệu thông qua Spring Data JPA.
- **Config**: cấu hình bảo mật (`SecurityConfig`) và khởi tạo dữ liệu.
- **View**: template Thymeleaf tại `src/main/resources/templates`.
