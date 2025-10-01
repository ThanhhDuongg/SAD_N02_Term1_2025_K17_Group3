# Views & Binding - Report

*Liên kết nhanh*: [Endpoints](../../APIs/Report/Endpoints.md) · [Use Cases](UseCases.md) · [Domain Model](DomainModel.mmd)

## Templates

- `reports/overview.html`

## Thuộc tính Model (model.addAttribute)

| Attribute | Nguồn dữ liệu |
| --- | --- |
| `availableBeds` | `availableBeds` |
| `buildingCount` | `buildingSummaries.size(` |
| `buildingOccupancyChartData` | `buildingOccupancyChartData` |
| `buildingSummaries` | `buildingSummaries` |
| `feeScopeChartData` | `feeScopeChartData` |
| `feeScopeSummaries` | `feeScopeSummaries` |
| `feeTypeChartData` | `feeTypeChartData` |
| `feeTypeSummaries` | `feeTypeSummaries` |
| `maintenanceSummary` | `maintenanceSummary` |
| `occupancyRate` | `occupancyRate` |
| `occupiedBeds` | `occupiedBeds` |
| `outstandingRevenue` | `outstandingRevenue` |
| `studentCount` | `studentService.countStudents(` |
| `totalCapacity` | `totalCapacity` |
| `totalRequests` | `allRequests.size(` |
| `totalRevenue` | `totalRevenue` |
