package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.LolChampion;
import br.edu.cefsa.cinema.model.Usuario; // Certifique-se que este import está correto
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService; // Importe o serviço de avaliação
import br.edu.cefsa.cinema.service.LolApiService;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Se for usar para pegar o usuário logado via @AuthenticationPrincipal
import org.springframework.beans.factory.annotation.Autowired; // Se for usar @Autowired
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional; // Para lidar com o Optional retornado pelo serviço

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
}