package me.ckho.scriptscompose.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "register_users")
class UserEntity(
    @Column(unique = true)
    private var username: String,
    private var passwordHash: String,
    private var role: String,
    @Id @GeneratedValue private var id: Long? = null
) : UserDetails {
    override fun toString(): String {
        return "UserEntity(username='$username', passwordHash='$passwordHash', role='$role', id=$id)"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role))
    }

    override fun getPassword(): String {
        return passwordHash
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}