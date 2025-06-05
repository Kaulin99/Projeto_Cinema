package br.edu.cefsa.cinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para garantir atomicidade nas operações de escrita

import br.edu.cefsa.cinema.model.AvaliacaoPersonagem;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.AvaliacaoPersonagemRepository;
import br.edu.cefsa.cinema.security.CustomUserDetails;

/**
 * Classe de serviço para gerenciar a lógica de negócios relacionada a avaliações de personagens.
 */
@Service
public class AvaliacaoPersonagemService {

    @Autowired
    private AvaliacaoPersonagemRepository avaliacaoRepository; // Repositório para acesso aos dados de avaliação

    /**
     * Salva uma nova avaliação ou atualiza uma existente para um personagem por um usuário.
     * @param personagemIdApi ID do personagem na API externa.
     * @param nomePersonagem Nome do personagem.
     * @param jogo Nome do jogo ("LOL" ou "VALORANT").
     * @param classeRole Classe/Role do personagem (pode ser nulo).
     * @param nota A avaliação (1-5).
     * @param usuarioLogado O objeto Usuario que está fazendo a avaliação.
     * @throws IllegalStateException Se o usuário não estiver logado.
     * @throws IllegalArgumentException Se a nota estiver fora do intervalo válido.
     */
    @Transactional // Garante que a operação seja atômica (ou tudo funciona, ou nada é alterado no banco)
    public void salvarOuAtualizarAvaliacao(String personagemIdApi, String nomePersonagem, String jogo, String classeRole, int nota, Usuario usuarioLogado) {
        // Validações de entrada
        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário precisa estar logado para avaliar.");
        }
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota da avaliação deve ser entre 1 e 5.");
        }

        // Verifica se o usuário já avaliou este personagem neste jogo
        Optional<AvaliacaoPersonagem> avaliacaoExistenteOpt = avaliacaoRepository.findByPersonagemIdApiAndJogoAndUsuario(personagemIdApi, jogo, usuarioLogado);

        AvaliacaoPersonagem avaliacao;
        if (avaliacaoExistenteOpt.isPresent()) {
            // Se já existe, atualiza a nota e a data da avaliação (data via @PreUpdate)
            avaliacao = avaliacaoExistenteOpt.get();
            avaliacao.setAvaliacao(nota);
        } else {
            // Se não existe, cria uma nova avaliação
            avaliacao = new AvaliacaoPersonagem();
            avaliacao.setPersonagemIdApi(personagemIdApi);
            avaliacao.setNomePersonagem(nomePersonagem);
            avaliacao.setJogo(jogo);
            avaliacao.setClasseRole(classeRole); // Atribui a classe/role
            avaliacao.setAvaliacao(nota);
            avaliacao.setUsuario(usuarioLogado);
            // A data da avaliação será definida automaticamente pelo @PrePersist na entidade
        }
        avaliacaoRepository.save(avaliacao); // Salva (ou atualiza) a avaliação no banco
    }

    /**
     * Obtém a avaliação (nota) que um usuário específico deu para um personagem de um jogo.
     * @param personagemIdApi ID do personagem na API externa.
     * @param jogo Nome do jogo.
     * @param usuarioLogado O objeto Usuario.
     * @return Um Optional contendo a nota (Integer) se encontrada, ou Optional.empty().
     */
    public Optional<Integer> getAvaliacaoDoUsuario(String personagemIdApi, String jogo, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return Optional.empty(); // Se não há usuário logado, não há avaliação
        }
        return avaliacaoRepository.findByPersonagemIdApiAndJogoAndUsuario(personagemIdApi, jogo, usuarioLogado)
                .map(AvaliacaoPersonagem::getAvaliacao); // Extrai a nota do objeto AvaliacaoPersonagem
    }

    /**
     * Calcula a média de todas as avaliações para um personagem específico de um jogo.
     * @param personagemIdApi ID do personagem na API externa.
     * @param jogo Nome do jogo.
     * @return Um Optional contendo a média (Double) se houver avaliações, ou Optional.empty().
     */
    public Optional<Double> getMediaAvaliacoes(String personagemIdApi, String jogo) {
        return avaliacaoRepository.findAverageRatingByPersonagem(personagemIdApi, jogo);
    }

    /**
     * Método auxiliar para obter o objeto Usuario do usuário atualmente autenticado.
     * @return O objeto Usuario se autenticado e o principal for CustomUserDetails, caso contrário null.
     */
    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Verifica se há autenticação e se o principal é do tipo esperado (CustomUserDetails)
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            // Faz o cast e obtém o objeto Usuario encapsulado
            return ((CustomUserDetails) authentication.getPrincipal()).getUsuario();
        }
        return null; // Retorna null se não houver usuário logado ou o tipo do principal for inesperado
    }

    /**
     * Busca dados para o dashboard de popularidade de Valorant.
     * @return Lista de Object[] contendo [nomePersonagem, totalAvaliacoes, mediaAvaliacoes].
     */
    public List<Object[]> getPopularidadeValorant() {
        return avaliacaoRepository.buscarPopularidadeValorant();
    }

    /**
     * Busca dados para o dashboard de popularidade de League of Legends.
     * @return Lista de Object[] contendo [nomePersonagem, totalAvaliacoes, mediaAvaliacoes].
     */
    public List<Object[]> getPopularidadeLOL() {
        return avaliacaoRepository.buscarPopularidadeLOL();
    }
}