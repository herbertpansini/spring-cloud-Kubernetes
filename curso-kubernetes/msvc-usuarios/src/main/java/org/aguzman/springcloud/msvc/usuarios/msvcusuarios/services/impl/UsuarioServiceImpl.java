package org.aguzman.springcloud.msvc.usuarios.msvcusuarios.services.impl;

import lombok.RequiredArgsConstructor;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.clients.CursoClientRest;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.repositories.UsuarioRepository;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.services.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    private final CursoClientRest cursoClientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return this.usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> porId(Long id) {
        return this.usuarioRepository.findById(id);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        this.usuarioRepository.deleteById(id);
        this.cursoClientRest.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorIds(Iterable<Long> ids) {
        return this.usuarioRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> porEmail(String email) {
        return this.usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return this.usuarioRepository.existsByEmail(email);
    }
}