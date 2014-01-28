package fephyfofum.genetreemachine;

import jade.MessageLogger;
import jade.tree.JadeTree;
import jade.tree.NexsonReader;
import jade.tree.TreeReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import opentree.GraphInitializer;
import opentree.exceptions.TaxonNotFoundException;


/**
 * this is the main runner file for the genetreemachine program
 * @author smitty
 *
 */
public class MainRunner {
	private static void loadTrees(String [] args){
		if (args.length != 4) {
			System.out.println("arguments should be: filename (taxacompletelyoverlap)T|F graphdbfolder");
			return;
		}
		String filename = args[1];
		String soverlap = args[2];
		boolean overlap = true;
		if (soverlap.toLowerCase().equals("f")){
			overlap = false;
		}
		String graphname = args[3];
		int treeCounter = 0;
		// Run through all the trees and get the union of the taxa for a raw taxonomy graph
		// read the tree from a file
		String ts = "";
		ArrayList<JadeTree> jt = new ArrayList<JadeTree>();
		MessageLogger messageLogger = new MessageLogger("justTreeAnalysis:");
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			System.out.println("Reading newick file...");
			TreeReader tr = new TreeReader();
			while ((ts = br.readLine()) != null) {
				if (ts.length() > 1) {
					jt.add(tr.readTree(ts));
					treeCounter++;
				}
			}
			br.close();
		} catch (IOException ioe) {}
		System.out.println(treeCounter + " trees read.");
		// Should abort here if no valid trees read
		
		HashSet<String> names = new HashSet<String>();
		for (int i = 0; i < jt.size(); i++) {
			for (int j = 0; j < jt.get(i).getExternalNodeCount(); j++) {
				String processedname = jt.get(i).getExternalNode(j).getName();
				if(processedname.contains("@")){
					processedname = processedname.split("@")[0];
				}
				names.add(processedname);
			}
		}
		PrintWriter outFile;
		try {
			outFile = new PrintWriter(new FileWriter("tax.temp"));
			ArrayList<String> namesal = new ArrayList<String>();
			namesal.addAll(names);
			for (int i = 0; i < namesal.size(); i++) {
				//outFile.write((i+2) + "\t|\t1\t|\t" + namesal.get(i) + "\t|\t\n");
				outFile.write((i+2)+"|1|"+namesal.get(i)+"| | | | | | | |\n");
				//tid  pid    name       rank+src+srce_id+srce_pid+uniqname (all empty)
			}
			//outFile.write("1\t|\t0\t|\tlife\t|\t\n");
			outFile.write("1| |root| | | | | | | |\n");
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GraphInitializer gin = new GraphInitializer(graphname);
		// make a temp file to be loaded into the tax loader, a hack for now
		try {
			gin.addInitialTaxonomyTableIntoGraph("tax.temp", "");
		} catch (TaxonNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Use the taxonomy as the first tree in the composite tree
		gin.shutdownDB();
		
		GraphImporter gi = new GraphImporter(graphname);
		System.out.println("started graph importer");
		/*
		 * if the taxa are not completely overlapping, we add, add again, then delete the first ones
		 * this is the first add
		 */
		if(overlap==false){
			// Go through the trees again and add and update as necessary
			for (int i = 0; i < jt.size(); i++) {
				String sourcename = "treeinfile";
				if (jt.get(i).getObject("ot:studyId") != null) { // use studyid (if present) as sourcename
					sourcename = (String)jt.get(i).getObject("ot:studyId");
				}
				sourcename += "t_" + String.valueOf(i);
	
				System.out.println("adding tree '" + sourcename + "' to the graph");
				gi.setTree(jt.get(i));
				gi.addSetTreeToGraph("root", sourcename, overlap);
				//gi.deleteTreeBySource(sourcename);
			}
		}
		
		/*
		 * If the taxa overlap completely, we only add once, this time
		 * If the taxa don't overlap, this is the second time
		 */
		for (int i = 0; i < jt.size(); i++) {
			String sourcename = "treeinfile";
			if (jt.get(i).getObject("ot:studyId") != null) { // use studyid (if present) as sourcename
				sourcename = (String)jt.get(i).getObject("ot:studyId");
			}
			sourcename += "_" + String.valueOf(i);

			System.out.println("adding tree '" + sourcename + "' to the graph");
			gi.setTree(jt.get(i));
			gi.addSetTreeToGraph("root", sourcename, overlap);
		}
		/*
		 * If the taxa don't overlap, we delete the second set of trees
		 */
		if(overlap == false){
			for (int i = 0; i < jt.size(); i++) {
				String sourcename = "treeinfile";
				if (jt.get(i).getObject("ot:studyId") != null) { // use studyid (if present) as sourcename
					sourcename = (String)jt.get(i).getObject("ot:studyId");
				}
				sourcename += "t_" + String.valueOf(i);
				gi.deleteTreeBySource(sourcename);
			}
		}
		gi.shutdownDB();
		return;

	}
	
	private static void printHelp(){
		System.out.println("loadTrees filename (taxacompletelyoverlap)T|F graphdbfolder");
	}
	
	public static void main(String [] args){
		if (args.length == 0 ){
			MainRunner.printHelp();
		}
		else if(args[0].equals("loadTrees") && args.length == 4){
			MainRunner.loadTrees(args);
	    }else{
			MainRunner.printHelp();
        }
	}
}
