package org.aguzman.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/curso")
@RequiredArgsConstructor
public class CursoController {
    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(this.cursoService.listar());
    }

    @GetMapping("{id}")
    public ResponseEntity<Curso> detalle(@PathVariable Long id) {
        return this.cursoService.porIdConUsuarios(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            return this.validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cursoService.guardar(curso));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            return this.validar(result);
        }
        Optional<Curso> o = this.cursoService.porId(id);
        if (o.isPresent()) {
            Curso cursoDb = o.get();
            BeanUtils.copyProperties(curso, cursoDb, "id");
            return ResponseEntity.ok(this.cursoService.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@PathVariable Long cursoId, @RequestBody Usuario usuario) {
        Optional<Usuario> o;
        try {
            o = this.cursoService.asignarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("Mensaje", "No existe el usuario por " +
                    "el id o error en la comunicacion: " + e.getMessage()));
        }

        if (o.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@PathVariable Long cursoId, @RequestBody Usuario usuario) {
        Optional<Usuario> o;
        try {
            o = this.cursoService.crearUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("Mensaje", "No se pudo crear usuario " +
                    "o error en la comunicacion: " + e.getMessage()));
        }

        if (o.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long cursoId, @RequestBody Usuario usuario) {
        Optional<Usuario> o;
        try {
            o = this.cursoService.eliminarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("Mensaje", "No existe el usuario por " +
                    "el id o error en la comunicacion: " + e.getMessage()));
        }

        if (o.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
        this.cursoService.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errorres = new HashMap<>();
        result.getFieldErrors().forEach(err -> errorres.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errorres);
    }
}