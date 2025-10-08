# Đặc tả Use Case - Quản lý hệ thống

## UC-SM-01 - Đăng nhập hệ thống

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-01 - Đăng nhập hệ thống |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Người dùng nhập thông tin xác thực để truy cập các chức năng phù hợp với vai trò được cấp. |
| Tác nhân chính | Người dùng nội bộ (Admin, Nhân viên hỗ trợ) hoặc Sinh viên |
| Tác nhân phụ | Hệ thống xác thực (AuthController, Spring Security) |
| Điều kiện tiên quyết | Người dùng đã có tài khoản hợp lệ trong hệ thống; trình duyệt có kết nối mạng ổn định. |
| Điều kiện kết thúc | Phiên đăng nhập được tạo, người dùng được điều hướng tới trang phù hợp với vai trò. |
| Hậu điều kiện | Thông tin phiên và thời gian đăng nhập được ghi nhận để phục vụ audit. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Người dùng truy cập trang đăng nhập và nhập email/tên đăng nhập cùng mật khẩu. |
| 2 | Hệ thống kiểm tra định dạng dữ liệu đầu vào. |
| 3 | Hệ thống xác thực thông tin đăng nhập với cơ sở dữ liệu người dùng. |
| 4 | Nếu hợp lệ, hệ thống tạo session/token và gán các quyền tương ứng. |
| 5 | Hệ thống chuyển hướng người dùng tới dashboard phù hợp (admin hoặc portal sinh viên). |

### Luồng thay thế
- 3a. Nếu người dùng bật tùy chọn "Ghi nhớ đăng nhập", hệ thống tạo refresh token để duy trì phiên lâu hơn.
- 4a. Nếu yêu cầu xác thực đa yếu tố (nếu được cấu hình), hệ thống gửi mã OTP và yêu cầu người dùng nhập trước khi hoàn tất bước 4.

### Các ngoại lệ
- E1: Thông tin đăng nhập không chính xác → Hiển thị thông báo lỗi và cho phép nhập lại.
- E2: Tài khoản bị khóa hoặc chưa kích hoạt → Thông báo trạng thái tài khoản và hướng dẫn liên hệ quản trị.
- E3: Hệ thống xác thực gặp lỗi dịch vụ → Ghi log và chuyển hướng tới trang lỗi chung.

### Điều kiện nghiệp vụ
- Mật khẩu phải đúng chính sách độ dài, ký tự đặc biệt.
- Số lần đăng nhập sai quá 5 lần liên tiếp sẽ khóa tài khoản trong 15 phút.

### Ghi chú
- Các sự kiện đăng nhập thành công/thất bại cần gửi tới module giám sát bảo mật.

### Các giả thiết
- Spring Security đã được cấu hình chuẩn với AuthController và nguồn dữ liệu người dùng.

---

## UC-SM-02 - Đăng xuất hệ thống

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-02 - Đăng xuất hệ thống |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Người dùng chủ động kết thúc phiên làm việc và hệ thống xóa thông tin phiên. |
| Tác nhân chính | Người dùng đã đăng nhập |
| Tác nhân phụ | AuthController, Spring Security |
| Điều kiện tiên quyết | Người dùng đã đăng nhập thành công. |
| Điều kiện kết thúc | Phiên bị hủy, người dùng được chuyển tới trang đăng nhập. |
| Hậu điều kiện | Thời điểm đăng xuất được ghi nhận trong nhật ký hệ thống. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Người dùng chọn chức năng "Đăng xuất" từ menu hệ thống. |
| 2 | Hệ thống hủy session/token đang hoạt động. |
| 3 | Hệ thống ghi log đăng xuất. |
| 4 | Hệ thống điều hướng về trang đăng nhập hoặc trang chủ công khai. |

### Luồng thay thế
- Không có.

### Các ngoại lệ
- E1: Phiên đã hết hạn trước đó → Hệ thống hiển thị thông báo và chuyển tới trang đăng nhập.

### Điều kiện nghiệp vụ
- Phải bảo đảm token bị vô hiệu ngay cả khi người dùng mở nhiều tab.

### Ghi chú
- Nút đăng xuất phải xuất hiện trên mọi layout sau khi người dùng đăng nhập.

### Các giả thiết
- Cơ chế CSRF được bật để bảo vệ yêu cầu đăng xuất.

---

