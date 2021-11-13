package me.ckho.scriptscompose.deprecated.service

interface AuthService{
    fun isAuthPass(username: String, password: String): Boolean
}