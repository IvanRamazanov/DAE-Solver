/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import Connections.Wire;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Graph {
    public static List<Arc> getTree(List<Wire> nodes, List<Arc> arcs){
        List<Arc> tree=new ArrayList<>();
        List<Wire> blacklist=new ArrayList<>();
        List<Wire> whitelist=new ArrayList<>();
        whitelist.addAll(nodes);
        tree.add(arcs.get(0));
        blacklist.add(arcs.get(0).getPositive());
        blacklist.add(arcs.get(0).getNegative());
        whitelist.removeAll(blacklist);
        while(blacklist.size()!=nodes.size()){
            whitelist.forEach(node -> {
                arcs.forEach(arc -> {
                    if(arc.getNegative()==node||arc.getPositive()==node){
                        blacklist.forEach(blackNode -> {
                            if(arc.getNegative()==blackNode||arc.getPositive()==blackNode){
                                tree.add(arc);
                                blacklist.add(node);
                            }
                        });
                    }
                });
            });
            whitelist.removeAll(blacklist);
        }
        return(tree);
    }
    
    public static List<Arc> getCoTree(List<Arc> arcs,List<Arc> tree){
        List<Arc> co_tree=new ArrayList<>();
        co_tree.addAll(arcs);
        co_tree.removeAll(tree);
        return(co_tree);
    };

    public static List<Arc> getCycle(List<Arc> tree, Arc coArc) {
        List<Arc> cycle=new ArrayList<>();
        List<Wire> nodes=new ArrayList<>();
        int length;
        cycle.addAll(tree);
        cycle.add(coArc);
        do{
            length=cycle.size();
            cycle.forEach(arc -> {
                nodes.add(arc.getPositive());
                nodes.add(arc.getNegative());
            });
            cycle.forEach(arc -> {
                if(nodes.indexOf(arc.getPositive())==nodes.lastIndexOf(arc.getPositive())){
                    cycle.remove(arc);
                }
                if(nodes.indexOf(arc.getNegative())==nodes.lastIndexOf(arc.getNegative())){
                    cycle.remove(arc);
                }
            });
            nodes.clear();
        }while(length!=cycle.size());
        cycle.get(0).setMuliplex(1);
        for(int i=0;i<cycle.size()-1;i++){
            for(int j=i+1;j<cycle.size();j++){
                Wire end=cycle.get(i).getNegative();
                if(end==cycle.get(j).getNegative()){
                    cycle.add(i+1, cycle.get(j));
                    cycle.remove(j+1);
                    cycle.get(i+1).setMuliplex(1);
                }
                if(end==cycle.get(j).getPositive()){
                    cycle.add(i+1, cycle.get(j));
                    cycle.remove(j+1);
                    cycle.get(i+1).setMuliplex(1);
                }
            }
        }
        return(cycle);
    } 
}