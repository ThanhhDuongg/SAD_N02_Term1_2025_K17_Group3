# Sơ đồ Use Case tổng thể

Sơ đồ dưới đây mô tả cái nhìn tổng quan về các tác nhân chính và những nhóm chức năng trọng tâm của hệ thống quản lý ký túc xá.

```mermaid
%%{init: {"theme": "default"}}%%
usecaseDiagram

actor SV as "Sinh viên"
actor Admin as "Quản trị viên"
actor Staff as "Nhân viên"

rectangle "Quản lý truy cập" {
  usecase UC_Login as "Đăng nhập/Đăng xuất"
  usecase UC_Register as "Sinh viên đăng ký tài khoản"
}

rectangle "Đăng ký ở ký túc xá" {
  usecase UC_OpenReg as "Mở/đóng đợt đăng ký"
  usecase UC_ApproveReg as "Duyệt yêu cầu đăng ký"
  usecase UC_RequestReg as "Sinh viên gửi yêu cầu đăng ký"
}

rectangle "Quản lý cơ sở & nội trú" {
  usecase UC_Building as "Quản lý tòa nhà"
  usecase UC_Room as "Quản lý phòng"
  usecase UC_Student as "Quản lý hồ sơ sinh viên"
  usecase UC_StudentDashboard as "Sinh viên xem dashboard cá nhân"
}

rectangle "Hợp đồng & phí" {
  usecase UC_CreateContract as "Tạo/cập nhật hợp đồng"
  usecase UC_SearchContract as "Tra cứu hợp đồng"
  usecase UC_Fee as "Quản lý khoản phí"
}

rectangle "Bảo trì & vi phạm" {
  usecase UC_MaintenanceRequest as "Sinh viên gửi yêu cầu bảo trì"
  usecase UC_MaintenanceProcess as "Xử lý yêu cầu bảo trì"
  usecase UC_Violation as "Quản lý vi phạm sinh viên"
}

rectangle "Báo cáo & giám sát" {
  usecase UC_Dashboard as "Xem dashboard hệ thống"
  usecase UC_Report as "Xuất/xem báo cáo"
}

SV --> UC_Register
SV --> UC_Login
SV --> UC_RequestReg
SV --> UC_StudentDashboard
SV --> UC_MaintenanceRequest

Admin --> UC_Login
Admin --> UC_OpenReg
Admin --> UC_ApproveReg
Admin --> UC_Building
Admin --> UC_Room
Admin --> UC_Student
Admin --> UC_CreateContract
Admin --> UC_SearchContract
Admin --> UC_Fee
Admin --> UC_MaintenanceProcess
Admin --> UC_Violation
Admin --> UC_Dashboard
Admin --> UC_Report

Staff --> UC_Login
Staff --> UC_Student
Staff --> UC_CreateContract
Staff --> UC_SearchContract
Staff --> UC_Fee
Staff --> UC_MaintenanceProcess
Staff --> UC_Violation
Staff --> UC_Dashboard
Staff --> UC_Report
```

> **Ghi chú:** Các đường nối thể hiện tác nhân nào tương tác trực tiếp với từng nhóm use case. Sơ đồ được giản lược theo ba tác nhân cốt lõi mà dự án đang sử dụng: **Quản trị viên** (quyền cao nhất), **Nhân viên** (cán bộ vận hành nội bộ) và **Sinh viên**.
