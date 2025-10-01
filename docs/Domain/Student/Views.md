# Views & Binding - Student

*Liên kết nhanh*: [Endpoints](../../APIs/Student/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `students/list.html`
- `students/detail.html`
- `students/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `contractEndDate` | `latestContract.getEndDate(` |
| `contractStartDate` | `latestContract.getStartDate(` |
| `pageNumbers` | `PageUtils.buildPageNumbers(studentsPage` |
| `rooms` | `roomService.getAllRooms(` |
| `search` | `search` |
| `student` | `new Student(` |
| `student` | `student` |
| `studentsPage` | `studentsPage` |
