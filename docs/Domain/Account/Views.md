# Views & Binding - Account

*Liên kết nhanh*: [Endpoints](../../APIs/Account/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `account/profile.html`
- `auth/change-password.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `canEditFullName` | `!isStudent` |
| `changePasswordForm` | `new ChangePasswordForm(` |
| `isStudent` | `isStudent` |
| `profileForm` | `form` |
| `studentProfile` | `student` |
