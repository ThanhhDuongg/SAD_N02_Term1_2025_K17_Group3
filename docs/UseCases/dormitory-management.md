# Đặc tả Use Case - Quản lý ký túc xá

## UC-DM-01 - Quản lý tòa nhà

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-01 - Quản lý tòa nhà |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên quản lý danh mục tòa nhà (thêm mới, chỉnh sửa, xóa, xem chi tiết). |
| Tác nhân chính | Quản trị viên ký túc xá |
| Tác nhân phụ | BuildingController |
| Điều kiện tiên quyết | Quản trị viên đã đăng nhập và có quyền quản lý cơ sở vật chất. |
| Điều kiện kết thúc | Thông tin tòa nhà được cập nhật trong hệ thống. |
| Hậu điều kiện | Dữ liệu tòa nhà đồng bộ tới các chức năng phòng, hợp đồng. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập màn hình danh sách tòa nhà. |
| 2 | Quản trị viên chọn hành động thêm mới/chỉnh sửa/xem chi tiết. |
| 3 | Hệ thống hiển thị form tương ứng. |
| 4 | Quản trị viên nhập thông tin (tên, mã, số tầng, mô tả) và lưu. |
| 5 | Hệ thống kiểm tra trùng mã, validate dữ liệu. |
| 6 | Hệ thống lưu thông tin và cập nhật danh sách. |

### Luồng thay thế
- 2a: Khi xem chi tiết, hệ thống hiển thị danh sách phòng thuộc tòa nhà.

### Các ngoại lệ
- E1: Xóa tòa nhà có phòng đang hoạt động → Từ chối, yêu cầu di chuyển hoặc vô hiệu hóa phòng trước.
- E2: Trùng mã tòa nhà → Hiển thị cảnh báo.

### Điều kiện nghiệp vụ
- Mỗi tòa nhà phải có mã duy nhất.
- Không cho phép xóa tòa nhà nếu còn hợp đồng hiệu lực.

### Ghi chú
- Có thể cần export danh sách tòa nhà cho báo cáo.

### Các giả thiết
- Danh sách tòa nhà không quá 100 phần tử nên có thể load toàn bộ lên giao diện.

---

## UC-DM-02 - Quản lý phòng

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-02 - Quản lý phòng |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên quản lý phòng (CRUD, xem số lượng sinh viên). |
| Tác nhân chính | Quản trị viên ký túc xá |
| Tác nhân phụ | RoomController |
| Điều kiện tiên quyết | Danh mục tòa nhà đã tồn tại. |
| Điều kiện kết thúc | Thông tin phòng được cập nhật và gán đúng tòa nhà. |
| Hậu điều kiện | Số lượng sinh viên/phòng được tái tính toán. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập danh sách phòng. |
| 2 | Quản trị viên lọc theo tòa nhà, trạng thái để tìm phòng. |
| 3 | Quản trị viên chọn thao tác thêm/sửa/xóa/xem chi tiết. |
| 4 | Hệ thống hiển thị form thông tin phòng (loại phòng, số giường, giá cơ bản). |
| 5 | Quản trị viên cập nhật và lưu dữ liệu. |
| 6 | Hệ thống xác thực sức chứa và lưu vào cơ sở dữ liệu. |
| 7 | Hệ thống cập nhật thống kê số lượng sinh viên trong phòng. |

### Luồng thay thế
- 3a: Khi xem chi tiết, hệ thống hiển thị danh sách sinh viên đang ở và trạng thái giường trống.

### Các ngoại lệ
- E1: Sức chứa nhập nhỏ hơn số sinh viên hiện hữu → Cảnh báo và yêu cầu điều chỉnh.
- E2: Phòng có hợp đồng đang hiệu lực → Không cho phép xóa, chỉ có thể chuyển trạng thái sang không hoạt động.

