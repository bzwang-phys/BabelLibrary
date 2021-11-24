package BooksClassify;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.TessAPI1;

public class CLCSpider {

    public static String serach(String isbn){
        if ("".equals(isbn))  return "";
        String clc = "";
        try {
            String baseUrl = "http://opac.calis.edu.cn/opac/";
            String query = "(bath.isbn=\"" + isbn + "*\")";
            String url = baseUrl + "doSimpleQuery.do";
            Connection.Response res = Jsoup.connect(url)
                    .data("actionType", "doSimpleQuery").data("pageno", "1").data("pagingType","0")
                    .data("operation","searchRetrieve").data("version","1.1").data("query",query)
                    .data("sortkey","title").data("maximumRecords","50").data("startRecord","1")
                    .data("dbselect","all").data("langBase","default").data("conInvalid","检索条件不能为空")
                    .data("indexkey", "bath.isbn|frt").data("condition",isbn)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.166 Safari/537.36")
                    .method(Connection.Method.POST)
                    .execute();
            Map<String, String> cookies = res.cookies();
            Document doc = res.parse();
            Elements books = doc.select("table[id=table4]>tbody>tr[bgcolor=#F2F2F2]");
            if (books.size() < 1){ return "";}
            Element jsUrl = books.get(0).select("td>a[href~=javascript:doShowDetails*]").get(0);


            // get the url of the detailed information.
            Pattern pattern = Pattern.compile("javascript:doShowDetails\\((.*?)\\)");
            Matcher matcher = pattern.matcher( jsUrl.attr("href") );
            String strs = "";
            if (matcher.find()) strs = matcher.group(1);
            else {}
            String[] split = strs.split(",");
            String recIndex, recTotal, fullTextType, dbselect, oid, sessionid;
            recIndex = trimAll(split[0]);
            recTotal = trimAll(split[1]);
            fullTextType = trimAll(split[2]);
            dbselect = trimAll(split[3]);
            oid = trimAll(split[4]);
            sessionid = trimAll(split[5]);
            String showDetails = "showDetails.do?recIndex="+recIndex+"&recTotal="+recTotal+"&fullTextType="+fullTextType+"&dbselect="+dbselect+"&oid="+oid;
            url = "http://opac.calis.edu.cn/opac/" + showDetails;

            clc = getDetails(isbn, url, cookies);

            } catch (IOException e) {
            e.printStackTrace();
        }
        return clc;
    }


    public static String getDetails(String isbn, String url, Map<String,String> cookies){
        String s = "";
        try {
            String baseUrl = "http://opac.calis.edu.cn/opac/";
            String query = "(bath.isbn=\"" + isbn + "*\")";
            // request the verification code.
            Connection.Response res = Jsoup.connect(url).data("actionType", "")
                    .data("pagingType", "1").data("dbselect", "all")
                    .data("operation", "searchRetrieve").data("version", "1.1").data("query", query)
                    .data("shw_cql", "ISBN=" + isbn + "*").data("sortkey", "title").data("maximumRecords", "50")
                    .data("startRecord", "1").data("dbselect", "all").data("langBase", "default")
                    .data("groupkey", "null").data("datepublication", "null").data("codelanguage", "null")
                    .data("searchlang", "null").data("creator2", "null").data("codetype", "null")
                    .data("series2", "null").data("unititle2", "null").data("CLC2", "null")
                    .data("queryType", "0").data("fromTree", "false").data("sortagainname", "title")
                    .data("selInvalid", "请选择记录").data("pageno", "1").data("pageno2", "1")
                    .data("indexkey", "bath.isbn|frt").data("condition", isbn)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.166 Safari/537.36")
                    .method(Connection.Method.POST)
                    .cookies(cookies)
                    .execute();
            Map<String, String> newCookies = res.cookies();
            Document doc = res.parse();
            if (doc.toString().contains("验证码")){
                doc = handleSecurityCode(isbn, newCookies);
            }
            s = getCLC(doc);

        }catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }


    public static Document handleSecurityCode(String isbn, Map<String,String> cookie){
        try {
            String baseUrl = "http://opac.calis.edu.cn/opac/";
            String url = baseUrl + "security";
            String query = "(bath.isbn=\"" + isbn + "*\")";
            Connection.Response res = Jsoup.connect(url).cookies(cookie).ignoreContentType(true).execute();
            Map<String,String> cookies = res.cookies();
            if ( !(new File("VerificationCode").exists()) ) new File("VerificationCode").mkdirs();
            File imgFile = new File("./VerificationCode/verifcode.jpeg");
            FileOutputStream fos = new FileOutputStream(imgFile);
            fos.write(res.bodyAsBytes());
            fos.close();

            ITesseract instance = new Tesseract();
            String ocrRes = trimAll( instance.doOCR(imgFile) );
            Thread.sleep(200);
            url = baseUrl + "check.do?subact=check&oid4Holding=null&fromType4Holding=null";
            res = Jsoup.connect(url).data("actionType", "").data("query",query)
                    .data("langBase","null").data("recIndex","1").data("recTotal","1")
                    .data("fullTextType","1").data("dbselect","all").data("queryType","0")
                    .data("curFullTextType","1").data("langBase4Holding","null")
                    .data("fromType4Holding","null").data("oid4Holding","null").data("userid","null")
                    .data("fullTextType","1").data("index","null").data("flag","null")
                    .data("subact","check").data("targetName","alertWin").data("passkey",ocrRes)
                    .data("jsfs6","---选择地区中心---").data("jsfs7","---选择省中心---")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.166 Safari/537.36")
                    .method(Connection.Method.POST)
                    .cookies(cookies)
                    .execute();
            Document doc = res.parse();
            return doc;
        } catch (IOException | TesseractException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCLC(Document doc){
        Element e = doc.select("a[href~=.*?bath.localClassification.*?]").get(0);
        return e.text();
    }


    public static String trimAll(String s){
        String sNew = s.trim().replaceAll("'","").replaceAll("\\s*","");
        return sNew;
    }
}
