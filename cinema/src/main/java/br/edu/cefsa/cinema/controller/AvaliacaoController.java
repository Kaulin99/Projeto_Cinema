package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AvaliacaoController {

    @Autowired
    private AvaliacaoPersonagemService avaliacaoService; // Serviço para lógica de avaliações

    /**
     * Mapeia a rota POST /avaliar/personagem para processar a submissão de uma avaliação.
     * @param personagemIdApi ID do personagem/agente da API.
     * @param nomePersonagem Nome do personagem/agente.
     * @param jogo Jogo ao qual o personagem pertence ("LOL" ou "VALORANT").
     * @param classeRole Classe/Role do personagem (opcional).
     * @param nota A nota da avaliação (1-5).
     * @param userDetails Detalhes do usuário autenticado.
     * @param redirectAttributes Usado para enviar mensagens de feedback (sucesso/erro) para a página de redirecionamento.
     * @return Redireciona de volta para a página de detalhes do personagem.
     */
    @PostMapping("/avaliar/personagem")
    public String submeterAvaliacao(@RequestParam String personagemIdApi,
                                    @RequestParam String nomePersonagem,
                                    @RequestParam String jogo,
                                    @RequestParam(required = false) String classeRole,
                                    @RequestParam int nota,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {

        // Verifica se o usuário está logado
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("erroAvaliacao", "Você precisa estar logado para avaliar.");
            // Define a página de origem para redirecionamento ou uma página de login
            String paginaOrigem = "LOL".equalsIgnoreCase(jogo) ? "/lol/campeoes/" + personagemIdApi : "/valorant/agentes/" + personagemIdApi;
            if (paginaOrigem.endsWith("/")) paginaOrigem = "/usuarios/login"; // Fallback se ID for nulo ou inválido
            return "redirect:" + paginaOrigem;
        }
        Usuario usuarioLogado = userDetails.getUsuario(); // Obtém o objeto Usuario

        try {
            // Tenta salvar ou atualizar a avaliação através do serviço
            avaliacaoService.salvarOuAtualizarAvaliacao(personagemIdApi, nomePersonagem, jogo, classeRole, nota, usuarioLogado);
            redirectAttributes.addFlashAttribute("sucessoAvaliacao", "Sua avaliação de " + nota + " estrela(s) foi registrada!");
        } catch (Exception e) {
            // Em caso de erro (ex: nota inválida, erro no banco)
            System.err.println("Erro ao registrar avaliação: " + e.getMessage());
            redirectAttributes.addFlashAttribute("erroAvaliacao", "Erro ao registrar avaliação: " + e.getMessage());
        }

        // Redireciona de volta para a página de detalhes do personagem correspondente
        if ("LOL".equalsIgnoreCase(jogo)) {
            return "redirect:/lol/campeoes/" + personagemIdApi;
        } else if ("VALORANT".equalsIgnoreCase(jogo)) {
            return "redirect:/valorant/agentes/" + personagemIdApi;
        }
        return "redirect:/"; // Fallback para a página inicial se o jogo não for reconhecido
    }
}