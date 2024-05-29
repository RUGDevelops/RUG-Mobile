package eu.virtusdevelops.datalib.models

enum class PackageHolderActionStatus {
    OPEN, // packager gets opened

    DELIVER_PACKAGE, // when delivery guy delivers package to you
    DEPOSIT_PACKAGE, // when you put in package to be delivered
    PACKAGE_TAKEN, // whenever you want to ship package and delivery guy takes it
    PACKAGE_RECEIVED, // when delivery guy delivered package to you and you took it
}