# OOP_N01_Term3_2025_K17_Group9
# Project name: Dormitory_Management
Group 9
Members;

Nguyễn Tùng Bách
Github: BachNguyenn

Nguyễn Thành Dương
Github: ThanhhDuongg

Lê Duy Thái Dương
Github: Nora-LeDuong

Nguyễn Lệ Thu
Github: nglthu
###  Phân tích các lớp chính trong hệ thống

Dưới đây là 4 lớp (class) cốt lõi của hệ thống quản lý ký túc xá, mỗi lớp đại diện cho một thực thể quan trọng trong bài toán.

---

### 1. `Student` – Lớp Sinh viên

**Vai trò:** Quản lý thông tin cá nhân và thông tin phòng ở của sinh viên nội trú trong ký túc xá.

**Thuộc tính:**

| Tên thuộc tính     | Kiểu dữ liệu | Mô tả |
|--------------------|--------------|-------|
| `name`             | `String`     | Họ và tên sinh viên |
| `dateOfBirth`      | `int`        | Năm sinh |
| `gender`           | `String`     | Giới tính |
| `studentId`        | `String`     | Mã số sinh viên |
| `address`          | `String`     | Địa chỉ cư trú |
| `phoneNumber`      | `String`     | Số điện thoại liên hệ |
| `email`            | `String`     | Email sinh viên |
| `identityCard`     | `String`     | Số CMND/CCCD |
| `roomNumber`       | `String`     | Số phòng đang ở |

**Phương thức nổi bật:**
- Getter/Setter cho tất cả thuộc tính
- `toString()` để hiển thị thông tin sinh viên

---

### 2. `Room` – Lớp Phòng

**Vai trò:** Quản lý thông tin phòng ở và danh sách sinh viên đang ở trong từng phòng.

**Thuộc tính:**

| Tên thuộc tính     | Kiểu dữ liệu       | Mô tả |
|--------------------|--------------------|-------|
| `roomNumber`       | `String`           | Số phòng |
| `buildingNumber`   | `String`           | Mã tòa nhà |
| `roomType`         | `String`           | Loại phòng (thường, điều hòa, VIP,...) |
| `roomPrice`        | `double`           | Giá Phòng |
| `maxOccupancy`     | `int`              | Sức chứa tối đa |
| `currentOccupancy` | `int`              | Số sinh viên hiện tại |
| `students`         | `List<Student>`    | Danh sách sinh viên trong phòng |

**Phương thức nổi bật:**
- Thêm/Xóa sinh viên khỏi phòng
- Kiểm tra số chỗ trống

---

### 3. `Contract` – Lớp Hợp đồng

**Vai trò:** Lưu trữ thông tin hợp đồng thuê phòng của từng sinh viên.

**Thuộc tính:**

| Tên thuộc tính     | Kiểu dữ liệu | Mô tả |
|--------------------|--------------|-------|
| `contractId`       | `String`     | Mã hợp đồng |
| `studentId`        | `String`     | Sinh viên ký hợp đồng |
| `roomNumber`       | `String`     | Phòng được thuê |
| `startDate`        | `String`     | Ngày bắt đầu hợp đồng |
| `endDate`          | `String`     | Ngày kết thúc hợp đồng |
| `paymentMethod`    | `String`     | Hình thức thanh toán |
| `isActive`         | `boolean`    | Hợp đồng còn hiệu lực hay không |

**Phương thức nổi bật:**
- Kích hoạt/Hủy hợp đồng
- Tính thời hạn hợp đồng

---

### 4. `Fee` – Lớp Phí dịch vụ

**Vai trò:** Theo dõi các khoản phí sinh hoạt mà sinh viên cần thanh toán.

**Thuộc tính:**

| Tên thuộc tính     | Kiểu dữ liệu | Mô tả |
|--------------------|--------------|-------|
| `feeId`            | `String`     | Mã phí |
| `studentId`        | `String`     | Mã sinh viên |
| `electricityFee`   | `double`     | Phí điện |
| `waterFee`         | `double`     | Phí nước |
| `cleaningFee`      | `double`     | Phí vệ sinh |
| `otherFee`         | `double`     | Phí khác |

**Phương thức nổi bật:**
- Tính tổng chi phí
- Xuất hóa đơn

---

## Câu 3 – Cấu trúc Folder của Project

