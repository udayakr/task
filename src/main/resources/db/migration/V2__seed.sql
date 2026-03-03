-- V2__seed.sql: Seed data for Task Management System

-- Admin user (password: Admin@1234 — BCrypt hash)
INSERT INTO users (id, email, password, first_name, last_name, role, is_active, email_verified)
VALUES (
    gen_random_uuid(),
    'admin@tms.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewLx.G6TDVwYQm7e',
    'Admin',
    'User',
    'ADMIN',
    true,
    true
);

-- Sample regular users
INSERT INTO users (id, email, password, first_name, last_name, role, is_active, email_verified)
VALUES
    (gen_random_uuid(), 'alice@tms.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewLx.G6TDVwYQm7e', 'Alice', 'Johnson', 'USER', true, true),
    (gen_random_uuid(), 'bob@tms.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewLx.G6TDVwYQm7e', 'Bob', 'Smith', 'USER', true, true);

-- Sample project (owned by alice)
DO $$
DECLARE
    alice_id UUID;
    bob_id UUID;
    project_id UUID;
    task1_id UUID;
    task2_id UUID;
    task3_id UUID;
BEGIN
    SELECT id INTO alice_id FROM users WHERE email = 'alice@tms.com';
    SELECT id INTO bob_id FROM users WHERE email = 'bob@tms.com';

    INSERT INTO projects (id, name, description, status, owner_id)
    VALUES (gen_random_uuid(), 'Website Redesign', 'Complete redesign of the company website', 'ACTIVE', alice_id)
    RETURNING id INTO project_id;

    -- Add bob as project member
    INSERT INTO project_members (project_id, user_id) VALUES (project_id, alice_id);
    INSERT INTO project_members (project_id, user_id) VALUES (project_id, bob_id);

    -- Sample tasks
    INSERT INTO tasks (id, title, description, status, priority, project_id, created_by_id, assignee_id, estimated_hours)
    VALUES (gen_random_uuid(), 'Design homepage mockup', 'Create wireframes and high-fidelity mockups for the homepage', 'DONE', 'HIGH', project_id, alice_id, alice_id, 8.0)
    RETURNING id INTO task1_id;

    INSERT INTO tasks (id, title, description, status, priority, project_id, created_by_id, assignee_id, due_date, estimated_hours)
    VALUES (gen_random_uuid(), 'Implement navigation component', 'Build responsive navigation with mobile hamburger menu', 'IN_PROGRESS', 'MEDIUM', project_id, alice_id, bob_id, NOW()::DATE + 3, 5.0)
    RETURNING id INTO task2_id;

    INSERT INTO tasks (id, title, description, status, priority, project_id, created_by_id, estimated_hours)
    VALUES (gen_random_uuid(), 'Write unit tests for API endpoints', 'Achieve 80% coverage for all service classes', 'TODO', 'MEDIUM', project_id, alice_id, 12.0)
    RETURNING id INTO task3_id;

    -- Sample comment
    INSERT INTO comments (content, task_id, author_id)
    VALUES ('Mockups approved by the design team. Ready to hand off to development.', task1_id, alice_id);
END $$;
