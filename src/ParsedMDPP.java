import java.util.ArrayList;
import java.util.List;

public class ParsedMDPP {
    private String pageTitle;
    private List<String> cssURLs;
    //private List<URLName> titlebarItems;
    private String titlebarTitle;
    private String htmlPageBody;
    private String htmlPageHead;
    public ParsedMDPP() {
        cssURLs = new ArrayList<>();
        //titlebarItems = new ArrayList<>();
        htmlPageBody = "";
        htmlPageHead = "";
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

    //public void addTitlebarItem(URLName titlebarItem) {
       // titlebarItems.add(titlebarItem);
    //}

    //public List<URLName> getTitlebarItems() {
      //  return this.titlebarItems;
    //}

    public void setTitlebarTitle(String titlebarTitle) {
        this.titlebarTitle = titlebarTitle;
    }

    public String getTitlebarTitle() {
        return this.titlebarTitle;
    }

    public String getHtmlPageBody() {
        return this.htmlPageBody;
    }

    public void appendToHtmlPageBody(String toAppend) {
       this.htmlPageBody =  this.htmlPageBody.concat(toAppend);
    }
}
