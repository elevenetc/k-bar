package com.elevenetc

import javafx.application.Platform
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.AbstractAction

/**
 * Sample execution to hide window
 * <p>
 * public static void main(String[] args) {
 * <p>
 * System.setProperty("apple.awt.UIElement", "true");
 * <p>
 * SwingUtilities.invokeLater(new Runnable() {
 *
 * @Override public void run() {
 * MenuApp app = new MenuApp();
 * app.addAppToTray();
 * }
 * });
 * }
 */
class MenuApp internal constructor(val title: String) {

    private var trayIcon: TrayIcon? = null
    private var items: List<Item>? = null

    private fun textToImage(Text: String, f: Font, Size: Float): BufferedImage {
        var f = f
        f = f.deriveFont(Size)

        val frc = FontRenderContext(null, true, true)

        val lm = f.getLineMetrics(Text, frc)
        val r2d = f.getStringBounds(Text, frc)
        val img =
            BufferedImage(Math.ceil(r2d.width).toInt(), Math.ceil(r2d.height).toInt(), BufferedImage.TYPE_INT_ARGB)
        val g2d = img.createGraphics()
        g2d.setRenderingHints(RenderingProperties)
        g2d.background = Color(0, 0, 0, 0)
        g2d.color = Color.BLACK

        g2d.clearRect(0, 0, img.width, img.height)
        g2d.font = f
        g2d.drawString(Text, 0f, lm.ascent)
        g2d.dispose()

        return img
    }

    fun show(): SystemTray {
        java.awt.Toolkit.getDefaultToolkit()

        if (!java.awt.SystemTray.isSupported()) {
            println("No system tray support, application exiting.")
            Platform.exit()
        }

        val tray = java.awt.SystemTray.getSystemTray()

        trayIcon = TrayIcon(iconImage(title))

        val openItem = java.awt.MenuItem("hello, world")
        val defaultFont = java.awt.Font.decode(null)
        val boldFont = defaultFont.deriveFont(java.awt.Font.BOLD)
        openItem.font = boldFont

        val exitItem = java.awt.MenuItem("Exit")
        exitItem.addActionListener { event ->
            Platform.exit()
            tray.remove(trayIcon)
        }

        openItem.addActionListener { event ->

        }

        // setup the rootMenu for the application.
        val rootMenu = java.awt.PopupMenu()

        for (item in items!!) {
            addItem(rootMenu, item)
        }

        trayIcon!!.popupMenu = rootMenu
        trayIcon!!.isImageAutoSize = true

        tray.add(trayIcon!!)

        return tray
    }

    private fun addItem(menu: PopupMenu, item: Item) {
        if (item.subItems.isEmpty()) {
            val newItem = MenuItem(item.title)
            newItem.addActionListener(object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent) {
                    item.action()
                }
            })
            menu.add(newItem)
        } else {
            val subMenu = PopupMenu(item.title)
            menu.add(subMenu)

            for (subItem in item.subItems) {
                addItem(subMenu, subItem)
            }
        }
    }

    private fun iconImage(text: String): BufferedImage {
        return textToImage(text, java.awt.Font.decode(null), 13f)
    }

    private fun setItems(items: List<Item>) {

        this.items = items
    }

    public class Builder {

        private val items = LinkedList<Item>()
        private lateinit var title: String

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun addItem(item: Item): Builder {
            items.add(item)
            return this
        }

        fun addItem(
            item: String, action: ()//> bn  0 -> Unit = {}): Builder {
            items.add(Item(item, action))
            return this
        }

        fun build(): MenuApp {
            val result = MenuApp(title)
            result.setItems(items)
            return result
        }

    }

    class Item(internal var title: String, internal var action: () -> Unit = {}) {

        internal var subItems: MutableList<Item> = LinkedList()

        fun addSub(item: Item): Item {
            subItems.add(item)
            return this
        }
    }

    companion object {

        val RenderingProperties = HashMap<RenderingHints.Key, Any>()

        init {
            RenderingProperties[RenderingHints.KEY_TEXT_ANTIALIASING] = RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            RenderingProperties[RenderingHints.KEY_STROKE_CONTROL] = RenderingHints.VALUE_STROKE_PURE
            RenderingProperties[RenderingHints.KEY_FRACTIONALMETRICS] = RenderingHints.VALUE_FRACTIONALMETRICS_ON
            RenderingProperties[RenderingHints.KEY_ALPHA_INTERPOLATION] =
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        }
    }
}
