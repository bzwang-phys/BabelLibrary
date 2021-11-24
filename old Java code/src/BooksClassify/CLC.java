package BooksClassify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;


public class CLC {

    public CLC(String path){
        File file = new File(path);
        TreeNode treeNode = new TreeNode(file.getName(), file.getName(), file.getPath(), null);
        buildFoldersTree(treeNode);
        printTree(treeNode, 0);
    }

    // build tree for files and folders.
    public void buildFilesTree(TreeNode rootNode){
        File file = new File(rootNode.path);
        File[] files = file.listFiles();
        for (File f : files) {
            TreeNode child = new TreeNode(f.getName(), f.getName(), f.getPath(), rootNode);
            rootNode.children.add(child);
            if (f.isDirectory())  buildFilesTree(child);
        }
    }

    // build tree for folders
    public void buildFoldersTree(TreeNode rootNode){
        File file = new File(rootNode.path);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()){
                TreeNode child = new TreeNode(f.getName(), f.getName(), f.getPath(), rootNode);
                rootNode.children.add(child);
                System.out.println(child.path);
                buildFoldersTree(child);
            }
        }
    }

    // build tree from a text file.
    public static void buildTreeFromTxt(TreeNode node){
        try (BufferedReader br = new BufferedReader(new FileReader("CLC.txt"))){
            TreeNode tree = new TreeNode("0", "root", ".", null);
            for (String line; (line=br.readLine())!=null;){
                String[] split = line.split("\\\\");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printTree(TreeNode node, int deep){
        for (int i = 0; i < deep; i++)  System.out.print("    ");
        System.out.println(node.name);
        for (TreeNode nd : node.children)  printTree(nd, deep + 1);
    }


    private static class TreeNode{
        public String id;
        public String name;
        public String path;
        public TreeNode parent;
        public ArrayList<TreeNode> children = new ArrayList<>();
        public TreeNode(String id, String name, String path, TreeNode parent){
            this.id = id;
            this.name = name;
            this.path = path;
            this.parent = parent;
        }
    }
}
