# 🗂️ Estratégias para Salvar Usuários

Este documento resume de forma clara e objetiva **todas as principais estratégias para estruturar, salvar e relacionar usuários em sistemas Java/Spring com JPA/Hibernate**.

Ele cobre:

* Denormalização
* Herança (Single Table Inheritance)
* Composição (Associação 1:1)
* Associação (1:N)
* Tabela de Junção (N:N)

Cada estratégia inclui explicação, vantagens, desvantagens e quando usar.

---

# 1. 📦 Denormalização de Dados (Duplicação de Informações)

A denormalização consiste em **duplicar dados essenciais do usuário** em outras entidades. Exemplo: Médico possui `id`, `nome`, `email`, e esses dados também ficam duplicados na tabela `usuario`.

### ✔️ Vantagens

* 🔥 **Alta performance em leitura** (evita joins pesados)
* 🧩 Entidades evoluem independentemente
* 🪶 Estrutura simples de consultar

### ❌ Desvantagens

* ⚠️ Risco alto de **inconsistência** entre tabelas
* 💾 Aumento de espaço em disco
* 🚫 Dificulta integridade referencial
* 🔄 Requer sincronização manual entre entidades

### 📌 Quando usar

* Quando **performance de leitura** é mais importante que integridade
* Sistemas simples, com poucos relacionamentos
* Quando atributos mudam raramente

---

# 2. 🧬 Herança (Single Table Inheritance)

Útil quando múltiplos tipos de usuários compartilham campos comuns.

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario { ... }

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario { ... }

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario { ... }
```

### ✔️ Vantagens

* 📁 Toda a hierarquia fica em **uma única tabela**
* 🔁 Evita duplicação de dados
* 🧼 Organização limpa e OO

### ❌ Desvantagens

* 📉 Muitos campos nulos para subclasses
* 🏋️ "Tabelona" gigante e difícil de manter
* 🐢 Pode afetar performance em larga escala

### 📌 Quando usar

* Quando existe **hierarquia clara** entre tipos de usuários
* Muitos usuários diferentes com atributos comuns
* Ex.: Admin, Cliente, Fornecedor, Atendente

---

# 3. 🧱 Composição (Associação 1:1)

O usuário tem dados básicos em `Usuario`, e dados extras em uma entidade `Perfil`.

```java
@Entity
public class Usuario {
    @OneToOne
    private Perfil perfil;
}
```

### ✔️ Vantagens

* 🧩 **Separação de responsabilidades**
* 📈 Facilita evolução do sistema
* 🔧 Permite perfis grandes sem poluir a entidade usuário

### ❌ Desvantagens

* 🔗 Depende de join para buscar dados completos
* 🧠 Lógica adicional para criar/associar perfis

### 📌 Quando usar

* Quando o perfil contém muitos campos opcionais
* Redes sociais, perfis profissionais, configurações de usuário

---

# 4. 📚 Relacionamento 1:N (Associação Um-para-Muitos)

Um usuário pode ter várias entidades associadas. Exemplo: Tarefas.

```java
@Entity
public class Usuario {
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Tarefa> tarefas;
}
```

### ✔️ Vantagens

* 🚀 Escalável para grandes volumes
* 🧩 Organização clara entre dono → itens
* 🔄 Cascade pode ajudar (com cuidado)

### ❌ Desvantagens

* 🔗 Muitos joins para recuperar dados relacionados
* ❗ Cascade.ALL pode apagar tudo acidentalmente

### 📌 Quando usar

* Sistemas com entidades "filhas"
* Ex.: Tarefas, Projetos, Agendamentos

---

# 5. 🔗 Tabela de Junção (Relacionamento Muitos-para-Muitos)

Útil quando ambos os lados podem ter muitos relacionamentos.

```java
@ManyToMany
@JoinTable(
  name = "usuario_curso",
  joinColumns = @JoinColumn(name = "usuario_id"),
  inverseJoinColumns = @JoinColumn(name = "curso_id")
)
private List<Curso> cursos;
```

### ✔️ Vantagens

* 🔄 Relacionamentos flexíveis e dinâmicos
* 📊 Muitos bancos são otimizados para N:N
* ➕ Fácil adicionar/remover associações

### ❌ Desvantagens

* 🧮 Tabela de junção cresce rápido
* ⚠️ Requer validações para evitar duplicidade
* 🧠 Lógica mais complexa para atualizar/remover

### 📌 Quando usar

* Plataformas de cursos
* Tags, grupos, permissões, categorias

---

# 🎯 Conclusão Geral

Cada estratégia possui seu próprio equilíbrio entre:

* Desempenho
* Integridade
* Complexidade
* Evolução do sistema

A escolha ideal depende de:

* Frequência de leitura/escrita
* Volume de dados
* Complexidade dos relacionamentos
* Manutenibilidade desejada

Nenhuma estratégia é a "melhor" sempre — o ideal é escolher a que resolve seu caso de uso com o menor custo e maior clareza.

---

Se quiser, posso adicionar:
✅ Diagramas UML
✅ Comparação lado a lado (tabela)
✅ Exemplos completos com repositório + serviço + controller
