# Views & Binding - UserManagement

*Liên kết nhanh*: [Endpoints](../../APIs/UserManagement/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `users/list.html`
- `users/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `allRoles` | `roles` |
| `roles` | `Arrays.stream(RoleName.values(` |
| `userForm` | `form` |
| `users` | `users` |
