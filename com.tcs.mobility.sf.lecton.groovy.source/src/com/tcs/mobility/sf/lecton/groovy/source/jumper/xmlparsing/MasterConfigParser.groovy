package com.tcs.mobility.sf.lecton.groovy.source.jumper.xmlparsing

import groovy.xml.DOMBuilder
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.util.regex.Matcher
import java.util.regex.Pattern

class MasterConfigParser {

    private Document doc
    def rootNodeName

    /** Pattern to match 'section' tag in masterConfig file */
    public static final String SF_MASTER_SECTION_OPEN = "<(?:\\s)*section(?:\\s)+path(?:\\s)*=\"([^\"]*)\""

    public static final String SF_MASTER_SECTION_CLOSE = "</section>"

    private static final Pattern PATTERN_MASTER_SECTION_OPEN = Pattern.compile(SF_MASTER_SECTION_OPEN)
    private static final Pattern PATTERN_MASTER_SECTION_CLOSE = Pattern.compile(SF_MASTER_SECTION_CLOSE)


    public MasterConfigParser() {
        // TODO Auto-generated constructor stub

    }

    public static void main(String[] args) {
        def obj = new MasterConfigParser()
        File file = new File('test/masterConfig.xml')

        //obj.run(file.getText(), "sf.security.permission")
        obj.check(file.getText(), "sf.security.permission")
    }

    private check(String content, String key) {
        println key
        String[] keys = key.split("\\.")

        List<String> sectionKeys = keys.toList();
        String configKey = sectionKeys.pop();

        Matcher sectionOpenMatcher = PATTERN_MASTER_SECTION_OPEN.matcher(content)
        Matcher sectionCloseMatcher = PATTERN_MASTER_SECTION_CLOSE.matcher(content)

        boolean found = false
        int sectionEnd = 0;

        content.eachLine {
            if (!found) {
                //println "OO : $it"
                if (sectionOpenMatcher.find()) {
                    String sectionPart = sectionOpenMatcher.group()

                    if (sectionPart.equalsIgnoreCase(sectionKeys.get(0))) {

                    } else {
                        sectionEnd++;
                    }
                }

            }
        }


        sectionKeys.each {
            println it
        }

        println configKey

    }


    private run(String content, String key) {
        println key

        def reader = new StringReader(content)
        def doc = DOMBuilder.parse(reader)
        Element root = doc.documentElement

        // Sets the root name, so that this is can be used while building the xml
        rootNodeName = root.getNodeName()

        println rootNodeName

        NodeList list = root.getChildNodes()
        for (Node node in list) {
            println node.getNodeName()
            println nodeToString(node)
        }
    }


    private String nodeToString(Node node) {
        StringWriter sw = new StringWriter()
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer()
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")

            t.transform(new DOMSource(node), new StreamResult(sw))
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception")
        }
        return sw.toString()
    }
}
