package BooksClassify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import org.bson.Document;

import java.io.File;
import java.util.*;

public class MainController {

    @FXML private TextField textBookPath, textDoubanUrl;
    @FXML private TextField title, author, publisher, year, pages, isbn, format, series, classify;
    @FXML private TextArea remark, filename;
    private HashMap<String, String> infoBkp;


    public void doubanSearch(ActionEvent e){
        String bookName = title.getText();
        Douban.doubanOpen(bookName);
    }

    public String authorStandard(String author){
        String authorStand = "";
        if (author != null) {
            authorStand = author.trim().replaceAll("\\s*", "")
                    .replaceAll("\\(", "[").replaceAll("\\)", "]"); }
        return trimAll(author);
        }

    public void getBookInfo(ActionEvent e){
        String url = textDoubanUrl.getText().trim();
        HashMap<String, String> bookInfo = Douban.getBookInfo(url);
        if (bookInfo == null)  {warningWindow("获取书籍信息失败", "bookInfo = null"); return;}
        String subhead = "";
        if (bookInfo.containsKey("副标题"))  subhead = "-" + bookInfo.get("副标题");
        title.setText( (bookInfo.get("书名")+subhead).replaceAll("：","-").replaceAll(":","-") );
        author.setText( authorStandard(bookInfo.get("作者")) );
        publisher.setText(bookInfo.get("出版社"));
        year.setText(bookInfo.get("出版年"));
        pages.setText(bookInfo.get("页数"));
        isbn.setText(bookInfo.get("ISBN"));
        series.setText(bookInfo.get("丛书"));
        filename.setText( newBookName(true,false) );
//        isbn.setText(bookInfo.get("出品方"));
        String clc = CLCSpider.serach(bookInfo.get("ISBN"));
        System.out.println(clc);

        classify.setText( trimAll(clc).replaceAll("\\s*","") );
        infoBkp = bookInfo;
    }

