-- Create
INSERT INTO task_schema.recurring_task (owner_id, summary, start, period, finish, status)
VALUES (1, 'recurringtask', '2024-05-20 12:00:00', '1 day', '2024-06-20 12:00:00', 0);

-- getInProgress
SELECT * FROM task_schema.recurring_task WHERE owner_id = id, status = 0

-- reschedule
UPDATE task_schema.recurring_task
SET start = 'yyyy-mm-dd hh:mm:ss', finish = 'yyyy-mm-dd hh:mm:ss', period = '1 day'
WHERE recurring_task_id = 1 AND owner_id = 1;

-- set_summary
UPDATE task_schema.recurring_task
SET summary = 'summary'
WHERE recurring_task_id = 1 AND owner_id = 1;

-- mark_in_progress (for done 1)
UPDATE task_schema.recurring_task
SET status = 0
WHERE recurring_task_id = 1 AND owner_id = 1;


-- FOR TASK SERVICE
-- Create
INSERT INTO task_schema.task (owner_id, summary, deadline, status, estimated_time, spent_time)
VALUES (2, 'task', '2024-05-20 12:00:00', 0, INTERVAL '2 days', INTERVAL '0 hours');

-- addSpentTime
UPDATE task_schema.task
SET spent_time = '1 day'
WHERE task_id = 1 AND owner_id = 1;

-- mark_in_progress (for done 1)
UPDATE task_schema.task
SET status = 0
WHERE task_id = 1 AND owner_id = 1;

-- Set deadline
UPDATE task_schema.task
SET deadline = '2024-05-20 12:00:00'
WHERE task_id = 1 AND owner_id = 1;

-- get by deadline
SELECT *
FROM task_schema.task
WHERE deadline BETWEEN '2024-05-01 00:00:00' AND '2024-05-31 23:59:59' AND owner_id = 1;