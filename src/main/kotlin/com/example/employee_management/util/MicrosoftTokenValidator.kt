// File: MicrosoftTokenValidator.kt
package com.example.employee_management.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class MicrosoftTokenValidator {

    private val microsoftKeysUrl = "https://login.microsoftonline.com/ee4a17dd-7304-4c95-8662-53cfd918f9d3/discovery/v2.0/keys"
    private val objectMapper = ObjectMapper()

    fun validateToken(accessToken: String): DecodedJWT {
        println("🧪 Raw accessToken received: $accessToken")
        println("🔐 Starting token validation...") // ✅ [ADDED] Debug log

        val jwt: DecodedJWT = try {
            JWT.decode(accessToken).also {
                println("📜 Decoded JWT: ${it.token}")
            }
        } catch (ex: Exception) {
            println("❌ JWT decoding failed: ${ex.message}")
            throw RuntimeException("Invalid access token format: ${ex.message}")
        }

        println("📜 Decoded JWT: $jwt") // ✅ [ADDED] Debug log
        val keyId = jwt.keyId
        println("🔑 Key ID (kid): $keyId") //
        val email = jwt.getClaim("upn").asString()
        val issuer = jwt.issuer

        println("📄 Decoded JWT -> email: $email") // ✅ [ADDED] Debug log
        println("🧾 keyId (kid): $keyId") // ✅ [ADDED] Debug log
        println("🌍 issuer: $issuer") // ✅ [ADDED] Debug log

        val keysJson = fetchMicrosoftKeys()
        val key = findMatchingKey(keysJson, keyId)

        val publicKey = constructRSAPublicKey(key)

        try {
            val algorithm = Algorithm.RSA256(publicKey, null)
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer) // ✅ [MODIFIED] use decoded issuer for safety
                .build()

            println("🔍 Verifying JWT...") // ✅ [ADDED] Debug log
            val verified = verifier.verify(jwt)
            println("✅ Token verification successful") // ✅ [ADDED] Debug log
            return verified
        } catch (ex: JWTVerificationException) {
            println("❌ Token verification failed: ${ex.message}") // ✅ [ADDED] Debug log
            throw RuntimeException("Invalid Microsoft token: ${ex.message}")
        }
    }

    private fun fetchMicrosoftKeys(): JsonNode {
        println("🌐 Fetching Microsoft public keys...") // ✅ [ADDED] Debug log
        URL(microsoftKeysUrl).openStream().use { inputStream ->
            return objectMapper.readTree(inputStream)
        }
    }

    private fun findMatchingKey(keysJson: JsonNode, keyId: String): JsonNode {
        println("🔎 Looking for matching key with kid: $keyId") // ✅ [ADDED] Debug log
        val keys = keysJson["keys"]
        for (key in keys) {
            if (key["kid"].asText() == keyId) {
                println("✅ Matching key found") // ✅ [ADDED] Debug log
                return key
            }
        }
        println("❌ No matching key found for kid: $keyId") // ✅ [ADDED] Debug log
        throw RuntimeException("No matching keyId found from Microsoft keys")
    }

    private fun constructRSAPublicKey(keyJson: JsonNode): RSAPublicKey {
        println("🔧 Constructing RSA public key...") // ✅ [ADDED] Debug log
        val n = Base64.getUrlDecoder().decode(keyJson["n"].asText())
        val e = Base64.getUrlDecoder().decode(keyJson["e"].asText())

        val modulus = BigInteger(1, n)
        val exponent = BigInteger(1, e)

        val spec = RSAPublicKeySpec(modulus, exponent)
        val factory = KeyFactory.getInstance("RSA")
        return factory.generatePublic(spec) as RSAPublicKey
    }
}
