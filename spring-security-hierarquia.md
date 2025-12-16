# Spring Security – Gestão de Acesso por Hierarquia

> **Nível:** Sênior / Arquiteto de Software  
> **Stack alvo:** Spring Boot 3.x • Spring Security 6.x • OAuth2 Resource Server • JWT  
> **Estilo arquitetural:** Stateless • Cloud-ready • Microservices-friendly

---

## 1. Visão Geral

Este documento descreve **passo a passo**, desde a configuração inicial até casos avançados de uso, **a forma moderna, performática e escalável** de gerenciar acesso a endpoints protegidos com **hierarquias de usuários** (ADMIN, MODERATOR, USER) utilizando Spring Security.

O foco está em:
- Segurança declarativa
- Zero acoplamento ao banco em tempo de request
- Alto desempenho
- Manutenibilidade
- Padrões usados em ambientes corporativos e SaaS

---

## 2. Problema a ser resolvido

Em sistemas modernos, precisamos:

- Controlar acesso por **níveis hierárquicos**
- Garantir que **ADMIN herde permissões** de outros perfis
- Evitar `if/else` espalhados no código
- Não depender de sessão HTTP
- Escalar horizontalmente sem fricção

Exemplo de hierarquia:

```
ADMIN > MODERATOR > USER
```

---

## 3. Arquitetura Recomendada

### 3.1 Modelo de Segurança

✔ **Stateless Security**  
✔ **Token-based Authentication (JWT)**  
✔ **OAuth2 Resource Server**  
✔ **RBAC com Hierarquia**

Fluxo simplificado:

```
Client → Auth Server → JWT → API (Resource Server)
```

> A API **não autentica usuários**, apenas **valida tokens**.

---

## 4. Dependências Essenciais

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

---

## 5. Modelo de Roles (Design Correto)

### 5.1 Enum de Roles

```java
public enum Role {
    ROLE_ADMIN,
    ROLE_MODERATOR,
    ROLE_USER
}
```

**Boas práticas:**
- Prefixo `ROLE_`
- Semântica clara
- Independente de persistência

---

## 6. Hierarquia de Roles (Ponto-Chave)

### 6.1 Configuração da Hierarquia

```java
@Bean
RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("""
        ROLE_ADMIN > ROLE_MODERATOR
        ROLE_MODERATOR > ROLE_USER
    """);
    return hierarchy;
}
```

### 6.2 Benefícios

- Elimina duplicação de regras
- ADMIN automaticamente possui permissões inferiores
- Código mais limpo e sustentável

---

## 7. Configuração Moderna do Spring Security

> **Importante:** `WebSecurityConfigurerAdapter` está obsoleto.

### 7.1 SecurityFilterChain

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/moderator/**").hasRole("MODERATOR")
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
        )
        .build();
}
```

---

## 8. JWT como Fonte Única de Autoridade

### 8.1 Exemplo de Payload JWT

```json
{
  "sub": "123",
  "email": "user@email.com",
  "roles": ["ROLE_ADMIN"]
}
```

✔ Nenhuma consulta ao banco  
✔ Autorização 100% em memória

---

## 9. Conversão de Claims → Authorities

### 9.1 JwtAuthenticationConverter

```java
@Bean
JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthoritiesClaimName("roles");
    converter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

    return jwtConverter;
}
```

### 9.2 Registro no Resource Server

```java
oauth2.jwt(jwt ->
    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
)
```

---

## 10. Autorização no Nível de Método (Recomendado)

### 10.1 Ativação

```java
@EnableMethodSecurity
```

### 10.2 Exemplos de Uso

```java
@PreAuthorize("hasRole('ADMIN')")
public void criarUsuario() {}

@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public void moderarConteudo() {}
```

**Vantagens:**
- Alta legibilidade
- Menos regras no controller
- Melhor manutenção

---

## 11. Casos Avançados – Autorização Baseada em Domínio (ABAC)

### 11.1 Quando usar

- Dono do recurso
- Regras contextuais
- Permissões dinâmicas

### 11.2 Exemplo

```java
@PreAuthorize("@securityService.isOwner(#postId)")
public void editarPost(Long postId) {}
```

```java
@Component
public class SecurityService {
    public boolean isOwner(Long postId) {
        Long userId = SecurityUtils.getUserId();
        return postRepository.isOwner(postId, userId);
    }
}
```

---

## 12. Performance e Escalabilidade

✔ Stateless
✔ Sem sessão HTTP
✔ Sem hits ao banco
✔ Ideal para Kubernetes
✔ Fácil horizontalização

---

## 13. Anti‑Padrões (Evite)

❌ Sessão HTTP  
❌ `UserDetailsService` por request  
❌ `if (user.isAdmin())`  
❌ `@Secured` (legado)  
❌ Regras de acesso no service layer

---

## 14. Casos de Uso Reais

- SaaS B2B
- Marketplaces
- Backoffice administrativo
- APIs públicas protegidas
- Microserviços corporativos

---

## 15. Conclusão

Este modelo representa **o estado da arte em Spring Security**:

- Seguro
- Performático
- Escalável
- Fácil de evoluir

Ele atende desde projetos médios até **ambientes corporativos de grande escala**.

---

## 16. Próximos Passos

- Integrar com Keycloak / Auth0
- Externalizar permissões
- Aplicar em arquitetura de microserviços
- Implementar testes de segurança

---

**Documento preparado no nível de Arquiteto para uso em produção.**

