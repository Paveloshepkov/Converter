package com.example.converter.network

import com.example.converter.database.DatabaseValute
import com.example.converter.domain.Valute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
data class NetworkValuteContainer(
    @field:ElementList(name = "Valute", inline = true)
    var valute: List<NetworkValute>? = null
)

@Root(name = "Valute", strict = false)
data class NetworkValute(
    @field:Element(name = "NumCode")
    var numCode: String? = null,
    @field:Element(name = "CharCode")
    var charCode: String? = null,
    @field:Element(name = "Nominal")
    var nominal: String? = null,
    @field:Element(name = "Name")
    var name: String? = null,
    @field:Element(name = "Value")
    var value: String? = null
)

fun NetworkValuteContainer.asDomainModel(): List<Valute>? {
    return valute?.map {
        Valute(
            numCode = it.numCode!!,
            charCode = it.charCode!!,
            nominal = it.nominal!!,
            name = it.name!!,
            value = it.value!!
        )
    }
}

fun NetworkValuteContainer.asDatabaseModel(): List<DatabaseValute>? {
    return valute?.map {
        DatabaseValute(
            numCode = it.numCode!!,
            charCode = it.charCode!!,
            nominal = it.nominal!!,
            name = it.name!!,
            value = it.value!!
        )
    }
}