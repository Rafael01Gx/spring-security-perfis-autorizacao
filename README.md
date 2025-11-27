<div align="center">

# 🔐 Spring Security – Perfis, Autorizações e Recuperação de Senha  
### _Autenticação, Perfis de Acesso, Tokens, E-mails e Segurança com Spring Boot 3_

[![Java](https://img.shields.io/badge/Java-25+-orange?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Authorization-green?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Hibernate](https://img.shields.io/badge/JPA%2FHibernate-ORM-blue?logo=hibernate&logoColor=white)](https://hibernate.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Lombok](https://img.shields.io/badge/Lombok-Ativo-green?logo=lombok&logoColor=white)](https://projectlombok.org/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-red?logo=flyway&logoColor=white)](https://flywaydb.org/)

</div>

---

## 🧭 Sobre o Projeto

Este repositório contém meus estudos do curso **“Java e Spring Security: crie perfis e autorize requisições”**.  
Aqui, evoluí uma API Java/Spring Boot implementando **segurança avançada**, com controle de perfis, permissões, alteração de senha, recuperação de acesso e envio de e-mails com links de verificação.

O sistema utiliza um conjunto de estratégias modernas e seguras baseadas em **Spring Security**, garantindo acesso controlado e experiências diferenciadas para cada perfil de usuário.

---

## 🧩 Conteúdos Abordados

### 🔐 Autenticação e Modelagem de Usuários
- Transformação de entidades existentes em usuários do sistema  
- Associação entre IDs e perfis  
- Modelagem de tabelas de usuários e papéis  

### 🛡️ Perfis e Controle de Acesso
- Criação e registro de perfis de acesso (roles)  
- Proteção de rotas com Spring Security  
- Anotações como `@PreAuthorize`  
- Restrições por tipo de usuário  
- Proteção completa de endpoints sensíveis  

### 📊 Visualizações Restritas
- Exibição diferenciada de dados conforme o perfil  
- Filtros automáticos por usuário/autorização  
- Consultas personalizadas no banco  

### 🔑 Alteração de Senha
- Endpoints para alteração segura  
- Validação da senha antiga  
- Hashing seguro e atualização no banco  
- Mecanismo de geração de senha aleatória  

### 📨 Esqueci minha Senha
- Criação de token UUID exclusivo para o usuário  
- Envio de links por e-mail  
- Validação de token e redefinição de senha  
- Expiração e redefinição do token  

### 🔗 Links de Verificação
- Confirmação por URL com token único  
- Reset de token e segurança contra reutilização  
- Controle de sessão e salvamento de estado  

---

## 🧰 Tecnologias Utilizadas

| Categoria | Ferramenta |
|------------|-------------|
| Linguagem | ![Java](https://img.shields.io/badge/Java-25+-orange?logo=openjdk&logoColor=white) |
| Framework | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen?logo=spring&logoColor=white) |
| Segurança | ![Spring Security](https://img.shields.io/badge/Spring%20Security-green?logo=springsecurity&logoColor=white) |
| Persistência | ![Hibernate](https://img.shields.io/badge/JPA%2FHibernate-blue?logo=hibernate&logoColor=white) |
| Migrações | ![Flyway](https://img.shields.io/badge/Flyway-red?logo=flyway&logoColor=white) |
| Banco de Dados | ![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql&logoColor=white) |
| Build | ![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white) |
| Utilitário | ![Lombok](https://img.shields.io/badge/Lombok-green?logo=lombok&logoColor=white) |
| E-mail | SMTP / JavaMailSender |

---

## ⚙️ Requisitos

- ☕ **Java 25+**  
- 🧱 **Maven 3.x**  
- 🗄️ **MySQL 8.x**  
- ✉️ Conta SMTP (Gmail, Outlook, etc.) para envio de e-mails  
- 🔐 Noções básicas de Spring Security  

---

## 🚀 Como Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/spring-security-perfis-autorizacao.git

# Entre na pasta
cd spring-security-perfis-autorizacao

# Execute a aplicação
./mvnw spring-boot:run
