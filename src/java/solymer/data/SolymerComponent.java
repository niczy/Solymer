package solymer.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import solymer.parser.ElementParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by nicholaszhao on 7/18/14.
 */
public class SolymerComponent {

    private static final Random random = new Random();

    private final Element element;
    private final String componentName;
    private final Element templateElement;
    private final Element scriptElement;
    private final Tag wrapperTag;
    private Map<String, SolymerComponent> dependencies = new HashMap<String, SolymerComponent>();

    public SolymerComponent(Document document) throws IOException {
        this.element = document.getElementsByTag("solymer-element").get(0);;
        this.componentName = element.attr("name");
        this.templateElement = element.getElementsByTag("template").get(0);
        this.scriptElement = element.getElementsByTag("script").get(0);
        this.wrapperTag = Tag.valueOf(element.attr("wrapper"));
        initDeps(document);
    }

    public String getComponentName() {
        return componentName;
    }

    public Element getScriptElement() {
        return scriptElement;
    }

    public Element resolveTemplate(String id, Attributes attrs) {
        return parseTemplate(id, attrs);
    }

    private void initDeps(Document document) throws IOException {
        System.out.println(document.baseUri());
        // A component call use itself.
        dependencies.put(componentName, this);
        Elements depLinks = document.select("link");
        for (Element element : depLinks) {
            String filePath = element.attr("href");
            SolymerComponent component = ElementParser.parseComponent(new File(new File(document.baseUri()).getParent(), filePath));
            dependencies.put(component.getComponentName(), component);
        }
        System.out.println("deps are " + dependencies);
    }

    @Override
    public String toString() {
        return componentName;
    }

    private String parseContent(String content, Attributes attrs) {
        Iterator<Attribute> it = attrs.iterator();

        while (it.hasNext()) {
            Attribute attr = it.next();
            content = content.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }
        return content;
    }

    private Element resolveTemplate(Element template) {
        final Stack<Node> stack = new Stack<Node>();
        Element root = new Element(Tag.valueOf("root"), "/", new Attributes());

        stack.push(root);
        template.traverse(new NodeVisitor() {

            int careLevel = Integer.MAX_VALUE;

            @Override
            public void head(Node node, int depth) {
                if (depth > careLevel) {
                    return;
                }
                if (node instanceof Element) {

                    Element element = (Element) node;
                    Element toBeAdded = new Element(element.tag(), element.baseUri(), element.attributes());
                    if (dependencies.get(node.nodeName()) != null) {
                        System.out.println("Resolving element " + node.nodeName() + " in element " + componentName);
                        careLevel = depth;
                        //System.out.println("Resolving " + node.nodeName());
                        SolymerComponent component = dependencies.get(node.nodeName());
                        String id = String.valueOf(random.nextInt());
                        toBeAdded = dependencies.get(node.nodeName()).resolveTemplate(id, node.attributes());

                        //componentInstances.add(new SolymerComponentInstance(id, component, node.attributes()));
                        toBeAdded.attr("id", id);
                        toBeAdded.attr("kind", component.getComponentName());
                    }
                    //System.out.println("append element " + node.nodeName());
                    ((Element) stack.peek()).appendChild(toBeAdded);
                    stack.push(toBeAdded);
                } else {
                    //System.out.println("append node " + node.nodeName());
                    Node newNode = node.clone();
                    ((Element) stack.peek()).appendChild(node.clone());

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

        return root.child(0);
    }

    private Element parseTemplate(final String id, Attributes attrs) {
        Element resolvedTemplate = templateElement.clone();
        bindEvents(id, resolvedTemplate);
        String html = resolveAttributes(attrs, resolvedTemplate);
        resolvedTemplate.html(html);
        resolvedTemplate = resolveTemplate(resolvedTemplate);
        Element element = new Element(wrapperTag, "/", attrs);
        element.html(resolvedTemplate.html());
        return element;
    }

    private String resolveAttributes(Attributes attrs, Element resolvedTemplate) {
        Iterator<Attribute> it = attrs.iterator();
        String html = resolvedTemplate.html();
        while (it.hasNext()) {
            Attribute attr = it.next();
            html = html.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }
        return html;
    }

    private void bindEvents(final String id, Element resolvedTemplate) {
        resolvedTemplate.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int i) {
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    for (Attribute attr : ele.attributes()) {
                        if (attr.getKey().startsWith("on-")) {
                            String eventFunc = attr.getValue();
                            assert eventFunc.startsWith("{{") && eventFunc.endsWith("}}");
                            ele.removeAttr(attr.getKey());
                            eventFunc = eventFunc.substring(2, eventFunc.length() - 2);
                            ele.attr(attr.getKey().replace("-", ""), "EventCenter.publishEvent('" + id +"', '" +  eventFunc + "')");
                        }
                    }

                } else {

                }
            }

            @Override
            public void tail(Node node, int i) {

            }
        });
    }
}
