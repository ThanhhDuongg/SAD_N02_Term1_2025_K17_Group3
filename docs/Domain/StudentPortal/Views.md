# Views & Binding - StudentPortal

*Liên kết nhanh*: [Endpoints](../../APIs/StudentPortal/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `student/dashboard.html`
- `student/profile.html`
- `student/contracts.html`
- `student/fees.html`
- `student/maintenance-form.html`
- `student/requests.html`
- `student/registration-list.html`
- `student/registration-form.html`
- `student/violations.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `activeRegistrationPeriod` | `activePeriod` |
| `alreadySubmittedInActivePeriod` | `alreadySubmitted` |
| `contracts` | `contractService.getContractsByStudent(student.getId(` |
| `currentContract` | `currentContract` |
| `fees` | `feeService.getFeesByStudent(student.getId(` |
| `hasHighSeverityViolation` | `latestHighSeverity.isPresent(` |
| `hasNotifications` | `!notifications.isEmpty(` |
| `latestHighSeverityViolation` | `latestHighSeverity.orElse(null` |
| `maintenanceRequest` | `new MaintenanceRequest(` |
| `maintenanceRequests` | `maintenanceRequests` |
| `notifications` | `notifications` |
| `registrationRequest` | `request` |
| `registrationRequests` | `dormRegistrationRequestService.findByStudent(student.getId(` |
| `registrationRequests` | `registrationRequests` |
| `requests` | `maintenanceRequestService.getRequestsByStudent(student.getId(` |
| `student` | `student` |
| `unpaidCount` | `unpaidFees.size(` |
| `unpaidFees` | `unpaidFees` |
| `violationCount` | `violations.size(` |
| `violations` | `violationService.getViolationsByStudent(student.getId(` |
| `violations` | `violations` |

## @ModelAttribute cấp controller

- `activeRegistrationPeriod`
- `registrationStatuses`
- `requestTypes`
- `roomTypeOptions`
