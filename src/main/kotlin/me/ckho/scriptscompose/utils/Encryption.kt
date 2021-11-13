package me.ckho.scriptscompose.utils

import org.jasypt.util.password.ConfigurablePasswordEncryptor
import java.security.Security

object Encryption {
    private val hashEncryption = ConfigurablePasswordEncryptor().apply {
        setProvider(Security.getProvider("SUN"))
        setAlgorithm("SHA-512")
        setPlainDigest(false)
        setStringOutputType("base64")
    }

    fun encrypt(input: String): String {
        return hashEncryption.encryptPassword(input)
    }

    fun verify(raw: String, encrypted: String): Boolean {
        return hashEncryption.checkPassword(raw, encrypted)
    }
}