package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "FAQ")
public class FAQ {

    @Element(name = "Title", required = false)
    String title;

    @Element(name = "Content", required = false)
    String content;

    @Element(name = "Category", required = false)
    String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
