# Views & Binding - MaintenanceRequest

*Liên kết nhanh*: [Endpoints](../../APIs/MaintenanceRequest/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `maintenance/list.html`
- `maintenance/detail.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `assignedToMe` | `Boolean.TRUE.equals(mine` |
| `request` | `request` |
| `requests` | `requests` |
| `searchQuery` | `keyword != null ? keyword : ""` |
| `selectedStatus` | `status != null ? status.toUpperCase(` |
| `selectedType` | `type != null ? type.toUpperCase(` |
| `statusSummary` | `statusSummary` |

## @ModelAttribute cấp controller

- `requestTypes`
- `staffMembers`
- `statusOptions`
