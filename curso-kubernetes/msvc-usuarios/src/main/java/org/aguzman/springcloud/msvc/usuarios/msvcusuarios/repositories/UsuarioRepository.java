package org.aguzman.springcloud.msvc.usuarios.msvcusuarios.repositories;

import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}