package org.aguzman.springcloud.msvc.cursos.repositories;

import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    @Query("DELETE FROM CursoUsuario cu WHERE cu.usuarioId = :id")
    void eliminarCursoUsuarioPorId(@Param("id") Long id);
}