### Điều kiện nghiệp vụ
- Mỗi phòng phải thuộc về một tòa nhà hợp lệ.
- Giá phòng có thể thay đổi theo loại phòng và được áp dụng cho hợp đồng mới.

### Ghi chú
- Nên có chức năng import phòng hàng loạt từ file Excel.

### Các giả thiết
- Số sinh viên trong phòng được tính từ hợp đồng còn hiệu lực.

---

## UC-DM-03 - Quản lý sinh viên nội trú

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-03 - Quản lý sinh viên nội trú |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Nhân viên KTX quản lý hồ sơ sinh viên, gắn hợp đồng khi thêm mới. |
| Tác nhân chính | Nhân viên quản lý sinh viên |
| Tác nhân phụ | StudentController |
| Điều kiện tiên quyết | Sinh viên đã có thông tin nhập học hợp lệ. |
| Điều kiện kết thúc | Hồ sơ sinh viên được lưu với trạng thái ở trọ. |
| Hậu điều kiện | Nếu tạo mới, hệ thống tạo hợp đồng hoặc liên kết tới hợp đồng hiện có. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Nhân viên truy cập danh sách sinh viên. |
| 2 | Nhân viên tìm kiếm theo MSSV, họ tên hoặc trạng thái. |
| 3 | Nhân viên chọn thao tác thêm/sửa/xem/xóa. |
| 4 | Hệ thống hiển thị form hồ sơ (thông tin cá nhân, liên hệ, phòng ở). |
| 5 | Nhân viên cập nhật thông tin và xác nhận lưu. |
| 6 | Hệ thống validate dữ liệu, kiểm tra trùng MSSV. |
| 7 | Hệ thống lưu thông tin và liên kết hợp đồng (nếu tạo mới). |

### Luồng thay thế
- 5a: Khi thêm mới, hệ thống cho phép chọn phòng và tạo hợp đồng tạm để giữ chỗ.

### Các ngoại lệ
- E1: MSSV đã tồn tại → Từ chối tạo mới, yêu cầu cập nhật hồ sơ hiện có.
- E2: Sinh viên có hợp đồng đang hiệu lực → Không cho phép xóa, chỉ đổi trạng thái.

### Điều kiện nghiệp vụ
- Sinh viên phải có ít nhất một thông tin liên hệ khẩn cấp.
- Hệ thống phải ghi nhận lịch sử điều chuyển phòng.

### Ghi chú
- Hỗ trợ import sinh viên từ file do phòng CTSV cung cấp.

### Các giả thiết
- Các thông tin sinh viên đồng bộ với hệ thống quản lý đào tạo.

---

## UC-DM-04 - Sinh viên xem dashboard cá nhân

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-04 - Sinh viên xem dashboard cá nhân |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên xem tổng quan tình trạng ở trọ, hợp đồng, phí, thông báo. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | StudentPortalController |
| Điều kiện tiên quyết | Sinh viên đã đăng nhập portal. |
| Điều kiện kết thúc | Dashboard hiển thị dữ liệu mới nhất cho sinh viên. |
| Hậu điều kiện | Lượt xem được ghi nhận để thống kê mức độ sử dụng. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên đăng nhập và truy cập dashboard. |
| 2 | Hệ thống lấy dữ liệu hợp đồng, phí, vi phạm, thông báo của sinh viên. |
| 3 | Hệ thống tổng hợp thành widget (hợp đồng hiện tại, phí chưa thanh toán, vi phạm gần nhất). |
| 4 | Dashboard hiển thị và cho phép truy cập nhanh đến các module chi tiết. |

### Luồng thay thế
- 2a: Nếu sinh viên chưa có hợp đồng, hệ thống hiển thị lời nhắc đăng ký ký túc xá. 

### Các ngoại lệ
- E1: Dữ liệu một module lỗi → Hiển thị thông báo thay thế nhưng vẫn cho phép xem dữ liệu khác.

