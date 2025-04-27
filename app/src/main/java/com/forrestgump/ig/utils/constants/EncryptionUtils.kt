package com.forrestgump.ig.utils.constants

import android.util.Base64
import android.util.Log
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionUtils @Inject constructor() {
    companion object {
        private const val TAG = "EncryptionUtils"
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

        private val SECRET_KEY = byteArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
            0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20
        )
    }


    fun encrypt(plainText: String): String {
        try {
            val secretKeySpec = SecretKeySpec(SECRET_KEY, ALGORITHM)

            // Create Cipher instance
            val cipher = Cipher.getInstance(TRANSFORMATION)

            // Generate random IV (Initialization Vector)
            val ivBytes = ByteArray(16) // 16 bytes for AES
            val random = SecureRandom()
            random.nextBytes(ivBytes)
            val ivSpec = IvParameterSpec(ivBytes)

            // Initialize cipher with key and IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)

            // Encrypt the data
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Combine IV and encrypted data
            val combined = ByteArray(ivBytes.size + encryptedBytes.size)
            System.arraycopy(ivBytes, 0, combined, 0, ivBytes.size)
            System.arraycopy(encryptedBytes, 0, combined, ivBytes.size, encryptedBytes.size)

            // Encode as Base64 string
            val encryptedBase64 = Base64.encodeToString(combined, Base64.NO_WRAP)

            return encryptedBase64

        } catch (e: Exception) {
            return "ENCRYPTION_FAILED"
        }
    }

    /**
     * Decrypt an AES encrypted string
     * @param encryptedText Base64 encoded encrypted string with IV prefixed
     * @return The decrypted plain text
     */
    fun decrypt(encryptedText: String): String {
        Log.d(TAG, "Starting decryption of text with length: ${encryptedText.length}")

        if (encryptedText == "ENCRYPTION_FAILED" || encryptedText.isEmpty()) {
            Log.e(TAG, "Cannot decrypt - input indicates encryption failure or is empty")
            return "[Decryption Failed]"
        }

        try {
            // Log key size for debugging
            Log.d(TAG, "Key length: ${SECRET_KEY.size} bytes")

            // Create a SecretKeySpec with the fixed 32-byte key
            val secretKeySpec = SecretKeySpec(SECRET_KEY, ALGORITHM)

            // Decode the Base64 string
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

            // Extract IV (first 16 bytes)
            val ivBytes = ByteArray(16)
            System.arraycopy(combined, 0, ivBytes, 0, ivBytes.size)
            val ivSpec = IvParameterSpec(ivBytes)

            // Extract encrypted data (remaining bytes)
            val encryptedBytes = ByteArray(combined.size - ivBytes.size)
            System.arraycopy(combined, ivBytes.size, encryptedBytes, 0, encryptedBytes.size)

            // Create Cipher instance
            val cipher = Cipher.getInstance(TRANSFORMATION)

            // Initialize cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)

            // Decrypt
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            // Convert to string
            val plainText = String(decryptedBytes, Charsets.UTF_8)

            Log.d(TAG, "Decryption successful. Result length: ${plainText.length}")
            return plainText

        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed", e)
            return "[Decryption Failed]"
        }
    }
}