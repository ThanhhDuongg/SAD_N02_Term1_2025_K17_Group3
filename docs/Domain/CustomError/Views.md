# Views & Binding - CustomError

*Liên kết nhanh*: [Endpoints](../../APIs/CustomError/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `error.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `errorMessage` | `resolvedMessage` |
| `path` | `attributes.get("path"` |
| `status` | `attributes.get("status"` |
| `timestamp` | `attributes.get("timestamp"` |
