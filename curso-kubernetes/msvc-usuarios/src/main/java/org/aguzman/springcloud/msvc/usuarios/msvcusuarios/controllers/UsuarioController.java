package org.aguzman.springcloud.msvc.usuarios.msvcusuarios.controllers;

import lombok.RequiredArgsConstructor;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.msvcusuarios.services.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(this.usuarioService.listar());
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> detalle(@PathVariable Long id) {
        return this.usuarioService.porId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(this.usuarioService.listarPorIds(ids));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            return this.validar(result);
        }

        if (!usuario.getEmail().isEmpty() && this.usuarioService.existePorEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico!"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.usuarioService.guardar(usuario));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            return this.validar(result);
        }
        Optional<Usuario> o = this.usuarioService.porId(id);
        if (o.isPresent()) {
            Usuario usuarioDb = o.get();

            if (!usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) && this.usuarioService.existePorEmail(usuario.getEmail())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico!"));
            }

            BeanUtils.copyProperties(usuario, usuarioDb, "id");
            return ResponseEntity.ok(this.usuarioService.guardar(usuarioDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errorres = new HashMap<>();
        result.getFieldErrors().forEach(err -> errorres.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errorres);
    }
}