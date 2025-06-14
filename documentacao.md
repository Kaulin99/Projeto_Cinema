# Documentação GamesCon

**Autores:**
* Robson Guilherme Ferrarezi - RA: 082220015
* Kauê dos Santos Andrade - RA: 082220027

**Disciplina(s):**
* Linguagem de Programação 2
* Modelagem de Software

**Semestre:**
* 6º semestre

---

## 1. Introdução e Visão Geral

### Contexto do Problema e Objetivos de Negócio/Projeto

O projeto GamesCon surge no contexto acadêmico do 6º semestre, para as disciplinas de Linguagem de Programação 2 e Modelagem de Software, visando aplicar conceitos de desenvolvimento e engenharia de software na criação de uma plataforma interativa.

* **Problema:** A dificuldade para jogadores encontrarem avaliações centralizadas e confiáveis de personagens específicos em jogos populares como League of Legends e Valorant, e a ausência de uma comunidade engajada em torno dessas avaliações em uma plataforma unificada que também ofereça dados técnicos e de performance dos personagens.
* **Objetivos de Negócio/Projeto:**
    * Desenvolver uma aplicação web funcional que permita aos usuários avaliar personagens (campeões/agentes) de jogos online.
    * Proporcionar uma interface intuitiva para visualização de personagens, submissão de avaliações e consulta de médias de avaliação.
    * Implementar um sistema de usuários com diferentes níveis de acesso (usuário comum e administrador).
    * Consumir dados de APIs públicas para obter informações atualizadas sobre os jogos e personagens.
    * Criar dashboards para visualização de tendências de avaliação e popularidade.
    * Aplicar boas práticas de desenvolvimento, modelagem de software e gerenciamento de dependências.

### Perspectiva do Produto e Escopo

* **Perspectiva do Produto:** O GamesCon é uma plataforma web que servirá como um hub comunitário para avaliações e discussões sobre personagens de jogos eletrônicos. Ele se integrará com APIs externas para obter dados de jogos e oferecerá funcionalidades de interação social através de avaliações.
* **Escopo (O que está dentro do sistema):**
    * Cadastro e autenticação de usuários (com papéis `LOGADO` e `ADMIN`).
    * Gerenciamento de perfil de usuário.
    * Listagem de personagens/agentes de League of Legends e Valorant (obtidos via API).
    * Visualização de detalhes de personagens/agentes.
    * Sistema de avaliação de personagens por usuários logados (nota de 1 a 5).
    * Cálculo e exibição da média de avaliações por personagem.
    * Painel administrativo para gerenciamento de usuários.
    * Dashboards de popularidade de personagens/agentes por jogo.
    * Cache local para dados de APIs externas.
* **Escopo (O que está fora do sistema):**
    * Comunicação direta entre usuários (mensagens privadas, fóruns complexos).
    * Sistema de e-commerce ou transações financeiras.
    * Criação de conteúdo de jogo (guias, notícias) pelos usuários, além das avaliações.
    * Múltiplos idiomas além do Português (no escopo atual).

---

## 2. Requisitos Específicos

### 2.1 Requisitos Funcionais (RF)

