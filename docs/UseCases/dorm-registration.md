# Đặc tả Use Case - Quản lý đăng ký ký túc xá

## UC-DR-01 - Mở đợt đăng ký ký túc xá

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DR-01 - Mở đợt đăng ký ký túc xá |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên cấu hình và mở đợt đăng ký KTX cho sinh viên. |
| Tác nhân chính | Quản trị viên KTX |
| Tác nhân phụ | DormRegistrationPeriodController |
| Điều kiện tiên quyết | Quản trị viên đã đăng nhập; kế hoạch năm học đã được phê duyệt. |
| Điều kiện kết thúc | Đợt đăng ký được tạo với thời gian bắt đầu/kết thúc, trạng thái mở. |
| Hậu điều kiện | Thông báo mở đợt gửi tới sinh viên, các chức năng đăng ký được kích hoạt. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập trang quản lý đợt đăng ký. |
| 2 | Quản trị viên chọn "Tạo đợt mới". |
| 3 | Hệ thống hiển thị form thông tin (tên đợt, mô tả, thời gian, số lượng chỗ). |
| 4 | Quản trị viên nhập thông tin và lưu. |
| 5 | Hệ thống kiểm tra trùng thời gian với đợt khác và validate dữ liệu. |
| 6 | Hệ thống lưu đợt đăng ký ở trạng thái "Sẵn sàng" hoặc "Đang mở" theo cấu hình. |

### Luồng thay thế
- 6a: Nếu chọn "Tự động mở", hệ thống chuyển trạng thái sang "Đang mở" khi tới thời điểm bắt đầu.

### Các ngoại lệ
- E1: Thời gian đợt đăng ký trùng với đợt đã mở → Hệ thống từ chối và yêu cầu điều chỉnh.

### Điều kiện nghiệp vụ
- Mỗi thời điểm chỉ có tối đa một đợt đăng ký mở.
- Số lượng chỗ phải phù hợp với phòng trống thực tế.

### Ghi chú
- Cần cho phép thiết lập tiêu chí ưu tiên xét duyệt (điểm rèn luyện, đối tượng chính sách).

### Các giả thiết
- Dữ liệu phòng trống được cập nhật chính xác từ module quản lý phòng.

---

## UC-DR-02 - Đóng đợt đăng ký ký túc xá

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DR-02 - Đóng đợt đăng ký ký túc xá |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên kết thúc đợt đăng ký và ngừng nhận yêu cầu mới. |
| Tác nhân chính | Quản trị viên KTX |
| Tác nhân phụ | DormRegistrationPeriodController |
| Điều kiện tiên quyết | Đợt đăng ký đang ở trạng thái mở. |
| Điều kiện kết thúc | Đợt đăng ký chuyển sang trạng thái "Đã đóng". |
| Hậu điều kiện | Hệ thống không cho phép sinh viên gửi yêu cầu mới; thông báo được phát đi. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập danh sách đợt đăng ký. |
| 2 | Quản trị viên chọn đợt đang mở và bấm "Đóng đợt". |
| 3 | Hệ thống yêu cầu xác nhận thao tác. |
| 4 | Sau khi xác nhận, hệ thống cập nhật trạng thái sang "Đã đóng" và ghi log. |

### Luồng thay thế
- 2a: Quản trị viên lên lịch đóng tự động → hệ thống đóng khi hết hạn.

### Các ngoại lệ
- E1: Đợt đã đóng trước đó → Hệ thống thông báo và không thực hiện lại.

### Điều kiện nghiệp vụ
- Trước khi đóng đợt phải xử lý tất cả yêu cầu ở trạng thái "Nháp".

### Ghi chú
- Khi đóng đợt, có thể tự động xuất danh sách yêu cầu để lưu trữ.

### Các giả thiết
- Cron job kiểm tra trạng thái đợt chạy đúng lịch.

---

