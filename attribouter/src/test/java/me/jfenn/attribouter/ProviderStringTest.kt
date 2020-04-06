package me.jfenn.attribouter

import me.jfenn.attribouter.provider.net.ProviderString
import org.junit.Test

class ProviderStringTest {

    @Test
    fun fromSimpleString() {
        val str = ProviderString("fennifith/Attribouter")

        assert(str.provider == "github")
        assert(str.id == "fennifith/Attribouter")
    }

    @Test
    fun fromProviderString() {
        val str = ProviderString("gitlab:fennifith")

        assert(str.provider == "gitlab")
        assert(str.id == "fennifith")
    }

    @Test
    fun fromContextualProviderString() {
        val str = ProviderString("gitea@jfenn.me:fennifith")

        assert(str.provider == "gitea")
        assert(str.context == "jfenn.me")
        assert(str.id == "fennifith")
    }

}