| ID    | Descrição                                                                                             | Prioridade | Critérios de Aceitação                                                                                                                                                                                                            | Dependências                                      |
| :---- | :---------------------------------------------------------------------------------------------------- | :--------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------ |
| RF001 | **Cadastro de Novo Usuário:** O sistema deve permitir que um novo usuário se cadastre fornecendo nome, e-mail, apelido e senha. O sistema deve verificar se o apelido já está em uso.                      | Alta       | 1. Usuário submete formulário com dados válidos.<br>2. Mensagem de erro se apelido já existir.<br>3. Senha armazenada com hash.<br>4. Usuário redirecionado para login com mensagem de sucesso.<br>5. Novo usuário com papel `LOGADO`. | RNF de Segurança                                  |
| RF002 | **Autenticação de Usuário (Login):** O sistema deve permitir que um usuário cadastrado se autentique usando seu apelido e senha.                                                                   | Alta       | 1. Usuário submete formulário.<br>2. Se credenciais corretas, usuário redirecionado e autenticado.<br>3. Se incorretas, mensagem de erro.<br>4. Sessão do usuário gerenciada.                                                       | RF001                                             |
| RF003 | **Logout de Usuário:** O sistema deve permitir que um usuário autenticado encerre sua sessão.           | Alta       | 1. Usuário aciona logout.<br>2. Sessão invalidada.<br>3. Usuário redirecionado para login com mensagem de sucesso.                                                                                                                  | RF002                                             |
| RF004 | **Visualizar Lista de Personagens/Agentes por Jogo:** O sistema deve exibir uma lista de personagens para um jogo, consumindo dados de APIs externas ou cache.                                    | Alta       | 1. Lista de LoL e Valorant exibida com nome/imagem.<br>2. Dados carregados do cache se API falhar.<br>3. Mensagem informativa se API e cache falharem.                                                                    | RNF de Desempenho                                 |
| RF005 | **Visualizar Detalhes de um Personagem/Agente:** O sistema deve permitir visualizar detalhes de um personagem, incluindo avaliações e, se logado, opção para avaliar.                               | Alta       | 1. Detalhes do personagem exibidos.<br>2. Média geral das avaliações exibida.<br>3. Se logado, avaliação pessoal (se houver) exibida.<br>4. Formulário de avaliação exibido para usuário logado.                               | RF004, RF006, RF002                               |
| RF006 | **Avaliar um Personagem/Agente:** Um usuário logado deve poder submeter uma avaliação (nota 1-5) para um personagem. O sistema deve salvar ou atualizar a avaliação.                                | Alta       | 1. Usuário logado submete nota (1-5).<br>2. Avaliação salva/atualizada no BD.<br>3. Mensagem de confirmação.<br>4. Média de avaliações reflete a nova avaliação.                                                            | RF002, RF005                                      |
| RF007 | **Visualizar Perfil do Usuário:** Um usuário logado deve poder acessar e visualizar suas informações de perfil.                                                                                   | Média      | 1. Usuário logado acessa página de perfil.<br>2. Informações corretas do perfil exibidas.                                                                                                                       | RF002                                             |
| RF008 | **Editar Perfil do Usuário:** Um usuário logado deve poder editar suas informações de perfil (nome, e-mail, apelido).                                                                                | Média      | 1. Usuário logado acessa formulário de edição.<br>2. Pode modificar nome, e-mail, apelido.<br>3. Mensagem de erro se novo apelido já em uso.<br>4. Alterações salvas no BD.<br>5. Informações atualizadas na sessão/interface. | RF007                                             |
| RF009 | **Excluir Conta do Usuário:** Um usuário logado deve poder excluir sua própria conta. Admins protegidos não podem se excluir por esta funcionalidade.                                                 | Baixa      | 1. Usuário logado inicia exclusão.<br>2. Conta removida do BD.<br>3. Usuário desconectado.<br>4. Admins protegidos não conseguem se excluir.                                                                               | RF002                                             |
| RF010 | **Acesso ao Painel de Administração (Admin):** Um usuário com papel "ADMIN" deve poder acessar uma seção de administração.                                                                       | Alta       | 1. Usuário ADMIN acessa painel de admin.<br>2. Usuário não-ADMIN é impedido.                                                                                                                                   | RF002                                             |
| RF011 | **Listar Usuários (Admin):** Um administrador deve poder visualizar uma lista de todos os usuários cadastrados.                                                                                     | Alta       | 1. Admin acessa listagem de usuários.<br>2. Lista exibe dados relevantes de todos os usuários.                                                                                                                | RF010                                             |
| RF012 | **Editar Usuário (Admin):** Um administrador deve poder editar informações de um usuário e seus papéis, com exceção de admins protegidos.                                                          | Alta       | 1. Admin acessa formulário de edição.<br>2. Pode modificar dados e papéis (exceto admins protegidos).<br>3. Mensagem de erro se novo apelido em uso.<br>4. Alterações salvas.                                          | RF011                                             |
| RF013 | **Visualizar Dashboard de Popularidade por Jogo:** O sistema deve exibir um dashboard para cada jogo mostrando personagens populares (quantidade e média de avaliações).                           | Alta       | 1. Dashboard de LoL existe.<br>2. Dashboard de Valorant existe.<br>3. Dashboards exibem personagens, total de avaliações, média.<br>4. Dados apresentados de forma clara.                                                | RF006                                             |

