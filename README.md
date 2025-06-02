# Documentação Técnica do Projeto: GamesCon

**Versão:** 1.0 (Conforme desenvolvimento até 02/06/2025)

## 1. Visão Geral do Projeto

### 1.1. Propósito
O GamesCon é uma plataforma web desenvolvida para entusiastas de E-Sports, com foco em fornecer informações detalhadas sobre personagens de jogos populares como League of Legends e Valorant. Atualmente, permite aos usuários visualizar listas de personagens, seus detalhes (lore, habilidades, estatísticas, visuais), e implementar um sistema de avaliação por estrelas para cada personagem. O projeto também inclui funcionalidades de gerenciamento de usuários e autenticação.

### 1.2. Tecnologias Principais
* **Backend:** Java 17+, Spring Boot 3.x, Spring MVC, Spring Security, Spring Data JPA
* **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
* **Banco de Dados:** H2 Database (em memória para desenvolvimento)
* **Build Tool:** Apache Maven
* **APIs Externas:**
    * Riot Data Dragon (League of Legends)
    * valorant-api.com (Valorant)
* **IDE:** IntelliJ IDEA (implícito pelo contexto)

---

## 2. Estrutura do Projeto (Maven)

O projeto segue a estrutura padrão do Maven:

* **`pom.xml`**: Define as dependências do projeto (Spring Boot starters, Thymeleaf, Spring Security, Jackson, etc.) e configurações de build.
* **`src/main/java`**: Contém o código fonte Java.
    * **`br.edu.cefsa.cinema`**: Pacote raiz.
        * `config`: Classes de configuração (ex: `SecurityConfig.java`).
        * `controller`: Controladores Spring MVC.
        * `Enum`: Enumerações (ex: `Role.java`).
        * `model`: Entidades JPA e DTOs.
        * `repository`: Interfaces Spring Data JPA.
        * `security`: Classes de segurança (ex: `CustomUserDetailsService.java`, `CustomUserDetails.java`).
        * `service`: Classes de serviço (lógica de negócios).
        * `CinemaApplication.java`: Classe principal Spring Boot.
* **`src/main/resources`**: Arquivos de configuração e recursos.
    * `application.properties`: Configurações da aplicação.
    * `static/`: Arquivos estáticos.
        * `css/`: Folhas de estilo customizadas.
        * `img/`: Imagens locais.
    * `templates/`: Templates Thymeleaf.
        * `fragments/`: Fragmentos reutilizáveis (ex: `_layout.html`).
        * Outras pastas específicas por funcionalidade (`lol/`, `valorant/`, `administracao/`, `usuarios/`, `genericos/`).
        * `index.html` (Página Home).

---

## 3. Componentes do Backend

### 3.1. Modelos (Entidades JPA e DTOs)

#### `Usuario.java`
* **Propósito:** Entidade JPA para usuários.
* **Mapeamento:** Tabela `tb_usuario`.
* **Campos Chave:** `idPadrao` (UUID, PK), `nome`, `email`, `apelido` (username), `senha` (hash), `roles` (Set<`Role`>).
* **Anotações JPA Comuns:** `@Entity`, `@Table`, `@Id`, `@Column`, etc.

#### `Role.java` (Enum)
* **Propósito:** Define papéis de usuário (ex: `ADMIN`, `LOGADO`).
* **Localização:** `br.edu.cefsa.cinema.Enum.Role`.

#### `AvaliacaoPersonagem.java`
* **Propósito:** Entidade JPA para avaliações de personagens.
* **Mapeamento:** Tabela `tb_avaliacao_personagem`.
* **Campos Chave:** `id` (UUID, PK), `personagemIdApi` (String), `nomePersonagem`, `jogo` (String), `classeRole`, `avaliacao` (int), `usuario` (@ManyToOne `Usuario`), `dataAvaliacao` (LocalDateTime).
* **Constraint:** Única para (`personagem_id_api`, `jogo`, `usuario_id`).
* **Callbacks:** `@PrePersist`, `@PreUpdate`.

#### Modelos para League of Legends API (Data Dragon)
* **`ChampionDataResponse.java`**: Wrapper para lista de campeões.
* **`SingleLolChampionResponse.java`**: Wrapper para dados de um campeão.
* **`LolChampion.java`**: Dados de um campeão (id, nome, título, lore, imagem, stats, spells).
* **`ChampionImage.java`**: Dados de imagem do campeão.
* **`ChampionStats.java`**: Estatísticas do campeão.
* **`LolSpell.java`**: Habilidades do campeão.
* **`LolSpellImage.java`**: Imagem da habilidade.
* **Anotações Jackson:** `@JsonIgnoreProperties(ignoreUnknown = true)`, `@JsonProperty`.

