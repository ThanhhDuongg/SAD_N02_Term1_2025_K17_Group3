# Views & Binding - Fee

*Liên kết nhanh*: [Endpoints](../../APIs/Fee/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `fees/list.html`
- `fees/detail.html`
- `fees/form.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `amountInputValue` | `""` |
| `amountInputValue` | `amountInput` |
| `amountInputValue` | `formatAmount(displayAmount` |
| `contracts` | `contractService.getAllContracts(` |
| `fee` | `fee` |
| `feesPage` | `feesPage` |
| `pageNumbers` | `PageUtils.buildPageNumbers(feesPage` |
| `search` | `search` |
