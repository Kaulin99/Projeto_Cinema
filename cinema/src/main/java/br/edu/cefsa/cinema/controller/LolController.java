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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/lol") // Define que todas as rotas neste controller começarão com /lol
public class LolController {

    private final LolApiService lolApiService; // Serviço para buscar dados da API do LoL
    private final AvaliacaoPersonagemService avaliacaoService; // Serviço para lidar com avaliações

    // Injeção de dependências via construtor
    @Autowired
    public LolController(LolApiService lolApiService, AvaliacaoPersonagemService avaliacaoService) {
        this.lolApiService = lolApiService;
        this.avaliacaoService = avaliacaoService;
    }

    /**
     * Mapeia a rota GET /lol/campeoes.
     * Busca a lista de todos os campeões do LoL e os adiciona ao modelo para exibição.
     * Trata exceções caso a API externa falhe.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para renderizar a lista de campeões.
     */
    @GetMapping("/campeoes")
    public String listarCampeoes(Model model) {
        try {
            // Busca campeões através do serviço da API
            var campeoes = lolApiService.getChampions().block();
            model.addAttribute("listaDeCampeoes", campeoes); // Adiciona a lista ao modelo

            // Verifica se a lista retornada é nula ou vazia (mesmo sem exceção)
            if (campeoes == null || campeoes.isEmpty()) {
                model.addAttribute("avisoListaVazia", "Nenhum campeão encontrado na API no momento.");
            }
        } catch (Exception e) {
            // Em caso de erro na chamada da API
            System.err.println("Falha ao carregar campeões da API LoL: " + e.getMessage());
            model.addAttribute("erroApi", "Oops! Não conseguimos carregar a lista de campeões no momento. Tente novamente mais tarde.");
            model.addAttribute("listaDeCampeoes", Collections.emptyList()); // Garante que a lista exista no modelo, mesmo vazia
        }
        return "lol/lista-campeoes"; // Retorna o nome do template HTML
    }

    /**
     * Mapeia a rota GET /lol/campeoes/{championId} para exibir detalhes de um campeão.
     * Busca dados detalhados do campeão, incluindo avaliações.
     * @param championId O ID do campeão vindo da URL.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para renderizar os detalhes do campeão.
     */
    @GetMapping("/campeoes/{championId}")
    public String detalheCampeao(@PathVariable String championId, Model model) {
        LolChampion campeao = null;
        boolean erroNaBusca = false;

        try {
            // Busca o campeão específico pelo ID através do serviço da API
            campeao = lolApiService.getChampionById(championId).block();
        } catch (Exception e) {
            System.err.println("Falha ao carregar detalhes do campeão " + championId + ": " + e.getMessage());
            model.addAttribute("erroApiCampeao", "Não foi possível carregar os detalhes deste campeão. Verifique o ID ou tente mais tarde.");
            erroNaBusca = true;
        }

        // Verifica se o campeão foi encontrado ou se houve erro na busca
        if (campeao == null && !erroNaBusca) {
            model.addAttribute("erroCampeaoNaoEncontrado", true);
            model.addAttribute("erroMensagemGeral", "Campeão com ID '" + championId + "' não encontrado.");
        } else if (campeao == null && erroNaBusca) {
            model.addAttribute("campeaoNaoEncontrado", true);
        }

        model.addAttribute("campeao", campeao); // Adiciona o campeão (pode ser null) ao modelo
        model.addAttribute("gameVersion", lolApiService.getGameVersion()); // Adiciona a versão do jogo para URLs de imagens

        // Busca e adiciona informações de avaliação ao modelo
        Usuario usuarioLogado = avaliacaoService.getUsuarioLogado();
        boolean isLogado = (usuarioLogado != null);
        model.addAttribute("usuarioLogado", isLogado); // Informa à view se o usuário está logado

        if (campeao != null) { // Só processa avaliações se o campeão foi carregado
            if (isLogado) {
                Optional<Integer> notaOpt = avaliacaoService.getAvaliacaoDoUsuario(campeao.getId(), "LOL", usuarioLogado);
                model.addAttribute("avaliacaoUsuario", notaOpt.orElse(null)); // Avaliação do usuário logado (ou null)
            } else {
                model.addAttribute("avaliacaoUsuario", null);
            }

            // Busca a média de avaliações para este campeão
            avaliacaoService.getMediaAvaliacoes(campeao.getId(), "LOL")
                    .ifPresentOrElse(
                            media -> model.addAttribute("mediaAvaliacoes", String.format("%.1f", media)),
                            () -> model.addAttribute("mediaAvaliacoes", "N/A")
                    );
        } else {
            // Se o campeão não foi carregado, define valores padrão para avaliações
            model.addAttribute("avaliacaoUsuario", null);
            model.addAttribute("mediaAvaliacoes", "N/A");
        }
        return "lol/detalhe-campeao"; // Retorna o nome do template HTML
    }

    /**
     * Mapeia a rota GET /lol/api/popularidade para fornecer dados JSON.
     * Usado pelo gráfico de popularidade para buscar dados de forma assíncrona.
     * @return Uma lista de mapas contendo nome, quantidade de avaliações e média para cada campeão.
     */
    @GetMapping("/api/popularidade")
    @ResponseBody // Indica que o retorno do método deve ser diretamente no corpo da resposta (JSON)
    public List<Map<String, Object>> popularidadeLOLJson() {
        List<Object[]> resultados = avaliacaoService.getPopularidadeLOL(); // Busca dados agregados do serviço

        List<Map<String, Object>> resposta = new ArrayList<>();
        // Transforma o resultado da query (List<Object[]>) em uma lista de mapas para fácil consumo pelo JSON
        for (Object[] linha : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", linha[0]);
            mapa.put("quantidade", linha[1]);
            mapa.put("media", linha[2] != null ? linha[2] : 0.0); // Trata média nula como 0.0
            resposta.add(mapa);
        }
        return resposta;
    }

    /**
     * Mapeia a rota GET /lol/dashboard-popularidade.
     * Simplesmente retorna a página HTML que conterá o gráfico (que buscará os dados via JS).
     * @param model Objeto para passar dados para a view (não usado neste método específico).
     * @return O nome do template Thymeleaf para o dashboard de popularidade.
     */
    @GetMapping("/dashboard-popularidade")
    public String dashboardPopularidade(Model model) {
        return "lol/dashboard-popularidade";
    }
}