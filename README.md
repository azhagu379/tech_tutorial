# Tutorial Application

This project is a **Technology Tutorial Application** built using Spring Boot for the backend and Next.js for the frontend. The application incorporates role-based authentication and authorization with support for dynamic permissions.

---

## **Tech Stack**

### **Backend**
- **Spring Boot**: Core framework for backend development.
- **PostgreSQL**: Database for storing users, roles, and permissions.
- **Redis**: Cache for performance optimization.
- **JWT**: Secure token-based authentication.
- **OpenAPI (Swagger)**: API documentation.

### **Frontend**
- **Next.js**: React-based framework for building the user interface.
- **Material-UI (MUI)**: For designing a modern and responsive UI.

---

## **Features**

1. **User Management**:
   - One user has one role.
   - Roles define user permissions.
2. **Role Management**:
   - Each role is associated with a set of permissions.
   - Permissions can include `READ`, `WRITE`, `DELETE`, etc.
3. **Authentication**:
   - Users authenticate via JWT tokens.
   - Secure login and logout functionality.
4. **Authorization**:
   - Role-based and permission-based access control.
   - Middleware to enforce secure access to resources.
5. **API Documentation**:
   - Explore and test APIs using Swagger.

---

## **Database Schema**

### **Users Table**
| Field       | Type     | Description       |
|-------------|----------|-------------------|
| `id`        | UUID     | Primary key       |
| `username`  | String   | Unique username   |
| `password`  | String   | Hashed password   |
| `role_id`   | UUID     | Foreign key to roles |

### **Roles Table**
| Field       | Type     | Description          |
|-------------|----------|----------------------|
| `id`        | UUID     | Primary key          |
| `name`      | String   | Role name (e.g., ADMIN, USER) |
| `description` | String | Role description     |

### **Permissions Table**
| Field       | Type     | Description          |
|-------------|----------|----------------------|
| `id`        | UUID     | Primary key          |
| `name`      | String   | Permission name (e.g., READ) |
| `description` | String | Permission description |

### **Role-Permissions Table**
| Field         | Type     | Description              |
|---------------|----------|--------------------------|
| `role_id`     | UUID     | Foreign key to roles     |
| `permission_id` | UUID   | Foreign key to permissions |

---

## **Getting Started**

### **Prerequisites**
- Java 17+
- Node.js 16+
- PostgreSQL
- Redis

### **Backend Setup**
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd tutorial-backend
   ```
2. Configure the database in `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/tutorial_db
       username: your_db_username
       password: your_db_password
     jpa:
       hibernate:
         ddl-auto: update
   ```
3. Run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```

### **Frontend Setup**
1. Navigate to the frontend directory:
   ```bash
   cd tutorial-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```

---

## **API Endpoints**

### **Authentication**
- `POST /api/auth/login`: User login.
- `POST /api/auth/logout`: User logout.

### **User Management**
- `GET /api/users`: List all users.
- `POST /api/users`: Create a new user.

### **Role Management**
- `GET /api/roles`: List all roles.
- `POST /api/roles`: Create a new role.

### **Permission Management**
- `GET /api/permissions`: List all permissions.
- `POST /api/permissions`: Create a new permission.

---

## **Contributing**

1. Fork the repository.
2. Create a new branch for your feature or bugfix:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add new feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Submit a pull request.

---

## **License**
This project is licensed under the MIT License. See the LICENSE file for details.

