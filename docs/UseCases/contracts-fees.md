# Đặc tả Use Case - Quản lý hợp đồng & phí

## UC-CF-01 - Tạo mới hợp đồng cho sinh viên

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-CF-01 - Tạo mới hợp đồng cho sinh viên |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Nhân viên KTX lập hợp đồng thuê phòng cho sinh viên, xác định thời hạn và chi phí. |
| Tác nhân chính | Nhân viên phụ trách hợp đồng |
| Tác nhân phụ | ContractController |
| Điều kiện tiên quyết | Sinh viên đã có hồ sơ, phòng có chỗ trống, kỳ đăng ký đang mở hoặc được phép tạo thủ công. |
| Điều kiện kết thúc | Hợp đồng mới được lưu ở trạng thái hiệu lực/đang chờ duyệt. |
| Hậu điều kiện | Thông tin phí và lịch thanh toán được khởi tạo theo hợp đồng. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Nhân viên chọn sinh viên và phòng muốn lập hợp đồng. |
| 2 | Hệ thống hiển thị form nhập thời hạn, giá, cọc, điều khoản. |
| 3 | Nhân viên nhập thông tin và lưu. |
| 4 | Hệ thống kiểm tra trùng hợp đồng đang hiệu lực và năng lực phòng. |
| 5 | Hệ thống tạo hợp đồng mới, phát sinh mã hợp đồng. |
| 6 | Hệ thống kích hoạt các khoản phí định kỳ tương ứng. |

### Luồng thay thế
- 3a: Nhân viên tải lên file hợp đồng scan; hệ thống lưu kèm đường dẫn.
- 6a: Nếu hợp đồng cần duyệt, trạng thái đặt là "Chờ duyệt" và gửi thông báo tới quản trị viên.

### Các ngoại lệ
- E1: Phòng đã đủ sinh viên → Từ chối, yêu cầu chọn phòng khác.
- E2: Sinh viên có hợp đồng trùng thời gian → Thông báo và dừng thao tác.

### Điều kiện nghiệp vụ
- Thời hạn hợp đồng tối thiểu 1 tháng, tối đa 12 tháng.
- Mã hợp đồng theo định dạng `CT-<Năm>-<Số tự tăng>`.

### Ghi chú
- Cho phép lưu nháp hợp đồng khi chưa đủ thông tin.

### Các giả thiết
- Hệ thống tính phí dựa trên giá phòng và bảng phí mặc định đã cấu hình.

---

## UC-CF-02 - Cập nhật hoặc chấm dứt hợp đồng

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-CF-02 - Cập nhật hoặc chấm dứt hợp đồng |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Nhân viên cập nhật thông tin hợp đồng hoặc kết thúc trước hạn. |
| Tác nhân chính | Nhân viên phụ trách hợp đồng |
| Tác nhân phụ | ContractController |
| Điều kiện tiên quyết | Hợp đồng tồn tại và ở trạng thái cho phép chỉnh sửa. |
| Điều kiện kết thúc | Hợp đồng cập nhật thành công, trạng thái mới được lưu. |
| Hậu điều kiện | Phí liên quan được điều chỉnh hoặc kết thúc theo thời hạn mới. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Nhân viên tìm kiếm và mở hợp đồng cần chỉnh sửa. |
| 2 | Hệ thống hiển thị chi tiết hợp đồng. |
| 3 | Nhân viên cập nhật thông tin (thời hạn, phòng, trạng thái). |
| 4 | Hệ thống kiểm tra các ràng buộc (phòng còn chỗ, không trùng hợp đồng khác). |
| 5 | Hệ thống lưu thay đổi và cập nhật các khoản phí liên quan. |

### Luồng thay thế
- 3a: Nhân viên chọn chấm dứt hợp đồng → hệ thống yêu cầu nhập lý do và ngày kết thúc.
- 5a: Khi chấm dứt, hệ thống tính toán phí hoàn trả hoặc bù trừ và ghi nhận vào lịch sử.

### Các ngoại lệ
- E1: Hợp đồng đã kết thúc → Không cho phép chỉnh sửa.
- E2: Sinh viên đang có yêu cầu chuyển phòng → Hệ thống cảnh báo để xử lý trước.

