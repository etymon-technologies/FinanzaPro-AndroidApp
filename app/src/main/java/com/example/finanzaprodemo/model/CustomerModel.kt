
data class CustomerResponse(
    val data: List<CustomerModel>,
    val pagination: Pagination
)

data class CustomerModel(
    val id: String,
    val name: String,
    val email: String?= null,
    val identificationNumber: String? = null,
    val identificationType: String? = null,
    val legalName: String? = null,
    val phone1: String?= null,
    val notes: String? = null,
    val state: String,
    val address: Address,
    val shippingAddresses: List<Address> = emptyList()
)

data class Address(
    val addressLines: String? = null,
    val country: Country? = null,
    val place: Place? = null
)

data class Country(
    val code: String,
    val name: String
)

data class Place(
    val adminCode: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val placeType: String? = null,
    val admin1Place: AdminPlace? = null,
    val admin2Place: AdminPlace? = null
)

data class AdminPlace(
    val adminCode: String,
    val id: Int,
    val name: String
)

data class Pagination(
    val count: Int,
    val next: String? = null,
    val page: Int,
    val perPage: Int
)