#### Modelos para Valorant API (valorant-api.com / Riot API)
* **Modelos para `valorant-api.com` (usado para detalhes/lista):**
    * `ValorantApiResponse.java`: Wrapper para lista de agentes.
    * `SingleValorantAgentResponse.java`: Wrapper para um agente.
    * `ValorantAgent.java`: Dados de um agente (uuid, nome, descrição, fullPortrait, role, abilities, voiceLine).
    * `AgentRole.java`: Função do agente.
    * `AgentAbility.java`: Habilidades do agente.
* **Modelos para Riot API Conteúdo (alternativa para lista):**
    * `RiotValorantContent.java`: Wrapper para conteúdo da Riot.
    * `RiotValorantCharacter.java`: Personagem da API da Riot (id, name, localizedNames).
* **Anotações Jackson:** `@JsonIgnoreProperties(ignoreUnknown = true)`, `@JsonProperty`.

### 3.2. Repositórios (Spring Data JPA)

#### `UsuarioRepository.java`
* Estende `JpaRepository<Usuario, UUID>`.
* Método customizado: `Optional<Usuario> findByApelido(String apelido)`.

#### `AvaliacaoPersonagemRepository.java`
* Estende `JpaRepository<AvaliacaoPersonagem, UUID>`.
* Métodos customizados:
    * `Optional<AvaliacaoPersonagem> findByPersonagemIdApiAndJogoAndUsuario(...)`
    * `@Query("SELECT AVG(a.avaliacao) ...") Optional<Double> findAverageRatingByPersonagem(...)`

### 3.3. Serviços (Lógica de Negócios)

#### `CustomUserDetailsService.java`
* Implementa `UserDetailsService`.
* `loadUserByUsername(String apelido)`: Busca `Usuario` e encapsula em `CustomUserDetails`.

#### `LolApiService.java`
* Usa `WebClient` para Riot Data Dragon.
* `getChampions()`: Lista de campeões.
* `getChampionById(String championId)`: Detalhes de um campeão.
* `getGameVersion()`: Versão do Data Dragon.

#### `ValorantApiService.java`
* Usa `WebClient` para `valorant-api.com` ou API da Riot.
* Configurado com `maxInMemorySize` aumentado para WebClient (se usando endpoint de conteúdo da Riot).
* `getAgents()`: Lista de agentes.
* `getAgentByUuid(String uuid)`: Detalhes de um agente (da `valorant-api.com`).

#### `AvaliacaoPersonagemService.java`
* Lógica para avaliações.
* `salvarOuAtualizarAvaliacao(...)`: Salva ou atualiza a nota de um personagem por um usuário.
* `getAvaliacaoDoUsuario(...)`: Busca a nota de um usuário.
* `getMediaAvaliacoes(...)`: Calcula a média de notas.
* `getUsuarioLogado()`: Helper para obter o usuário autenticado.

### 3.4. Controladores (Spring MVC)

#### `CinemaController.java`
* Rotas base: `/`, `/usuarios/cadastro`, `/usuarios/login`.

#### `UsuarioController.java` (`/usuarios`)
* `POST /cadastrar`: Cadastro de usuários.
* `GET /perfil`, `GET /editar/{id}`, `POST /editar/{id}`: Perfil e edição do próprio usuário.
* `POST /excluir/{id}`: Exclusão da própria conta.

#### `AdminUsuarioController.java` (`/administracao`)
* Protegido com `@PreAuthorize("hasRole('ADMIN')")`.
* `GET /lista-usuarios`: Lista todos os usuários.
* `GET /editar/{id}`, `POST /editar/{id}`: Edição de usuários pelo admin (com lógica para proteger contas admin específicas).

#### `LolController.java` (`/lol`)
* `GET /campeoes`: Lista de campeões.
* `GET /campeoes/{championId}`: Detalhes de um campeão (inclui dados de avaliação).

#### `ValorantController.java` (`/valorant`)
* `GET /agentes`: Lista de agentes.
* `GET /agentes/{uuid}`: Detalhes de um agente (inclui dados de avaliação).

