# Tài liệu cơ sở dữ liệu

## Bảng và mô tả

| Thực thể | Mô tả | Khóa/Quan hệ chính |
| --- | --- | --- |
| Building | Danh mục tòa nhà với mã duy nhất, thông tin địa chỉ và tổng số tầng. | 1-n với `Room` |
| Room | Phòng ở thuộc tòa nhà, có loại, sức chứa, giá và danh sách sinh viên. | n-1 `Building`, 1-n `Student`, 1-n `Contract` |
| Student | Hồ sơ sinh viên, liên kết tới phòng và tài khoản người dùng. | n-1 `Room`, 1-1 `User`, 1-n `Contract` |
| User | Tài khoản hệ thống với thông tin đăng nhập và vai trò. | - |
| Role | Vai trò hệ thống (ADMIN, STAFF, STUDENT). | - |
| Contract | Hợp đồng ở ký túc xá giữa sinh viên và phòng. | n-1 `Student`, n-1 `Room`, 1-n `Fee` |
| Fee | Khoản phí phát sinh trên hợp đồng, hỗ trợ phân bổ theo phòng. | n-1 `Contract`, thuộc `FeeType/FeeScope`, trạng thái `PaymentStatus` |
| MaintenanceRequest | Yêu cầu bảo trì/báo hỏng do sinh viên gửi. | n-1 `Student`, n-1 `Room`, n-1 `User` (handledBy) |
| Violation | Biên bản vi phạm nội quy của sinh viên. | n-1 `Student`, n-1 `Room`, n-1 `User` (createdBy) |
| DormRegistrationPeriod | Đợt đăng ký KTX (thời gian, chỉ tiêu, trạng thái). | 1-n `DormRegistrationRequest` |
| DormRegistrationRequest | Hồ sơ đăng ký tham gia đợt KTX của sinh viên. | n-1 `Student`, n-1 `DormRegistrationPeriod` |
| RoleName | Enum hiển thị tên vai trò. | - |
| FeeType | Enum loại phí (điện, nước, vệ sinh...). | - |
| FeeScope | Enum phạm vi phí (cá nhân/phòng). | - |
| PaymentStatus | Enum trạng thái thanh toán phí. | - |

## ERD

Sơ đồ đầy đủ: [ERD.mmd](ERD.mmd).
