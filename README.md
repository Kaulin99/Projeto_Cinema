# Documentação GamesCon

**Autores:**
* Robson Guilherme Ferrarezi - RA: 082220015
* Kauê dos Santos Andrade - RA: 082220027

**Disciplinas:**
* Linguagem de Programação 2
* Modelagem de Software

---

## 1. Visão Geral do Projeto

* **Projeto:** "GamesCon"
* **Descrição:** Criação de um site de avaliação de jogos (inicialmente Valorant e LoL) utilizando Java com Spring Boot, HTML, CSS, entre outras tecnologias.
* **Funcionalidades Principais:**
    * Login de usuários (com papéis de usuário normal e ADMIN).
    * Usuários normais podem ter perfil e fazer avaliações de campeões/agentes.
    * Usuários ADMIN têm acesso a um painel de administração e também podem fazer avaliações.
    * As avaliações (jogo, agente/campeão, nota, usuário) são salvas em um banco de dados.
    * Dashboards exibem a média de avaliações por agente/campeão e a quantidade de avaliações (um para cada jogo).
    * As informações dos jogos são consumidas de APIs públicas.
* **Objetivo da Documentação:**
    * Incluir informações sobre as tecnologias utilizadas e como são empregadas.
    * Conter dados de modelagem, como diagramas de classe (ou suas descrições textuais) e outros aspectos da modelagem de software.

---

## 2. Arquitetura e Tecnologias Utilizadas

* **Arquitetura Geral:**
    * O projeto segue uma arquitetura **MVC (Model-View-Controller)**, comum em aplicações Spring Boot Web.
        * **Model:** Representado pelas entidades JPA (`Usuario`, `AvaliacaoPersonagem`, entidades de cache), DTOs para APIs externas e o Enum `Role`.
        * **View:** Implementada utilizando **Thymeleaf** para renderização de HTML no lado do servidor.
        * **Controller:** Representado pelas classes `@Controller` que lidam com as requisições HTTP.
    * A arquitetura também pode ser descrita como uma **aplicação em camadas**: Apresentação (Controllers, Views), Serviço, Acesso a Dados (Repositories) e Configuração.
* **Componentes Principais:**
    * **Backend:** Desenvolvido em **Java 17** com o framework **Spring Boot 3.4.5**.
    * **Frontend:** Utiliza **Thymeleaf** para a renderização de páginas HTML dinâmicas, com **HTML5** e **CSS**.
    * **Banco de Dados:** **H2 Database** configurado em modo arquivo, para persistência de dados de usuários, avaliações e cache.
* **Tecnologias e Frameworks Chave:**
    * **Java:** Versão 17.
    * **Spring Boot:** Versão 3.4.5 (incluindo Starters para Web, Data JPA, Security, Thymeleaf, WebFlux).
    * **Spring MVC:** Para a construção da aplicação web.
    * **Spring Data JPA:** Com Hibernate como provedor, para persistência de dados.
    * **Spring Security:** Para gerenciamento de autenticação e autorização.
    * **Spring WebFlux:** Utilizado para o `WebClient` (chamadas reativas a APIs).
    * **Thymeleaf:** Motor de templates, com `thymeleaf-extras-springsecurity6`.
    * **H2 Database:** Banco de dados relacional.
    * **Jackson Databind:** Para manipulação de JSON.
    * **Maven:** Gerenciamento de dependências e build.
    * **Lombok:** (Presente no `pom.xml`).
    * **Spring Boot DevTools:** Para desenvolvimento.
* **APIs Externas Consumidas:**
    * **Riot Data Dragon API (League of Legends):** Para dados de campeões do LoL (`https://ddragon.leagueoflegends.com`).
    * **valorant-api.com:** Para dados de agentes do Valorant (`https://valorant-api.com/v1`).

---

## 3. Modelagem de Software

Esta seção detalha as estruturas de dados do projeto, incluindo entidades de banco de dados, objetos de transferência de dados para APIs externas e enumerações.

### 3.1 Entidades JPA Principais

**a. Classe `Usuario`**
* **Tipo:** Entidade JPA
* **Tabela Mapeada:** `tb_usuario`
* **Propósito:** Representa um usuário do sistema, contendo informações de identificação, credenciais para login, dados pessoais e os papéis (permissões) que o usuário possui. Implementa `java.io.Serializable`.
* **Atributos Chave:**
    * `idPadrao` (UUID, `@Id`, `@GeneratedValue`, PK)
    * `nome` (String, Não Nulo, `length=50`)
    * `email` (String, Não Nulo)
    * `apelido` (String, Não Nulo, Único)
    * `senha` (String, Não Nulo)
    * `roles` (Set<Role>, `@ElementCollection`, `FetchType.EAGER`, mapeado para tabela `tb_usuario_role` com `EnumType.STRING`)
