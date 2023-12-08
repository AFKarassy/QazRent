package com.example.qazrent

class User {
    var firstName: String? = null
    var name: String? = null
    var lastName: String? = null
    var sex: String? = null
    var iin: String? = null
    var administrator: Boolean? = false // добавил булевые значения для определения типа пользователя
    var renter: Boolean? = false
    var tenant: Boolean? = false

    constructor() {
        // Пустой конструктор требуется для Firebase
    }
    // Добавил варианты типа в конструктор
    constructor(firstName: String?, name: String?, lastName: String?, sex: String?, iin: String?, administrator: Boolean?, renter: Boolean?, tenant: Boolean? ) {
        this.firstName = firstName
        this.name = name
        this.lastName = lastName
        this.sex = sex
        this.iin = iin
        this.administrator = administrator
        this.renter = renter
        this.tenant = tenant
    }
}
