package com.elevenetc

import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    System.setProperty("apple.awt.UIElement", "true")

    SwingUtilities.invokeLater({
        MenuApp.Builder().title("Hello").build().show()
    })
}