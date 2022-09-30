package org.aguzman.springcloud.msvc.cursos.services.impl;

import lombok.RequiredArgsConstructor;
import org.aguzman.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.aguzman.springcloud.msvc.cursos.repositories.CursoRepository;
import org.aguzman.springcloud.msvc.cursos.services.CursoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {
    private final CursoRepository cursoRepository;

    private final UsuarioClientRest usuarioClientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return this.cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return this.cursoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> o = this.cursoRepository.findById(id);
        if (o.isPresent()) {
            Curso curso = o.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream().map(co -> co.getUsuarioId()).collect(Collectors.toList());
                List<Usuario> usuarios = usuarioClientRest.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    public Curso guardar(Curso curso) {
        return this.cursoRepository.save(curso);
    }

    @Override
    public void eliminar(Long id) {
        this.cursoRepository.deleteById(id);
    }

    @Override
    public void eliminarCursoUsuarioPorId(Long id) {
        this.cursoRepository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = this.cursoRepository.findById(cursoId);
        if (o.isPresent()) {
            Usuario usuarioDb = this.usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioDb.getId());
            curso.addCursoUsuario(cursoUsuario);
            this.cursoRepository.save(curso);
            return Optional.of(usuarioDb);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = this.cursoRepository.findById(cursoId);
        if (o.isPresent()) {
            Usuario usuarioDb = this.usuarioClientRest.crear(usuario);
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioDb.getId());
            curso.addCursoUsuario(cursoUsuario);
            this.cursoRepository.save(curso);
            return Optional.of(usuarioDb);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = this.cursoRepository.findById(cursoId);
        if (o.isPresent()) {
            Usuario usuarioDb = this.usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioDb.getId());

            curso.removeCursoUsuario(cursoUsuario);
            this.cursoRepository.save(curso);
            return Optional.of(usuarioDb);
        }
        return Optional.empty();
    }
}