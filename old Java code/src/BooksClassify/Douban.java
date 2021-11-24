package BooksClassify;

import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Douban {

    public static void doubanOpen(String bookname){
        try {
            String url = "https://search.douban.com/book/subject_search?search_text=";
            url += java.net.URLEncoder.encode(bookname, "utf-8");
//          Document doc = Jsoup.connect(url).get();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("无法打开浏览器");
                alert.setContentText("只能自己手动搜索了");
                alert.showAndWait();
                return;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }


    }


    public static HashMap<String,String> getBookInfo(String url){
        try {
            HashMap<String,String> info = new HashMap<String,String>();
            Document doc = Jsoup.connect(url).get();
            // get the name of book.
            Element bookName = doc.select("h1>span[property]").get(0);
            info.put("书名", bookName.text());
            // get other information.
            Element allInfo = doc.select("div[id=info]").get(0);
            // handle the item like this: <span>  <a >  </span>
            Elements elements = allInfo.select("span>a");
            for (Element ele : elements) {
                String val = ele.text().trim();
                String key = ele.parent().getElementsByClass("pl").text().replaceAll(":","").trim();
                info.put(key, val);
                System.out.println("1: " + key + val);
            }
            // handle the item like this: <span class="pl">装帧:</span>  精装
            elements = allInfo.select("span[class=pl]");
            for (Element ele : elements) {
                String key = ele.text().replaceAll(":","").trim();
                if (info.containsKey(key)) continue;
                String val = ele.nextSibling().toString();
                if (val.trim().contains("&nbsp;")) val = ele.nextElementSibling().text();   //<span class="pl">丛书:</span> &nbsp; <a ....>
                val = val.replaceAll("<a>","").replaceAll("&nbsp;","").trim();
                info.put(key, val);
                System.out.println("2: " + key + val);
            }
            System.out.println(info);
            System.exit(0);
            return info;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
