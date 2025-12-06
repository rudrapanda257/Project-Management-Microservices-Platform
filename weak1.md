## **DAY 3: Project Service - CRUD Operations**

### **🎯 Goals:**

- Create Project Service with CRUD operations
- Implement Task entity and CRUD
- JWT validation in Project Service
- Pagination and sorting

### **📁 New Files to Create:**

```

project-service/
├── pom.xml
└── src/main/
├── java/com/taskmanager/project/
│ ├── ProjectServiceApplication.java
│ ├── config/
│ │ ├── SecurityConfig.java
│ │ └── JwtConfig.java
│ ├── controller/
│ │ ├── ProjectController.java
│ │ └── TaskController.java
│ ├── service/
│ │ ├── ProjectService.java
│ │ └── TaskService.java
│ ├── repository/
│ │ ├── ProjectRepository.java
│ │ └── TaskRepository.java
│ ├── entity/
│ │ ├── Project.java
│ │ └── Task.java
│ ├── dto/
│ │ ├── ProjectRequest.java
│ │ ├── ProjectResponse.java
│ │ ├── TaskRequest.java
│ │ └── TaskResponse.java
│ └── security/
│ └── JwtAuthFilter.java
└── resources/
└── application.yml

```

### **🤖 AI Prompts for Day 3:**

**Prompt 1: Project Service POM**

```

Create pom.xml for project-service with same dependencies as user-service plus:

- Server port will be 8082
- Add spring-boot-starter-data-jpa pagination support
- Parent: task-management-platform

```

**Prompt 2: Project Entity**

```

Create Project JPA entity with:

- @Entity, @Table(name = "projects")
- Fields: id, name, description, ownerId (Long), createdAt
- @OneToMany relationship with Task (mappedBy = "project", cascade = ALL, orphanRemoval = true)
- Lombok annotations
- Package: com.taskmanager.project.entity

```

**Prompt 3: Task Entity**

```

Create Task JPA entity with:

- @Entity, @Table(name = "tasks")
- Fields: id, title, description, assigneeId, status (enum: TODO, IN_PROGRESS, DONE), priority (enum: HIGH, MEDIUM, LOW), dueDate, createdAt, updatedAt
- @ManyToOne relationship with Project (fetch = LAZY, @JoinColumn name = "project_id")
- @PrePersist for createdAt, @PreUpdate for updatedAt
- Lombok annotations
- Package: com.taskmanager.project.entity

```

**Prompt 4: Project Repository with Custom Queries**

```

Create ProjectRepository interface extending JpaRepository<Project, Long>:

- Add custom query: findByOwnerId(Long ownerId, Pageable pageable)
- Add custom query using @Query JPQL: findByNameContainingIgnoreCase
- Package: com.taskmanager.project.repository

```

**Prompt 5: Task Repository with JPQL Queries**

```

Create TaskRepository extending JpaRepository<Task, Long> with these @Query methods:

1. findByProjectId(Long projectId, Pageable pageable)
2. findByAssigneeIdAndStatus(Long assigneeId, String status)
3. findByProjectIdAndStatus(Long projectId, String status)
4. findOverdueTasks() - tasks where due_date < current_date and status != DONE
5. countByProjectIdAndStatus(Long projectId, String status)

Use JPQL for all queries.
Package: com.taskmanager.project.repository

```

**Prompt 6: Project Service**

```

Create ProjectService with:

- @Service, inject ProjectRepository
- Methods:
  1. createProject(ProjectRequest, Long userId) - userId from JWT
  2. getAllProjects(int page, int size) - with pagination
  3. getProjectById(Long id)
  4. getMyProjects(Long userId, int page, int size)
  5. updateProject(Long id, ProjectRequest, Long userId) - check ownership
  6. deleteProject(Long id, Long userId) - check ownership
- Throw custom exceptions: ProjectNotFoundException, UnauthorizedException
- Package: com.taskmanager.project.service

```

**Prompt 7: Task Service**

```

Create TaskService with:

- @Service, inject TaskRepository, ProjectRepository
- Methods:
  1. createTask(Long projectId, TaskRequest, Long userId)
  2. getTasksByProject(Long projectId, int page, int size)
  3. getTaskById(Long taskId)
  4. updateTask(Long taskId, TaskRequest, Long userId)
  5. updateTaskStatus(Long taskId, String status, Long userId)
  6. deleteTask(Long taskId, Long userId)
  7. getMyTasks(Long userId, String status) - optional status filter
  8. getOverdueTasks()

Include pagination where appropriate.
Package: com.taskmanager.project.service

```

**Prompt 8: Project Controller**

