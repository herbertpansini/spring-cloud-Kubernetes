package org.aguzman.springcloud.msvc.cursos.models.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@EqualsAndHashCode
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curso_id")
    private List<CursoUsuario> cursoUsuarios = new ArrayList<>();

    @Transient
    private List<Usuario> usuarios = new ArrayList<>();

    public void addCursoUsuario(CursoUsuario cursoUsuario) {
        this.cursoUsuarios.add(cursoUsuario);
    }

    public void removeCursoUsuario(CursoUsuario cursoUsuario) {
        this.cursoUsuarios.remove(cursoUsuario);
    }
}