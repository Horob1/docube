package com.horob1.auth_service.service

import com.cloudinary.Cloudinary
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CloudinaryService(
    @Value("\${cloudinary.cloud-name}") private val cloudName: String,
    @Value("\${cloudinary.api-key}") private val apiKey: String,
    @Value("\${cloudinary.api-secret}") private val apiSecret: String,
) {
    private val cloudinary: Cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret,
        )
    )

    /**
     * Upload avatar image to Cloudinary.
     * Uses a fixed public_id based on userId so that re-uploading automatically
     * overwrites the old avatar — no orphaned images.
     *
     * @param file     The image file from the multipart request.
     * @param userId   The user's UUID string, used as the Cloudinary public_id.
     * @return         The HTTPS secure URL of the uploaded image.
     */
    fun uploadAvatar(file: MultipartFile, userId: String): String {
        val options = mapOf(
            "folder" to "docube/avatars",
            "public_id" to "avatar_$userId",
            "overwrite" to true,
            "resource_type" to "image",
        )
        @Suppress("UNCHECKED_CAST")
        val result = cloudinary.uploader().upload(file.bytes, options) as Map<String, Any>
        return result["secure_url"] as String
    }
}
