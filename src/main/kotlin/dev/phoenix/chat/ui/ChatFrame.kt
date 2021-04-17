package dev.phoenix.chat.ui

import com.formdev.flatlaf.FlatDarkLaf
import dev.phoenix.chat.Client
import dev.phoenix.chat.ui.TabCloseCallbackHolder.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.*
import java.util.function.BiConsumer
import java.util.function.IntConsumer
import javax.swing.*
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultCaret
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit

/**
 * @author unknown
 */
class ChatFrame : JFrame() {
    var tabsByName: MutableMap<String, Component> = HashMap()

    fun destroyTabs() {
        while (tabbedPane.tabCount > 0)
        {
            tabbedPane.remove(0)
        }
        tabsByName = HashMap()
    }

    fun createTabWithName(name: String) {
        val tab = TabPanel(name)
        tabbedPane.addTab(name, tab)
        tabsByName[name] = tab
    }

    fun addLineToTabWithName(name: String?, message: String) {
        // :vomit:
        // this is a bit more complex than i'd like it to be, but hopefully it's
        //      at least fairly efficient?

        if (!tabsByName.containsKey(name) && name != null) // could this be elsewhere?
            createTabWithName(name)
            

        val formatted = StringBuilder() // This is the final string we'll be adding
        var didntFindColorCode = true                // If this stays true, we didn't hit a format string
        var i = 0                       // character index in original string
        var skip = false                // When this flips to true, we don't process the next character in the string.
                                        //      (when we already processed it bc it was a color code)


        for (token in message.toCharArray()) {
            if (token == '\u00A7') { // "Section Symbol", denotes a color code in vanilla minecraft.

                if (didntFindColorCode) 
                    didntFindColorCode = false // We've hit a color code, so permanently set 'didntFindColorCode' to false
                else 
                    formatted.append("</span>") // This color code will override the last, so end the last section here.
                                                //      if this is our first color code, we dont add this.
                
                skip = true
                val color = message.toCharArray()[i + 1]
                when (color) {
                    '0' -> formatted.append("<span style=\"color:#000000\">")
                    '1' -> formatted.append("<span style=\"color:#0000AA\">")
                    '2' -> formatted.append("<span style=\"color:#00AA00\">")
                    '3' -> formatted.append("<span style=\"color:#00AAAA\">")
                    '4' -> formatted.append("<span style=\"color:#AA0000\">")
                    '5' -> formatted.append("<span style=\"color:#AA00AA\">")
                    '6' -> formatted.append("<span style=\"color:#FFAA00\">")
                    '7' -> formatted.append("<span style=\"color:#AAAAAA\">")
                    '8' -> formatted.append("<span style=\"color:#555555\">")
                    '9' -> formatted.append("<span style=\"color:#5555FF\">")
                    'a' -> formatted.append("<span style=\"color:#55FF55\">")
                    'b' -> formatted.append("<span style=\"color:#55FFFF\">")
                    'c' -> formatted.append("<span style=\"color:#FF5555\">")
                    'd' -> formatted.append("<span style=\"color:#FF55FF\">")
                    'e' -> formatted.append("<span style=\"color:#FFFF55\">")
                    'f' -> formatted.append("<span style=\"color:#FFFFFF\">")
                    else ->                         //TODO: hijack something and render this properly or w/e
                        formatted.append("<span style=\"color:#FFFFFF\">")
                }
            } 
            else { // not a section character
                if (skip) // if it came directly after a section character, skip
                    skip = false 
                else {
                    when (token) { // we need to escape these for html
                        '<' -> formatted.append("&lt;")
                        '>' -> formatted.append("&gt;")
                        '&' -> formatted.append("&amp;")
                        else ->  // just a normal character, then
                            formatted.append(token)
                    }
                }
            }
            i++
        }

        if (!didntFindColorCode) // make sure we close things up.
            formatted.append("</span>")
        
        // add the string now.
        val tab = tabsByName[name] as TabPanel? 
        tab!!.appendToChatPane(formatted.toString())
    }