```

Create ProjectController REST API with:

- @RestController, @RequestMapping("/api/projects")
- Inject ProjectService
- Extract userId from JWT in SecurityContext

Endpoints:

1. POST / - create project
2. GET / - get all projects (page, size params)
3. GET /my - get my projects (page, size params)
4. GET /{id} - get project by id
5. PUT /{id} - update project
6. DELETE /{id} - delete project

Return ResponseEntity with proper status codes.
Package: com.taskmanager.project.controller

```

**Prompt 9: Task Controller**

```

Create TaskController with:

- @RestController, @RequestMapping("/api/projects/{projectId}/tasks")
- Inject TaskService

Endpoints:

1. POST / - create task in project
2. GET / - get all tasks in project (paginated)
3. GET /task/{taskId} - get task details
4. PUT /task/{taskId} - update task
5. PATCH /task/{taskId}/status - update task status only
6. DELETE /task/{taskId} - delete task

Additional endpoint:

- GET /api/tasks/my - get my assigned tasks (query param: status)
- GET /api/tasks/overdue - get overdue tasks

Package: com.taskmanager.project.controller

```

**Prompt 10: DTOs**

```

Create these DTOs in com.taskmanager.project.dto:

1. ProjectRequest:

   - name (String, @NotBlank)
   - description (String)

2. ProjectResponse:

   - id, name, description, ownerId, createdAt
   - taskCount (Integer)

3. TaskRequest:

   - title (@NotBlank)
   - description
   - assigneeId (Long, @NotNull)
   - status (@NotBlank)
   - priority (@NotBlank)
   - dueDate (LocalDate)

4. TaskResponse:
   - All task fields
   - projectId, projectName

Use Lombok annotations.

```

**Prompt 11: JWT Security Config (copy from User Service)**

```

Copy JwtAuthFilter and SecurityConfig from user-service to project-service.
Modify SecurityConfig to:

- Permit: /api/projects/** and /api/tasks/** (all require authentication)
- Keep same JWT validation logic

```

**Prompt 12: Application Configuration**

```

Create application.yml for project-service:

- Server port: 8082
- Spring application name: project-service
- Same database configuration as user-service
- Same JWT secret
- JPA settings
  💻 Manual Tasks for Day 3:

1. Create folder structure
2. Copy all AI-generated code
3. Run application:
   bash
   cd project-service
   mvn clean install
   mvn spring-boot:run
4. Test with Postman:
   Create Project:
   json
   POST http://localhost:8082/api/projects
   Authorization: Bearer <token-from-day2>
   Content-Type: application/json

{
"name": "Mobile App Development",
"description": "iOS and Android app"
}
Create Task:
json
POST http://localhost:8082/api/projects/1/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
"title": "Design login screen",
"description": "Create UI mockups",
"assigneeId": 1,
"status": "TODO",
"priority": "HIGH",
"dueDate": "2024-12-31"
}

```

**Get Tasks (paginated):**

```

GET http://localhost:8082/api/projects/1/tasks?page=0&size=10
Authorization: Bearer <token>

```

### **✅ Day 3 Checklist:**

- [ ] Project Service runs on port 8082
- [ ] Can create projects with JWT
- [ ] Can create tasks under projects
- [ ] Pagination works for projects and tasks
- [ ] JPQL queries return correct results
- [ ] Can filter tasks by status
- [ ] Postman collection updated
- [ ] Code committed to GitHub

### **🎓 Skills Used Today:**

- Spring Data JPA relationships (@OneToMany, @ManyToOne)
- JPQL custom queries
- Pagination with Pageable
- RESTful resource design
- JWT token extraction from SecurityContext

---

## **DAY 4: Advanced Queries & Comments**

### **🎯 Goals:**

- Add Comments functionality (One-to-Many with Task)
- Implement Criteria API for dynamic filtering
- Add file attachments entity
- Database performance optimization

### **📁 New Files to Create:**

```

project-service/
└── src/main/java/com/taskmanager/project/
├── entity/
│ ├── Comment.java
│ └── Attachment.java
├── repository/
│ ├── CommentRepository.java
│ └── AttachmentRepository.java
├── service/
│ ├── CommentService.java
│ ├── AttachmentService.java
│ └── TaskSearchService.java (Criteria API)
├── controller/
│ ├── CommentController.java
│ └── AttachmentController.java
└── dto/
├── CommentRequest.java
├── CommentResponse.java
├── TaskSearchCriteria.java
└── AttachmentResponse.java

```

### **🤖 AI Prompts for Day 4:**

**Prompt 1: Comment Entity**

```

Create Comment JPA entity with:

- @Entity, @Table(name = "comments")
- Fields: id, content (text), userId (Long), createdAt
- @ManyToOne with Task (fetch = LAZY, @JoinColumn name = "task_id")
- Add @PrePersist for createdAt
- Lombok annotations
- Package: com.taskmanager.project.entity

```

