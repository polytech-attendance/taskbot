-- Create
INSERT INTO task_schema.recurring_task (owner_id, summary, start, period, finish, status)
VALUES (1, 'Повторяющаяся задача', '2024-05-20 12:00:00', '1 day', '2024-06-20 12:00:00', 0);

-- getInProgress
SELECT * FROM task_schema.recurring_task WHERE owner_id = id, status = 0

-- reschedule
UPDATE task_schema.recurring_task
SET start = 'yyyy-mm-dd hh:mm:ss', finish = 'yyyy-mm-dd hh:mm:ss', period = '1 day'
WHERE task_id = 1 AND owner_id = 1;

-- set_summary
UPDATE task_schema.recurring_task
SET summary = 'summary'
WHERE task_id = 1 AND owner_id = 1;

-- mark_in_progress (for done 1)
UPDATE task_schema.recurring_task
SET status = 0
WHERE task_id = 1 AND owner_id = 1;


-- FOR TASK SERVICE


