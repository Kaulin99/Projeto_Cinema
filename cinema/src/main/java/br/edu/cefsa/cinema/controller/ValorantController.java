package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.model.ValorantAgent;
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.service.ValorantApiService;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Necessário se usar @AuthenticationPrincipal

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/valorant") // Define que todas as rotas neste controller começarão com /valorant
public class ValorantController {

    private final ValorantApiService valorantApiService; // Serviço para buscar dados da API do Valorant
    private final AvaliacaoPersonagemService avaliacaoService; // Serviço para lidar com avaliações

    // Injeção de dependências via construtor
    @Autowired
    public ValorantController(ValorantApiService valorantApiService, AvaliacaoPersonagemService avaliacaoService) {
        this.valorantApiService = valorantApiService;
        this.avaliacaoService = avaliacaoService;
    }

    /**
     * Mapeia a rota GET /valorant/agentes.
     * Busca a lista de todos os agentes do Valorant e os adiciona ao modelo para exibição.
     * Trata exceções caso a API externa falhe.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para renderizar a lista de agentes.
     */
    @GetMapping("/agentes")
    public String listarAgentes(Model model) {
        try {
            // Busca agentes através do serviço da API (valorant-api.com)
            var agentes = valorantApiService.getAgents().block();
            model.addAttribute("listaDeAgentes", agentes); // Adiciona a lista ao modelo

            // Verifica se a lista retornada é nula ou vazia
            if (agentes != null && agentes.isEmpty()) {
                model.addAttribute("avisoListaVazia", "Nenhum agente encontrado na API no momento.");
            }
        } catch (Exception e) {
            System.err.println("Falha ao carregar agentes da API Valorant: " + e.getMessage());
            model.addAttribute("erroApi", "Oops! Não conseguimos carregar a lista de agentes no momento. Tente novamente mais tarde.");
            model.addAttribute("listaDeAgentes", Collections.emptyList()); // Garante que a lista exista no modelo
        }
        return "valorant/lista-agentes"; // Retorna o nome do template HTML
    }

    /**
     * Mapeia a rota GET /valorant/agentes/{uuid} para exibir detalhes de um agente.
     * Busca dados detalhados do agente, incluindo avaliações.
     * @param uuid O UUID do agente vindo da URL.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para renderizar os detalhes do agente.
     */
    @GetMapping("/agentes/{uuid}")
    public String detalheAgente(@PathVariable String uuid, Model model) {
        ValorantAgent agente = null;
        boolean erroNaBusca = false;

        try {
            // Busca o agente específico pelo UUID através do serviço da API (valorant-api.com)
            agente = valorantApiService.getAgentByUuid(uuid).block();
        } catch (Exception e) {
            System.err.println("Falha ao carregar detalhes do agente " + uuid + ": " + e.getMessage());
            model.addAttribute("erroApiAgente", "Não foi possível carregar os detalhes deste agente. Verifique o UUID ou tente mais tarde.");
            erroNaBusca = true;
        }

        // Verifica se o agente foi encontrado ou se houve erro na busca
        if (agente == null && !erroNaBusca) {
            model.addAttribute("agenteNaoEncontrado", true);
            model.addAttribute("erroMensagemGeral", "Agente com UUID '" + uuid + "' não encontrado.");
        } else if (agente == null && erroNaBusca) {
            model.addAttribute("agenteNaoEncontrado", true);
        }

        model.addAttribute("agente", agente); // Adiciona o agente (pode ser null) ao modelo

        // Busca e adiciona informações de avaliação ao modelo
        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado); // Informa à view se o usuário está logado

        if (agente != null) { // Só processa avaliações se o agente foi carregado
            if (isLogado) {
                Optional<Integer> notaOpt = avaliacaoService.getAvaliacaoDoUsuario(uuid, "VALORANT", usuarioLogado);
                model.addAttribute("avaliacaoUsuario", notaOpt.orElse(null)); // Avaliação do usuário logado (ou null)
            } else {
                model.addAttribute("avaliacaoUsuario", null);
            }

            // Busca a média de avaliações para este agente
            avaliacaoService.getMediaAvaliacoes(uuid, "VALORANT")
                    .ifPresentOrElse(
                            media -> model.addAttribute("mediaAvaliacoes", String.format("%.1f", media)),
                            () -> model.addAttribute("mediaAvaliacoes", "N/A")
                    );
        } else {
            // Se o agente não foi carregado, define valores padrão para avaliações
            model.addAttribute("avaliacaoUsuario", null);
            model.addAttribute("mediaAvaliacoes", "N/A");
        }
        return "valorant/detalhe-agente"; // Retorna o nome do template HTML
    }

    /**
     * Mapeia a rota GET /valorant/api/popularidade-valorant para fornecer dados JSON.
     * Usado pelo gráfico de popularidade do Valorant.
     * @return Uma lista de mapas contendo nome, quantidade de avaliações e média para cada agente.
     */
    @GetMapping("/api/popularidade-valorant")
    @ResponseBody // Indica que o retorno é diretamente no corpo da resposta (JSON)
    public List<Map<String, Object>> popularidadeValorantJson() {
        List<Object[]> resultados = avaliacaoService.getPopularidadeValorant(); // Busca dados agregados do serviço

        List<Map<String, Object>> resposta = new ArrayList<>();
        // Transforma o resultado da query em uma lista de mapas
        for (Object[] linha : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", linha[0]);
            mapa.put("quantidade", linha[1]);
            mapa.put("media", linha[2] != null ? linha[2] : 0.0); // Trata média nula
            resposta.add(mapa);
        }
        return resposta;
    }

    /**
     * Mapeia a rota GET /valorant/dashboard-popularidade-valorant.
     * Retorna a página HTML do dashboard de popularidade do Valorant.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf.
     */
    @GetMapping("/dashboard-popularidade-valorant")
    public String dashboardPopularidadeValorant(Model model) {
        return "valorant/dashboard-popularidade-valorant";
    }
}