**Prompt 2: Update Task Entity**

```

Update the existing Task entity to add:

- @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();
- Add helper method: addComment(Comment comment) that sets bidirectional relationship

```

**Prompt 3: Comment Repository**

```

Create CommentRepository extending JpaRepository<Comment, Long>:

- Custom query: findByTaskIdOrderByCreatedAtDesc(Long taskId)
- Custom query: findByUserId(Long userId, Pageable pageable)
- Count query: countByTaskId(Long taskId)
  Package: com.taskmanager.project.repository

```

**Prompt 4: Comment Service**

```

Create CommentService with:

- @Service, inject CommentRepository, TaskRepository
- Methods:
  1. addComment(Long taskId, CommentRequest request, Long userId)
  2. getCommentsByTask(Long taskId)
  3. updateComment(Long commentId, String content, Long userId) - verify ownership
  4. deleteComment(Long commentId, Long userId) - verify ownership
- Throw exceptions: TaskNotFoundException, CommentNotFoundException, UnauthorizedException
  Package: com.taskmanager.project.service

```

**Prompt 5: Comment Controller**

```

Create CommentController with:

- @RestController, @RequestMapping("/api/tasks/{taskId}/comments")
- Inject CommentService

Endpoints:

1. POST / - add comment to task
2. GET / - get all comments for task
3. PUT /{commentId} - update comment
4. DELETE /{commentId} - delete comment

Extract userId from JWT SecurityContext.
Package: com.taskmanager.project.controller

```

**Prompt 6: Comment DTOs**

```

Create in com.taskmanager.project.dto:

1. CommentRequest:

   - content (String, @NotBlank, @Size max=1000)

2. CommentResponse:
   - id, content, userId, userName (placeholder), taskId, createdAt

Use Lombok.

```

**Prompt 7: Attachment Entity**

```

Create Attachment JPA entity with:

- @Entity, @Table(name = "attachments")
- Fields: id, fileName, filePath, uploadedBy (Long), uploadedAt
- @ManyToOne with Task (fetch = LAZY, @JoinColumn name = "task_id")
- @PrePersist for uploadedAt
- Lombok annotations
  Package: com.taskmanager.project.entity

```

**Prompt 8: Attachment Service (Mock File Upload)**

```

Create AttachmentService with:

- @Service, inject AttachmentRepository, TaskRepository
- Methods:
  1. uploadAttachment(Long taskId, String fileName, Long userId)
     - For now, just save fileName and mock path: "/uploads/" + fileName
     - Return AttachmentResponse
  2. getAttachmentsByTask(Long taskId)
  3. deleteAttachment(Long attachmentId, Long userId) - verify ownership

Note: We'll implement real file upload later. For now, just store metadata.
Package: com.taskmanager.project.service

```

**Prompt 9: Attachment Controller**

```

Create AttachmentController with:

- @RestController, @RequestMapping("/api/tasks/{taskId}/attachments")

Endpoints:

1. POST / - mock upload (just pass fileName in request body)
2. GET / - get all attachments for task
3. DELETE /{attachmentId} - delete attachment

Package: com.taskmanager.project.controller

```

**Prompt 10: Criteria API - Task Search Service**

```

Create TaskSearchService using JPA Criteria API for dynamic filtering:

- @Service, inject EntityManager
- Method: searchTasks(TaskSearchCriteria criteria, Pageable pageable)
- TaskSearchCriteria has: projectId, assigneeId, status, priority, dueDateFrom, dueDateTo
- Build dynamic predicates based on non-null criteria fields
- Support pagination
- Return Page<Task>

Example structure:
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Task> query = cb.createQuery(Task.class);
Root<Task> task = query.from(Task.class);
List<Predicate> predicates = new ArrayList<>();

if (criteria.getStatus() != null) {
predicates.add(cb.equal(task.get("status"), criteria.getStatus()));
}
// ... add more predicates

query.where(predicates.toArray(new Predicate[0]));

Package: com.taskmanager.project.service

```

**Prompt 11: Add Search Endpoint to TaskController**

```

Update TaskController to add:

- GET /api/tasks/search - accepts query params (projectId, assigneeId, status, priority, dueDateFrom, dueDateTo, page, size)
- Use TaskSearchService to perform search
- Return Page<TaskResponse>

```

**Prompt 12: Database Performance Script**