### 2.2 Requisitos Não-Funcionais (RNF)

| ID     | Descrição                                                                               | Categoria                     | Prioridade | Critérios de Aceitação                                                                                                                                                                |
| :----- | :-------------------------------------------------------------------------------------- | :---------------------------- | :--------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RNF001 | **Desempenho no Carregamento de Listas de Personagens.** | Desempenho                    | Alta       | 1. A lista de personagens/agentes deve ser exibida em no máximo **1 segundo** após a requisição.<br>2. O carregamento a partir do cache deve ocorrer em no máximo 0.5 segundos.           |
| RNF002 | **Segurança no Armazenamento de Senhas.** | Segurança                     | Altíssima  | 1. As senhas devem ser armazenadas no BD utilizando um algoritmo de hashing forte com salt (BCrypt).<br>2. Não deve ser possível reverter o hash para a senha original.                   |
| RNF003 | **Usabilidade da Interface de Navegação.** | Usabilidade                   | Média      | 1. Um novo usuário consegue encontrar as seções de LoL e Valorant em menos de 3 cliques a partir da home.<br>2. Opções de login/cadastro/perfil estão visíveis no cabeçalho.                    |
| RNF004 | **Disponibilidade do Serviço de Avaliação.** | Confiabilidade/Disponibilidade | Alta       | 1. O sistema deve ter um uptime de **90%** para as funcionalidades de avaliação.<br>2. Falhas na API de um jogo não devem impedir a visualização de dados do outro jogo.                     |
| RNF005 | **Consistência Visual da Interface.** | Usabilidade                   | Baixa      | 1. Cabeçalho, rodapé e menus mantêm a mesma aparência e posicionamento.<br>2. Paleta de cores e tipografia definidas são aplicadas consistentemente.                                     |
| RNF006 | **Proteção Contra CSRF.** | Segurança                     | Altíssima  | 1. As requisições que alteram estado devem incluir e validar um token CSRF.                                                                                                            |
| RNF007 | **Tempo de Resposta para Salvar Avaliação.** | Desempenho                    | Média      | 1. Após o usuário submeter uma avaliação, a confirmação (ou erro) deve ser exibida em até **2 segundos**.                                                                               |
| RNF008 | **Responsividade da Aplicação.** | Usabilidade / Portabilidade   | Alta       | 1. O layout se ajusta para uma única coluna em telas de smartphones.<br>2. O menu de navegação é convertido para um menu "hambúrguer" em telas menores.<br>3. O conteúdo é legível em todos os dispositivos. |

### 2.3 Requisitos de Interface (RI)