## UC-DR-03 - Sinh viên gửi yêu cầu đăng ký

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DR-03 - Sinh viên gửi yêu cầu đăng ký |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên điền thông tin và nộp yêu cầu đăng ký chỗ ở trong đợt mở. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | DormRegistrationRequestController |
| Điều kiện tiên quyết | Đợt đăng ký đang mở, sinh viên đã đăng nhập portal. |
| Điều kiện kết thúc | Yêu cầu đăng ký được lưu ở trạng thái "Chờ duyệt". |
| Hậu điều kiện | Sinh viên nhận email xác nhận; yêu cầu xuất hiện trong hàng chờ duyệt. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên truy cập chức năng đăng ký KTX. |
| 2 | Hệ thống hiển thị form bao gồm thông tin cá nhân, nhu cầu phòng, minh chứng ưu tiên. |
| 3 | Sinh viên điền thông tin, tải lên giấy tờ và nộp. |
| 4 | Hệ thống kiểm tra điều kiện (đợt mở, chưa có yêu cầu chờ duyệt) và lưu bản ghi. |
| 5 | Hệ thống gửi thông báo xác nhận và hiển thị trạng thái yêu cầu. |

### Luồng thay thế
- 3a: Sinh viên lưu bản nháp để hoàn thiện sau → trạng thái "Nháp".

### Các ngoại lệ
- E1: Sinh viên đã có hợp đồng đang hiệu lực → Hệ thống từ chối đăng ký mới. 
- E2: Đợt đăng ký đã đủ chỉ tiêu → Hệ thống thông báo đóng nhận yêu cầu.

### Điều kiện nghiệp vụ
- Mỗi sinh viên chỉ có tối đa một yêu cầu ở trạng thái mở mỗi đợt.
- Minh chứng ưu tiên bắt buộc đối với đối tượng chính sách.

### Ghi chú
- Cần kiểm soát dung lượng file tải lên.

### Các giả thiết
- Các trường thông tin được tự động điền từ hồ sơ sinh viên để giảm thời gian nhập liệu.

---

## UC-DR-04 - Duyệt hoặc từ chối yêu cầu đăng ký

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DR-04 - Duyệt hoặc từ chối yêu cầu đăng ký |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên xem xét, phê duyệt hoặc từ chối yêu cầu đăng ký của sinh viên. |
| Tác nhân chính | Quản trị viên KTX |
| Tác nhân phụ | DormRegistrationRequestController |
| Điều kiện tiên quyết | Yêu cầu ở trạng thái "Chờ duyệt"; đợt đăng ký còn hiệu lực. |
| Điều kiện kết thúc | Yêu cầu chuyển sang trạng thái "Đã duyệt" hoặc "Từ chối". |
| Hậu điều kiện | Nếu duyệt, hệ thống khóa chỗ tạm thời và tạo hướng dẫn lập hợp đồng. Nếu từ chối, thông báo lý do cho sinh viên. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập danh sách yêu cầu chờ duyệt. |
| 2 | Quản trị viên xem chi tiết từng yêu cầu và minh chứng. |
| 3 | Quản trị viên chọn phê duyệt hoặc từ chối. |
| 4 | Hệ thống yêu cầu nhập ghi chú hoặc lý do. |
| 5 | Hệ thống cập nhật trạng thái và gửi thông báo đến sinh viên. |
| 6 | Nếu phê duyệt, hệ thống gợi ý tạo hợp đồng mới hoặc chuyển tới module hợp đồng. |

### Luồng thay thế
- 2a: Quản trị viên lọc danh sách theo tiêu chí ưu tiên (điểm rèn luyện, khóa học).

### Các ngoại lệ
- E1: Đợt đăng ký đã đóng → Không thể duyệt, hệ thống cảnh báo.
- E2: Quản trị viên vượt quá quota phê duyệt → Hệ thống yêu cầu điều chỉnh lại chỉ tiêu.

### Điều kiện nghiệp vụ
- Quyết định duyệt phải căn cứ vào tiêu chí ưu tiên được thiết lập trước.
- Hệ thống lưu lịch sử phê duyệt (ngày giờ, người thực hiện, kết quả).

### Ghi chú
- Nên hỗ trợ phê duyệt hàng loạt.

### Các giả thiết
- Module phân bổ phòng sẽ xử lý tiếp sau khi yêu cầu được duyệt.
