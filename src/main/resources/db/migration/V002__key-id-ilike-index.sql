CREATE EXTENSION pg_trgm;
CREATE INDEX trgm_idx_users_username ON key USING gin (id gin_trgm_ops);