| ID      | Tipo (Tela / API) | Especificações                                                                                                                                                                                                                                                                                                                          | Dependências                                                                                                              |
| :------ | :---------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------ |
| **Geral** | **Layout (`_layout.html`)** | **Tipo:** Fragmento de Tela<br>**Especificações:** Define a estrutura base das páginas (head, navbar, footer), com navegação dinâmica baseada na autenticação e papel do usuário (`sec:authorize`).                                                                                                                               | Bootstrap, Spring Security.                                                                                               |
| RI001   | Tela              | **Página Inicial (`/`, `index.html`)**<br>**Especificações:** Apresenta o projeto, cards para cada jogo e uma seção de personagens populares.                                                                                                                                                                                              | RNF003, RNF005, RNF008.                                                                                                   |
| RI002   | Tela              | **Cadastro de Usuário (`/usuarios/cadastro`, `cadastro.html`)**<br>**Especificações:** Formulário de cadastro com campos para Nome, Nickname, E-mail, Senha e campo CSRF. Inclui modal para erro de nickname.                                                                                                                            | RF001.                                                                                                                    |
| RI003   | Tela              | **Login de Usuário (`/usuarios/login`, `login.html`)**<br>**Especificações:** Formulário de login com campos para Nickname, Senha e campo CSRF. Inclui modal para erro de login.                                                                                                                                                            | RF002.                                                                                                                    |
| RI004   | Tela              | **Lista de Personagens/Agentes (`/lol/campeoes`, `/valorant/agentes`)**<br>**Especificações:** Exibe uma grade de personagens com imagem, nome e link para detalhes. Apresenta botão para o dashboard de popularidade e trata erros de API.                                                                                                   | RF004, RNF001.                                                                                                            |
| RI005   | Tela              | **Detalhes do Personagem/Agente (`/lol/campeoes/{id}`, `/valorant/agentes/{uuid}`)**<br>**Especificações:** Exibe detalhes completos (lore, habilidades, stats). Apresenta a média de avaliações e, para usuários logados, um formulário para submeter/atualizar sua avaliação.                                                                  | RF005, RF006, `AvaliacaoPersonagem`, DTOs de personagem.                                                                   |
| RI006   | Tela              | **Perfil do Usuário (`/usuarios/perfil`, `perfil.html`)**<br>**Especificações:** Exibe dados do usuário logado (nome, apelido, email) e links para editar perfil e excluir conta (com modal de confirmação).                                                                                                                                    | RF007, `Usuario`.                                                                                                         |
| RI007   | Tela              | **Edição de Perfil do Usuário (`/usuarios/editar/{id}`, `editar.html`)**<br>**Especificações:** Formulário pré-preenchido para editar nome, apelido e email.                                                                                                                                                                                   | RF008, `Usuario`.                                                                                                         |
| RI008   | Tela              | **Painel de Administração (`/administracao/**`)**<br>**Especificações:** Conjunto de telas para gerenciamento de usuários, acessível apenas por `ROLE_ADMIN`. Inclui lista e formulário de edição de usuários.                                                                                                                              | RF010, RF011, RF012.                                                                                                      |
| RI009   | Tela              | **Página Sobre (`/genericos/sobre`, `sobre.html`)**<br>**Especificações:** Apresenta texto sobre o projeto, os desenvolvedores e a instituição.                                                                                                                                                                                               | RNF003, RNF005.                                                                                                           |
| RI010   | Tela              | **Dashboards de Popularidade (`/lol/dashboard-popularidade`, etc.)**<br>**Especificações:** Exibe gráficos de barras (usando `Chart.js`) com a média e o número de avaliações por personagem. Os dados são carregados via JavaScript de uma API interna.                                                                                         | RF013, RI011, RI012.                                                                                                      |
| RI011   | API (JSON)        | **Dados de Popularidade LoL (`/lol/api/popularidade`)**<br>**Especificações:** Endpoint GET que retorna um array de objetos JSON com `nome`, `quantidade`, `media` para os campeões de LoL.                                                                                                                                                 | `AvaliacaoPersonagemService`.                                                                                             |
| RI012   | API (JSON)        | **Dados de Popularidade Valorant (`/valorant/api/popularidade-valorant`)**<br>**Especificações:** Similar ao RI011, mas para os agentes de Valorant.                                                                                                                                                                                            | `AvaliacaoPersonagemService`.                                                                                             |

### 2.4 Requisitos de Dados (RD)

