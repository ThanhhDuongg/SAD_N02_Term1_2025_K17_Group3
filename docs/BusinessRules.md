# Ràng buộc nghiệp vụ

Tổng hợp các kiểm tra/chuẩn hóa được áp dụng trong tầng service và controller:

## Building

- Mã tòa nhà phải duy nhất, viết hoa và không rỗng; không thể xóa tòa nhà vẫn còn phòng trực thuộc.
- Số tầng nếu nhập phải >= 0.

## Room

- Tên phòng không được trống và duy nhất; sức chứa > 0.
- Giá phòng phải > 0, nếu không nhập sẽ suy ra theo loại phòng chuẩn.
- Phải gán phòng cho tòa nhà hợp lệ.

## Student

- Mã sinh viên và email phải duy nhất trong hệ thống.
- Kiểm tra sức chứa phòng trước khi gán sinh viên.
- Năm học (studyYear) bắt buộc từ 1-6.

## Contract

- Một sinh viên chỉ có tối đa một hợp đồng đang hoạt động.
- Không thể gán hợp đồng vào phòng đã đầy.
- Cập nhật hợp đồng sẽ đồng bộ lại phòng của sinh viên.
- Khoảng thời gian trong hợp đồng được chuẩn hóa theo tháng và kiểm tra trùng lặp trước khi tạo.

## Fee

- Khi phân bổ phí theo phòng, hệ thống chia đều cho tất cả hợp đồng trong phòng và đảm bảo tổng bằng giá trị gốc.
- Phải chọn hợp đồng hợp lệ trước khi tạo phí.
- Cho phép chuyển đổi linh hoạt giữa phí cá nhân và phí phòng.

## DormRegistrationPeriod

- Chỉ được phép có một đợt đăng ký ở trạng thái OPEN tại một thời điểm.
- Tên đợt phải có, chỉ tiêu (nếu có) phải > 0, thời gian kết thúc phải sau thời gian bắt đầu.
- Đợt tự động đóng khi hết hạn hoặc đạt đủ chỉ tiêu hồ sơ.

## DormRegistrationRequest

- Sinh viên chỉ được gửi một hồ sơ cho mỗi đợt đăng ký đang mở.
- Ngày dự kiến vào ở là bắt buộc.
- Không thể nhận hồ sơ nếu đợt đã đầy chỉ tiêu.
- Khi duyệt hồ sơ, thời gian ở được chuẩn hóa theo tháng và không vượt quá 12 tháng cho mỗi hợp đồng; dài hơn phải tách hợp đồng mới.
- Hệ thống chỉ đề xuất các phòng còn chỗ phù hợp với loại phòng mong muốn và thời gian cư trú được chọn.

## MaintenanceRequest

- Trạng thái yêu cầu mặc định PENDING và luôn được chuẩn hóa in hoa.
- Chỉ nhân viên hỗ trợ mới được nhận (assign) yêu cầu; có thể hủy phân công nếu là người xử lý hoặc quản trị.
- Ghi nhận thời gian cập nhật khi thay đổi trạng thái.

## Violation

- Mô tả vi phạm bắt buộc; mức độ chỉ chấp nhận LOW/MEDIUM/HIGH.
- Nếu không chọn loại vi phạm hoặc mức độ, hệ thống đặt mặc định (OTHER, LOW).

## User

- Username và email duy nhất; bắt buộc ít nhất một vai trò khi cập nhật.
- Đổi mật khẩu yêu cầu xác thực mật khẩu hiện tại.
- Cập nhật hồ sơ kiểm tra email không trùng với tài khoản khác.

## Account

- Ảnh đại diện chỉ chấp nhận MIME type hình ảnh và dung lượng ≤ 5MB.
- Sinh viên cập nhật thông tin liên hệ đồng bộ với hồ sơ Student.

## StudentPortal

- Không cho phép gửi hồ sơ đăng ký KTX mới nếu đã gửi trong đợt đang mở.
- Yêu cầu bảo trì mới sẽ tự gán sinh viên và phòng hiện tại; loại yêu cầu mặc định MAINTENANCE.
