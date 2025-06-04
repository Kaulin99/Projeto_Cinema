package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.LolChampion;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.service.LolApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections; // Import para Collections.emptyList()
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/lol")
public class LolController {

    private final LolApiService lolApiService;
    private final AvaliacaoPersonagemService avaliacaoService;

    @Autowired
    public LolController(LolApiService lolApiService, AvaliacaoPersonagemService avaliacaoService) {
        this.lolApiService = lolApiService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/campeoes")
    public String listarCampeoes(Model model) {
        try {
            var campeoes = lolApiService.getChampions().block(); // Chamada à API
            model.addAttribute("listaDeCampeoes", campeoes);
            if (campeoes == null || campeoes.isEmpty()) {
                // Se a API retornar uma lista vazia ou nula (sem lançar exceção)
                model.addAttribute("avisoListaVazia", "Nenhum campeão encontrado na API no momento.");
            }
        } catch (Exception e) {
            // Loga o erro no console do servidor para depuração
            System.err.println("Falha ao carregar campeões da API LoL: " + e.getMessage());
            // Adiciona uma mensagem de erro amigável para o usuário
            model.addAttribute("erroApi", "Oops! Não conseguimos carregar a lista de campeões no momento. Tente novamente mais tarde.");
            // Garante que listaDeCampeoes exista no modelo, mesmo que vazia, para evitar erros no Thymeleaf
            model.addAttribute("listaDeCampeoes", Collections.emptyList());
        }
        return "lol/lista-campeoes";
    }

    @GetMapping("/campeoes/{championId}")
    public String detalheCampeao(@PathVariable String championId, Model model) {
        LolChampion campeao = null;
        boolean erroNaBusca = false;
        try {
            campeao = lolApiService.getChampionById(championId).block();
        } catch (Exception e) {
            System.err.println("Falha ao carregar detalhes do campeão " + championId + ": " + e.getMessage());
            model.addAttribute("erroApiCampeao", "Não foi possível carregar os detalhes deste campeão. Verifique o ID ou tente mais tarde.");
            erroNaBusca = true;
        }

        if (campeao == null && !erroNaBusca) { // Se campeao é null mas não houve exceção na API (ex: ID não existe)
            model.addAttribute("erroCampeaoNaoEncontrado", true);
            model.addAttribute("erroMensagemGeral", "Campeão com ID '" + championId + "' não encontrado.");
        } else if (campeao == null && erroNaBusca) {
            // A mensagem erroApiCampeao já foi setada
            model.addAttribute("campeaoNaoEncontrado", true); // Para o th:unless funcionar na página
        }

        model.addAttribute("campeao", campeao); // Pode ser null aqui se a busca falhou
        model.addAttribute("gameVersion", lolApiService.getGameVersion());

        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado);

        if (campeao != null) { // Só tenta buscar avaliações se o campeão foi carregado
            if (isLogado) {
                Optional<Integer> notaOpt = avaliacaoService.getAvaliacaoDoUsuario(campeao.getId(), "LOL", usuarioLogado);
                model.addAttribute("avaliacaoUsuario", notaOpt.orElse(null));
            } else {
                model.addAttribute("avaliacaoUsuario", null);
            }

            avaliacaoService.getMediaAvaliacoes(campeao.getId(), "LOL")
                    .ifPresentOrElse(
                            media -> model.addAttribute("mediaAvaliacoes", String.format("%.1f", media)),
                            () -> model.addAttribute("mediaAvaliacoes", "N/A")
                    );
        } else { // Se o campeão não foi carregado, define valores padrão para avaliações
            model.addAttribute("avaliacaoUsuario", null);
            model.addAttribute("mediaAvaliacoes", "N/A");
        }

        // Debug (pode ser mantido ou removido em produção)
        System.out.println("--- DEBUG LOL CONTROLLER (DETALHE CAMPEAO) ---");
        System.out.println("Campeão ID Requisitado: " + championId);
        System.out.println("Objeto Campeão (no model): " + (model.getAttribute("campeao") != null ? ((LolChampion)model.getAttribute("campeao")).getNome() : "NULO"));
        System.out.println("Usuário está logado? " + isLogado);
        System.out.println("Avaliação do Usuário (no model): " + model.getAttribute("avaliacaoUsuario"));
        System.out.println("Média de Avaliações (no model): " + model.getAttribute("mediaAvaliacoes"));
        System.out.println("----------------------------------------------");

        return "lol/detalhe-campeao";
    }

    @GetMapping("/api/popularidade")
    @ResponseBody
    public List<Map<String, Object>> popularidadeLOLJson() {
        // Você precisará implementar getPopularidadeLOL() no seu AvaliacaoPersonagemService
        // e ajustar a query no AvaliacaoPersonagemRepository para 'LOL'.
        // A query atual no seu repositório está como 'VALORANT' para buscarPopularidadeLOL.
        List<Object[]> resultados = avaliacaoService.getPopularidadeLOL();

        List<Map<String, Object>> resposta = new ArrayList<>();
        for (Object[] linha : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", linha[0]);
            mapa.put("quantidade", linha[1]);
            mapa.put("media", linha[2]); // Certifique-se que o AVG(a.avaliacao) não seja null
            resposta.add(mapa);
        }
        return resposta;
    }

    @GetMapping("/dashboard-popularidade")
    public String dashboardPopularidade(Model model) {
        // Nenhuma alteração necessária aqui para a lógica de erro,
        // pois os dados são carregados via JavaScript/API separadamente.
        return "lol/dashboard-popularidade";
    }
}