```

Create a SQL script (database-optimization.sql) with:

1. Create indexes:
   - CREATE INDEX idx_tasks_project_status ON tasks(project_id, status);
   - CREATE INDEX idx_tasks_assignee_due ON tasks(assignee_id, due_date);
   - CREATE INDEX idx_comments_task ON comments(task_id, created_at DESC);
2. Analyze query performance command for PostgreSQL:

   - EXPLAIN ANALYZE SELECT \* FROM tasks WHERE project_id = 1 AND status = 'IN_PROGRESS';

3. Connection pool configuration for application.yml:
   spring:
   datasource:
   hikari:
   maximum-pool-size: 10
   minimum-idle: 5
   connection-timeout: 20000
   💻 Manual Tasks for Day 4:
4. Create all new files
5. Copy AI-generated code
6. Run the optimization SQL script:
   bash
   psql -U postgres -d taskmanager_dev -f database-optimization.sql
7. Update application.yml with connection pool config
8. Restart application:
   bash
   mvn clean install
   mvn spring-boot:run
9. Test with Postman:
   Add Comment:
   json
   POST http://localhost:8082/api/tasks/1/comments
   Authorization: Bearer <token>
   Content-Type: application/json

{
"content": "This is a test comment on the task"
}

```

**Get Comments:**

```

GET http://localhost:8082/api/tasks/1/comments
Authorization: Bearer <token>

```

**Search Tasks (Criteria API):**

````

GET http://localhost:8082/api/tasks/search?status=TODO&priority=HIGH&page=0&size=10
Authorization: Bearer <token>
Mock File Upload:
RN
Continue
POST http://localhost:8082/api/tasks/1/attachments Authorization: Bearer <token> Content-Type: application/json
{ "fileName": "design-mockup.png" }

### **✅ Day 4 Checklist:**

- [ ] Comments functionality working
- [ ] Can add/view/update/delete comments
- [ ] Attachment metadata saving (mock upload)
- [ ] Criteria API search working with multiple filters
- [ ] Database indexes created
- [ ] Connection pool configured
- [ ] All tests passing in Postman
- [ ] Code committed to GitHub

### **🎓 Skills Used Today:**

- JPA Criteria API for dynamic queries
- One-to-Many bidirectional relationships
- Database indexing and optimization
- Connection pooling
- Complex query building

---

## **DAY 5: Eureka Server & Service Discovery**

### **🎯 Goals:**

- Setup Eureka Server
- Register User Service and Project Service with Eureka
- Test service discovery

### **📁 New Files to Create:**

eureka-server/ ├── pom.xml └── src/main/ ├── java/com/taskmanager/eureka/ │ └── EurekaServerApplication.java └── resources/ └── application.yml

### **🤖 AI Prompts for Day 5:**

**Prompt 1: Eureka Server POM**
Create pom.xml for eureka-server module with:
• Parent: task-management-platform
• Artifact ID: eureka-server
• Dependencies:
o spring-cloud-starter-netflix-eureka-server
o spring-boot-starter-web
• Build plugin: spring-boot-maven-plugin

**Prompt 2: Eureka Server Application**
Create EurekaServerApplication class with:
• @SpringBootApplication
• @EnableEurekaServer
• main method with SpringApplication.run
• Package: com.taskmanager.eureka

**Prompt 3: Eureka Server Configuration**
Create application.yml for eureka-server: server: port: 8761
spring: application: name: eureka-server
eureka: client: register-with-eureka: false fetch-registry: false server: enable-self-preservation: false

**Prompt 4: Update User Service POM for Eureka Client**
Add these dependencies to user-service pom.xml:
• spring-cloud-starter-netflix-eureka-client

**Prompt 5: Update User Service Configuration**
Update user-service application.yml to add:
eureka: client: service-url: defaultZone: http://localhost:8761/eureka/ register-with-eureka: true fetch-registry: true instance: hostname: localhost prefer-ip-address: true

**Prompt 6: Enable Eureka Client in User Service**
Update UserServiceApplication to add:
• @EnableDiscoveryClient annotation

**Prompt 7: Update Project Service for Eureka**

1. Add spring-cloud-starter-netflix-eureka-client dependency to project-service pom.xml
2. Update ProjectServiceApplication to add @EnableDiscoveryClient
3. Update application.yml to add same eureka configuration as user-service

### **💻 Manual Tasks for Day 5:**

1. **Create eureka-server folder and files**
2. **Update parent pom.xml** to ensure all modules included
3. **Start services in order:**

