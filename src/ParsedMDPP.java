import java.util.ArrayList;
import java.util.List;

public class ParsedMDPP {
    private String pageTitle;
    private List<String> cssURLs;

    public ParsedMDPP() {
        cssURLs = new ArrayList<>();
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void addCSSURL(String cssURL) {
        cssURLs.add(cssURL);
    }

    public List<String> getCSSURLs() {
        return this.cssURLs;
    }
}
