# Bộ tài liệu hệ thống - Phần mềm quản lý KTX

## Mục tiêu
- Chuẩn hóa hiểu biết về cấu trúc kiến trúc, dữ liệu và nghiệp vụ của hệ thống.
- Hỗ trợ đội dự án (dev/test/vận hành) tra cứu nhanh các phân hệ và API.

## Phạm vi
- Toàn bộ ứng dụng Spring Boot `com.example.dorm` bao gồm controller, service, repository, view Thymeleaf và cấu hình bảo mật.
- Các phân hệ chính dành cho quản trị viên, nhân viên hỗ trợ và sinh viên.

## Phân hệ chính
- **Account**: Trang cá nhân cho người dùng đã đăng nhập (cập nhật thông tin và đổi mật khẩu). ([Endpoints](APIs/Account/Endpoints.md))
- **Auth**: Luồng đăng nhập và tự đăng ký tài khoản cho sinh viên. ([Endpoints](APIs/Auth/Endpoints.md))
- **Building**: Quản lý danh mục tòa nhà ký túc xá. ([Endpoints](APIs/Building/Endpoints.md))
- **Contract**: Quy trình lập và cập nhật hợp đồng ở ký túc xá. ([Endpoints](APIs/Contract/Endpoints.md))
- **CustomError**: Trang/thông điệp lỗi tùy biến cho toàn hệ thống. ([Endpoints](APIs/CustomError/Endpoints.md))
- **Dashboard**: Bảng điều khiển dành cho quản trị viên/nhân viên với các chỉ số nhanh. ([Endpoints](APIs/Dashboard/Endpoints.md))
- **DormRegistrationPeriod**: Thiết lập, mở/đóng các đợt đăng ký KTX. ([Endpoints](APIs/DormRegistrationPeriod/Endpoints.md))
- **DormRegistrationRequest**: Tiếp nhận và duyệt hồ sơ đăng ký ở ký túc xá. ([Endpoints](APIs/DormRegistrationRequest/Endpoints.md))
- **Fee**: Quản lý các khoản phí và phân bổ theo hợp đồng/phòng. ([Endpoints](APIs/Fee/Endpoints.md))
- **MaintenanceRequest**: Tiếp nhận và xử lý yêu cầu bảo trì từ sinh viên. ([Endpoints](APIs/MaintenanceRequest/Endpoints.md))
- **Report**: Tổng hợp báo cáo vận hành (tài chính, tỷ lệ sử dụng). ([Endpoints](APIs/Report/Endpoints.md))
- **Room**: Quản trị thông tin phòng và phân bổ sức chứa. ([Endpoints](APIs/Room/Endpoints.md))
- **Student**: Quản lý hồ sơ sinh viên nội trú. ([Endpoints](APIs/Student/Endpoints.md))
- **StudentPortal**: Cổng dịch vụ tự phục vụ cho sinh viên (dashboard, phí, hợp đồng, đăng ký). ([Endpoints](APIs/StudentPortal/Endpoints.md))
- **UserManagement**: Quản trị người dùng nội bộ và phân quyền. ([Endpoints](APIs/UserManagement/Endpoints.md))
- **Violation**: Ghi nhận và thống kê vi phạm nội quy của sinh viên. ([Endpoints](APIs/Violation/Endpoints.md))

## Liên kết nhanh
- [Kiến trúc tổng thể](Architecture.md)
- [Tài liệu cơ sở dữ liệu](Database.md)
- [Chính sách bảo mật và phân quyền](Security.md)
- [Ràng buộc nghiệp vụ](BusinessRules.md)
- [Bản đồ thư mục nguồn → tài liệu](Appendix/FolderMap.md)