| ID    | Descrição                                         | Tipo de Dado                                                    | Volume Estimado                                                              | Retenção                                                                                                                             | Backup                                                                                                                          |
| :---- | :-------------------------------------------------- | :-------------------------------------------------------------- | :--------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------ |
| RD001 | **Dados de Usuários** | Estruturado, relacional (tabela `tb_usuario`).                    | Inicialmente baixo (< 1.000 usuários), com crescimento gradual.                | Mantidos enquanto a conta estiver ativa. Excluídos permanentemente após solicitação do usuário.                                        | Coberto pelo plano de backup do banco de dados (cópia do arquivo H2; plano mais robusto para produção).                        |
| RD002 | **Dados de Avaliações de Personagens** | Estruturado, relacional (tabela `tb_avaliacao_personagem`).         | Potencialmente alto, crescendo com o número de usuários e avaliações.        | Mantidas enquanto a conta do usuário associado estiver ativa. Excluídas juntamente com a conta.                                   | Coberto pelo mesmo plano de backup do banco de dados principal.                                                               |
| RD003 | **Dados de Cache (Agentes e Campeões)** | Estruturado, relacional (tabelas de cache).                       | Relativamente baixo e com crescimento lento/fixo, correspondente ao número de personagens. | Dados voláteis, podem ser limpos e recriados a qualquer momento. Não há requisito de retenção a longo prazo.                  | Backup de baixa prioridade, pois os dados podem ser reconstruídos a partir das APIs externas.                                 |

---

## 3. Modelagem de Processos de Negócio (BPMN)

Esta seção ilustra o processo de negócio "Cadastro de Novo Usuário" utilizando a notação BPMN.

**Participantes (Pools):** `Novo Usuário`, `Sistema GamesCon`.

**Fluxo Resumido:**
1.  O usuário acessa e preenche o formulário de cadastro.
2.  O sistema recebe os dados e verifica se o apelido já existe.
3.  **Se sim:** O sistema exibe uma mensagem de erro e o fluxo termina.
4.  **Se não:** O sistema codifica a senha, salva o novo usuário no banco de dados com o papel `LOGADO` e redireciona para a página de login com uma mensagem de sucesso.

### Diagrama BPMN: "Cadastro de Novo Usuário"

---
![Diagrama BPMN](diagrama/diagrmaBPMN.png "Diagrama BPMN")
---

---

## 4. Modelagem UML e Diagramas

Esta seção apresenta a modelagem do sistema GamesCon utilizando diagramas da UML.

### 4.1 Diagrama de Casos de Uso

O diagrama descreve a funcionalidade do sistema do ponto de vista do usuário.

**Atores:** `Visitante`, `Usuário Logado`.

**Casos de Uso Principais:**
* **UC01:** Visualizar Conteúdo do Site
* **UC02:** Cadastrar-se na Plataforma
* **UC03:** Autenticar-se (Fazer Login)
* **UC04:** Gerenciar Próprio Perfil
* **UC05:** Avaliar Personagem
* **UC06:** Visualizar Dashboards de Popularidade
* **UC07:** Gerenciar Usuários (requer papel `ADMIN`)
* **UC08:** Fazer Logout

#### Diagrama de Casos de Uso

---
![Diagrama de Caso de Uso](diagrama/diagramaCasoDeUso.png "Diagrama de Caso de Uso")
---

### 4.2 Diagrama de Sequência: "Avaliar Personagem"

O diagrama detalha o fluxo de mensagens entre os componentes do sistema durante a avaliação de um personagem.

**Participantes:** `:UsuarioLogado`, `:AvaliacaoController`, `:AvaliacaoPersonagemService`, `:AvaliacaoPersonagemRepository`.

#### Diagrama de Sequência

---
![Diagrama de Sequência](diagrama/diagramaSequencia.png "Diagrama de Sequência")
---

### 4.3 Modelo de Domínio (Diagrama de Classes de Alto Nível)

