CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE job_applications (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL,
    position VARCHAR(150) NOT NULL,
    status VARCHAR(32) NOT NULL,
    applied_date DATE NOT NULL,
    notes VARCHAR(2000),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_applications_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT ck_job_applications_status CHECK (status IN ('APPLIED', 'INTERVIEW', 'REJECTED', 'OFFER'))
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);

CREATE INDEX idx_job_applications_status ON job_applications (status);
CREATE INDEX idx_job_applications_company_name ON job_applications (company_name);
CREATE INDEX idx_job_applications_applied_date ON job_applications (applied_date);
CREATE INDEX idx_job_applications_user_id ON job_applications (user_id);
