package com.ivieleague.testbluetooth

import com.lightningkite.kotlin.networking.gsonFrom
import com.lightningkite.kotlin.networking.gsonToString
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.bluetooth.UUID
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnectionNotifier

val uuid = UUID("00001101-0000-1000-8000-00805F9B34FB".filter { it.isLetterOrDigit() }, false)

fun main(vararg args: String) {
    println("HI")

    val methods = listOf(
            Method(
                    name = "test",
                    description = "A test method.",
                    arguments = listOf()
            )
    )

    LocalDevice.getLocalDevice().discoverable = DiscoveryAgent.GIAC
    val notifier = Connector.open("btspp://localhost:" + uuid + ";name=RemoteBluetooth") as StreamConnectionNotifier
    while (true) {
        try {
            System.out.println("Waiting for connection...")
            val connection = notifier.acceptAndOpen()
            val output = connection.openOutputStream()
            val input = connection.openInputStream()

            System.out.println("Writing methods... (${methods.gsonToString()})")
            output.writer().apply {
                appendln(methods.gsonToString())
                flush()
            }

            while (true) {
                val request = input.bufferedReader().readLine().gsonFrom<Request>()
                System.out.println("Request received... ($request)")
                if (request != null && request.method == "test") {
                    output.writer().apply {
                        appendln(Response(
                                id = 0,
                                result = "Hooray!",
                                error = null
                        ).gsonToString())
                        flush()
                    }
                } else {
                    output.writer().apply {
                        appendln(Response(
                                id = 0,
                                result = null,
                                error = "NOPE"
                        ).gsonToString())
                        flush()
                    }
                }
            }

//            output.close()
//            input.close()
//            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}