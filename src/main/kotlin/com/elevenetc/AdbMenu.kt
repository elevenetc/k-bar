package com.elevenetc

import java.io.IOException

class AdbMenu {
    init {
        //TODO: "avdmanager list avd" see list of current avds
        //TODO: "emulator -avd avd_name" start emulator
        val devices = execute(Command("adb", "devices").withFiler(".+(?=\t)"))

        val builder = MenuApp.Builder().title("adb:" + devices.size)

        val tray = builder
            .addItem(addDevices(devices))
            .addItem(addAVDs())
            .addItem(MenuApp.Item("Exit") { System.exit(0) })
            .build()
            .show()

        Thread {
            while (true) {
                Thread.sleep(1000)
            }
        }.start()
    }

    private fun addAVDs(): MenuApp.Item {
        val sub = MenuApp.Item("AVDs")


        val devices = execute(Command("emulator", "-list-avds"))


        devices.forEach {
            val avdName = it
            val avdItem = MenuApp.Item(avdName)
            avdItem.addSub(MenuApp.Item("Start") {
                Runtime.getRuntime().exec("emulator -avd $avdName")
//                execute(Command("emulator", "-avd", avdName))
            })
            sub.addSub(avdItem)
        }

        return sub
    }

    private fun addDevices(devices: List<String>): MenuApp.Item {

        val sub = MenuApp.Item("Devices")

        for (device in devices)
            sub.addSub(MenuApp.Item(device).addSub(addDeviceReboot(device)))

        return sub
    }

    private fun addDeviceReboot(device: String): MenuApp.Item {
        return MenuApp.Item("Reboot") {

            try {
                //TODO: filter offline devices
                showNotification("adb", "Reboot started")
                execute(Command("adb", "-s", device, "reboot"))
            } catch (e: IOException) {
                showNotification("adb", "Reboot error: " + e.message)
                e.printStackTrace()
            }
        }
    }
}