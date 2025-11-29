package med.voll.web_application.domain.usuario;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name="usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Boolean senhaAlterada;
    @Enumerated(EnumType.STRING)
    private Perfil perfil;
    private String token;
    private LocalDateTime expiracao_token;

    public Usuario() {}

    public Usuario(String nome, String email, String senha, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.senhaAlterada = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }
    public Perfil getPerfil() {
        return perfil;
    }
    public void alterarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }
    public Boolean getSenhaAlterada() {
        return senhaAlterada;
    }

    public void setSenhaAlterada(Boolean senhaAlterada) {
        this.senhaAlterada = senhaAlterada;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiracaoToken(LocalDateTime localDateTime) {
        this.expiracao_token = localDateTime;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiracaoToken() {
        return expiracao_token;
    }

}
