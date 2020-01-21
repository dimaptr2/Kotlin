package ru.velkomfood.sap.services.sd.load.bus

import kotlinx.coroutines.channels.Channel

class DataBus {

    val materialChannel = Channel<Material>()
    val accountChannel = Channel<Account>()

}