    // rest of this file is autogenerated, not much worth looking at here.

    private fun initComponents() {


        //======== this ========
        val contentPane = contentPane

        //======== tabbedPane1 ========
        run {}
        val contentPaneLayout = GroupLayout(contentPane)
        contentPane.layout = contentPaneLayout
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(tabbedPane)
        )
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(tabbedPane)
        )
        pack()
        setLocationRelativeTo(owner)
        preferredSize = Dimension(600, 400)
        setSize(600, 400)

        tabbedPane.putClientProperty("JTabbedPane.tabClosable", true)

        tabbedPane.putClientProperty( "JTabbedPane.tabCloseCallback",
            consumer
        )
    }

    val tabbedPane = JTabbedPane()

    private inner class TabPanel(private val contextName: String) : JPanel() {
        fun appendToChatPane(string: String?) {
            val doc = chatEditorPane!!.document as HTMLDocument
            val editorKit = chatEditorPane!!.editorKit as HTMLEditorKit
            try {
                editorKit.insertHTML(doc, doc.length, string, 0, 0, null)
            } catch (e: BadLocationException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            chatEditorPane!!.caretPosition = chatEditorPane!!.document.length
        }

        private fun initComponents() {
            scrollPane1 = JScrollPane()
            chatEditorPane = JEditorPane()
            textArea1 = JTextField()
            scrollPane2 = JScrollPane()
            memberEditorPane = JEditorPane()

            //======== this ========


            //======== scrollPane1 ========
            run {
                scrollPane1!!.border = BorderFactory.createEmptyBorder()

                //---- editorPane2 ----
                chatEditorPane!!.border = null
                chatEditorPane!!.isEditable = false
                chatEditorPane!!.contentType = "text/html"
                scrollPane1!!.setViewportView(chatEditorPane)
                scrollPane1!!.autoscrolls = true
                val caret = chatEditorPane!!.caret as DefaultCaret
                caret.updatePolicy = DefaultCaret.ALWAYS_UPDATE
            }

            //======== scrollPane2 ========
            run {
                scrollPane2!!.border = null
                scrollPane2!!.setViewportView(memberEditorPane)
            }
            layout = GridBagLayout()
            val c = GridBagConstraints()
            c.fill = GridBagConstraints.BOTH
            c.gridx = 0
            c.gridy = 0
            c.weightx = 1.0
            c.weighty = 1.0
            c.anchor = GridBagConstraints.PAGE_START
            c.insets = Insets(10, 10, 0, 10)
            add(scrollPane1!!, c)
            textArea1!!.maximumSize = Dimension(Int.MAX_VALUE,
                    30
            )
            c.gridy = 1
            c.weighty = 0.0
            c.insets = Insets(10, 10, 10, 10)
            c.fill = GridBagConstraints.HORIZONTAL
            c.anchor = GridBagConstraints.PAGE_END
            add(textArea1!!, c)
            val action: Action = object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent) {
                    val msg = textArea1!!.text
                    Client.currentServer?.sendChatFromTab((textArea1!!.parent as TabPanel).contextName, msg)
                    textArea1!!.text = ""
                }
            }
            textArea1!!.addActionListener(action)
        }

        private var scrollPane1: JScrollPane? = null
        private var chatEditorPane: JEditorPane? = null
        private var textArea1: JTextField? = null
        private var scrollPane2: JScrollPane? = null
        private var memberEditorPane: JEditorPane? = null

        init {
            initComponents()
        }
    }

    init {
        FlatDarkLaf.install()
        println(UIManager.getLookAndFeel().name)
        initComponents()
        title = "Chat"
        val icon: Image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE)
        iconImage = icon
        SwingUtilities.updateComponentTreeUI(contentPane)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        this.isVisible = true
    }
}