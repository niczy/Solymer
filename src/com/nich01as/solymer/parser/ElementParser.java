package com.nich01as.solymer.parser;

import data.SolymerComponent;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nicholaszhao on 7/18/14.
 */
public class ElementParser {

    public static Element parseTemplate(Element templateElement) {
        Attributes attrs = templateElement.attributes();
        Iterator<Attribute> it = attrs.iterator();
        String html = templateElement.html();
        while (it.hasNext()) {
            Attribute attr = it.next();
            html = html.replace("{{" + attr.getKey() + "}}", attr.getValue());
        }
        templateElement.html(html);
        return templateElement;
    }

    public static SolymerComponent parseComponent(File file) throws IOException {
        Document document = Jsoup.parse(file, "utf-8");
        //System.out.println(document);
        Element element = document.getElementsByTag("solymer-element").get(0);
        return new SolymerComponent(element);
    }

    public static Map<String, SolymerComponent> parseComponents(File dir) throws IOException {
        Map<String, SolymerComponent> components = new HashMap<String, SolymerComponent>();
        for (File file : dir.listFiles()) {
            if (!file.getName().startsWith(".")) {
                SolymerComponent component = parseComponent(file);
                components.put(component.getComponentName(), component);
            }
        }
        return components;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "components/input.html";

        Element solymerElement = Jsoup.parse(new File(fileName), "utf-8");
        Element templateElement = solymerElement.getElementsByTag("template").get(0);
        Attributes attrs = templateElement.attributes();

        Element scriptElement = solymerElement.getElementsByTag("script").get(0);
        templateElement = parseTemplate(templateElement);
        System.out.println(templateElement.html());
        System.out.println(scriptElement);
    }
}
