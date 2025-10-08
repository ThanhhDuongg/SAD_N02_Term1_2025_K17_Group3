# Đặc tả Use Case - Quản lý bảo trì & vi phạm

## UC-MV-01 - Sinh viên gửi yêu cầu bảo trì

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-MV-01 - Sinh viên gửi yêu cầu bảo trì |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên báo cáo sự cố cơ sở vật chất tại phòng ở thông qua portal. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | MaintenanceRequestController |
| Điều kiện tiên quyết | Sinh viên đã đăng nhập; phòng thuộc quyền sử dụng của sinh viên. |
| Điều kiện kết thúc | Yêu cầu bảo trì được tạo với trạng thái "Chờ xử lý". |
| Hậu điều kiện | Email/thông báo được gửi đến bộ phận kỹ thuật. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên mở chức năng "Yêu cầu bảo trì" trên portal. |
| 2 | Hệ thống hiển thị form yêu cầu (loại sự cố, mô tả, hình ảnh đính kèm). |
| 3 | Sinh viên nhập thông tin và gửi yêu cầu. |
| 4 | Hệ thống kiểm tra sinh viên có hợp đồng hiệu lực và phòng hợp lệ. |
| 5 | Hệ thống lưu yêu cầu ở trạng thái "Chờ xử lý", gán mã ticket và gửi thông báo. |

### Luồng thay thế
- 3a: Sinh viên lưu yêu cầu dạng nháp để bổ sung thông tin sau.

### Các ngoại lệ
- E1: Sinh viên chưa có hợp đồng hiệu lực → Hệ thống từ chối tạo yêu cầu.
- E2: File đính kèm vượt dung lượng → Thông báo và yêu cầu giảm kích thước.

### Điều kiện nghiệp vụ
- Mỗi sự cố phải gắn với phòng hoặc khu vực cụ thể.
- Yêu cầu bảo trì có SLA xử lý tùy mức độ ưu tiên.

### Ghi chú
- Nên cho phép sinh viên theo dõi trạng thái xử lý theo thời gian thực.

### Các giả thiết
- Hệ thống thông báo nội bộ có thể gửi email/push tới kỹ thuật viên trực ca.

---

## UC-MV-02 - Kỹ thuật viên xử lý yêu cầu bảo trì

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-MV-02 - Kỹ thuật viên xử lý yêu cầu bảo trì |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Kỹ thuật viên tiếp nhận, cập nhật tiến độ và hoàn tất yêu cầu bảo trì. |
| Tác nhân chính | Kỹ thuật viên/Bộ phận bảo trì |
| Tác nhân phụ | MaintenanceRequestController |
| Điều kiện tiên quyết | Yêu cầu bảo trì tồn tại và được phân công cho kỹ thuật viên. |
| Điều kiện kết thúc | Yêu cầu chuyển sang trạng thái hoàn tất hoặc chuyển escalated. |
| Hậu điều kiện | Biên bản xử lý và thời gian hoàn thành được ghi nhận. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Kỹ thuật viên đăng nhập và truy cập danh sách yêu cầu được phân công. |
| 2 | Kỹ thuật viên xem chi tiết yêu cầu và chuẩn bị vật tư cần thiết. |
| 3 | Kỹ thuật viên cập nhật trạng thái sang "Đang xử lý". |
| 4 | Sau khi sửa chữa, kỹ thuật viên nhập kết quả, hình ảnh trước/sau và đánh dấu "Hoàn tất". |
| 5 | Hệ thống ghi nhận thời gian hoàn thành, gửi thông báo cho sinh viên. |

### Luồng thay thế
- 3a: Nếu không thể xử lý, kỹ thuật viên chuyển trạng thái "Cần hỗ trợ" và ghi chú lý do.

### Các ngoại lệ
- E1: Yêu cầu không thuộc phạm vi → Kỹ thuật viên trả lại để quản trị viên phân công lại.
- E2: Thiết bị/linh kiện chưa có sẵn → Chuyển trạng thái "Chờ vật tư" và cập nhật lịch hẹn.

### Điều kiện nghiệp vụ
- Thời gian xử lý phải trong SLA theo loại sự cố.
- Biên bản hoàn thành phải có chữ ký điện tử hoặc xác nhận của sinh viên.

### Ghi chú
- Có thể tích hợp đánh giá mức độ hài lòng sau khi hoàn tất.

### Các giả thiết
- Hệ thống phân công tự động dựa trên lịch trực hoặc khu vực phụ trách.

---

## UC-MV-03 - Quản lý vi phạm sinh viên

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-MV-03 - Quản lý vi phạm sinh viên |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên ghi nhận, phân loại và thống kê vi phạm nội quy của sinh viên. |
| Tác nhân chính | Quản trị viên/Kỷ luật viên |
| Tác nhân phụ | ViolationController |
| Điều kiện tiên quyết | Sinh viên có hồ sơ trong hệ thống. |
| Điều kiện kết thúc | Vi phạm được lưu với mức độ, hình thức xử lý tương ứng. |
| Hậu điều kiện | Điểm rèn luyện/cảnh cáo được cập nhật; thông báo gửi tới sinh viên. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên mở module quản lý vi phạm. |
| 2 | Quản trị viên thêm mới bản ghi vi phạm với thông tin (loại vi phạm, thời gian, mô tả, mức phạt). |
| 3 | Hệ thống kiểm tra sinh viên và phòng hợp lệ. |
| 4 | Hệ thống lưu bản ghi, cập nhật thống kê vi phạm theo tháng. |
| 5 | Hệ thống gửi thông báo tới sinh viên và các bên liên quan. |

### Luồng thay thế
- 2a: Quản trị viên đính kèm biên bản hoặc hình ảnh minh chứng.

### Các ngoại lệ
- E1: Sinh viên không thuộc KTX → Hệ thống từ chối ghi nhận.

### Điều kiện nghiệp vụ
- Vi phạm phải phân loại theo mức độ (nhẹ, trung bình, nặng).
- Các vi phạm nghiêm trọng yêu cầu họp hội đồng kỷ luật trước khi xác nhận.

### Ghi chú
- Cần cung cấp báo cáo tổng hợp vi phạm theo phòng, tòa nhà.

### Các giả thiết
- ViolationController hỗ trợ xuất dữ liệu để chia sẻ với phòng công tác sinh viên.
