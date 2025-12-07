-- Database Optimization Script for Task Manager
-- Day 4: Performance Indexes and Query Analysis

-- =====================================================
-- CREATE PERFORMANCE INDEXES
-- =====================================================

-- Index for tasks by project and status (commonly queried together)
CREATE INDEX IF NOT EXISTS idx_tasks_project_status 
ON tasks(project_id, status);

-- Index for tasks by assignee and due date (for user task views)
CREATE INDEX IF NOT EXISTS idx_tasks_assignee_due 
ON tasks(assignee_id, due_date);

-- Index for comments by task with ordering
CREATE INDEX IF NOT EXISTS idx_comments_task_created 
ON comments(task_id, created_at DESC);

-- Index for attachments by task
CREATE INDEX IF NOT EXISTS idx_attachments_task 
ON attachments(task_id);

-- Index for tasks priority (for filtering)
CREATE INDEX IF NOT EXISTS idx_tasks_priority 
ON tasks(priority);

-- Index for tasks due date range queries
CREATE INDEX IF NOT EXISTS idx_tasks_due_date 
ON tasks(due_date);

-- =====================================================
-- ANALYZE QUERY PERFORMANCE
-- =====================================================

-- Test query performance for common searches
-- (Run these individually to see execution plans)

-- Query 1: Find tasks by project and status
EXPLAIN ANALYZE 
SELECT * FROM tasks 
WHERE project_id = 1 AND status = 'IN_PROGRESS';

-- Query 2: Find tasks by assignee with due date
EXPLAIN ANALYZE 
SELECT * FROM tasks 
WHERE assignee_id = 1 
ORDER BY due_date ASC;

-- Query 3: Get comments for a task
EXPLAIN ANALYZE 
SELECT * FROM comments 
WHERE task_id = 1 
ORDER BY created_at DESC;

-- Query 4: Complex search with multiple filters
EXPLAIN ANALYZE 
SELECT t.* FROM tasks t
WHERE t.project_id = 1 
  AND t.status = 'TODO'
  AND t.priority = 'HIGH'
  AND t.due_date >= CURRENT_DATE
ORDER BY t.created_at DESC;

-- =====================================================
-- DATABASE STATISTICS
-- =====================================================

-- Update table statistics for better query planning
ANALYZE tasks;
ANALYZE comments;
ANALYZE attachments;
ANALYZE projects;

-- =====================================================
-- VERIFY INDEXES
-- =====================================================

-- List all indexes on tasks table
SELECT 
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'tasks'
ORDER BY indexname;

-- List all indexes on comments table
SELECT 
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'comments'
ORDER BY indexname;

-- =====================================================
-- MONITORING QUERIES (Optional)
-- =====================================================

-- Check table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;