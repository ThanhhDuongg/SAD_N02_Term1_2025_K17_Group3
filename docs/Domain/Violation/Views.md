# Views & Binding - Violation

*Liên kết nhanh*: [Endpoints](../../APIs/Violation/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `violations/list.html`
- `violations/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `roomSummary` | `roomSummary` |
| `selectedRoomId` | `roomId` |
| `selectedSeverity` | `""` |
| `selectedSeverity` | `severityUpper` |
| `selectedStudentId` | `studentId` |
| `selectedType` | `""` |
| `selectedType` | `typeUpper` |
| `severitySummary` | `severitySummary` |
| `studentSummary` | `studentSummary` |
| `typeSummary` | `typeSummary` |
| `violation` | `new Violation(` |
| `violations` | `violations` |

## @ModelAttribute cấp controller

- `rooms`
- `severityLevels`
- `students`
- `violationTypes`
