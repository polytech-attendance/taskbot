# Task Management Bot

Built app is now on Docker Hub. See <https://hub.docker.com/repository/docker/mariohuq/tasks-bot/>.
To use it, specify `services.app.image: mariohuq/tasks-bot`
in `docker-compose.yml`
instead of
`services.app.build`.

## Dependencies
 - TelegramAPI java;
 - Spring Web Flux;
 - JOOQ;
 - PostgreSQL;

### Common features:
 - Use **/help** in the telegram bot to run it.
 - Now we provide token of bot to test it.

### Features

1. Task
   - Create task (summary, estimation, deadline)
   - Update task (add spent time, mark as done, change deadline)
   - Show tasks by deadline (today, tomorrow, week)
2. Recurring tasks
   - Create recurring tasks (hourly, daily, weekly, monthly)
   - Show recurring tasks
   - Update recurring tasks
   - Delete recurring tasks
3. Reminder
   - Remind about deadlines
   - Remind about not updated tasks

---

## Available commands

- **/task_create**
Creating new task. Usage: `/task_create [Summary] [Deadline] [Estimated time]` (`/task_create Driving exam 2023-05-12T12:00:00Z 10 hours`)

- **/reschedule**
Reschedule existing recurring task. Usage: `/reschedule [Recurring Id] [hourly\daily\weekly\monthly] [Deadline]` (`/reschedule 10 daily 2024-05-30T12:00:00Z`)

- **/task**
Show all tasks.

- **/task_deadline**
Change deadline for task. Usage: `/task_deadline [Task_id] [Deadline]` (`/task_deadline 10 2023-05-12T12:00:00Z`)

- **/recurring**
Get recurring tasks in progress.

- **/start**
Register in the bot.

- **/recurring_create**
Creating new recurring task. Usage: `/recurring_create [Summary] [hourly\daily\weekly\monthly] [Deadline]` (`/recurring_create Reading book daily 2024-05-24T12:00:00Z`)

- **/recurring_summary**
Edit summary for existing recurring task. Usage: `/recurring_summary [Recurring Id] [New summary]` (`/recurring_summary 10 Read big books`)

- **/task_spenttime**
Add spent time for task. Usage: `/task_spenttime [Task_id] [Duration]` (`/create_task 3 hours`)


For mark tasks, we add buttons:

![example1.png](media/example2.png)

![example2.png](media/example1.png)

## Task
1. To create task using: **/task_create**
2. To update task using:
   - Buttons on **/task**
     - **/task_deadline** - to change deadline
     - **/task_spenttime** - to add spent time
3. To show tasks use **/task**;

## Recurring tasks
1. To create recurring using: **/recurring_create**
2. Show recurring: **/recurring**
3. Update recurring:
   - Use buttons on tasks outputted by **/recurring**
   - Use:
     - **/recurring_summary** - to change summary for recurring
     - **/reschedule** - to rescheduled recurring
4. Delete recurring: use button

## Reminder

For each bot user we add them to session map and they receive notifications about 
recurring tasks and tasks' deadlines:

![example3.jpg](media/example3.jpg)

# Deploy

```sh
mvn package # requires `docker compose up db` to run generate maven task.
docker compose build
docker compose up
```

# To publish Docker image

```sh
$ sudo docker compose up db # in another terminal
$ systemctl --user start docker.service
$ mvn package
$ sudo docker compose build app
$ sudo docker login --username=mariohuq
$ sudo docker image tag  taskbot-app:latest mariohuq/tasks-bot:latest
$ sudo docker push mariohuq/tasks-bot:latest
```

## TODO:

- Make more user-friendly UI in Telegram.
- Make inline menu for using main functions.
- Pause reminders on user command.