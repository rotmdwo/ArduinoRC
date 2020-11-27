package edu.skku.cs.arduinorc_server.gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

class GUI(): KeyListener {
    //private var image = ImageIcon("/Users/sungjaelee/Desktop/스크린샷 2020-10-02 오후 7.30.28.png")
    private var image: ImageIcon? = null
    val frame = JFrame()
    private val panel = JPanel()
    private val button = JButton()

    init {
        button.icon = image
        button.addKeyListener(this)

        panel.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        panel.layout = GridLayout(1, 1)
        panel.add(button)

        frame.add(panel, BorderLayout.CENTER)
        frame.preferredSize = Dimension(756, 1008)
        frame.setSize(756, 1008)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.title = "Window"
        frame.pack()
        frame.validate()
        frame.isVisible = true

    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {

    }

    override fun keyReleased(e: KeyEvent?) {
        if (e?.keyChar == 'w') {
            println("key: 'w'")
            image = ImageIcon("/Users/sungjaelee/Desktop/스크린샷 2020-10-02 오후 7.30.28.png")
            button.icon = image
        } else if (e?.keyChar == 'a') {
            println("key: 'a'")
        } else if (e?.keyChar == 's') {
            println("key: 's'")
            image = ImageIcon("/Users/sungjaelee/Desktop/스크린샷 2020-10-02 오후 7.29.46.png")
            button.icon = image
        } else if (e?.keyChar == 'd') {
            println("key: 'd'")
        } else if (e?.keyChar == ' ') {
            println("key: ' '")
        }
    }

    fun changePicture(picture: ByteArray) {
        image = ImageIcon(picture)
        var img = image?.image
        img = img?.getScaledInstance(756,1008, java.awt.Image.SCALE_SMOOTH)
        image = ImageIcon(img)
        button.icon = image
    }
}