* **Relacionamentos:**
    * Com `Role` (Enum): Muitos-para-Muitos implícito via `tb_usuario_role`.
    * Com `AvaliacaoPersonagem`: Um `Usuario` para Muitas `AvaliacaoPersonagem` (lado "Um").

**b. Classe `AvaliacaoPersonagem`**
* **Tipo:** Entidade JPA
* **Tabela Mapeada:** `tb_avaliacao_personagem` (com `UniqueConstraint` para `personagem_id_api`, `jogo`, `usuario_id`)
* **Propósito:** Representa a avaliação (nota) de um personagem de jogo feita por um usuário.
* **Atributos Chave:**
    * `id` (UUID, `@Id`, `@GeneratedValue`, PK)
    * `personagemIdApi` (String, Não Nulo)
    * `nomePersonagem` (String, Não Nulo)
    * `jogo` (String, Não Nulo)
    * `classeRole` (String)
    * `avaliacao` (int, Não Nulo)
    * `usuario` (`Usuario`, `@ManyToOne`, `FetchType.LAZY`, `JoinColumn(name="usuario_id")`, Não Nulo)
    * `dataAvaliacao` (LocalDateTime, Não Nulo, gerenciado por `@PrePersist`, `@PreUpdate`)
* **Relacionamentos:**
    * Com `Usuario`: Muitos-para-Um (lado "Muitos").

### 3.2 Enum

**a. Enum `Role`**
* **Tipo:** Enum
* **Pacote:** `br.edu.cefsa.cinema.Enum`
* **Propósito:** Define os papéis (níveis de permissão) que um usuário pode ter (`ADMIN`, `LOGADO`).
* **Uso:** Associado à entidade `Usuario` e utilizado nas configurações do Spring Security.

### 3.3 Entidades de Cache JPA

**a. Classe `CampeaoLoLCacheado`**
* **Tipo:** Entidade de Cache JPA
* **Tabela Mapeada:** `tb_campeao_lol_cache`
* **Propósito:** Armazena dados cacheados de Campeões do LoL da API Data Dragon.
* **Atributos Chave:**
    * `id` (String, `@Id`, ID da API, PK)
    * `nome`, `titulo`, `biografia` (`@Lob`), `imagemIconeFull`, `hp`, `armor`, `attackDamage`
    * `spellsJson` (String, `@Lob`, armazena lista de `LolSpell` como JSON)
    * `jogo` (String, final "LOL")
    * `dataCache` (LocalDateTime, gerenciado por `@PrePersist`, `@PreUpdate`)

**b. Classe `AgenteValorantCacheado`**
* **Tipo:** Entidade de Cache JPA
* **Tabela Mapeada:** `tb_agente_valorant_cache`
* **Propósito:** Armazena dados cacheados de Agentes do Valorant da API valorant-api.com.
* **Atributos Chave:**
    * `uuid` (String, `@Id`, UUID da API, PK)
    * `nome`, `descricao` (`@Lob`), `fullPortraitUrl`, `roleDisplayName`
    * `abilitiesJson` (String, `@Lob`, armazena lista de `AgentAbility` como JSON)
    * `voiceLinesJson` (String, `@Lob`)
    * `jogo` (String, final "VALORANT")
    * `dataCache` (LocalDateTime, gerenciado por `@PrePersist`, `@PreUpdate`)

### 3.4 Data Transfer Objects (DTOs)

* **Propósito Geral:** Classes POJO (como `LolChampion`, `ValorantAgent`, `ChampionImage`, `LolSpell`, `AgentAbility`, `AgentRole`, e DTOs de resposta como `ChampionDataResponse`, `SingleValorantAgentResponse`, `ValorantApiResponse`) usadas para mapear dados de/para as APIs externas utilizando anotações Jackson (`@JsonProperty`).
* **Relacionamentos:** DTOs frequentemente contêm outros DTOs ou listas de DTOs, representando relações de composição (ex: `LolChampion` contém `ChampionImage` e `List<LolSpell>`). Os serviços (`LolApiService`, `ValorantApiService`) convertem DTOs para/de Entidades de Cache.

### 3.5 Diagrama de Classes e Modelo Entidade-Relacionamento

---
**[ESPAÇO PARA IMAGEM DO DIAGRAMA DE CLASSES E/OU MODELO ENTIDADE-RELACIONAMENTO]**

*Cole aqui a imagem gerada a partir do código PlantUML ou outro software de modelagem, ilustrando as classes principais, seus atributos chave e relacionamentos.*
---

---

## 4. Camada de Repositório (Interfaces Spring Data JPA)

Interfaces responsáveis pela interação com o banco de dados, estendendo `JpaRepository` para fornecer métodos CRUD e consultas customizadas.

