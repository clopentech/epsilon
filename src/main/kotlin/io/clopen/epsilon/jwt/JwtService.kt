import java.time.Instant
import java.util.Optional

interface JwtService {
    /**
     * Generates a JWT token for the given user with mandatory claims.
     *
     * @param userId The ID of the user.
     * @param expirationTime The exact expiration time for the token.
     * @param applicationName The application name to be added to the claims.
     * @param companyName The company name to be added to the claims.
     * @return A JWT token as a String.
     */
    fun generateToken(
        userId: String,
        expirationTime: Instant,  // Expiration as Instant
        applicationName: String,
        companyName: String
    ): String

    /**
     * Validates the given JWT token.
     *
     * @param token The JWT token to validate.
     * @return True if the token is valid, otherwise false.
     */
    fun validateToken(token: String): Boolean

    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token The JWT token.
     * @return The user ID extracted from the token, or an empty Optional if invalid.
     */
    fun extractUserId(token: String): Optional<String>
}
