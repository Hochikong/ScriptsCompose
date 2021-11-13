import org.junit.jupiter.api.Test
import org.springframework.security.crypto.factory.PasswordEncoderFactories

class GeneratePassword {
    @Test
    fun generate_hash(){
        val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        println(passwordEncoder.encode("~"))
    }
}