* **`UsuarioRepository`:** Gerencia a entidade `Usuario`. Método customizado: `findByApelido(String apelido)`.
* **`AvaliacaoPersonagemRepository`:** Gerencia `AvaliacaoPersonagem`. Métodos customizados para buscar avaliação específica, média de personagem e dados de popularidade para LoL e Valorant (via `@Query`).
* **`AgenteValorantCacheadoRepository`:** Gerencia `AgenteValorantCacheado`. Métodos customizados para buscar por jogo, UUID e jogo, e deletar por jogo.
* **`CampeaoLoLCacheadoRepository`:** Gerencia `CampeaoLoLCacheado`. Métodos customizados similares aos do cache de Valorant.

---

## 5. Configuração de Segurança (Spring Security)

Implementada usando Spring Security para autenticação, autorização e proteção.

* **`SecurityConfig.java`:**
    * Configura CORS, CSRF (com `CookieCsrfTokenRepository`).
    * **Regras de Autorização:**
        * `/administracao/**`: Requer `ROLE_ADMIN`.
        * Rotas públicas: `/`, `/usuarios/cadastro`, `/usuarios/login`, `/lol/**`, `/valorant/**`, recursos estáticos.
        * Outras: Requerem autenticação.
    * **Formulário de Login:** Customizado (`/usuarios/login`), com URLs de sucesso e falha.
    * **Logout:** Configurado (`/logout`).
    * **PasswordEncoder:** `BCryptPasswordEncoder`.
    * Ignora requisições para `/h2-console/**`.
* **`CustomUserDetailsService.java`:** Implementa `UserDetailsService` para carregar `Usuario` (via `UsuarioRepository`) pelo apelido.
* **`CustomUserDetails.java`:** Implementa `UserDetails`, encapsulando `Usuario` e fornecendo seus detalhes (username como apelido, senha, roles prefixadas com "ROLE_") para o Spring Security.

---

## 6. Camada de Serviço (Services)

Contém a lógica de negócios da aplicação.

* **`UsuarioService.java`:**
    * Responsável por operações de negócio de `Usuario`.
    * Método principal: `@Transactional salvarUsuario(Usuario usuario)`.
* **`AvaliacaoPersonagemService.java`:**
    * Gerencia a lógica de avaliações: salvar/atualizar (com validações), obter avaliação do usuário, calcular média, obter usuário logado, e buscar dados para dashboards de popularidade.
* **`LolApiService.java`:**
    * Interage com a API Riot Data Dragon (LoL) usando `WebClient`.
    * Implementa estratégia de cache "API primeiro, depois cache local (`CampeaoLoLCacheadoRepository`)".
    * Converte DTOs da API para entidades de cache (serializando `spells` para JSON) e vice-versa.
* **`ValorantApiService.java`:**
    * Interage com a API valorant-api.com usando `WebClient`.
    * Implementa estratégia de cache similar para agentes do Valorant (usando `AgenteValorantCacheadoRepository` e serializando `abilities` para JSON).

---

## 7. Camada de Controller (Controllers)

Lida com as requisições HTTP, interage com serviços e prepara dados para as views Thymeleaf.

* **`CinemaController.java` (`@RequestMapping("")`):** Rotas base (home, login, cadastro).
* **`UsuarioController.java` (`@RequestMapping("/usuarios")`):** Cadastro, perfil, edição e exclusão de conta do usuário.
* **`AdminUsuarioController.java` (`@RequestMapping("/administracao")`, `@PreAuthorize("hasRole('ADMIN')")`):** Gerenciamento de usuários por administradores.
* **`AvaliacaoController.java`:** Processa submissão de avaliações.
* **`LolController.java` (`@RequestMapping("/lol")`):** Exibe campeões do LoL, detalhes, e fornece dados para dashboard de popularidade (incluindo endpoint JSON `@ResponseBody`).
* **`ValorantController.java` (`@RequestMapping("/valorant")`):** Exibe agentes do Valorant, detalhes, e fornece dados para dashboard de popularidade (incluindo endpoint JSON `@ResponseBody`).
* **`GenericoController.java` (`@RequestMapping("/genericos")`):** Páginas genéricas (ex: "Sobre").

---

## 8. Arquivos de Configuração do Projeto

* **`pom.xml` (Maven):**
    * Define identificação do projeto, parent POM (`spring-boot-starter-parent:3.4.5`), Java 17.
    * Gerencia dependências chave: Spring Boot Starters (Web, Data JPA, Security, Thymeleaf, WebFlux), H2, Jackson, Thymeleaf Security Extras, DevTools, Lombok.
    * Configura `spring-boot-maven-plugin`.
* **`application.properties`:**
    * Define `spring.application.name=gamescon`, `server.port=8080`.
    * **Configuração H2:** Banco de dados em arquivo (`jdbc:h2:file:./data/banco_cinema`), driver, credenciais (sa/vazia), console H2 habilitado em `/h2-console`.
    * **Configuração JPA/Hibernate:** `spring.jpa.hibernate.ddl-auto=update`.

---
