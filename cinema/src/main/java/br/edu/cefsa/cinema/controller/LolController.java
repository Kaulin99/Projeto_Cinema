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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/lol")
public class LolController {

    private final LolApiService lolApiService;
    private final AvaliacaoPersonagemService avaliacaoService; // Injetar o serviço de avaliação

    // Injeção via construtor (recomendado)
    @Autowired // Opcional se tiver apenas um construtor
    public LolController(LolApiService lolApiService, AvaliacaoPersonagemService avaliacaoService) {
        this.lolApiService = lolApiService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/campeoes")
    public String listarCampeoes(Model model) {
        var campeoes = lolApiService.getChampions().block();
        model.addAttribute("listaDeCampeoes", campeoes);
        return "lol/lista-campeoes";
    }

    @GetMapping("/campeoes/{championId}")
    public String detalheCampeao(@PathVariable String championId, Model model) {
        LolChampion campeao = lolApiService.getChampionById(championId).block();

        if (campeao == null) {
            // Adicionar um atributo para indicar que o campeão não foi encontrado
            model.addAttribute("erroCampeaoNaoEncontrado", true);
            // Você pode querer redirecionar para uma página de erro ou para a lista
            return "lol/lista-campeoes"; // Ou uma página de erro específica
        }
        model.addAttribute("campeao", campeao);
        model.addAttribute("gameVersion", lolApiService.getGameVersion());

        // Lógica para buscar e adicionar avaliações ao modelo
        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado);

        if (isLogado) {
            Optional<Integer> notaOpt = avaliacaoService.getAvaliacaoDoUsuario(championId, "LOL", usuarioLogado);
            model.addAttribute("avaliacaoUsuario", notaOpt.orElse(null)); // Adiciona a nota ou null
        } else {
            model.addAttribute("avaliacaoUsuario", null); // Garante que a variável exista como null se não logado
        }

        avaliacaoService.getMediaAvaliacoes(championId, "LOL")
                .ifPresentOrElse(
                        media -> model.addAttribute("mediaAvaliacoes", String.format("%.1f", media)),
                        () -> model.addAttribute("mediaAvaliacoes", "N/A")
                );

        // Debug para verificar o que está sendo enviado ao template
        System.out.println("--- DEBUG LOL CONTROLLER (DETALHE CAMPEAO) ---");
        System.out.println("Campeão ID: " + championId);
        System.out.println("Usuário está logado? " + isLogado);
        System.out.println("Avaliação do Usuário (no model): " + model.getAttribute("avaliacaoUsuario"));
        System.out.println("Média de Avaliações (no model): " + model.getAttribute("mediaAvaliacoes"));
        System.out.println("----------------------------------------------");

        return "lol/detalhe-campeao";
    }

    @GetMapping("/api/popularidade")
    @ResponseBody
    public List<Map<String, Object>> popularidadeLOLJson() {
        List<Object[]> resultados = avaliacaoService.getPopularidadeLOL();

        List<Map<String, Object>> resposta = new ArrayList<>();
        for (Object[] linha : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", linha[0]);
            mapa.put("quantidade", linha[1]);
            mapa.put("media", linha[2]);
            resposta.add(mapa);
        }
        return resposta;
    }

    @GetMapping("/dashboard-popularidade")
    public String dashboardPopularidade(Model model) {
        return "lol/dashboard-popularidade"; // nome do arquivo .html em templates (Thymeleaf)
    }
}