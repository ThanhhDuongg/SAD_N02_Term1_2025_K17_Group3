# Views & Binding - Room

*Liên kết nhanh*: [Endpoints](../../APIs/Room/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `rooms/list.html`
- `rooms/detail.html`
- `rooms/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `formError` | `e.getMessage(` |
| `occupancies` | `occupancies` |
| `occupancy` | `room.getStudents(` |
| `pageNumbers` | `PageUtils.buildPageNumbers(roomsPage` |
| `room` | `new Room(` |
| `room` | `room` |
| `roomsPage` | `roomsPage` |
| `search` | `search` |
| `selectedBuildingId` | `buildingId` |
| `selectedBuildingId` | `null` |
| `selectedBuildingId` | `room.getBuilding(` |

## @ModelAttribute cấp controller

- `buildings`
