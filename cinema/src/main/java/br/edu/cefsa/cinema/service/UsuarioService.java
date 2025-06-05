package br.edu.cefsa.cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import para @Transactional

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;

/**
 * Classe de serviço para gerenciar a lógica de negócios relacionada a Usuários.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositório para acesso aos dados de usuário

    /**
     * Salva um novo usuário no banco de dados.
     * A senha do usuário já deve vir criptografada do controller.
     * @param usuario O objeto Usuario a ser salvo.
     */
    @Transactional // Garante que a operação de salvar seja atômica
    public void salvarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // Outros métodos de serviço relacionados a usuários podem ser adicionados aqui
    // Ex: buscarUsuarioPorId, atualizarDadosUsuario (sem ser senha), etc.
}