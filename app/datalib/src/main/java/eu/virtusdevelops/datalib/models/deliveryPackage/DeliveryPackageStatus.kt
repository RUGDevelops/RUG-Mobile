package eu.virtusdevelops.datalib.models.deliveryPackage

enum class DeliveryPackageStatus {
    CREATED,
    WAITING_IN_PACKAGE_HOLDER, // when u send the package
    PICKED_UP_BY_DELIVERY, // when delivery guy picks up the package
    ON_ROUTE, // when delivery is in progress
    DELIVERED, // when package is waiting for recipient in package box
    CLAIMED // when it was claimed
}