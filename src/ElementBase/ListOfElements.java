/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.TilePane;


/**
 *
 * @author Ivan
 */
public class ListOfElements {
    TilePane elemLayout;
    TreeView<Category> tv=new TreeView<>();

    public ListOfElements(){
        Path p=new File("src/Elements").toPath();

        TreeItem<Category> root=treeCreate(p);

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
        Path path;
        List<Node> view;

        Category(Path dir){
            path=dir;
            view=new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path file: stream) {
                    if(isLeaf(file)){
                        String elemName=file.getFileName().toString();
                        String className=file.resolve(elemName).toString().replace('\\','.').replace("src.","");


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

        List<Node> getView(){
            return view;
        }

        @Override
        public String toString(){
            return "";
        }
    }
}