```bash
# Terminal 1: Start Eureka Server
cd eureka-server
mvn clean install
mvn spring-boot:run

# Wait for Eureka to start (check http://localhost:8761)

# Terminal 2: Start User Service
cd user-service
mvn clean install
mvn spring-boot:run

# Terminal 3: Start Project Service
cd project-service
mvn clean install
mvn spring-boot:run
````

4. **Verify in Eureka Dashboard:**

   - Open browser: http://localhost:8761
   - Should see USER-SERVICE and PROJECT-SERVICE registered

5. **Test that services still work:**
   - Login via User Service (port 8081)
   - Create project via Project Service (port 8082)

### **✅ Day 5 Checklist:**

- [ ] Eureka Server running on port 8761
- [ ] Eureka dashboard accessible
- [ ] User Service registered with Eureka
- [ ] Project Service registered with Eureka
- [ ] Both services show "UP" status
- [ ] Services remain functional
- [ ] Code committed to GitHub

### **🎓 Skills Used Today:**

- Spring Cloud Netflix Eureka
- Service discovery concepts
- Microservices registration
- Multi-module Maven builds

---

## **DAY 6: Config Server & API Gateway**

### **🎯 Goals:**

- Setup Config Server with Git repository
- Create API Gateway for routing
- Centralize configuration
- Test routing through gateway

### **📁 New Files to Create:**

config-server/ ├── pom.xml └── src/main/ ├── java/com/taskmanager/config/ │ └── ConfigServerApplication.java └── resources/ └── application.yml
api-gateway/ ├── pom.xml └── src/main/ ├── java/com/taskmanager/gateway/ │ ├── GatewayApplication.java │ ├── config/ │ │ └── GatewayConfig.java │ └── filter/ │ └── JwtAuthenticationFilter.java └── resources/ └── application.yml
config-repo/ (separate Git repo) ├── user-service.yml ├── project-service.yml ├── api-gateway.yml └── application.yml (shared config)

### **🤖 AI Prompts for Day 6:**

**Prompt 1: Config Server POM**
Create pom.xml for config-server with:
• Parent: task-management-platform
• Dependencies:
o spring-cloud-config-server
o spring-boot-starter-web
• Build plugin

**Prompt 2: Config Server Application**
Create ConfigServerApplication with:
• @SpringBootApplication
• @EnableConfigServer
• main method
• Package: com.taskmanager.config

**Prompt 3: Config Server Configuration**
Create application.yml for config-server: server: port: 8888
spring: application: name: config-server cloud: config: server: git: uri: file:///${user.home}/config-repo # Later can change to GitHub: https://github.com/yourusername/config-repo default-label: main

**Prompt 4: Create Config Repository Files**
Create these YAML files for config-repo directory:

1. application.yml (shared across all services): jwt: secret: mySecretKey12345678901234567890 expiration: 86400000
   spring: jpa: hibernate: ddl-auto: update show-sql: true properties: hibernate: dialect: org.hibernate.dialect.PostgreSQLDialect
2. user-service.yml: server: port: 8081
   spring: datasource: url: jdbc:postgresql://localhost:5432/taskmanager_dev username: postgres password: postgres
3. project-service.yml: server: port: 8082
   spring: datasource: url: jdbc:postgresql://localhost:5432/taskmanager_dev username: postgres password: postgres
4. api-gateway.yml: server: port: 8080
   spring: cloud: gateway: routes: - id: user-service uri: lb://USER-SERVICE predicates: - Path=/api/auth/** - id: project-service-projects uri: lb://PROJECT-SERVICE predicates: - Path=/api/projects/** - id: project-service-tasks uri: lb://PROJECT-SERVICE predicates: - Path=/api/tasks/\*\*

**Prompt 5: API Gateway POM**
Create pom.xml for api-gateway with:
• Parent: task-management-platform
• Dependencies:
o spring-cloud-starter-gateway
o spring-cloud-starter-netflix-eureka-client
o spring-cloud-starter-config
o jjwt libraries (for JWT validation)
o lombok

**Prompt 6: API Gateway Application**
Create GatewayApplication with:
• @SpringBootApplication
• @EnableDiscoveryClient
• main method
• Package: com.taskmanager.gateway

**Prompt 7: API Gateway JWT Filter**
Create JwtAuthenticationFilter implementing GlobalFilter with:
• Inject JwtUtil (copy from user-service)
• In filter method:
o Extract JWT from Authorization header
o Validate token
o If valid, continue chain
o If invalid or missing (and not auth endpoints), return 401
• Order: -1 (high priority)
• Package: com.taskmanager.gateway.filter

**Prompt 8: API Gateway Configuration**
Create bootstrap.yml (not application.yml) for api-gateway: spring: application: name: api-gateway cloud: config: uri: http://localhost:8888 fail-fast: true

**Prompt 9: Update Services to Use Config Server**
For user-service and project-service:

1. Add dependency to pom.xml:
   o spring-cloud-starter-config
2. Rename application.yml to bootstrap.yml
3. Update bootstrap.yml: spring: application: name: user-service # or project-service cloud: config: uri: http://localhost:8888 fail-fast: true
4. Remove all configuration that's now in config-repo (keep only app name and config server URI)

### **💻 Manual Tasks for Day 6:**

1. **Create config-repo directory:**

```bash
cd ~
mkdir config-repo
cd config-repo
git init
# Copy all YAML files from Prompt 4
git add .
git commit -m "Initial configuration"
```

2. **Create config-server and api-gateway modules**

3. **Start services in order:**

```bash
# Terminal 1: Config Server
cd config-server
mvn clean install
mvn spring-boot:run