Visão simplificada das entidades de negócio mais importantes e seus relacionamentos.

**Classes Centrais:** `Usuario`, `AvaliacaoPersonagem`, `Role`.

#### Modelo de Domínio

---
![Diagrama de Dominio](diagrama/modeloDominio.png "Diagrama de Dominio")
---

### 4.4 Diagrama de Classes Detalhado

Representação completa das classes do sistema, incluindo Entidades JPA, DTOs, Enums e Entidades de Cache, com seus atributos e relacionamentos.

#### Diagrama de Classes Detalhado

---
![Diagrama de Classe](diagrama/DiagramaDeClasse.png "Diagrama de Classe")
---

---

## 5. Protótipos e Wireframes

Esta seção apresenta os artefatos visuais que representam a interface do usuário e a jornada de interação.

### 5.1 Protótipos de Alta Fidelidade (Telas do Sistema)

Os screenshots do sistema finalizado servem como protótipos de alta fidelidade das principais telas.

---
![Index](diagrama/index1.png "Index")
![Index](diagrama/index2.png "Index")
![Index](diagrama/index3.png "Index")
![Login](diagrama/login.png "Login")
![Cadastro](diagrama/cadastrar.png "Cadastro")
![Perfil](diagrama/perfil.png "Perfil")
![Admin](diagrama/admin1.png "Admin")
![Admin](diagrama/admin2.png "Admin")
![Lista de Campeões](diagrama/listaCampeao.png "Lista de Campeões")
![Campeão](diagrama/detalheCampeao1.png "Campeão")
![Campeão](diagrama/detalheCampeao2.png "Campeão")
![Dashboard LOL](diagrama/dashCampeos.png "Dashboard LOL")
![Lista de Agentes](diagrama/listaAgentes.png "Lista de Agentes")
![Agente](diagrama/detalheAgente1.png "Agente")
![Agente](diagrama/detalheAgente2.png "Agente")
![Dashboard Valorant](diagrama/dashAgentes.png "Dashboard Valorant")
![Sobre](diagrama/sobre1.png "Sobre")
![Sobre](diagrama/sobre2.png "Sobre")
![Sobre](diagrama/sobre3.png "Sobre")
---

### 5.2 Storyboard: "Um Novo Usuário Avalia um Personagem"

Ilustração da jornada de um usuário realizando uma tarefa crítica, desde o cadastro até a primeira avaliação.

---
Selecione o botão Cadastrar

![Cadastrar](diagrama/cad.png "Cadastrar")

Coloque os dados no cadastro

![Colocar dados de cadastro](diagrama/cad1.png "Colocar dados de cadastro")

Faça o login

![Logar](diagrama/log.png "Logar")

Selecione o botão *Campeões LoL*

![Logado](diagrama/passo4.png "Logado")

Selecione um campeão

![Selecionar Campeão](diagrama/selecai.png "Selecionar Campeão")

Desça a tela toda

![Akali](diagrama/akali.png "Akali")

Avalie como quiser, e clique no botão *Enviar/Atualizar avaliação.*

![Avaliar](diagrama/avaliar1.png "Avaliar")

Pronto, você já avaliou

![Avaliar](diagrama/avaliar.png "Avaliar")
---

### 5.3 Story Map

Organização do backlog de funcionalidades para visualizar o escopo do produto e planejar os releases.

---
![Storymap](diagrama/storymap.png "Storymap")
---

---

## 6. Análise de Prioridade de Recursos (Modelo Kano)

Classificação de funcionalidades-chave para entender seu impacto na satisfação do usuário.