#### `AvaliacaoController.java`
* `POST /avaliar/personagem`: Processa submissão de avaliações.

### 3.5. Segurança (`SecurityConfig.java`)
* Configuração do `SecurityFilterChain`:
    * Autorização de rotas (`/administracao/**` para ADMIN, rotas públicas, outras autenticadas).
    * Form Login customizado.
    * Logout.
    * CSRF e CORS.
* `PasswordEncoder` (BCrypt).
* `UserDetailsService` (`CustomUserDetailsService`) e `UserDetails` (`CustomUserDetails`).

### 3.6. Configuração (`application.properties`)
* **H2 Database:** Configurações de URL, driver, username, password, console.
* **JPA/Hibernate:** Plataforma, `ddl-auto`.
* **API Keys:** `riot.api.key=SUA_CHAVE_AQUI`.
* **Server Port:** `server.port=8080`.

---

## 4. Componentes do Frontend

### 4.1. Thymeleaf Templates
* Motor de templates para renderização dinâmica.
* **Fragmentos (`resources/templates/fragments/_layout.html`):**
    * `header(pageTitle, pageSpecificCssPath)`: Cabeçalho HTML (metatags, título, CSS global e específico).
    * `navbar`: Barra de navegação responsiva (Bootstrap) com links condicionais (`sec:authorize`).
    * `footer`: Rodapé padrão (inclui Bootstrap JS).
* **Páginas Chave:** `index.html`, `login.html`, `cadastro.html`, `perfil.html`, `editar-perfil.html`, `lista-usuarios.html`, `editar-usuario.html`, `lista-campeoes.html`, `detalhe-campeao.html`, `lista-agentes.html`, `detalhe-agente.html`, `sobre.html`.
* **Atributos Thymeleaf:** Uso extensivo de `th:text`, `th:href`, `th:src`, `th:object`, `th:field`, `th:each`, `th:if`, `th:unless`, `th:replace`.
* **Dialeto Spring Security:** `sec:authorize`, `#authentication.principal`.

### 4.2. CSS
* **Bootstrap 5:** Framework base para layout e componentes.
* **CSS Customizados (em `static/css/`):**
    * `lol-auth-theme.css`: Tema LoL para login/cadastro.
    * `lol-lista.css` & `lol-detalhe.css`: Tema LoL para páginas de campeões.
    * `valorant-detalhe.css` & `valorant-lista.css`: Tema Valorant para páginas de agentes.
    * `admin-gamer-theme.css`: Tema "gamer escuro" para admin.
    * `home-theme.css`: Tema para a Home.
* **Abordagens:** Temas escuros, cores de destaque temáticas, fundos com imagens, responsividade.

### 4.3. JavaScript
* **Bootstrap Bundle (`bootstrap.bundle.min.js`):** Essencial para dropdowns, modais.
* **Scripts Inline:** Para exibição de modais de erro baseados em parâmetros de URL/atributos do modelo.

---

## 5. Banco de Dados
* **H2 Database (em memória):**
    * Console: `/h2-console` (se habilitado).
    * Esquema: Gerado/atualizado por Hibernate (`ddl-auto`).
* **Tabelas:** `tb_usuario` (e join table de roles), `tb_avaliacao_personagem`.

---

## 6. APIs Externas Utilizadas

* **Riot Data Dragon (League of Legends):**
    * Base URL: `https://ddragon.leagueoflegends.com`
    * Endpoints: `/cdn/{v}/data/pt_BR/champion.json`, `/cdn/{v}/data/pt_BR/champion/{id}.json`.
* **valorant-api.com (Valorant):**
    * Base URL: `https://valorant-api.com/v1`
    * Endpoints: `/agents?isPlayableCharacter=true&language=pt-BR`, `/agents/{uuid}?language=pt-BR`.
* **Riot Games API (Valorant - Conteúdo):**
    * Base URL: `https://{region}.api.riotgames.com/val`
    * Endpoint: `/content/v1/contents`
    * Requer `X-Riot-Token`.

---

## 7. Como Construir e Executar
* **Requisitos:** JDK 17+, Maven.
* **Build:** `mvn clean install` ou `mvn clean package`.
* **Execução:**
    * Via IDE: Rodar `CinemaApplication.java`.
    * Linha de Comando: `java -jar target/cinema-0.0.1-SNAPSHOT.jar`.
* **Acesso:** `http://localhost:8080`.

---
