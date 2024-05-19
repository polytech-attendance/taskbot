# Task Management Bot

## Requirements

### Common requirements:

1. Sources and artifacts
  - Code should be somewhere in GitHub (your own)
  - README with a guide!
  - Target artifact for app - runnable fat jar
  - Target build object for deploy - docker container
  - Build tool should be used (Maven or Gradle)
2. Tech stack
  - Telegram API
  - Java 21 / Kotlin / Scala 3
  - Spring 5 / Spring 6
  - **Spring Boot is prohibited for all students**
  - Docker, Docker compose, Docker Hub
3. Try to develop "good architecture"
4. How to submit:
  - Link to code (github repo) with guide (!!!)
  - Link to DockerHub
  - Link to TelegramBot
5. Specific requirements:
  - Spring WebFlux should be used
  - JOOQ should be used

### Features

1. Sign up
2. Sign in
3. Task
  - Create task (summary, estimation, deadline)
  - Update task (add spent time, mark as done, change deadline)
  - Show tasks by deadline (today, tomorrow, week)
4. Recurring tasks
  - Create reccuring tasks (hourly, daily, weekly, monthly)
  - Show recurring tasks
  - Update recurring tasks
  - Delete recurring tasks
5. Reminder
  - Remind about deadlines
  - Remind about not updated tasks

### Bonus

1. Integration with issue tracker (Youtrack, ClickUp, Jira, Trello...)
2. Integration with Calendar 