### Điều kiện nghiệp vụ
- Dashboard phải cập nhật dữ liệu thời gian thực hoặc sau mỗi 5 phút.

### Ghi chú
- Hỗ trợ tùy biến widget theo nhu cầu sinh viên trong tương lai.

### Các giả thiết
- StudentPortalController đã tích hợp với các service thống kê.

---

## UC-DM-05 - Sinh viên cập nhật hồ sơ cá nhân

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-05 - Sinh viên cập nhật hồ sơ cá nhân |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên chỉnh sửa thông tin liên lạc, người thân, tài khoản ngân hàng trên portal. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | StudentPortalController, StudentController |
| Điều kiện tiên quyết | Sinh viên đã đăng nhập; hồ sơ hiện có trong hệ thống. |
| Điều kiện kết thúc | Dữ liệu cập nhật được lưu và chờ duyệt (nếu cần). |
| Hậu điều kiện | Các thay đổi được ghi log và thông báo tới nhân viên quản lý. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên truy cập mục hồ sơ cá nhân. |
| 2 | Hệ thống hiển thị thông tin hiện tại. |
| 3 | Sinh viên chỉnh sửa các trường được phép (số điện thoại, địa chỉ, người liên hệ khẩn cấp). |
| 4 | Sinh viên lưu thay đổi. |
| 5 | Hệ thống validate, lưu dữ liệu và thông báo kết quả. |

### Luồng thay thế
- 4a: Nếu thay đổi thông tin nhạy cảm (CMND, tài khoản ngân hàng), hệ thống chuyển trạng thái chờ duyệt bởi nhân viên.

### Các ngoại lệ
- E1: Sinh viên nhập thiếu thông tin bắt buộc → Hiển thị thông báo lỗi theo trường.
- E2: Sinh viên cố thay đổi trường không được phép → Hệ thống từ chối và ghi log bảo mật.

### Điều kiện nghiệp vụ
- Lịch sử thay đổi hồ sơ phải lưu tối thiểu 12 tháng.

### Ghi chú
- Có thể bật thông báo email khi hồ sơ được duyệt.

### Các giả thiết
- Hệ thống có cơ chế duyệt thay đổi thông tin quan trọng.

---

## UC-DM-06 - Sinh viên xem hợp đồng, phí, vi phạm, thông báo

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-DM-06 - Sinh viên xem hợp đồng, phí, vi phạm, thông báo |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên truy cập các module chi tiết để xem hợp đồng, lịch sử phí, biên bản vi phạm và thông báo nhận được. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | StudentPortalController |
| Điều kiện tiên quyết | Sinh viên đã đăng nhập; dữ liệu liên quan tồn tại. |
| Điều kiện kết thúc | Thông tin được hiển thị đầy đủ theo từng tab/module. |
| Hậu điều kiện | Trạng thái đọc thông báo được cập nhật. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên chọn module (Hợp đồng/Phí/Vi phạm/Thông báo). |
| 2 | Hệ thống truy vấn dữ liệu tương ứng từ các service. |
| 3 | Hệ thống trình bày thông tin dạng bảng hoặc thẻ chi tiết. |
| 4 | Sinh viên xem chi tiết từng mục; đối với thông báo, trạng thái chuyển sang đã đọc. |

### Luồng thay thế
- 3a: Sinh viên tải về file hợp đồng hoặc hóa đơn nếu được cấp quyền.

### Các ngoại lệ
- E1: Dữ liệu không tồn tại → Hiển thị thông điệp "Chưa có dữ liệu".

### Điều kiện nghiệp vụ
- Thông tin phí phải hiển thị trạng thái thanh toán và hạn đóng.
- Vi phạm phải thể hiện điểm trừ hoặc mức phạt kèm biên bản.

### Ghi chú
- Cần đảm bảo phân trang khi danh sách quá dài.

### Các giả thiết
- Các service hợp đồng, phí, vi phạm đã sẵn API cho portal.
