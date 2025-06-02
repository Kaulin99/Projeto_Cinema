package br.edu.cefsa.cinema.controller;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.service.AvaliacaoPersonagemService;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Sua classe CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AvaliacaoController {

    @Autowired
    private AvaliacaoPersonagemService avaliacaoService;

    @PostMapping("/avaliar/personagem")
    public String submeterAvaliacao(@RequestParam String personagemIdApi,
                                    @RequestParam String nomePersonagem,
                                    @RequestParam String jogo, // "LOL" ou "VALORANT"
                                    @RequestParam(required = false) String classeRole, // Pode ser nulo/opcional
                                    @RequestParam int nota,
                                    @AuthenticationPrincipal CustomUserDetails userDetails, // Pega o usuário logado
                                    RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("erroAvaliacao", "Você precisa estar logado para avaliar.");
            // Define a página de origem para redirecionamento
            String paginaOrigem = "LOL".equalsIgnoreCase(jogo) ? "/lol/campeoes/" + personagemIdApi : "/valorant/agentes/" + personagemIdApi;
            if ("redirect:/".equals(paginaOrigem)) paginaOrigem = "/usuarios/login"; // Se algo der errado, manda pro login
            return "redirect:" + paginaOrigem;
        }
        Usuario usuarioLogado = userDetails.getUsuario();

        try {
            avaliacaoService.salvarOuAtualizarAvaliacao(personagemIdApi, nomePersonagem, jogo, classeRole, nota, usuarioLogado);
            redirectAttributes.addFlashAttribute("sucessoAvaliacao", "Sua avaliação de " + nota + " estrela(s) foi registrada!");
        } catch (Exception e) { // Captura exceções mais genéricas também
            redirectAttributes.addFlashAttribute("erroAvaliacao", "Erro ao registrar avaliação: " + e.getMessage());
        }

        // Redireciona de volta para a página de detalhes do personagem
        if ("LOL".equalsIgnoreCase(jogo)) {
            return "redirect:/lol/campeoes/" + personagemIdApi;
        } else if ("VALORANT".equalsIgnoreCase(jogo)) {
            return "redirect:/valorant/agentes/" + personagemIdApi;
        }
        return "redirect:/"; // Fallback para a home
    }
}