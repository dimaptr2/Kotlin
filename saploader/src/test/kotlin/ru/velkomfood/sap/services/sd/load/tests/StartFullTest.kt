package ru.velkomfood.sap.services.sd.load.tests

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import ru.velkomfood.sap.services.sd.load.AppReader
import ru.velkomfood.sap.services.sd.load.bus.DataBus
import ru.velkomfood.sap.services.sd.load.inbound.AlphaTransformator
import ru.velkomfood.sap.services.sd.load.inbound.ErpReader
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StartFullTest {

    @Test
    fun readDataTest() {

        val bus = DataBus()
        assertNotNull(bus)
        val params = AppReader.readApplicationProperties()
        assertNotNull(params)
        assertFalse { params.isEmpty }
        val log = LoggerFactory.getLogger("test.logger")
        assertNotNull(log)
        val reader = ErpReader(params, bus, log)
        assertNotNull(reader)
        val idLow = AlphaTransformator.transform(18, params.getProperty("sap.material.low"))
        assertTrue { idLow.length == 18 }
        val idHigh = AlphaTransformator.transform(18, params.getProperty("sap.material.high"))
        assertTrue { idHigh.length == 18 }
        GlobalScope.launch {
            reader.readMaterials()
        }

    }

}