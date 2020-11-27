package edu.skku.cs.arduinorc_server

import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

class GUI(): KeyListener {
    var image = ImageIcon("/Users/sungjaelee/Desktop/스크린샷 2020-10-02 오후 7.30.28.png")
    val button = JButton()

    init {
        val frame = JFrame()

        button.icon = image
        button.addKeyListener(this)

        val panel = JPanel()
        panel.border = BorderFactory.createEmptyBorder(30, 30, 10, 30)
        panel.layout = GridLayout(0, 1)
        panel.add(button)

        frame.add(panel, BorderLayout.CENTER)
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

}