# Use Case - Report

*Liên kết nhanh*: [Endpoints](../../APIs/Report/Endpoints.md) · [Domain Model](DomainModel.mmd) · [Views](Views.md)

Mô tả phân hệ: Tổng hợp báo cáo vận hành (tài chính, tỷ lệ sử dụng).

## UC_Report_Overview

* **Mục tiêu**: Overview.
* **Tác nhân chính**: Quản trị viên.
* **Điều kiện tiên quyết**: Người dùng đã xác thực theo vai trò tương ứng.
* **Luồng chính**:
  1. Người dùng gửi yêu cầu GET tới `/reports`.
  2. Hệ thống thực thi handler `overview` và áp dụng nghiệp vụ tương ứng.
  3. Trả về trang giao diện hoặc chuyển hướng.
* **Kết quả**: Dữ liệu được hiển thị/cập nhật theo yêu cầu.
