package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.AvaliacaoPersonagem;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.AvaliacaoPersonagemRepository;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Sua classe CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AvaliacaoPersonagemService {

    @Autowired
    private AvaliacaoPersonagemRepository avaliacaoRepository;

    @Transactional
    public void salvarOuAtualizarAvaliacao(String personagemIdApi, String nomePersonagem, String jogo, String classeRole, int nota, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário precisa estar logado para avaliar.");
        }
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota da avaliação deve ser entre 1 e 5.");
        }

        Optional<AvaliacaoPersonagem> avaliacaoExistenteOpt = avaliacaoRepository.findByPersonagemIdApiAndJogoAndUsuario(personagemIdApi, jogo, usuarioLogado);

        AvaliacaoPersonagem avaliacao;
        if (avaliacaoExistenteOpt.isPresent()) {
            // Atualiza a avaliação existente
            avaliacao = avaliacaoExistenteOpt.get();
            avaliacao.setAvaliacao(nota);
            // dataAvaliacao será atualizada pelo @PreUpdate
        } else {
            // Cria uma nova avaliação
            avaliacao = new AvaliacaoPersonagem();
            avaliacao.setPersonagemIdApi(personagemIdApi);
            avaliacao.setNomePersonagem(nomePersonagem);
            avaliacao.setJogo(jogo);
            avaliacao.setAvaliacao(nota);
            avaliacao.setUsuario(usuarioLogado);
            // dataAvaliacao será definida pelo @PrePersist
        }
        avaliacaoRepository.save(avaliacao);
    }

    public Optional<Integer> getAvaliacaoDoUsuario(String personagemIdApi, String jogo, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return Optional.empty();
        }
        return avaliacaoRepository.findByPersonagemIdApiAndJogoAndUsuario(personagemIdApi, jogo, usuarioLogado)
                .map(AvaliacaoPersonagem::getAvaliacao);
    }

    public Optional<Double> getMediaAvaliacoes(String personagemIdApi, String jogo) {
        return avaliacaoRepository.findAverageRatingByPersonagem(personagemIdApi, jogo);
    }

    // Método auxiliar para obter o usuário logado atualmente
    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsuario();
        }
        return null; // Retorna null se não houver usuário logado ou o principal não for CustomUserDetails
    }
}