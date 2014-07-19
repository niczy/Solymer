package data;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by nicholaszhao on 7/18/14.
 */
public class SolymerPage {

    private final Document document;

    public SolymerPage(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }


}
