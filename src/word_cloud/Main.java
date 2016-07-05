package word_cloud;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import com.sun.org.apache.xpath.internal.operations.And;

public class Main{
	public static void main(String[] args) throws IOException{
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		String styleSheet =
			    "node {" +
			    "   fill-color: blue;" +
			    "	size-mode: dyn-size;"+
			    "	size: 3px;"+
			    "	text-alignment: under;"+
			    "	text-size: 8px;"+
			    "}" +

			    "edge {"+
			    "	shape: line;"+
			    "	fill-color: #222;"+
			    "}";
		URL data = Main.class.getResource("test.csv");
		Graph tweets = new SingleGraph("Word Cloud");
		tweets.addAttribute("ui.stylesheet", styleSheet);
		tweets.addAttribute("ui.quality");
		tweets.addAttribute("ui.antialias");
		tweets.addAttribute("ui.screenshoot", "graph.png");
		
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		//Predef lang = spanish
		String lang = "es";
		
		try {
			br = new BufferedReader(new FileReader(data.getPath()));
			while ((line = br.readLine()) != null) {
				//Use comma as separator
				String[] tweet = line.split(cvsSplitBy);
				//Detects language for each tweet
				if (tweet[5].equals(lang)){
					StringTokenizer tokens=new StringTokenizer(tweet[4], "&");
					String prev = "";
					while(tokens.hasMoreTokens()){
						String str=tokens.nextToken();						
						if (!str.isEmpty()){
							//If node doesn't exist
							if (tweets.getNode(str)== null){
								tweets.addNode(str).addAttribute("weight", 1);
								tweets.getNode(str).addAttribute("cons", 0);
								
								if(!prev.equals("")){
									int j = Integer.parseInt(tweets.getNode(str).getAttribute("cons").toString())+1;
									int k = Integer.parseInt(tweets.getNode(prev).getAttribute("cons").toString())+1;
									//Add edge to connect on tweet
									String edge_name = prev+str;
									tweets.addEdge(edge_name, prev, str);
									tweets.getNode(str).setAttribute("cons", j);
									tweets.getNode(prev).setAttribute("cons", k);
									prev = str;
								}
								prev = str;
							}
							//If node exists, modifies weight
							else{
								int i = tweets.getNode(str).getAttribute("weight");
								tweets.getNode(str).setAttribute("weight", i+1);
								//If node doesn't have prev node
								if(!prev.equals("")){
									String edge_name = prev+str;
									String edge_name2 = str+prev;
									//Find if edge doesn't exists
									if((tweets.getEdge(edge_name) == null) && (tweets.getEdge(edge_name2) == null)){
										int j = Integer.parseInt(tweets.getNode(str).getAttribute("cons").toString())+1;
										int k = Integer.parseInt(tweets.getNode(prev).getAttribute("cons").toString())+1;
										tweets.getNode(str).setAttribute("cons", j);
										tweets.getNode(prev).setAttribute("cons", k);
										tweets.addEdge(edge_name, prev, str);
									}
								}
								
							}
						}
					}
				}
			
			} 
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (Node n: tweets){
				n.addAttribute("ui.label", n.getId());
				int size = Integer.parseInt(n.getAttribute("weight").toString());
				size = size +3;
				n.addAttribute("ui.size", size );
				
		}
		//display
		Viewer viewer = tweets.display(true);
		
		/*View view = viewer.getDefaultView();
		view.getCamera().setViewPercent(0.5);*/

	}
	
	
	
}
