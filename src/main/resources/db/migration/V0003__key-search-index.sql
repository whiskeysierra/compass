CREATE INDEX ON key USING GIN (id gin_trgm_ops);
CREATE INDEX ON key USING GIN (description gin_trgm_ops);