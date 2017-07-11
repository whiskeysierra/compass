# Pagination

API layer
- easily construct query from set of query params
- infer direction from presence
- extract individual query params from next/prev pivots to construct links

Service layer
- cut head/tail from result list and derive cursor from it

DB layer
- apply ORDER BY, SEEK (BEFORE|AFTER) and LIMIT predicate to query
