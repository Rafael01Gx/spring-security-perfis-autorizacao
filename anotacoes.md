# 🛡️ Anotações — SecurityFilterChain no Spring Security

Este documento reúne anotações diretas, resumidas e úteis sobre **todos os filtros da SecurityFilterChain**, organizados por tópicos para estudo rápido.

---

## 🔗 O que é a SecurityFilterChain?

A **SecurityFilterChain** é uma cadeia de filtros criados automaticamente quando adicionamos o Spring Security ao projeto. Cada filtro intercepta a requisição HTTP e aplica alguma regra de segurança.

No log de inicialização, aparece algo como:

```
Will secure any request with [DisableEncodeUrlFilter, WebAsyncManagerIntegrationFilter, SecurityContextHolderFilter, HeaderWriterFilter, CorsFilter, CsrfFilter, LogoutFilter, UsernamePasswordAuthenticationFilter, DefaultLoginPageGeneratingFilter, DefaultLogoutPageGeneratingFilter, BasicAuthenticationFilter, RequestCacheAwareFilter, SecurityContextHolderAwareRequestFilter, AnonymousAuthenticationFilter, ExceptionTranslationFilter, AuthorizationFilter]
```

A seguir, cada filtro explicado de forma clara.

---

## 🧩 1. Filtros relacionados a Sessão e Contexto de Segurança

### **DisableEncodeUrlFilter**

#### Exemplo de configuração

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .sessionManagement(session -> session
                    .sessionFixation().migrateSession()
            );
    return http.build();
}
```

* Impede que o ID de sessão seja anexado na URL (ex: `?JSESSIONID=12345`)
* Previne roubo de sessão por compartilhamento de links
* Obriga o uso de cookies para manter a sessão (mais seguro)

### **WebAsyncManagerIntegrationFilter**

#### Exemplo de uso automático (já incluso pelo Spring Security)

```java
// Nenhuma configuração manual necessária.
// O filtro é habilitado automaticamente ao usar SecurityContext.
```

* Garante que o **SecurityContext** seja preservado também em requisições assíncronas
* Mantém a autenticação válida em chamadas async

### **SecurityContextHolderFilter**

#### Exemplo de acesso ao SecurityContext

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

* Preenche o `SecurityContextHolder` com o contexto do usuário
* Deixa autenticação e permissões disponíveis durante todo o processamento da requisição

---

## 🧩 2. Filtros de Cabeçalhos HTTP

### **HeaderWriterFilter**

#### Como configurar headers personalizados

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers
            .xssProtection(xss -> xss.block(true))
            .frameOptions(frame -> frame.sameOrigin())
    );
    return http.build();
}
```

Adiciona cabeçalhos de proteção, como:

* **X-Content-Type-Options** — impede interpretação incorreta de MIME
* **X-Frame-Options** — previne clickjacking
* **X-XSS-Protection** — proteção adicional contra XSS

### **CorsFilter**

#### Configuração global de CORS

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");
        }
    };
}
```

* Controla políticas de CORS
* Define quais domínios podem consumir a API

### **CsrfFilter**

#### Desabilitando CSRF em APIs stateless

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    return http.build();
}
```

* Protege contra ataques CSRF
* Exige tokens para requisições que modificam estado (POST, PUT, DELETE)

---

## 🧩 3. Filtros de Autenticação

### **UsernamePasswordAuthenticationFilter**

#### Rota de login customizada

```java
@Bean
public SecurityFilterChain filter(HttpSecurity http) throws Exception {
    http.formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/home", true)
    );
    return http.build();
}
```

* Processa login com usuário e senha
* Se as credenciais forem válidas → autentica e cria o SecurityContext

### **BasicAuthenticationFilter**

#### Habilitando HTTP Basic

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.httpBasic(withDefaults());
    return http.build();
}
```

* Lida com autenticação HTTP Basic
* Credenciais enviadas via Base64 a cada requisição

### **AnonymousAuthenticationFilter**

* Fornece um usuário "anônimo" para requisições sem autenticação
* Garante comportamento uniforme mesmo para visitantes

#### Como redefinir usuário anônimo

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.anonymous(anon -> anon
        .principal("guest-user")
        .authorities("ROLE_GUEST")
    );
    return http.build();
}
```

---

