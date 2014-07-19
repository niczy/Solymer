package com.nich01as.solymer.data;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeVisitor;

import java.util.Iterator;

/**
 * Created by nicholaszhao on 7/18/14.
 */
public class SolymerComponent {

    private final Element element;
    private final String componentName;
    private final Element templateElement;
    private final Element scriptElement;
    private final Tag wrapperTag;

    public SolymerComponent(Element element) {
        this.element = element;
        this.componentName = element.attr("name");
        this.templateElement = element.getElementsByTag("template").get(0);
        this.scriptElement = element.getElementsByTag("script").get(0);
        this.wrapperTag = Tag.valueOf(element.attr("wrapper"));
    }

    public String getComponentName() {
        return componentName;
    }

    public String getJsName() {
        return componentName.replace("-", "_");
    }

    public Element getTemplateElement() {
        return templateElement;
    }

    public Element getScriptElement() {
        return scriptElement;
    }

    public Element resolveTemplate(String id, Attributes attrs) {
        return parseTemplate(id, attrs);
    }

    private String parseContent(String content, Attributes attrs) {
        Iterator<Attribute> it = attrs.iterator();

        while (it.hasNext()) {
            Attribute attr = it.next();
            content = content.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }
        return content;
    }

    private Element parseTemplate(final String id, Attributes attrs) {
        Element resolvedTemplate = templateElement.clone();
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
        Iterator<Attribute> it = attrs.iterator();
        String html = resolvedTemplate.html();
        while (it.hasNext()) {
            Attribute attr = it.next();
            html = html.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }


        Element element = new Element(wrapperTag, "/", attrs);
        element.html(html);
        return element;
    }
}
