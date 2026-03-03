package com.superappzw.services

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object FirebaseErrorMapper {

    fun mapError(error: Throwable): String {
        // Cast to FirebaseAuthException to check error codes
        val firebaseError = when {
            error is FirebaseAuthException -> error
            error.cause is FirebaseAuthException -> error.cause as FirebaseAuthException
            else -> null
        }

        return when {
            // Email validation errors
            error is FirebaseAuthInvalidCredentialsException ->
                "The email address is badly formatted."

            error is FirebaseAuthUserCollisionException ->
                "This email is already registered. Try signing in."

            error.message?.contains("The password is invalid") == true ->
                "Incorrect password. Please try again."

            error is FirebaseAuthInvalidUserException ->
                "No account found with this email. Please sign up first."

            // Network errors
            error is FirebaseNetworkException ->
                "Network error. Please check your connection."

            error.message?.contains("TOO_MANY_REQUESTS") == true ||
                    error is FirebaseTooManyRequestsException ->
                "Too many attempts. Please try again later."

            error.message?.contains("invalid-credential") == true ||
                    error.message?.contains("credential-already-in-use") == true ->
                "The sign-in credentials are invalid or have expired. Please try again."

            error.message?.contains("account-exists-with-different-credential") == true ->
                "An account exists with the same email but a different sign-in method. Try signing in with that provider."

            error.message?.contains("operation-not-allowed") == true ->
                "This sign-in method is not allowed. Contact support."

            error.message?.contains("user-disabled") == true ->
                "This account has been disabled. Contact support."

            // Generic token/session errors
            error.message?.contains("token") == true ||
                    error.message?.contains("session") == true ->
                "Your session expired. Please sign in again."

            // Default fallback for all other errors
            else -> "An unknown error occurred. Please try again."
        }
    }
}
