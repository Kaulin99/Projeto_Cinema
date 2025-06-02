package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.model.ValorantAgent; // Modelo para dados da valorant-api.com
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.service.ValorantApiService;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Para @AuthenticationPrincipal

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * Exibe a lista de todos os agentes jogáveis do Valorant.
     * Os dados são buscados da valorant-api.com.
     */
    @GetMapping("/agentes")
    public String listarAgentes(Model model) {
        try {
            // Este método deve buscar de valorant-api.com e retornar List<ValorantAgent>
            var agentes = valorantApiService.getAgents().block();
            model.addAttribute("listaDeAgentes", agentes);
        } catch (Exception e) {
            // Logar o erro e talvez adicionar uma mensagem de erro ao modelo
            System.err.println("Erro ao buscar lista de agentes do Valorant: " + e.getMessage());
            model.addAttribute("erroListaAgentes", "Não foi possível carregar a lista de agentes no momento.");
            // Você pode também adicionar uma lista vazia para evitar erros no Thymeleaf
            // model.addAttribute("listaDeAgentes", Collections.emptyList());
        }
        return "valorant/lista-agentes";
    }

    /**
     * Exibe a página de detalhes para um agente específico do Valorant,
     * incluindo informações de avaliação.
     * Os dados do agente são buscados da valorant-api.com.
     */
    @GetMapping("/agentes/{uuid}")
    public String detalheAgente(@PathVariable String uuid, Model model) {
        ValorantAgent agente = null;
        try {
            // Este método DEVE buscar da valorant-api.com/v1/agents/{uuid}
            // e retornar o modelo ValorantAgent completo com abilities, voiceLine, etc.
            agente = valorantApiService.getAgentByUuid(uuid).block();
        } catch (Exception e) {
            System.err.println("Erro ao buscar detalhes do agente " + uuid + ": " + e.getMessage());
            // Tratar o erro, talvez redirecionar ou mostrar uma mensagem
        }

        if (agente == null) {
            model.addAttribute("erroAgenteNaoEncontrado", true);
            // Redireciona para a lista se o agente não for encontrado,
            // ou você pode ter uma página de erro específica.
            return "valorant/lista-agentes";
        }
        model.addAttribute("agente", agente);

        // Lógica para buscar e adicionar avaliações ao modelo
        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado);

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

        // Debug (opcional, remova em produção)
        System.out.println("--- DEBUG VALORANT CONTROLLER (DETALHE AGENTE) ---");
        System.out.println("Agente UUID: " + uuid);
        System.out.println("Agente Nome (do model): " + (agente != null ? agente.getNome() : "AGENTE NULO"));
        System.out.println("Usuário está logado? " + isLogado);
        System.out.println("Avaliação do Usuário (no model): " + model.getAttribute("avaliacaoUsuario"));
        System.out.println("Média de Avaliações (no model): " + model.getAttribute("mediaAvaliacoes"));
        System.out.println("----------------------------------------------");

        return "valorant/detalhe-agente";
    }
}