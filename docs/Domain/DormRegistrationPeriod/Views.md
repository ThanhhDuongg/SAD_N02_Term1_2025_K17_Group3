# Views & Binding - DormRegistrationPeriod

*Liên kết nhanh*: [Endpoints](../../APIs/DormRegistrationPeriod/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `registrations/periods.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `activePeriod` | `dormRegistrationPeriodService.getOpenPeriod(` |
| `periodForm` | `new DormRegistrationPeriod(` |
| `periods` | `dormRegistrationPeriodService.findAll(` |
