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
    * **Frontend:** Utiliza **Thymeleaf** para a renderização de páginas HTML dinâmicas, com **HTML5** e **CSS
