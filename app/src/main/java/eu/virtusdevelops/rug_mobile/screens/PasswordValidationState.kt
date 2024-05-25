package eu.virtusdevelops.rug_mobile.screens

data class PasswordValidationState(
    val matching: Boolean = false,
    val hasUpperCaseLetter: Boolean = false,
    val hasLowerCaseLetter: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialCharacter: Boolean = false,
    val hasNumberSequence: Boolean = false,
    val hasDigitSequence: Boolean = false,
    val successful: Boolean = false
)
