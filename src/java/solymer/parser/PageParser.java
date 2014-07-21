package solymer.parser;

import solymer.data.SolymerComponent;
import solymer.data.SolymerComponentInstance;
import solymer.data.SolymerPage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by nicholaszhao on 7/18/14.
 */
public class PageParser {

    private static Map<String, SolymerComponent> components;
    private static Random random = new Random();

    public static Document resolvePage(final SolymerPage page) throws IOException {
        final Stack<Node> stack = new Stack<Node>();
        Element root = new Element(Tag.valueOf("root"), "/", new Attributes());
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("<script>\n");

        final Set<String> parsedComponents = new HashSet<String>();
        final Set<SolymerComponentInstance> componentInstances = new HashSet<SolymerComponentInstance>();
        stack.push(root);
        page.getDocument().traverse(new NodeVisitor() {

            int careLevel = Integer.MAX_VALUE;

            @Override
            public void head(Node node, int depth) {
                if (depth > careLevel) {
                    return;
                }
                if (node instanceof Document) {
                    //System.out.println("append document " + node.nodeName());
                    Document newDoc = new Document(node.baseUri());
                    ((Element)stack.peek()).appendChild(newDoc);
                    stack.push(newDoc);
                } else if (node instanceof  Element) {

                    Element element = (Element) node;
                    Element toBeAdded = new Element(element.tag(), element.baseUri(), element.attributes());
                    if (components.get(node.nodeName()) != null) {
                        System.out.println("Resolving " + node.nodeName() + " in page");
                        SolymerComponent component = components.get(node.nodeName());
                        String id = String.valueOf(random.nextInt());
                        toBeAdded = components.get(node.nodeName()).resolveTemplate(id, node.attributes(), element.children());
                        //element.children().remove();
                        //componentInstances.add(new SolymerComponentInstance(id, component, node.attributes()));
                        toBeAdded.attr("id", id);
                        toBeAdded.attr("kind", component.getComponentName());
                        if (!parsedComponents.contains(component.getComponentName())) {
                            parsedComponents.add(component.getComponentName());
                            scriptBuilder.append(StringEscapeUtils.unescapeHtml4(component.getScriptElement().html())
                                    + "\n");
                        }
                    }
                    //System.out.println("append element " + node.nodeName());
                    ((Element)stack.peek()).appendChild(toBeAdded);
                    stack.push(toBeAdded);
                } else {
                    //System.out.println("append node " + node.nodeName());
                    Node newNode = node.clone();
                    ((Element)stack.peek()).appendChild(node.clone());

                    stack.push(newNode);
                }
            }

            @Override
            public void tail(Node node, int depth) {
                if (depth > careLevel) {
                    return;
                }

                stack.pop();

                if (depth <= careLevel) {
                    careLevel = Integer.MAX_VALUE;
                }
            }
        });


        scriptBuilder.append("</script>");

        Document document = (Document) root.child(0);

        document.body().append(scriptBuilder.toString());
        return document;
    }

    public static SolymerPage parsePage(File file) throws IOException {
        return new SolymerPage(Jsoup.parse(file, "utf-8"));
    }

    public static void main(String[] args) throws IOException {
        components = ElementParser.parseComponents(new File("components"));
        SolymerPage solymerPage = parsePage(new File("pages/helloworld.html"));
        Document document = resolvePage(solymerPage);
        System.out.println(document);
        FileUtils.writeStringToFile(new File("page-output.html"), document.toString());
    }
}
