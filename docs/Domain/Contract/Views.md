# Views & Binding - Contract

*Liên kết nhanh*: [Endpoints](../../APIs/Contract/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `contracts/list.html`
- `contracts/detail.html`
- `contracts/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `contract` | `contract` |
| `contract` | `new Contract(` |
| `contractsPage` | `contractsPage` |
| `errorMessage` | `"Vui lòng chọn sinh viên hợp lệ"` |
| `pageNumbers` | `PageUtils.buildPageNumbers(contractsPage` |
| `rooms` | `roomService.getAllRooms(` |
| `search` | `search` |
