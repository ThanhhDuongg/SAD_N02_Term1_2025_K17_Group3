# Chính sách bảo mật & phân quyền

Dựa trên `SecurityConfig`, các vai trò và phạm vi truy cập chính như sau:

| Vai trò | Quyền truy cập chính |
| --- | --- |
| Quản trị viên (ROLE_ADMIN) | `/dashboard`, `/rooms/**`, `/students/**`, `/contracts/**`, `/fees/**`, `/users/**`, `/buildings/**`, `/reports/**` |
| Nhân viên hỗ trợ (ROLE_STAFF) | `/dashboard`, `/maintenance/**`, `/violations/**`, `/registrations/**`, `/account/**` |
| Sinh viên (ROLE_STUDENT) | `/student/**`, `/account/**` |
| Công khai | `/login`, `/register`, tài nguyên tĩnh, `/error` |

Tất cả yêu cầu khác phải đăng nhập và có vai trò tương ứng. Khi đăng nhập thành công, hệ thống tự điều hướng tới dashboard phù hợp (sinh viên → `/student/dashboard`, nhân sự → `/dashboard`).
