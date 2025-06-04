package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.model.ValorantAgent;
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.service.ValorantApiService;
// Removido import não utilizado: br.edu.cefsa.cinema.repository.AvaliacaoPersonagemRepository;
// Se você usa CustomUserDetails para @AuthenticationPrincipal, certifique-se do import
import br.edu.cefsa.cinema.security.CustomUserDetails;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para pegar o usuário logado
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
@RequestMapping("/valorant")
public class ValorantController {

    private final ValorantApiService valorantApiService;
    private final AvaliacaoPersonagemService avaliacaoService;

    @Autowired
    public ValorantController(ValorantApiService valorantApiService, AvaliacaoPersonagemService avaliacaoService) {
        this.valorantApiService = valorantApiService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/agentes")
    public String listarAgentes(Model model) {
        try {
            // Assume que este método busca da valorant-api.com e retorna List<ValorantAgent>
            var agentes = valorantApiService.getAgents().block();
            model.addAttribute("listaDeAgentes", agentes);
            if (agentes != null && agentes.isEmpty()) {
                model.addAttribute("avisoListaVazia", "Nenhum agente encontrado na API no momento.");
            }
        } catch (Exception e) {
            System.err.println("Falha ao carregar agentes da API Valorant: " + e.getMessage());
            model.addAttribute("erroApi", "Oops! Não conseguimos carregar a lista de agentes no momento. Tente novamente mais tarde.");
            model.addAttribute("listaDeAgentes", Collections.emptyList());
        }
        return "valorant/lista-agentes";
    }

    @GetMapping("/agentes/{uuid}")
    public String detalheAgente(@PathVariable String uuid, Model model) {
        ValorantAgent agente = null;
        boolean erroNaBusca = false;
        try {
            // Assume que este método busca da valorant-api.com/v1/agents/{uuid}
            agente = valorantApiService.getAgentByUuid(uuid).block();
        } catch (Exception e) {
            System.err.println("Falha ao carregar detalhes do agente " + uuid + ": " + e.getMessage());
            model.addAttribute("erroApiAgente", "Não foi possível carregar os detalhes deste agente. Verifique o UUID ou tente mais tarde.");
            erroNaBusca = true;
        }

        if (agente == null && !erroNaBusca) {
            model.addAttribute("agenteNaoEncontrado", true);
            model.addAttribute("erroMensagemGeral", "Agente com UUID '" + uuid + "' não encontrado.");
        } else if (agente == null && erroNaBusca) {
            model.addAttribute("agenteNaoEncontrado", true);
        }

        model.addAttribute("agente", agente); // Pode ser null se a busca falhou

        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado);

        if (agente != null) { // Só tenta buscar avaliações se o agente foi carregado
            if (isLogado) {
                Optional<Integer> notaOpt = avaliacaoService.getAvaliacaoDoUsuario(uuid, "VALORANT", usuarioLogado);
                model.addAttribute("avaliacaoUsuario", notaOpt.orElse(null));
            } else {
                model.addAttribute("avaliacaoUsuario", null);
            }

            avaliacaoService.getMediaAvaliacoes(uuid, "VALORANT")
                    .ifPresentOrElse(
                            media -> model.addAttribute("mediaAvaliacoes", String.format("%.1f", media)),
                            () -> model.addAttribute("mediaAvaliacoes", "N/A")
                    );
        } else { // Se o agente não foi carregado, define valores padrão para avaliações
            model.addAttribute("avaliacaoUsuario", null);
            model.addAttribute("mediaAvaliacoes", "N/A");
        }

        // Debug (opcional)
        System.out.println("--- DEBUG VALORANT CONTROLLER (DETALHE AGENTE) ---");
        System.out.println("Agente UUID Requisitado: " + uuid);
        System.out.println("Objeto Agente (no model): " + (model.getAttribute("agente") != null ? ((ValorantAgent)model.getAttribute("agente")).getNome() : "NULO"));
        System.out.println("Usuário está logado? " + isLogado);
        System.out.println("Avaliação do Usuário (no model): " + model.getAttribute("avaliacaoUsuario"));
        System.out.println("Média de Avaliações (no model): " + model.getAttribute("mediaAvaliacoes"));
        System.out.println("----------------------------------------------");

        return "valorant/detalhe-agente";
    }

    // Endpoint da API para dados de popularidade do Valorant
    @GetMapping("/api/popularidade-valorant")
    @ResponseBody
    public List<Map<String, Object>> popularidadeValorantJson() {
        List<Object[]> resultados = avaliacaoService.getPopularidadeValorant(); // Método já existente no seu service

        List<Map<String, Object>> resposta = new ArrayList<>();
        for (Object[] linha : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", linha[0]);       // nomePersonagem
            mapa.put("quantidade", linha[1]); // COUNT(a)
            mapa.put("media", linha[2] != null ? linha[2] : 0.0); // AVG(a.avaliacao) - Tratando possível null
            resposta.add(mapa);
        }
        return resposta;
    }

    // Endpoint para a página do Dashboard de Popularidade do Valorant
    @GetMapping("/dashboard-popularidade-valorant")
    public String dashboardPopularidadeValorant(Model model) {
        return "valorant/dashboard-popularidade"; // Novo arquivo HTML
    }
}