# Verify: http://localhost:8888/user-service/default
# Should return configuration

# Terminal 2: Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 3: User Service
cd user-service
mvn clean install
mvn spring-boot:run

# Terminal 4: Project Service
cd project-service
mvn clean install
mvn spring-boot:run

# Terminal 5: API Gateway
cd api-gateway
mvn clean install
mvn spring-boot:run
```

4. **Test through API Gateway:**

**Register (through gateway):**

```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "gateway@test.com",
  "password": "test123",
  "name": "Gateway Test",
  "role": "MEMBER"
}
```

**Create Project (through gateway):**

```json
POST http://localhost:8080/api/projects
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Gateway Test Project",
  "description": "Testing via API Gateway"
}
```

5. **Verify routing:**
   - All requests go to :8080 (gateway)
   - Gateway routes to correct service
   - JWT validation happens at gateway level

### **✅ Day 6 Checklist:**

- [ ] Config Server running on port 8888
- [ ] Config repo initialized with Git
- [ ] Services fetch config from Config Server
- [ ] API Gateway running on port 8080
- [ ] Can register user through gateway
- [ ] Can create project through gateway
- [ ] JWT validation working at gateway
- [ ] All 5 services visible in Eureka
- [ ] Code committed to GitHub

### **🎓 Skills Used Today:**

- Spring Cloud Config Server
- Git-based configuration
- Spring Cloud Gateway
- API Gateway patterns
- Centralized authentication
- Load balancing with Eureka

---

## **DAY 7: Feign Client & Kafka Setup**

### **🎯 Goals:**

- Implement Feign Client for inter-service communication
- Setup Kafka with Docker
- Create Notification Service
- Integrate Kafka producer in Project Service

### **📁 New Files to Create:**

project-service/ └── src/main/java/com/taskmanager/project/ ├── client/ │ └── UserServiceClient.java ├── config/ │ └── FeignConfig.java └── kafka/ ├── KafkaProducerConfig.java ├── TaskEventProducer.java └── dto/ └── TaskEvent.java
notification-service/ ├── pom.xml └── src/main/ ├── java/com/taskmanager/notification/ │ ├── NotificationServiceApplication.java │ ├── entity/ │ │ └── Notification.java │ ├── repository/ │ │ └── NotificationRepository.java │ ├── service/ │ │ └── NotificationService.java │ ├── controller/ │ │ └── NotificationController.java │ ├── kafka/ │ │ ├── KafkaConsumerConfig.java │ │ ├── TaskEventConsumer.java │ │ └── dto/ │ │ └── TaskEvent.java │ └── dto/ │ └── NotificationResponse.java └── resources/ └── application.yml
docker-compose.yml (root directory)

### **🤖 AI Prompts for Day 7:**

**Prompt 1: Docker Compose for Kafka**
Create docker-compose.yml in project root with:
• Zookeeper service (image: confluentinc/cp-zookeeper:latest)
o Environment: ZOOKEEPER_CLIENT_PORT=2181
• Kafka service (image: confluentinc/cp-kafka:latest)
o Depends on Zookeeper
o Ports: 9092:9092
o Environment:
 KAFKA_BROKER_ID=1
 KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
 KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
 KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
• PostgreSQL service (for convenience)
o Image: postgres:15
o Environment: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
o Ports: 5432:5432

**Prompt 2: Feign Client Interface**
Create UserServiceClient Feign interface in project-service:
• @FeignClient(name = "user-service")
• Methods:
o @GetMapping("/api/users/{id}") UserDTO getUserById(@PathVariable Long id)
o @GetMapping("/api/users/email/{email}") UserDTO getUserByEmail(@PathVariable String email)
• Create UserDTO:
o id, email, name, role
o Lombok annotations
• Package: com.taskmanager.project.client

**Prompt 3: Feign Configuration**
Create FeignConfig in project-service:
• @Configuration
• Add @EnableFeignClients(basePackages = "com.taskmanager.project.client")
• Create RequestInterceptor bean to add JWT token to Feign requests:
o Extract token from SecurityContext
o Add to Authorization header
• Add error decoder for handling Feign exceptions
• Package: com.taskmanager.project.config

**Prompt 4: Update Project Service POM for Feign**
Add to project-service pom.xml:
• spring-cloud-starter-openfeign
• Ensure Spring Cloud version is compatible

**Prompt 5: Use Feign Client in TaskService**
Update TaskService to:
• Inject UserServiceClient
• In createTask method, fetch user details using Feign: UserDTO user = userServiceClient.getUserById(request.getAssigneeId()); // Validate user exists before creating task
• In getTaskById, enrich response with assignee name from User Service

**Prompt 6: Kafka Producer Configuration**
Create KafkaProducerConfig in project-service:
• @Configuration
• @EnableKafka
• Bean: ProducerFactory<String, TaskEvent>
o Bootstrap servers: localhost:9092
o Key serializer: StringSerializer
o Value serializer: JsonSerializer
• Bean: KafkaTemplate<String, TaskEvent>
• Package: com.taskmanager.project.kafka

**Prompt 7: Task Event DTO**
Create TaskEvent class in project-service:
• Fields:
o eventType (String: TASK_CREATED, TASK_ASSIGNED, TASK_UPDATED, TASK_COMPLETED)
o taskId (Long)
o taskTitle (String)
o assigneeId (Long)
o assigneeName (String)
o projectId (Long)
o projectName (String)
o timestamp (LocalDateTime)
• Lombok: @Data, @NoArgsConstructor, @AllArgsConstructor
• Package: com.taskmanager.project.kafka.dto

**Prompt 8: Task Event Producer**
Create TaskEventProducer in project-service:
• @Service
• Inject KafkaTemplate<String, TaskEvent>
• Method: sendTaskEvent(TaskEvent event)
o Topic name: "task-events"
o Send event with taskId as key
o Log success/failure
• Package: com.taskmanager.project.kafka

**Prompt 9: Update TaskService to Publish Events**
Update TaskService to:
• Inject TaskEventProducer
• In createTask: publish TASK_CREATED event
• In updateTask: publish TASK_UPDATED event
• In updateTaskStatus: publish TASK_COMPLETED event if status = DONE
• Include all relevant task details in event

**Prompt 10: Update Project Service POM for Kafka**
Add to project-service pom.xml:
• spring-kafka
• jackson-databind (for JSON serialization)

**Prompt 11: Notification Service POM**
Create pom.xml for notification-service with:
• Parent: task-management-platform
• Dependencies:
o spring-boot-starter-web
o spring-boot-starter-data-jpa
o spring-kafka
o postgresql
o spring-cloud-starter-netflix-eureka-client
o spring-cloud-starter-config
o lombok

**Prompt 12: Notification Entity**
Create Notification JPA entity:
• @Entity, @Table(name = "notifications")
• Fields:
o id (Long, @Id, @GeneratedValue)
o userId (Long, not null)
o message (String, not null)
o type (String: TASK_ASSIGNED, TASK_UPDATED, etc.)
o isRead (Boolean, default false)
o createdAt (LocalDateTime)
• @PrePersist for createdAt
• Lombok annotations
• Package: com.taskmanager.notification.entity

**Prompt 13: Notification Repository**
Create NotificationRepository extending JpaRepository<Notification, Long>:
• Custom query: findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable)
• Custom query: countByUserIdAndIsReadFalse(Long userId)
• Custom query: findByUserIdAndIsReadFalse(Long userId)
• Package: com.taskmanager.notification.repository

**Prompt 14: Kafka Consumer Configuration**
Create KafkaConsumerConfig in notification-service:
• @Configuration
• @EnableKafka
• Bean: ConsumerFactory<String, TaskEvent>
o Bootstrap servers: localhost:9092
o Group ID: notification-service-group
o Key deserializer: StringDeserializer
o Value deserializer: JsonDeserializer
o Trusted packages: \*
• Bean: ConcurrentKafkaListenerContainerFactory
• Package: com.taskmanager.notification.kafka

**Prompt 15: Copy TaskEvent DTO**
Copy TaskEvent class from project-service to notification-service:
• Same fields and structure
• Package: com.taskmanager.notification.kafka.dto

**Prompt 16: Task Event Consumer**
Create TaskEventConsumer in notification-service:
• @Service
• Inject NotificationService
• Method with @KafkaListener:
o Topic: "task-events"
o GroupId: "notification-service-group"
o Process TaskEvent and create notification:
 If TASK_ASSIGNED: "You have been assigned to task: {taskTitle}"
 If TASK_UPDATED: "Task {taskTitle} has been updated"
 If TASK_COMPLETED: "Task {taskTitle} has been completed"
o Call notificationService.createNotification()
• Add error handling
• Package: com.taskmanager.notification.kafka

**Prompt 17: Notification Service**
Create NotificationService with:
• @Service
• Inject NotificationRepository
• Methods:

1. createNotification(Long userId, String message, String type)
2. getUserNotifications(Long userId, int page, int size) - paginated
3. getUnreadCount(Long userId)
4. markAsRead(Long notificationId, Long userId)
5. markAllAsRead(Long userId)
   • Package: com.taskmanager.notification.service

**Prompt 18: Notification Controller**
Create NotificationController with:
• @RestController, @RequestMapping("/api/notifications")
• Extract userId from JWT SecurityContext
• Endpoints:

1. GET / - get user notifications (paginated)
2. GET /unread-count - get unread notification count
3. PUT /{id}/read - mark notification as read
4. PUT /read-all - mark all as read
   • Package: com.taskmanager.notification.controller

**Prompt 19: Notification DTO**
Create NotificationResponse in com.taskmanager.notification.dto:
• Fields: id, message, type, isRead, createdAt
• Lombok annotations

**Prompt 20: Notification Service Configuration**
Create application.yml for notification-service: server: port: 8083
spring: application: name: notification-service datasource: url: jdbc:postgresql://localhost:5432/taskmanager_dev username: postgres password: postgres jpa: hibernate: ddl-auto: update kafka: bootstrap-servers: localhost:9092 consumer: group-id: notification-service-group auto-offset-reset: earliest
eureka: client: service-url: defaultZone: http://localhost:8761/eureka/

**Prompt 21: Notification Service Main Class**
Create NotificationServiceApplication with:
• @SpringBootApplication
• @EnableDiscoveryClient
• main method
• Package: com.taskmanager.notification

**Prompt 22: Add Security Config to Notification Service**
Copy JWT security configuration from user-service to notification-service:
• JwtAuthFilter
• SecurityConfig
• JwtUtil
• Same JWT validation logic

**Prompt 23: Update API Gateway Routes**
Update api-gateway.yml in config-repo to add notification service route:
• id: notification-service uri: lb://NOTIFICATION-SERVICE predicates:
o Path=/api/notifications/\*\*

### **💻 Manual Tasks for Day 7:**

1. **Start Kafka with Docker:**

```bash
cd <project-root>
docker-compose up -d