## UC-SM-03 - Đăng ký tài khoản

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-03 - Đăng ký tài khoản |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Sinh viên tự tạo tài khoản để truy cập portal, thông tin đăng ký được xác minh trước khi kích hoạt. |
| Tác nhân chính | Sinh viên |
| Tác nhân phụ | AuthController, Email Service |
| Điều kiện tiên quyết | Cổng đăng ký mở, sinh viên có mã số và email trường cấp. |
| Điều kiện kết thúc | Tài khoản ở trạng thái "Chờ xác minh" hoặc "Hoạt động" tùy chính sách. |
| Hậu điều kiện | Email xác nhận được gửi tới sinh viên; bản ghi người dùng mới được tạo trong hệ thống. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Sinh viên truy cập trang đăng ký và nhập thông tin cá nhân (MSSV, email, mật khẩu). |
| 2 | Hệ thống kiểm tra trùng lặp MSSV/email. |
| 3 | Hệ thống lưu tài khoản ở trạng thái chờ duyệt hoặc kích hoạt ngay tùy cấu hình. |
| 4 | Hệ thống gửi email xác nhận/kích hoạt. |
| 5 | Sinh viên xác nhận qua email (nếu cần) và có thể đăng nhập. |

### Luồng thay thế
- 3a: Nếu tài khoản cần duyệt thủ công, quản trị viên sẽ nhận thông báo và kích hoạt ở use case UC-SM-04.

### Các ngoại lệ
- E1: Thông tin bắt buộc thiếu hoặc sai định dạng → Thông báo lỗi chi tiết cho từng trường.
- E2: MSSV/email đã tồn tại → Gợi ý sử dụng chức năng quên mật khẩu.

### Điều kiện nghiệp vụ
- Mật khẩu phải đáp ứng chính sách bảo mật của hệ thống.
- MSSV phải khớp với danh sách sinh viên do phòng CTSV cung cấp.

### Ghi chú
- Cần chống spam đăng ký bằng captcha hoặc rate limit.

### Các giả thiết
- Hệ thống có sẵn dịch vụ gửi email hoạt động ổn định.

---

## UC-SM-04 - Quản lý thông tin tài khoản

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-04 - Quản lý thông tin tài khoản |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên cập nhật, khóa/mở khóa hoặc xóa tài khoản người dùng. |
| Tác nhân chính | Quản trị viên hệ thống |
| Tác nhân phụ | AccountController, UserManagementController |
| Điều kiện tiên quyết | Quản trị viên đã đăng nhập với quyền quản lý người dùng. |
| Điều kiện kết thúc | Thông tin tài khoản được cập nhật và lưu xuống cơ sở dữ liệu. |
| Hậu điều kiện | Ghi log hành động quản trị để phục vụ kiểm toán. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên truy cập danh sách tài khoản. |
| 2 | Quản trị viên chọn tài khoản cần thao tác (xem chi tiết, cập nhật, khóa). |
| 3 | Hệ thống hiển thị form chi tiết tài khoản. |
| 4 | Quản trị viên chỉnh sửa thông tin (họ tên, email, trạng thái, vai trò cơ bản) và lưu. |
| 5 | Hệ thống xác thực dữ liệu và cập nhật vào cơ sở dữ liệu. |
| 6 | Hệ thống phản hồi kết quả thành công và cập nhật giao diện danh sách. |

### Luồng thay thế
- 2a: Quản trị viên sử dụng bộ lọc/tìm kiếm để nhanh chóng tìm tài khoản theo email/MSSV.
- 4a: Quản trị viên chọn hành động khóa/xóa tài khoản → hệ thống yêu cầu xác nhận trước khi thực thi.

### Các ngoại lệ
- E1: Không tìm thấy tài khoản → Hiển thị thông báo lỗi và yêu cầu kiểm tra lại thông tin.
- E2: Lỗi ghi dữ liệu → Thông báo thất bại, ghi log lỗi và giữ nguyên dữ liệu cũ.

### Điều kiện nghiệp vụ
- Chỉ tài khoản ở trạng thái "Hoạt động" mới có thể bị khóa.
- Không được phép xóa tài khoản gắn với hợp đồng đang hiệu lực; phải khóa thay vì xóa.

### Ghi chú
- Cần theo dõi lịch sử thay đổi bằng bảng audit hoặc versioning. 

### Các giả thiết
- Quản trị viên thuộc nhóm quyền `ROLE_ADMIN` và không bị giới hạn chức năng.

---

