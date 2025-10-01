# Views & Binding - DormRegistrationRequest

*Liên kết nhanh*: [Endpoints](../../APIs/DormRegistrationRequest/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `registrations/list.html`
- `registrations/detail.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `activePeriod` | `dormRegistrationPeriodService.getOpenPeriod(` |
| `periods` | `periods` |
| `request` | `request` |
| `requests` | `requests` |
| `searchQuery` | `keyword != null ? keyword : ""` |
| `selectedPeriodId` | `periodId` |
| `selectedStatus` | `status != null ? status.toUpperCase(` |

## @ModelAttribute cấp controller

- `statusOptions`