    public void classifySave(ActionEvent e){
        String clcText = trimAll(classify.getText()).replaceAll("\\s*","");
        classify.setText(clcText);

        ArrayList<ArrayList<String>> twotree = CLCAnalyse(clcText);
        ArrayList<String> tree = twotree.get(0);
        ArrayList<String> foldertree = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < tree.size()) foldertree.add(tree.get(i)); }

        HashMap<String,Object> item = new HashMap<>();
        item.put("title", trimAll(title.getText()).replaceAll(":","-") );
        item.put("author", trimAll(author.getText()) );
        item.put("publisher", trimAll(publisher.getText()) );
        item.put("year", trimAll(year.getText()) );
        item.put("pages", trimAll(pages.getText()) );
        item.put("isbn", trimAll(isbn.getText()) );
        item.put("series", trimAll(series.getText()) );
        item.put("format", trimAll(format.getText()) );
        item.put("clc", clcText);
        item.put("remark", trimAll(remark.getText()) );
        item.put("folderpath", foldertree);
        item.put("tree", tree);
        item.put("nametree", twotree.get(1));

        String bookpath = getBookPath(foldertree);
        if (bookpath != null){  // null means the operation moveBook() is failed, due to the repeated books.
            item.put("bookname", trimAll(filename.getText()) );
            item.entrySet().removeIf(entry -> entry.getValue().toString().equals(""));
            boolean isConfirm = isBookInfoOK(item);
            if (isConfirm){
                moveBook(foldertree);
                Mongodb.additem(item);
                clearTxtField();
            }
        }
    }

    public void dragFilesOver(DragEvent event){
        textBookPath.setText("拖动文件到此处");
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        event.consume();
    }



    public void dragFilesDropped(DragEvent event){
        Dragboard db = event.getDragboard();
        if (!db.hasFiles()) {
            event.setDropCompleted(false);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("文件似乎并不存在");
            alert.setContentText("你所拖拽的文件似乎并不存在，请尝试手动输入文件路径");
            alert.showAndWait();
            return;
        }

        String path = db.getFiles().get(0).toString();
        String fFullName = new File(path).getName();
        String fn = fFullName.substring(0, fFullName.lastIndexOf("."));  // without suffix
        String fnFormat = fFullName.substring(fFullName.lastIndexOf(".")+1);
        textBookPath.setText(path);
        title.setText(fn);
        format.setText(fnFormat);
        filename.setText(fFullName);
        event.setDropCompleted(true);
        event.consume();
    }


    public void warningWindow(String header, String context){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.showAndWait();
    }

    public ArrayList<ArrayList<String>> CLCAnalyse(String clsfy){
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        ArrayList<String> tree = new ArrayList<>();
        ArrayList<String> fulltree = new ArrayList<>();

        CLCMongodb.connect();
        // initial the first folder: CLC/
        Document clc = CLCMongodb.search("clc", "CLC");
        ArrayList<String> children = clc.get("children", ArrayList.class);
        tree.add(clc.get("clc", String.class).trim());
        fulltree.add(clc.get("fullname", String.class).trim());

        // if the folder is others.....
        boolean clcAnalyseQ = true;
        if (clsfy.toLowerCase(Locale.ROOT).startsWith("others")) {
            clcAnalyseQ = false;
            String[] pathlist = clsfy.split("/");
            for (String s : pathlist) {
                tree.add(s);
                fulltree.add(s);
            }
        }


        while (clcAnalyseQ && (children.size() > 0)) {
            boolean flag = true;
            for (String s : children) {
                System.out.println(s);
                if (clsfy.startsWith(s)){
                    clc = CLCMongodb.search("clc", s);
                    children = clc.get("children", ArrayList.class);
                    tree.add(clc.get("clc", String.class).trim());
                    fulltree.add(clc.get("fullname", String.class).trim());
                    flag = false;
                    break;
                }
            }
            if (flag) { // If the the category can not be found,
                String s = children.get(children.size()-1);
                if (s.contains("-")){
                    clc = CLCMongodb.search("clc", s);
                    tree.add(clc.get("clc", String.class).trim());
                    fulltree.add(clc.get("fullname", String.class).trim());
                }
                break;
            }
        }
        res.add(tree);
        res.add(fulltree);
        return  res;
    }

    public String getBookPath(ArrayList<String> folderlist){
        String path = String.join(File.separator, folderlist);
        String filepath = path + File.separator + trimAll(filename.getText());
        File newfile = new File(filepath);

        // Maybe the same-name book has be exist.
        if (newfile.exists()){
            String fname = newBookName(true,true);
            filename.setText(fname);
            filepath = path + File.separator + fname;
            newfile = new File(filepath);
            if (newfile.exists()){ warningWindow("文件夹中已经存在此书", "请重新设置一个文件名"); return null;}
        }

        return filepath;
    }


    public String moveBook(ArrayList<String> folderlist){
        // if the path is not exist, we need to create it.
        String path = String.join(File.separator, folderlist);
        File file = new File(path);
        if ( !file.exists() )  file.mkdirs();

        // original file and new file.
        String filepath = path + File.separator + trimAll(filename.getText());
        File originfile = new File(textBookPath.getText());
        File newfile = new File(filepath);

        originfile.renameTo(newfile);
        return filepath;
    }


    public boolean isBookInfoOK(HashMap<String,Object> bookInfo){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("请检查书籍信息");
        alert.setHeaderText(String.valueOf(bookInfo.get("title")));
        alert.initModality(Modality.APPLICATION_MODAL);

        String info = "";
        for (Map.Entry<String, Object> entry : bookInfo.entrySet()) {
            info += entry.getKey() + " : " + entry.getValue().toString() + "\n";
        }
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setContentText(info);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == ButtonType.OK)  return true;
        else return false;
    }

    public void clearTxtField(){
        title.clear();
        author.clear();
        textBookPath.clear();
        textDoubanUrl.clear();
        publisher.clear();
        year.clear();
        pages.clear();
        isbn.clear();
        format.clear();
        series.clear();
        classify.clear();
        remark.clear();
        filename.clear();
    }

    public void clear3(){
        if ("".equals(publisher.getText()) || "".equals(year.getText()) || "".equals(pages.getText())){
            publisher.setText(infoBkp.get("出版社"));
            year.setText(infoBkp.get("出版年"));
            pages.setText(infoBkp.get("页数"));
            series.setText(infoBkp.get("丛书"));
        } else {
            publisher.clear();
            year.clear();
            pages.clear();
            series.clear();
            filename.setText( newBookName(false,false) );
        }
    }

    public String trimAll(String s){
        String res = "";
        if (s != null){ res = s.trim().replaceAll("\\s*",""); }
        return res;
    }

    public String newBookName(boolean yearQ, boolean publisherQ){
        String yearText = addUnderLine( trimAll(year.getText()).split("-")[0] );
        String authorText = author.getText();
        authorText = addUnderLine(authorText.substring(authorText.lastIndexOf("·")+1));
        String publisherText = addUnderLine(publisher.getText());
        String fname = "";
        if (yearQ & !publisherQ)
            fname = trimAll(title.getText()) + yearText + authorText + "." + trimAll(format.getText());
        else if (yearQ & publisherQ)
                fname = trimAll(title.getText()) + yearText + authorText + publisherText + "." + trimAll(format.getText());
        else if (!yearQ & !publisherQ)
            fname = trimAll(title.getText()) + authorText + "." + trimAll(format.getText());
        else if (!yearQ & publisherQ)
            fname = trimAll(title.getText()) + publisherText + authorText + "." + trimAll(format.getText());

        return fname;
    }

    public String addUnderLine(String s){
        String res = trimAll(s);
        if (!"".equals(res))  res = "_" + res;
        return res;
    }

}