| Funcionalidade-Chave (RF relacionado)         | Categoria Kano     | Justificativa                                                                                                                                                                                                        |
| :--------------------------------------------- | :----------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1. Autenticação de Usuário (RF002)** | **Must-Be (Obrigatória)** | Para um site com perfis e conteúdo personalizado, a capacidade de fazer login é uma expectativa fundamental. A ausência dessa função tornaria o sistema inútil para seu propósito principal.                         |
| **2. Avaliar Personagem com Estrelas (RF006)** | **Performance (Unidimensional)** | A funcionalidade central do site é avaliar. Uma avaliação simples (1-5 estrelas) atende à necessidade, mas a satisfação do usuário aumentaria com mais refinamento (ex: comentários). "Quanto mais, melhor". |
| **3. Dashboards de Popularidade (RF013)** | **Attractive (Atrativa)** | Um usuário não espera encontrar dashboards visuais com gráficos interativos. Sua ausência não causaria insatisfação, mas sua presença agrega muito valor, surpreende positivamente e encanta o usuário.            |

---

## 7. Casos de Teste Funcionais

Projeto de testes para verificar se os Requisitos Funcionais são atendidos corretamente.

| ID    | Objetivo do Teste                                                                         | Requisito Associado | Pré-condições                                                                                | Dados de Entrada                                                                                | Procedimento                                                                                                                              | Resultado Esperado                                                                                                                             |
| :---- | :---------------------------------------------------------------------------------------- | :------------------ | :------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------- |
| CT001 | Verificar cadastro de usuário bem-sucedido.                                                 | RF001               | Estar na pág. de cadastro; apelido "teste_sucesso" não existe.                               | Nome: "Teste Sucesso", Apelido: "teste_sucesso", Email: "teste@sucesso.com", Senha: "SenhaForte123" | 1. Preencher campos.<br>2. Clicar em "Cadastrar".                                                                                          | Redirecionamento para a pág. de login com mensagem de sucesso. Usuário existe no BD com papel `LOGADO` e senha com hash.                         |
| CT002 | Verificar bloqueio de cadastro com apelido existente.                                       | RF001               | Estar na pág. de cadastro; usuário com apelido "admin_existente" já existe.                  | Apelido: "admin_existente", outros dados quaisquer.                                              | 1. Preencher campos.<br>2. Clicar em "Cadastrar".                                                                                          | Cadastro bloqueado. Exibição de mensagem de erro "O nickname informado já está em uso.".                                                    |
| CT003 | Verificar login bem-sucedido.                                                             | RF002               | Usuário "usuario_comum" com senha "senha123" existe; estar na pág. de login.                 | Apelido: "usuario_comum", Senha: "senha123".                                                     | 1. Preencher campos.<br>2. Clicar em "Entrar".                                                                                             | Redirecionamento para a home (`/`). Interface indica que o usuário está autenticado.                                                          |
| CT004 | Verificar submissão de avaliação por usuário logado.                                        | RF006               | Usuário "usuario_comum" está logado; estar na pág. de detalhes de um personagem.            | Nota: 4 estrelas.                                                                               | 1. Clicar na 4ª estrela.<br>2. Clicar em "Enviar/Atualizar Avaliação".                                                                   | Página recarregada com mensagem de sucesso. A seção "Sua Avaliação Atual" exibe 4 estrelas.                                                    |
| CT005 | Verificar bloqueio de acesso não autorizado à página de admin.                              | RF010               | Usuário "usuario_comum" (com papel `LOGADO`, sem `ADMIN`) está logado.                      | URL: `/administracao/lista-usuarios`                                                            | 1. Acessar a URL diretamente no navegador.                                                                                                | Acesso bloqueado. Sistema redireciona para uma pág. de erro de acesso negado (HTTP 403 Forbidden) ou para a pág. de login.                     |

---

## 8. Matrizes de Rastreabilidade

### 8.1 Matriz: Requisitos × Objetivos do Sistema

**Objetivos:** OBJ01 (Plataforma), OBJ02 (Interface), OBJ03 (Usuários), OBJ04 (APIs), OBJ05 (Dashboards).