# Verify Kafka is running
docker ps
```

2. **Create notification-service module structure**

3. **Start all services in order:**

```bash
# Config Server (8888)
# Eureka Server (8761)
# User Service (8081)
# Project Service (8082)
# Notification Service (8083)
# API Gateway (8080)
```

4. **Test end-to-end flow:**

**Create task (triggers Kafka event):**

```json
POST http://localhost:8080/api/projects/1/tasks
Authorization: Bearer <token>

{
  "title": "Test Kafka Integration",
  "description": "This should trigger a notification",
  "assigneeId": 1,
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31"
}
```

**Check notifications:**
GET http://localhost:8080/api/notifications Authorization: Bearer <token>

**Check unread count:**
GET http://localhost:8080/api/notifications/unread-count Authorization: Bearer <token>

5. **Verify Kafka:**

```bash
# Check Kafka topics
docker exec -it <kafka-container-id> kafka-topics --list --bootstrap-server localhost:9092

# Should see "task-events" topic
```

### **✅ Day 7 Checklist:**

- [ ] Docker Compose running Kafka and Zookeeper
- [ ] Feign Client working (can fetch user from User Service)
- [ ] Task creation enriched with user details
- [ ] Kafka producer sending events on task creation
- [ ] Notification Service consuming Kafka events
- [ ] Notifications saved in database
- [ ] Can view notifications via API
- [ ] Unread count working
- [ ] Mark as read functionality working
- [ ] All services communicating properly
- [ ] Code committed to GitHub

### **🎓 Skills Used Today:**

- Spring Cloud OpenFeign
- Inter-service communication
- Apache Kafka setup with Docker
- Kafka Producer/Consumer
- Event-driven architecture
- JSON serialization/deserialization
- Docker Compose

---

# 🎉 **WEEK 1 COMPLETE!**

## **What You've Built:**

✅ 4 microservices (User, Project, Notification, API Gateway)
✅ Eureka Service Discovery
✅ Config Server with Git
✅ JWT Authentication
✅ Complex JPA relationships
✅ JPQL and Criteria API queries
✅ Kafka event-driven notifications
✅ Feign Client inter-service calls
✅ Docker Compose for infrastructure

## **Running Services:**

- Config Server: 8888
- Eureka Server: 8761
- User Service: 8081
- Project Service: 8082
- Notification Service: 8083
- API Gateway: 8080
- Kafka: 9092
- PostgreSQL: 5432

---
