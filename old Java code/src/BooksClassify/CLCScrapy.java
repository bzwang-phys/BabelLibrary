package BooksClassify;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CLCScrapy {
    public FileWriter fileWriter;

    public CLCScrapy(){
        String relativeurl = "/category/B516.3/";
//        Path currentPath = Paths.get(System.getProperty("user.dir"));
//        Path clc = Paths.get(currentPath.toString(), "CLC");
        getCLCFromUrl(relativeurl, "CLC");

    }

    public void getCLCFromUrl(String relativeUrl, String absoluteDirName){
        try {
//            Writer out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream("CLC.txt"), "UTF-8"));

            boolean flag = true;
            Random r = new Random();
            String baseUrl = "https://www.clcindex.com";
            String[] relativeUrls = relativeUrl.split("/");
            relativeUrls[relativeUrls.length-1] =  URLEncoder.encode(relativeUrls[relativeUrls.length-1], "utf-8");
            relativeUrl = String.join("/", relativeUrls);
            String url = baseUrl + "/" + relativeUrl;

            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("tr[name=item-row]");
            Elements paths = doc.select("ul[class=breadcrumb]");
            String path = "";
            for (Element e : paths.get(0).children()) {
                path = path + e.text().replaceAll("中图分类","CLC") + "\\";
            }
            path = path.substring(0, path.length()-1);

            for (Element e : items) {

                String id = e.child(1).text();
//                char first = Character.toLowerCase(id.charAt(0));
//                if (first <= 's')  continue;
                String name = e.child(2).text();
                String newDirName = id + " " + name;
                String filePath = path + '\\' + newDirName;

//                if (!filePath.toFile().exists())      filePath.toFile().mkdirs();

                fileWriter = new FileWriter("CLC.txt", true);
                fileWriter.write(filePath + "\n");
                fileWriter.close();
                System.out.println(filePath);

                String newRelativeUrl = e.child(2).child(0).attr("href");

                TimeUnit.SECONDS.sleep(1+r.nextInt(5));
                getCLCFromUrl(newRelativeUrl, filePath);

            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