## 🧩 6. Remember-Me (Persistência de Login)

### **RememberMeAuthenticationFilter / RememberMeServices**

* Permite que usuários permaneçam autenticados entre sessões ("Lembrar-me")
* Usa um cookie persistente que referencia um token no servidor (melhor) ou contém dados embutidos (menos seguro)
* Deve ser usado com cuidado: configurar `tokenValiditySeconds`, `key` e idealmente `PersistentTokenRepository`

#### Exemplo — Configuração com Persistent Token (recomendado)

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        // repo.setCreateTableOnStartup(true); // criar tabela automaticamente na primeira execução
        return repo;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .rememberMe(remember -> remember
                .rememberMeParameter("remember-me")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(14 * 24 * 60 * 60) // 14 dias
                .key("umaChaveSecretaEUnica")
            );

        return http.build();
    }
}
```

#### Exemplo — Configuração simples (in-memory token)

```java
http.rememberMe(remember -> remember
    .key("umaChaveSecretaEUnica")
    .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 dias
    .userDetailsService(userDetailsService)
);
```

#### Observações de segurança

* Preferir `PersistentTokenRepository` + tabela no banco a armazenar dados completos no cookie.
* Sempre usar HTTPS para cookies de lembrete (atributo `Secure`) e `HttpOnly` quando aplicável.
* Rotacionar ou invalidar tokens em logout e quando houver alteração crítica de conta.

---

## 🧩 4. Filtros de Logout e Páginas Padrão

### **LogoutFilter**

#### Configuração de logout

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.logout(logout -> logout
        .logoutUrl("/auth/logout")
        .logoutSuccessUrl("/login?logout=true")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
    );
    return http.build();
}
```

* Executa o processo de logout
* Encerra a sessão e limpa o SecurityContext

### **DefaultLoginPageGeneratingFilter**

#### Desabilitar página de login padrão

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.formLogin(form -> form.disable());
    return http.build();
}
```

* Gera a página de login padrão do Spring Security
* Usado quando não criamos uma página de login customizada

### **DefaultLogoutPageGeneratingFilter**

#### Desabilitar página de logout padrão

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.logout(logout -> logout.disable());
    return http.build();
}
```

* Gera a página padrão de logout
* Ao acessá-la, o logout é executado

---

## 🧩 5. Outros Filtros Importantes

### **RequestCacheAwareFilter**

#### Definindo estratégia de cache

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.requestCache(cache -> cache
        .requestCache(new HttpSessionRequestCache())
    );
    return http.build();
}
```

* Armazena a requisição que falhou por falta de autenticação
* Após login → redireciona o usuário para a URL original
* Exemplo: tentou `/medicos`, foi para login, e volta para `/medicos` após autenticar

### **SecurityContextHolderAwareRequestFilter**

#### Acessando dados via HttpServletRequest

```java
@GetMapping("/user")
public String getUser(HttpServletRequest req) {
    return req.getRemoteUser();
}
```

* Adiciona métodos úteis ao HttpServletRequest, como:

    * `getRemoteUser()`
    * `isUserInRole()`
* Facilita verificações no front/controller

### **ExceptionTranslationFilter**

#### Configuração de páginas de erro

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.exceptionHandling(ex -> ex
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        .accessDeniedPage("/403")
    );
    return http.build();
}
```

* Captura erros de segurança (AuthenticationException, AccessDeniedException)
* Redireciona:

    * Para página de login (não autenticado)
    * Para página de acesso negado (sem permissão)

### **AuthorizationFilter**

#### Definindo permissões por rota

```java
@Bean
public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/user/**").authenticated()
            .anyRequest().permitAll()
    );
    return http.build();
}
```

* O filtro responsável por verificar **autorização**
* Garante que somente perfis permitidos acessem cada rota

---

## ✅ Resumo Geral

* A SecurityFilterChain é uma cadeia completa de proteção
* Cada filtro resolve um tipo específico de vulnerabilidade
* Juntos, fornecem uma camada robusta de segurança padrão

---

## 📌 Finalidade Geral

Entender esses filtros ajuda a compreender como o Spring Security protege a aplicação e por que cada etapa da requisição passa por diversas camadas de segurança antes de chegar ao controlador.

---

Fim da anotação sobre os filtros da **SecurityFilterChain**.
