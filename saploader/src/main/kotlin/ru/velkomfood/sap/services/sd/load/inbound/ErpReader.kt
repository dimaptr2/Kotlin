package ru.velkomfood.sap.services.sd.load.inbound

import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoDestinationManager
import com.sap.conn.jco.JCoException
import org.slf4j.Logger
import ru.velkomfood.sap.services.sd.load.bus.Account
import ru.velkomfood.sap.services.sd.load.bus.DataBus
import ru.velkomfood.sap.services.sd.load.bus.Material
import java.util.*

object AlphaTransformator {

    fun transform(sz: Int, txtValue: String): String {
        var newValue = ""
        val delta = sz - txtValue.length
        for (idx in 0 until delta) {
            newValue += "0"
        }
        newValue += txtValue
        return newValue
    }

}

class ErpReader(val parameters: Properties, val dataBus: DataBus, val logger: Logger) {


    suspend fun readMaterials() {

        try {
            val destination = createDestination()
            val bapiMatList = destination.repository.getFunction("BAPI_MATERIAL_GETLIST")
            val selection = bapiMatList.tableParameterList.getTable("MATNRSELECTION")
            selection.appendRow()
            selection.setValue("SIGN", "I")
            selection.setValue("OPTION", "BT")
            val lowId = AlphaTransformator.transform(18, parameters.getProperty("sap.material.low"))
            val highId = AlphaTransformator.transform(18, parameters.getProperty("sap.material.high"))
            selection.setValue("MATNR_LOW", lowId)
            selection.setValue("MATNR_HIGH", highId)
            bapiMatList.execute(destination)
            val resTab = bapiMatList.tableParameterList.getTable("MATNRLIST")
            if (resTab.numRows > 0) {
                do {
                    val id = resTab.getLong("MATERIAL")
                    val description = resTab.getString("MATL_DESC")
                    val material = Material(id, description)
                    dataBus.materialChannel.send(material)
                } while (resTab.nextRow())
                dataBus.materialChannel.close()
            }
        } catch (ex: JCoException) {
            logger.error(ex.message)
        }

    }

    suspend fun readAccounts() {

        try {
            val destination = createDestination()
            val bapiAccounts = destination.repository.getFunction("BAPI_GL_ACC_GETLIST")
            bapiAccounts.importParameterList.setValue("COMPANYCODE", parameters.getProperty("sap.company"))
            bapiAccounts.importParameterList.setValue("LANGUAGE", parameters.getProperty("RU"))
            bapiAccounts.execute(destination)
            val accountList = bapiAccounts.tableParameterList.getTable("ACCOUNT_LIST")
            if (accountList.numRows > 0) {
                do {
                    val id = accountList.getString("GL_ACCOUNT")
                    val desc = accountList.getString("LONG_TEXT")
                    val account = Account(id, desc)
                    dataBus.accountChannel.send(account)
                } while (accountList.nextRow())
                dataBus.accountChannel.close()
            }
        } catch (ex: JCoException) {
            logger.error(ex.message)
        }

    }

    // private section
    private fun createDestination(): JCoDestination {
        return JCoDestinationManager.getDestination(parameters.getProperty("sap.destination"))
    }

}