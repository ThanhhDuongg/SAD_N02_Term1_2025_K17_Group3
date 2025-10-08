# Đặc tả Use Case - Báo cáo & Dashboard

## UC-RD-01 - Admin xem dashboard hệ thống

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-RD-01 - Admin xem dashboard hệ thống |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên xem các chỉ số tổng quan về sinh viên, phòng, hợp đồng, phí, vi phạm, bảo trì. |
| Tác nhân chính | Quản trị viên/nhân viên vận hành |
| Tác nhân phụ | DashboardController |
| Điều kiện tiên quyết | Người dùng đã đăng nhập với vai trò phù hợp. |
| Điều kiện kết thúc | Dashboard hiển thị dữ liệu mới nhất, có thể drill-down. |
| Hậu điều kiện | Hệ thống ghi nhận lượt truy cập để thống kê.

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập trang dashboard. |
| 2 | Hệ thống truy vấn dữ liệu tổng hợp (số sinh viên đang ở, phòng trống, hợp đồng sắp hết hạn...). |
| 3 | Hệ thống hiển thị biểu đồ, số liệu dạng card và bảng top vấn đề. |
| 4 | Quản trị viên tương tác với bộ lọc (thời gian, tòa nhà) để điều chỉnh dữ liệu hiển thị. |

### Luồng thay thế
- 2a: Nếu dữ liệu lớn, hệ thống tải từng phần và hiển thị skeleton UI.

### Các ngoại lệ
- E1: Module thống kê lỗi → Hệ thống hiển thị cảnh báo và dữ liệu cũ gần nhất.

### Điều kiện nghiệp vụ
- Dữ liệu dashboard phải cập nhật ít nhất mỗi 15 phút.
- Quyền truy cập từng widget phụ thuộc vai trò người dùng.

### Ghi chú
- Nên hỗ trợ xuất snapshot dashboard ra PDF.

### Các giả thiết
- DashboardController đã tích hợp với các service thống kê và bộ nhớ đệm.

---

## UC-RD-02 - Xuất báo cáo chi tiết

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-RD-02 - Xuất báo cáo chi tiết |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Người dùng nội bộ xuất báo cáo liên quan đến sinh viên, phòng, hợp đồng, phí, vi phạm. |
| Tác nhân chính | Quản trị viên/nhân viên |
| Tác nhân phụ | ReportController |
| Điều kiện tiên quyết | Người dùng có quyền truy cập module báo cáo; dữ liệu yêu cầu có sẵn. |
| Điều kiện kết thúc | File báo cáo (PDF/Excel) được tạo và tải về. |
| Hậu điều kiện | Lịch sử xuất báo cáo được lưu để kiểm soát truy cập. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Người dùng mở module báo cáo và chọn loại báo cáo. |
| 2 | Hệ thống hiển thị bộ lọc (thời gian, tòa nhà, trạng thái). |
| 3 | Người dùng thiết lập bộ lọc và yêu cầu xuất báo cáo. |
| 4 | Hệ thống tổng hợp dữ liệu, tạo file theo định dạng đã chọn. |
| 5 | Hệ thống cung cấp liên kết tải về và lưu log. |

### Luồng thay thế
- 3a: Người dùng đặt lịch gửi báo cáo qua email định kỳ. |

### Các ngoại lệ
- E1: Dữ liệu vượt quá giới hạn → Hệ thống yêu cầu thu hẹp bộ lọc.
- E2: Quyền truy cập không đủ → Hệ thống hiển thị thông báo từ chối.

### Điều kiện nghiệp vụ
- Báo cáo tài chính cần có chữ ký số khi xuất bản chính thức.
- Lịch sử xuất báo cáo lưu tối thiểu 12 tháng.

### Ghi chú
- Hỗ trợ song song định dạng PDF và Excel.

### Các giả thiết
- ReportController sử dụng engine JasperReports hoặc tương đương.

---

## UC-RD-03 - Xem báo cáo thống kê theo phòng/sinh viên/hợp đồng/phí/vi phạm

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-RD-03 - Xem báo cáo thống kê |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Người dùng xem báo cáo trực tuyến về phòng, sinh viên, hợp đồng, phí, vi phạm. |
| Tác nhân chính | Quản trị viên/nhân viên |
| Tác nhân phụ | ReportController |
| Điều kiện tiên quyết | Người dùng có quyền truy cập báo cáo tương ứng. |
| Điều kiện kết thúc | Báo cáo hiển thị trên giao diện với biểu đồ/bảng chi tiết. |
| Hậu điều kiện | Người dùng có thể xuất báo cáo hoặc chia sẻ liên kết nội bộ. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Người dùng chọn báo cáo thống kê cần xem. |
| 2 | Hệ thống truy vấn dữ liệu dựa trên bộ lọc mặc định hoặc người dùng cấu hình. |
| 3 | Hệ thống hiển thị biểu đồ, bảng dữ liệu tương tác. |
| 4 | Người dùng drill-down vào chi tiết (ví dụ danh sách sinh viên trong phòng). |

### Luồng thay thế
- 2a: Người dùng lưu bộ lọc yêu thích để tái sử dụng. |

### Các ngoại lệ
- E1: Dữ liệu trống → Hệ thống thông báo và gợi ý điều chỉnh bộ lọc.

### Điều kiện nghiệp vụ
- Số liệu phải đồng nhất với các module nguồn (phòng, hợp đồng, phí, vi phạm).
- Chỉ người có quyền mới xem được dữ liệu nhạy cảm (phí, vi phạm).

### Ghi chú
- Nên hỗ trợ hiển thị trên thiết bị di động.

### Các giả thiết
- ReportController sử dụng cơ chế cache để tăng tốc độ truy vấn.
