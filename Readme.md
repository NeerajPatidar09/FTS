
# FTS

FTS is an enterprise-level financial monitoring and compliance management platform designed to help financial institutions monitor transactions, identify suspicious activities, manage risk alerts, and conduct investigations. It provides centralized management of customers, accounts, transactions, alerts, and investigations, along with secure user access through role-based permissions, multi-factor authentication (MFA), and maker-checker approval workflows.

The platform is intended for banks, financial institutions, compliance teams, risk management departments, fraud investigators, and financial administrators who need a secure, centralized solution for financial surveillance, operational control, and regulatory compliance.


## Environment Variables

To run this project locally, configure the following environment variables in your `.env` file or provide them through your deployment environment.

### Backend Configuration

| Variable | Description |
|---|---|
| `DB_URL` | MySQL database connection URL |
| `DB_USERNAME` | MySQL database username |
| `DB_PASSWORD` | MySQL database password |
| `JWT_SECRET` | Secret key used for JWT signing |
| `JWT_ACCESS_EXPIRATION` | Access token expiration time |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration time |
| `MFA_ENCRYPTION_KEY` | Base64-encoded AES-256 encryption key for MFA secrets |
| `MAIL_HOST` | SMTP server hostname |
| `MAIL_PORT` | SMTP server port |
| `MAIL_USERNAME` | SMTP username |
| `MAIL_PASSWORD` | SMTP password |
| `APP_FRONTEND_URL` | Frontend application URL |

### Example `.env` file

```env
DB_URL=jdbc:mysql://localhost:3306/fts_db
DB_USERNAME=root
DB_PASSWORD=your_database_password

JWT_SECRET=your_secure_jwt_secret
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

MFA_ENCRYPTION_KEY=your_base64_encoded_32_byte_key

MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password

APP_FRONTEND_URL=http://localhost:5173
```

## Tech Stack

**Client:** React, TypeScript, Vite, React Router DOM,

**Server:** Java 17, Spring Boot, Maven

**Database:** MySQL


## Screenshots

### Login Page

![FTS Login Page](docs/screenshots/login-page.jpg)