## UC-SM-05 - Phân quyền người dùng

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-05 - Phân quyền người dùng |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Quản trị viên gán vai trò (Admin, Student, Lecturer, Staff) cho người dùng để kiểm soát phạm vi truy cập. |
| Tác nhân chính | Quản trị viên hệ thống |
| Tác nhân phụ | UserManagementController, GlobalModelAttributes |
| Điều kiện tiên quyết | Người dùng mục tiêu đã tồn tại; danh sách vai trò được định nghĩa trong hệ thống. |
| Điều kiện kết thúc | Vai trò được gán và đồng bộ với cơ chế phân quyền. |
| Hậu điều kiện | Các menu/chức năng được cập nhật tương ứng với vai trò mới khi người dùng đăng nhập lại. |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Quản trị viên mở trang chi tiết người dùng. |
| 2 | Hệ thống hiển thị danh sách vai trò khả dụng và vai trò hiện tại. |
| 3 | Quản trị viên chọn hoặc bỏ chọn vai trò. |
| 4 | Hệ thống xác nhận quyền hạn của quản trị viên và lưu thay đổi. |
| 5 | Hệ thống cập nhật cache/menu chung thông qua GlobalModelAttributes. |

### Luồng thay thế
- 3a: Quản trị viên nhập ghi chú cho việc thay đổi quyền, hệ thống lưu kèm audit.

### Các ngoại lệ
- E1: Quản trị viên không đủ thẩm quyền để gán vai trò cao hơn chính mình → Hiển thị cảnh báo và từ chối thao tác.
- E2: Vai trò không tồn tại hoặc đã bị vô hiệu hóa → Thông báo lỗi hệ thống.

### Điều kiện nghiệp vụ
- Mỗi người dùng phải có ít nhất một vai trò.
- Vai trò `ROLE_ADMIN` chỉ được gán bởi siêu quản trị viên.

### Ghi chú
- Sau khi đổi quyền, người dùng phải đăng nhập lại để quyền có hiệu lực đầy đủ.

### Các giả thiết
- Danh sách vai trò được cấu hình trong bảng `roles` và ánh xạ với Spring Security.

---

## UC-SM-06 - Xử lý và hiển thị lỗi hệ thống

| Thông tin | Chi tiết |
| --- | --- |
| Số và tên UC | UC-SM-06 - Xử lý và hiển thị lỗi hệ thống |
| Người tạo UC | Nhóm BA K17 |
| Ngày tạo UC | 2024-05-21 |
| Mô tả | Hệ thống bắt và hiển thị thông điệp lỗi thân thiện cho người dùng khi xảy ra lỗi 404, 500 hoặc lỗi nghiệp vụ. |
| Tác nhân chính | Người dùng cuối |
| Tác nhân phụ | CustomErrorController, GlobalModelAttributes |
| Điều kiện tiên quyết | Hệ thống xảy ra lỗi trong quá trình xử lý yêu cầu. |
| Điều kiện kết thúc | Người dùng nhận được trang lỗi thân thiện, lỗi được ghi log. |
| Hậu điều kiện | Thông tin lỗi được gửi tới hệ thống giám sát (nếu có). |

### Luồng thông thường
| Bước | Hành động |
| --- | --- |
| 1 | Lỗi phát sinh tại bất kỳ controller/service. |
| 2 | CustomErrorController tiếp nhận mã lỗi và thông tin ngữ cảnh. |
| 3 | Hệ thống chuẩn hóa thông điệp, chọn template lỗi phù hợp. |
| 4 | Hệ thống hiển thị trang lỗi với hướng dẫn/kênh hỗ trợ. |
| 5 | Hệ thống ghi log chi tiết để phục vụ xử lý. |

### Luồng thay thế
- 3a: Nếu lỗi liên quan quyền truy cập, hệ thống chuyển hướng tới trang 403 với thông báo riêng.

### Các ngoại lệ
- E1: Template lỗi bị thiếu → Hiển thị trang lỗi dự phòng tối giản.

### Điều kiện nghiệp vụ
- Không hiển thị thông tin kỹ thuật nhạy cảm cho người dùng cuối.
- Mọi lỗi mức 500 phải được cảnh báo tới nhóm vận hành trong vòng 5 phút.

### Ghi chú
- Cần hỗ trợ đa ngôn ngữ cho thông điệp lỗi.

### Các giả thiết
- Cấu hình `server.error.whitelabel.enabled=false` để dùng trang lỗi tùy chỉnh.
