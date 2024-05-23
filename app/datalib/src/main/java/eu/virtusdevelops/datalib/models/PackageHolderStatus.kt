package eu.virtusdevelops.datalib.models

enum class PackageHolderStatus {
    EMPTY,
    HOLDING_RECEIVED_PACKAGE,
    HOLDING_TO_SEND_PACKAGE,
    WAITING_PACKAGE_INSERT, // when you try to send package (waits for u to insert it so it can weight it)
    WAITING_PACKAGE_DEPOSIT // waiting for package to be delivered
}