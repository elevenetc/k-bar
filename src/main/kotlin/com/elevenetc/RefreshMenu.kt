package com.elevenetc

class RefreshMenu {
    init {

        var counter = 0

        MenuApp.Builder()
            .title("Hello")
            .addItem(MenuApp.Item("Refreshed"), { app, container, added ->
                added.label = "Hello:$counter"
                counter++
            }, 1000)
            .build()
            .show()
    }
}