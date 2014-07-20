package solymer.data;

import org.jsoup.nodes.Document;

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
