# 📝 Anotações — Spring Expression Language (SpEL)

Estas anotações explicam, de forma objetiva e prática, o que é o **Spring Expression Language (SpEL)**, como ele funciona e como é utilizado especialmente dentro do **Spring Security**.

---

## 🔍 O que é o Spring Expression Language (SpEL)?

O **SpEL** é uma linguagem de expressões do Spring que permite:

* Avaliar valores em tempo de execução
* Acessar propriedades e métodos de objetos
* Fazer operações lógicas e aritméticas
* Manipular listas e mapas
* Criar condições dinâmicas para segurança

Ele é interpretado **em tempo de execução**, então erros nas expressões só aparecem quando o código é executado.

---

## 🛡️ Por que SpEL é importante no Spring Security?

O Spring Security usa SpEL dentro das anotações para restringir acesso com base em:

* **Papéis (roles)**
* **Autoridades (permissions)**
* **Propriedades do usuário logado**
* **Regras de negócio dinâmicas**

As anotações mais comuns são:

* `@PreAuthorize` — antes da execução
* `@PostAuthorize` — após a execução

---

# 🧩 Exemplos Essenciais de SpEL no Spring Security

A seguir, os exemplos mais utilizados no dia a dia.

---

## 🔐 1. Verificar ROLE do usuário

A expressão SpEL verifica se o usuário possui a role **ADMIN**:

```java
@PreAuthorize("hasRole('ADMIN')")
public String apenasAdmin() {
    return "Acesso permitido apenas para administradores";
}
```

### ✔ Quando usar?

* Quando seu sistema trabalha com **papéis maiores**, como ADMIN, USER, MODERATOR.

---

## 🔐 2. Verificar AUTHORITY (permissões)

Spring Security diferencia **roles** e **authorities**:

* Role → representa grupo de permissões
* Authority → permissão específica

Exemplo verificando uma permissão:

```java
@PreAuthorize("hasAuthority('PERMISSAO_EDICAO')")
public String edicaoUsuario() {
    return "Edição de usuário permitida";
}
```

### ✔ Quando usar?

* Quando você quer controle **fino** de permissões.

---

## 👤 3. Acessar propriedades do usuário autenticado

Você pode usar o objeto `authentication` dentro das expressões.

```java
@PreAuthorize("#user.name == authentication.name")
public String accessProfile(User user) {
    return "Acesso ao próprio perfil permitido";
}
```

### 🔎 O que acontece aqui?

* `#user.name` → pega o nome do user passado como parâmetro
* `authentication.name` → pega o nome do usuário logado
* Permite acesso **somente ao próprio perfil**

---

## 🧠 4. Outras expressões SpEL úteis no Spring Security

### ✔ Verificar múltiplas roles

```java
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
```

### ✔ Verificar múltiplas permissões

```java
@PreAuthorize("hasAnyAuthority('CRIAR', 'EDITAR', 'DELETAR')")
```

### ✔ Condição lógica

```java
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
```

### ✔ Negação

```java
@PreAuthorize("!hasRole('BLOQUEADO')")
```

### ✔ Acessando atributos do principal

```java
@PreAuthorize("authentication.principal.ativo == true")
```

---

## 🛠️ Onde SpEL também aparece no Spring (fora do Security)

* Valores dinâmicos em configurações de Beans
* `@Value("#{...}")` para expressões dinâmicas
* Arquivos YAML e propriedades
* Criação de regras customizadas

Exemplo:

```java
@Value("#{2 * 5}")
private int calculo;
```

---

# 📘 Resumo Geral

| Recurso                 | Exemplo                  | Uso                              |
| ----------------------- | ------------------------ | -------------------------------- |
| Role                    | `hasRole('ADMIN')`       | Controle por grupo de permissões |
| Authority               | `hasAuthority('EDITAR')` | Controle fino de permissões      |
| Propriedades do usuário | `authentication.name`    | Verificação dinâmica             |
| Acessar parâmetros      | `#id`, `#user.email`     | Regras por argumento             |

---

## 📌 Conclusão

O **Spring Expression Language (SpEL)** é uma ferramenta poderosa e flexível dentro do Spring Security. Ele permite criar regras de autorização dinâmicas, claras e muito expressivas — essenciais para sistemas que exigem segurança robusta.

Se quiser, posso criar:

* Uma versão **resumida para revisão rápida**
* Uma página **só com exercícios práticos de SpEL**
* Um guia avançado com expressões mais complexas 💡

---

# 🛡️ Outras Anotações de Permissão no Spring Security

A seguir, complementamos o conteúdo anterior adicionando todas as **anotações usadas para controle de permissões**, incluindo SpEL, filtros de coleção e anotações JSR‑250.

---

## 🔹 1. @PreAuthorize

Usada **antes** da execução do método. A validação ocorre *antes do método começar*.

### ✔️ Exemplo (role):

```java
@PreAuthorize("hasRole('ADMIN')")
public void deletarUsuario(Long id) {
    service.delete(id);
}
```

### ✔️ Exemplo (permission/authority):

```java
@PreAuthorize("hasAuthority('USER_EDIT')")
public User editar(User u) { return repo.save(u); }
```

Se o usuário não possuir permissão → erro antes da execução.

---

## 🔹 2. @PostAuthorize

A permissão é verificada **depois que o método executa**.

### ✔️ Exemplo:

```java
@PostAuthorize("returnObject.dono == authentication.name")
public Documento buscarDocumento(Long id) {
    return documentoRepository.findById(id).orElseThrow();
}
```

Caso a condição falhe → o retorno é bloqueado.

---

## 🔹 3. @PreFilter

Filtra coleções **antes** do método executar.

### ✔️ Exemplo:

```java
@PreFilter("filterObject.dono == authentication.name")
public void processarDocumentos(List<Documento> documentos) {
    documentos.forEach(this::processar);
}
```

Elementos que não atendem ao filtro são removidos automaticamente.

---

## 🔹 4. @PostFilter

Filtra coleções **depois** do método executar.

### ✔️ Exemplo:

```java
@PostFilter("filterObject.dono == authentication.name")
public List<Documento> buscarTodosDocumentos() {
    return documentoRepository.findAll();
}
```

O usuário só recebe os objetos permitidos.

---

## 🔹 5. Ativando anotações JSR‑250

Para habilitar `@RolesAllowed`, `@PermitAll`, `@DenyAll`:

```java
@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {}
```

---

## 🔹 6. @RolesAllowed

Equivalente a roles, mas da especificação JSR‑250.

### ✔️ Exemplo:

```java
@RolesAllowed("ADMIN")
public void acaoRestrita() {}
```

---

## 🔹 7. @PermitAll

Permite acesso para **qualquer usuário** (autenticado ou não).

### ✔️ Exemplo:

```java
@PermitAll
public String publico() {
    return "Endpoint público";
}
```

---

## 🔹 8. @DenyAll

Bloqueia **qualquer acesso**, útil para desativar endpoints.

### ✔️ Exemplo:

```java
@DenyAll
public void metodoBloqueado() {}
```

---

## ✔️ Conclusão

Agora o documento inclui todas as principais anotações de segurança:

* @PreAuthorize / @PostAuthorize
* @PreFilter / @PostFilter
* JSR‑250: @RolesAllowed, @PermitAll, @DenyAll

Com isso, seu material cobre **100% das anotações de método do Spring Security**.
