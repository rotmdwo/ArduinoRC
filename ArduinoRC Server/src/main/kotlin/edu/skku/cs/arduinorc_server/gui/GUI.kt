package edu.skku.cs.arduinorc_server.gui

import edu.skku.cs.arduinorc_server.controller.Controller.Companion.buffer
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
            println("Front")
            buffer.add('g')
        } else if (e?.keyChar == 'a') {
            println("Left")
            buffer.add('l')
        } else if (e?.keyChar == 's') {
            println("Backward")
            buffer.add('b')
        } else if (e?.keyChar == 'd') {
            println("Right")
            buffer.add('r')
        } else if (e?.keyChar == ' ') {
            println("Stop")
            buffer.add('s')
        }
    }

    fun changePicture(picture: ByteArray) {
        val time = System.currentTimeMillis()
        image = ImageIcon(picture)
        button.icon = image
    }
}