### Điều kiện nghiệp vụ
- Mọi thay đổi phải được lưu trong lịch sử với thông tin người thao tác.
- Chấm dứt hợp đồng trước hạn yêu cầu phê duyệt của quản trị viên.

### Ghi chú
- Nên hỗ trợ tải biên bản thanh lý lên hệ thống.

### Các giả thiết
- Tất cả khoản phí được cấu hình theo hợp đồng tự động cập nhật khi thay đổi thời hạn.

---

## UC-CF-03 - Tìm kiếm hợp đồng theo sinh viên hoặc phòng

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-CF-03 - Tìm kiếm hợp đồng theo sinh viên hoặc phòng |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Người dùng nội bộ tra cứu hợp đồng dựa trên MSSV hoặc mã phòng. |
| Tác nhân chính | Quản trị viên/nhân viên |
| Tác nhân phụ | ContractController |
| Điều kiện tiên quyết | Người dùng đã đăng nhập và có quyền xem hợp đồng. |
| Điều kiện kết thúc | Danh sách hợp đồng phù hợp được hiển thị. |
| Hậu điều kiện | Người dùng có thể chọn hợp đồng để xem chi tiết hoặc thao tác tiếp. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Người dùng mở trang tìm kiếm hợp đồng. |
| 2 | Người dùng nhập tiêu chí (MSSV, tên sinh viên, mã phòng, trạng thái). |
| 3 | Hệ thống truy vấn và trả về danh sách hợp đồng phù hợp. |
| 4 | Người dùng chọn một hợp đồng để xem chi tiết. |

### Luồng thay thế
- 2a: Người dùng lọc thêm theo khoảng thời gian ký hợp đồng.

### Các ngoại lệ
- E1: Không tìm thấy hợp đồng → Hiển thị thông điệp tương ứng.

### Điều kiện nghiệp vụ
- Người dùng chỉ xem được hợp đồng thuộc phạm vi quyền hạn (ví dụ nhân viên tòa nhà chỉ xem hợp đồng tòa nhà phụ trách).

### Ghi chú
- Cần hỗ trợ export danh sách hợp đồng ra Excel.

### Các giả thiết
- Dữ liệu hợp đồng được lập chỉ mục để đáp ứng tìm kiếm nhanh.

---

## UC-CF-04 - Quản lý phí (thêm, sửa, xóa, xem danh sách)

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-CF-04 - Quản lý phí |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Nhân viên tài chính thiết lập các khoản phí điện, nước, dịch vụ và phân bổ cho hợp đồng/phòng. |
| Tác nhân chính | Nhân viên tài chính |
| Tác nhân phụ | FeeController |
| Điều kiện tiên quyết | Danh mục loại phí và hợp đồng liên quan đã tồn tại. |
| Điều kiện kết thúc | Khoản phí được tạo hoặc cập nhật chính xác. |
| Hậu điều kiện | Số dư công nợ và bảng kê phí được cập nhật. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Nhân viên mở màn hình quản lý phí. |
| 2 | Nhân viên thêm mới hoặc chọn khoản phí cần chỉnh sửa. |
| 3 | Hệ thống hiển thị form với thông tin (loại phí, kỳ áp dụng, số tiền, đối tượng áp dụng). |
| 4 | Nhân viên nhập/cập nhật dữ liệu và lưu. |
| 5 | Hệ thống validate (không âm, đúng định dạng) và lưu vào cơ sở dữ liệu. |
| 6 | Hệ thống cập nhật danh sách và tổng hợp phí liên quan. |

### Luồng thay thế
- 2a: Nhân viên nhập file hóa đơn điện/nước hàng loạt → hệ thống tự động tạo khoản phí cho từng phòng.

### Các ngoại lệ
- E1: Loại phí không hợp lệ → Từ chối lưu và hiển thị thông báo.
- E2: Khoản phí đã được thu → Không được phép xóa, chỉ điều chỉnh với biên bản.

### Điều kiện nghiệp vụ
- Phí phải gắn với hợp đồng hoặc phòng cụ thể.
- Các thay đổi sau khi chốt sổ phải có phê duyệt của kế toán trưởng.

### Ghi chú
- Nên có báo cáo công nợ theo khoản phí để đối soát nhanh.

### Các giả thiết
- FeeController tích hợp với hệ thống thanh toán nội bộ để đồng bộ trạng thái.
