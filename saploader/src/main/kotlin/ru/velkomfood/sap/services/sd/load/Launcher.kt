package ru.velkomfood.sap.services.sd.load

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import ru.velkomfood.sap.services.sd.load.bus.DataBus
import ru.velkomfood.sap.services.sd.load.inbound.ErpReader
import java.util.*

object AppReader {

    fun readApplicationProperties(): Properties {
        val props = Properties()
        val inStream = javaClass.getResourceAsStream("/app.properties")
        props.load(inStream)
        inStream.close()
        return props
    }

}

fun main() = runBlocking {

    println("Start the data reading...")

    val bus = DataBus()
    val params = AppReader.readApplicationProperties()
    val logger = LoggerFactory.getLogger("saploader.logger")
    val erp = ErpReader(params, bus, logger)

    val inputJob = GlobalScope.launch {
        erp.readMaterials()
        erp.readAccounts()
    }

    val outputJob = GlobalScope.launch {
        for (material in bus.materialChannel) {

        }
        for (account in bus.accountChannel) {
            println(account.toString())
        }
    }

    inputJob.join();
    outputJob.join();

}
