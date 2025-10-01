# Views & Binding - Building

*Liên kết nhanh*: [Endpoints](../../APIs/Building/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `buildings/list.html`
- `buildings/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `building` | `building` |
| `building` | `new Building(` |
| `buildingSummaries` | `buildingService.getBuildingSummaries(` |
| `errorMessage` | `e.getMessage(` |
| `isEdit` | `false` |
| `isEdit` | `true` |