```plaintext

Dự án được tổ chức theo mô hình hướng đối tượng, chia rõ các thành phần theo chức năng như sau:

DormitoryManagement/
│
├── README.md # Tài liệu mô tả project
└── src/
├── Model/ # Chứa các lớp mô hình dữ liệu chính
│ ├── student.java # Lớp đại diện cho sinh viên
│ ├── room.java # Lớp đại diện cho phòng
│ ├── contract.java # Lớp đại diện cho hợp đồng
│ └── fee.java # Lớp đại diện cho các loại phí
│
├── Test/ # Chứa các class dùng để kiểm thử chức năng
│ └── roomtest.java # Kiểm thử cho class Room
│
└── connectionDB/ # Chứa lớp kết nối CSDL
└── DBConnection.java # Quản lý kết nối cơ sở dữ liệu (JDBC)

```
## Câu 5 – Kiểm thử chức năng Room

File `roomtest.java` dùng để kiểm thử các chức năng chính của class `Room`, bao gồm:

- Tạo đối tượng `Room`
- Tạo và thêm một đối tượng `Student` vào phòng
- Kiểm tra số lượng sinh viên hiện tại (`currentOccupancy`)
- In ra danh sách sinh viên trong phòng

Kết quả được in ra màn hình để xác minh hoạt động của các phương thức `addStudent()`, `getCurrentOccupancy()` và `getStudents()`.

##  Yêu cầu Practice 3
 Nội dung 1:
- **Giao diện:** Java Spring Boot.
- **Chức năng quản lý Sinh viên:**
  - Thêm, sửa, xóa thông tin **sinh viên**.
  - Liệt kê danh sách sinh viên, **lọc theo tên hoặc mã sinh viên**.

- **Chức năng quản lý Phòng:**
  - Thêm, sửa, xóa **phòng** ký túc xá.

- **Gán sinh viên vào phòng:**
  - Thực hiện thông qua việc tạo **hợp đồng thuê phòng**.

- **Quản lý Phí:**
  - Thêm, sửa, xóa các loại phí (điện, nước, vệ sinh).
  - Lọc phí theo loại phí.

- **Lưu trữ dữ liệu:**
  - Dữ liệu được lưu dưới dạng **file nhị phân**.
  - Sử dụng các lớp như `Student`, `Room`, `Contract` để đọc/ghi dữ liệu.

- **Dữ liệu trong bộ nhớ:**
  - Lưu trữ bằng các cấu trúc như `ArrayList`, `LinkedList`, `Map`,...

- **Chức năng mở rộng:**
  - Thống kê số lượng sinh viên theo phòng.
  - Tìm phòng còn trống.
  - Lọc hợp đồng còn hiệu lực.
  - Tính tổng phí theo sinh viên hoặc theo phòng.
  - Xuất báo cáo thống kê ra file.

---

 Nội dung 2:

 ![class_diagram](https://github.com/BachNguyenn/OOP_N01_Term3_2025_K17_Group9/blob/main/class_diagram.jpg)

## Công nghệ sử dụng

- Java
- Java Spring Boot
- MySQL
- JDBC

Practice 4:
Nội dung 3: 
Sequence Diagram
 Chức năng: Sinh viên đăng ký phòng (Student)

 Student       Room        Fee
   |            |           |
   |---requestRoom()------->|
   |            |           |
   |     checkAvailability()|
   |<---confirmRoom()-------|
   |            |           |
   |---createFee()--------->|
   |            |           |
   |<---feeDetails()--------|
   |---payFee()------------>|
   |<---paymentStatus()-----|

Activity Diagram 
Chức năng: Đăng ký phòng ký túc xá (Room)
[Start]
   ↓
[Nhập thông tin sinh viên]
   ↓
[Kiểm tra phòng trống]
   ↓
┌────────────┐
│Phòng trống?│
└────┬───────┘
     │Yes                    No
     ↓                       ↓
[Phân bổ phòng]        [Hiển thị lỗi]
     ↓                       ↓
[Tạo hóa đơn phí]           [End]
     ↓
[Thanh toán phí]
     ↓
[Hiển thị trạng thái thanh toán]
     ↓
[End]

Activity Diagram 
Chức năng: Quản lý phí của sinh viên (fee)
[Start]
   ↓
[Truy vấn sinh viên]
   ↓
[Hiển thị các khoản phí]
   ↓
┌────────────┐
│Có phí chưa?│
└────┬───────┘
     │Yes                   No
     ↓                      ↓
[Chọn hình thức thanh toán]  [Kết thúc]
     ↓
[Thực hiện thanh toán]
     ↓
[Cập nhật trạng thái phí]
     ↓
[End]

