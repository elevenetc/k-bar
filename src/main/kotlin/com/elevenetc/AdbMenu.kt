package com.elevenetc

import java.io.IOException

class AdbMenu {
    init {
        //TODO: "avdmanager list avd" see list of current avds
        //TODO: "emulator -avd avd_name" start emulator

        val devices = execute(Command("adb", "devices").withFiler(".+(?=\t)"))

        val builder = MenuApp.Builder().title("adb:" + devices.size)

        val devicesMenu = MenuApp.Item("Devices")


        for (device in devices) {

            val deviceItem = MenuApp.Item(device)

            deviceItem.addSub(MenuApp.Item("Reboot") {

                try {
                    //TODO: filter offline devices
                    showNotification("adb", "Reboot started")
                    execute(Command("adb", "-s", device, "reboot"))
                } catch (e: IOException) {
                    showNotification("adb", "Reboot error: " + e.message)
                    e.printStackTrace()
                }
            })

            devicesMenu.addSub(deviceItem)
        }

        builder
            .addItem(devicesMenu)
            .addItem("Show") {
                showNotification("Hello", "Custom")
            }
            .build()
            .show()
    }
}