/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 *
 * @author Ivan
 */
public class ListOfElements {
    TilePane elemLayout;
    TreeView<Category> tv=new TreeView<>();

    public ListOfElements(){
        TreeItem<Category> root;

        try{
//            URL s=getClass().getResource("/Elements");

            File f=new File("");
            List<String> paths=new ArrayList<>();
            ZipInputStream zis=new ZipInputStream(new FileInputStream(f.getAbsolutePath()+"\\RaschetKz.jar"));
            for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                String entryName=entry.getName();
                if (entryName.startsWith("Elements")&&!entry.isDirectory() && entryName.endsWith(".class")
                        && !entryName.contains("$")) {
                    // This ZipEntry represents a class. Now, what class does it represent?
                    String className = entry.getName().replace('/', '.'); // including ".class"
                    paths.add(className.substring("Elements.".length(), className.length() - ".class".length()));
                }
            }

            root=treeCreate("Elements",paths);
        }catch(Exception ex){
            //ex.printStackTrace(System.err);

            Path p=new File("src/Elements").toPath();

            root=treeCreate(p);


        }
        tv.setRoot(root);
        tv.setShowRoot(false);

        tv.getSelectionModel().selectedItemProperty().addListener((t,o,n)->{
            if(n.isLeaf()){
                elemLayout.getChildren().clear();
                elemLayout.getChildren().addAll(n.getValue().getView());
            }
        });
    }

    public TreeView getCategories() {
        return tv;
    }

    public void setElemPane(TilePane elems) {
        this.elemLayout=elems;
    }

    TreeItem<Category> treeCreate(Path dir){
        TreeItem<Category> trIt=new TreeItem<>();
        Category cat=new Category(dir);
        trIt.setValue(cat);
        trIt.setGraphic(new Label(dir.getFileName().toString()));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                if(!isLeaf(file)){
                    trIt.getChildren().add(treeCreate(file));
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }

        return trIt;
    }

    TreeItem<Category> treeCreate(String className,List<String> classes) {
        TreeItem<Category> trIt = new TreeItem<>();
        Category cat = new Category(className, classes);
        trIt.setValue(cat);
        int i=className.lastIndexOf(".")+1;
        trIt.setGraphic(new Label(className.substring(i)));

        String directory = classes.get(0).substring(0, classes.get(0).indexOf("."));

        //filter
        List<String> subCategorie = new ArrayList<>();
        int cnt = 0;

        while (cnt <classes.size()){
            String str=classes.get(cnt);
            if (str.startsWith(directory)) {
                subCategorie.add(str.substring(directory.length()+1));
            } else {
                if(subCategorie.get(0).indexOf(".")!=-1)
                    trIt.getChildren().add(treeCreate(className+"."+directory, subCategorie));

                subCategorie.clear();
                directory = classes.get(cnt).substring(0, classes.get(cnt).indexOf("."));
                subCategorie.add(str.substring(directory.length()+1));
            }
            cnt++;
        }
        if(!subCategorie.isEmpty()) {
            if(subCategorie.get(0).indexOf(".")!=-1)
                trIt.getChildren().add(treeCreate(className+"."+directory, subCategorie));
        }
        return trIt;
    }

    private boolean isLeaf(Path p){
        boolean flag=true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path file: stream) {
                if(Files.isDirectory(file)){
                    flag=false;
                    break;
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        return flag;
    }

    private class Category{
        List<Node> view;

        Category(Path dir){
            view=new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path file: stream) {
                    if(isLeaf(file)){
                        String elemName=file.getFileName().toString();
                        String className=file.resolve(elemName).toString();
                        className=className.substring(className.lastIndexOf("Elements\\"));
                        className=className.replace('\\','.').replace("src.","");


                        Class<?> clas=Class.forName(className);
                        Constructor ctor=clas.getConstructor(boolean.class);
                        Element elem=(Element)ctor.newInstance(true);
                        view.add(elem.getView());
                    }
                }

            }catch (Exception x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                x.printStackTrace(System.err);
            }
        }

        Category(String className,List<String> list){
            view=new ArrayList<>();

            for(String str:list) {
                if (str.indexOf(".") == str.lastIndexOf(".")){
                    try {
                        Class<?> clas = Class.forName(className+"."+str);
                        Constructor ctor = clas.getConstructor(boolean.class);
                        Element elem = (Element) ctor.newInstance(true);
                        view.add(elem.getView());
                    }catch(Exception ex){
                        ex.printStackTrace(System.err);
                    }
                }

            }
        }

        List<Node> getView(){
            return view;
        }

        @Override
        public String toString(){
            return "";
        }
    }
}

