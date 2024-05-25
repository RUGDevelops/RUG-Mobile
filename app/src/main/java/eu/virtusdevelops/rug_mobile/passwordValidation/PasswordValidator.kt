package eu.virtusdevelops.rug_mobile.passwordValidation

import android.util.Log

object PasswordValidator {

    fun execute(password: String, repeatPassword: String): PasswordValidationState {
        val validateMatch = validateMatching(password, repeatPassword)
        val validateUpperCase = validateUpperCaseLetter(password)
        val validateLowerCase = validateLowerCaseLetter(password)
        val validateDigit = validateDigit(password)
        val validateSpecialCharacter = validateSpecialCharacter(password)

        val hasError = listOf(
            validateMatch,
            validateUpperCase,
            validateLowerCase,
            validateDigit,
            validateSpecialCharacter
        ).all { it }

        Log.d("PASSWORD_VALIDATOR", "$hasError $password $repeatPassword")

        return PasswordValidationState(
            matching = validateMatch,
            hasUpperCaseLetter = validateUpperCase,
            hasLowerCaseLetter = validateLowerCase,
            hasDigit = validateDigit,
            hasSpecialCharacter = validateSpecialCharacter,
            successful = hasError
        )
    }

    private fun validateMatching(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword && password.isNotEmpty()
    }

    private fun validateUpperCaseLetter(password: String): Boolean {
        return password.contains(Regex("[A-Z]"))
    }

    private fun validateLowerCaseLetter(password: String): Boolean {
        return password.contains(Regex("[a-z]"))
    }

    private fun validateDigit(password: String): Boolean {
        return password.contains(Regex("[0-9]"))
    }

    private fun validateSpecialCharacter(password: String): Boolean {
        return password.contains(Regex("[^A-Za-z0-9]"))
    }

    //TODO: Number and letter sequence validation
}