| Requisito Funcional (RF)                    | OBJ01 | OBJ02 | OBJ03 | OBJ04 | OBJ05 |
| :------------------------------------------ | :---: | :---: | :---: | :---: | :---: |
| **RF001:** Cadastro de Novo Usuário         |   X   |   X   |   X   |       |       |
| **RF002:** Autenticação de Usuário          |   X   |   X   |   X   |       |       |
| **RF004:** Visualizar Lista de Personagens  |   X   |   X   |       |   X   |       |
| **RF006:** Avaliar um Personagem            |   X   |   X   |   X   |       |       |
| **RF013:** Visualizar Dashboard             |   X   |   X   |       |       |   X   |
| *... (completar para todos os RFs)* |       |       |       |       |       |

### 8.2 Matriz: Requisitos × Casos de Teste

| Requisito Funcional (RF)                  | CT001 | CT002 | CT003 | CT004 | CT005 |
| :---------------------------------------- | :---: | :---: | :---: | :---: | :---: |
| **RF001:** Cadastro de Novo Usuário       |   X   |   X   |       |       |       |
| **RF002:** Autenticação de Usuário        |       |       |   X   |       |       |
| **RF006:** Avaliar um Personagem          |       |       |       |   X   |       |
| **RF010:** Acesso ao Painel Admin         |       |       |       |       |   X   |
| *... (completar para todos os RFs e CTs)* |       |       |       |       |       |

---

## 9. Glossário

| Termo / Abreviação | Descrição                                                                                                                                |
| :----------------- | :--------------------------------------------------------------------------------------------------------------------------------------- |
| **API** | *Application Programming Interface*. Conjunto de rotinas e ferramentas para construir software. Usado para comunicação com sistemas externos. |
| **BPMN** | *Business Process Model and Notation*. Notação gráfica padrão para modelagem de processos de negócio.                                      |
| **CRUD** | Acrônimo para as quatro operações básicas de persistência de dados: *Create*, *Read*, *Update* e *Delete*.                                    |
| **CSRF** | *Cross-Site Request Forgery*. Um tipo de ataque que o Spring Security ajuda a mitigar.                                                     |
| **DTO** | *Data Transfer Object*. Objeto usado para transportar dados entre camadas e sistemas.                                                      |
| **JPA** | *Jakarta Persistence API*. Especificação Java para mapeamento objeto-relacional (ORM).                                                     |
| **MVC** | *Model-View-Controller*. Padrão de arquitetura de software para separar a lógica de negócio da interface do usuário.                      |
| **PlantUML** | Ferramenta para criar diagramas a partir de uma linguagem de texto.                                                                        |
| **RF** | *Requisito Funcional*.                                                                                                                   |
| **RNF** | *Requisito Não-Funcional*.                                                                                                               |
| **UML** | *Unified Modeling Language*. Linguagem de modelagem visual para sistemas de software.                                                    |
| **UUID** | *Universally Unique Identifier*. Identificador único universal usado como chave primária.                                                |

---

## 10. Referências

* **Frameworks e Bibliotecas:**
    * Spring Boot: `https://spring.io/projects/spring-boot`
    * Spring Security: `https://spring.io/projects/spring-security`
    * Thymeleaf: `https://www.thymeleaf.org/documentation.html`
    * Bootstrap CSS: `https://getbootstrap.com/docs/5.3/getting-started/introduction/`
    * Chart.js: `https://www.chartjs.org/docs/latest/`
* **Ferramentas de Desenvolvimento:**
    * Java SE Development Kit (JDK): `https://www.oracle.com/java/technologies/downloads/`
    * H2 Database: `https://www.h2database.com/html/main.html`
* **APIs Externas:**
    * Riot Data Dragon API (League of Legends): `https://developer.riotgames.com/docs/lol#data-dragon`
    * valorant-api.com (Valorant): `https://valorant-api.com/`
* **Ferramentas de Modelagem:**
    * PlantUML: `https://plantuml.com/`
    * Lucidhat: 'https://lucidhart.com'
