package solymer.data;

import org.jsoup.nodes.Attributes;

/**
 * Created by nicholaszhao on 7/19/14.
 */
public class SolymerComponentInstance {

    private final long id;
    private final SolymerComponent component;
    private final Attributes attrs;

    public SolymerComponentInstance(long id, SolymerComponent solymerComponent, Attributes attrs) {
        this.id = id;
        this.component = solymerComponent;
        this.attrs = attrs;
    }

    public long getId() {
        return id;
    }

    public SolymerComponent getComponent() {
        return component;
    }

    public Attributes getAttrs() {
        return attrs;
    }
}
