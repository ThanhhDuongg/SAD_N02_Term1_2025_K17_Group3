# Sơ đồ Use Case tổng thể

Sơ đồ dưới đây mô tả cái nhìn tổng quan về các tác nhân chính và những nhóm chức năng trọng tâm của hệ thống quản lý ký túc xá.

```mermaid
%%{init: {"theme": "default"}}%%
usecaseDiagram

actor SV as "Sinh viên"
actor Admin as "Quản trị viên KTX"
actor StuMgr as "Nhân viên quản lý sinh viên"
actor ContractStaff as "Nhân viên phụ trách hợp đồng"
actor Finance as "Nhân viên tài chính"
actor Tech as "Kỹ thuật viên"
actor Discipline as "Cán bộ kỷ luật"
actor SysAdmin as "Quản trị viên hệ thống"
actor Internal as "Người dùng nội bộ"

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
Admin --> UC_Dashboard
Admin --> UC_Report

StuMgr --> UC_Login
StuMgr --> UC_Student
StuMgr --> UC_Report

ContractStaff --> UC_Login
ContractStaff --> UC_CreateContract
ContractStaff --> UC_SearchContract
ContractStaff --> UC_Report

Finance --> UC_Login
Finance --> UC_Fee
Finance --> UC_Report

Tech --> UC_Login
Tech --> UC_MaintenanceProcess

Discipline --> UC_Login
Discipline --> UC_Violation

SysAdmin --> UC_Login
SysAdmin --> UC_Report
SysAdmin --> UC_Register
SysAdmin --> UC_OpenReg
SysAdmin --> UC_ApproveReg
SysAdmin --> UC_Building
SysAdmin --> UC_Room
SysAdmin --> UC_Student
SysAdmin --> UC_CreateContract
SysAdmin --> UC_SearchContract
SysAdmin --> UC_Fee
SysAdmin --> UC_MaintenanceProcess
SysAdmin --> UC_Violation
SysAdmin --> UC_Dashboard

Internal --> UC_Login
Internal --> UC_Dashboard
Internal --> UC_Report
```

> **Ghi chú:** Các đường nối thể hiện tác nhân nào tương tác trực tiếp với từng nhóm use case. "Quản trị viên hệ thống" có quyền cao nhất nên có thể truy cập tất cả chức năng nội bộ để cấu hình và giám sát. "Người dùng nội bộ" biểu diễn các vai trò vận hành (quản trị viên, nhân viên) khi sử dụng các dashboard/báo cáo dùng chung.
