package data;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

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
        this.wrapperTag = Tag.valueOf(element.attr("extend"));
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

    public Element resolveTemplate(Attributes attrs) {
        //System.out.println("attrs is " + attrs);
        return parseTemplate(attrs);
    }

    private Element parseTemplate(Attributes attrs) {
        Element resolvedTemplate = templateElement.clone();
        Iterator<Attribute> it = attrs.iterator();
        String html = templateElement.html();
        while (it.hasNext()) {
            Attribute attr = it.next();
            html = html.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }
        Element element = new Element(wrapperTag, "/", attrs);
        element.html(html);
        return element;
    }
}
