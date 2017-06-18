import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class TestC {
	// variables to keep track of things
	static boolean loggedIn = false;
	static String name;
	static boolean ag = false;
	static boolean sg = false;
	static boolean rg = false;
	static boolean reading = false;
	static List<String> allGroups = new ArrayList<String>();
	static List<String> myGroups = new ArrayList<String>();
	static List<String> allPosts = new ArrayList<String>();
	static List<String> postComm = new ArrayList<String>();
	static List<String> seenPosts = new ArrayList<String>();
	static List<String> sortPosts = new ArrayList<String>();
	static String curGroup = "";
	static String ip = "";
	static String port = "";
	static int pcIndex = 5;
	static int pcN = 5;

	public static void main(String[] args) throws FileNotFoundException, IOException{
	if(args.length == 2){
		ip = args[0];
		port = args[1];
	} else{
		ip = "localHost";
		port = "5000";
	}
	
	//more variables to keep track of stuff
	String inputString = "";
	boolean mainLoop = true;
	System.out.println("hello to client");
	
	
	int agIndex = 0;
	int agN = 5;
	int sgIndex = 0;
	int sgN = 5;
	int rgIndex = 0;

	int rgN = 5;
	
	boolean test = true;
	
	// the main loop to keep reading input
	while(mainLoop){
		Scanner s = new Scanner(System.in);
		inputString = s.nextLine();
		
		
		if(inputString.split(" ")[0].equalsIgnoreCase("login") && loggedIn == false){
			if(inputString.split(" ").length > 1)
				sendStuff(inputString);
			else
				System.out.println("bad input");
		} else if(inputString.equals("help")){
			System.out.println("List of available commands with descriptions: (Type 'help' to view this menu)\n");
			System.out.println("login user_ID\tdetermines which discussion user is subscribed to");
			System.out.println("ag [N]\t\t\tlists all existing groups. Optional arg 'N' lists upto 'N' groups");
			System.out.println("\tag sub-commands:");
			System.out.println("\ts id [N]\t\t\tsubscribe to group at id or between 'id' and 'N'");
			System.out.println("\tu id [N]\t\t\tunsubscribe from group at 'id' or between 'id' and 'N'");
			System.out.println("\tn\t\t\tlists the next 'n' discussion groups");
			System.out.println("\tq\t\t\texit the ag command");
			System.out.println("sg [N]\t\t\tlists all subscribed groups w/ # of new posts. Optional arg 'N' lists upto 'N' subscribed groups");
			System.out.println("\tsg sub-commands:");
			System.out.println("\tu id [N]\t\t\tunsubscribe from group at 'id' or between 'id' and 'N'");
			System.out.println("\tn\t\t\tlists the next 'n' discussion groups");
			System.out.println("\tq\t\t\texit the sg command");
			System.out.println("rg gname [N]\t\t\tdisplays all posts in 'gname'. Optional arg 'N' lists upto 'N' posts in 'gname'");
			System.out.println("\trg sub-commands:");
			System.out.println("\tid\t\t\tspecifies content of post 'id'");
			System.out.println("\t\tid sub-commands:");
			System.out.println("\t\tn\t\t\tdisplay n more lines of the post");
			System.out.println("\t\tq\t\t\tgo back to rg command");
			System.out.println("\tr id [N]\t\t\tmarks post at 'id', or between 'id' and 'N', as read");
			System.out.println("\tn\t\t\tlists the next 'n' posts");
			System.out.println("\tp\t\t\tsubmit a new post");
			System.out.println("\tq\t\t\texit the rg command");
			System.out.println("logout\t\t\tlogs out current user and terminates client program");			
		} else if((inputString.equals("ag") || inputString.split(" ")[0].equalsIgnoreCase("ag")) && loggedIn == true && ag == false){
			// handles ag
			ag = true;
			sendStuff(inputString);
			
			try{
				agN = Integer.parseInt(inputString.split(" ")[1]);
			} catch(Exception e){
			}
			
			
			try{
				for(int i = 0; i < agN; i++){
					if(myGroups.contains(allGroups.get(i))){
						System.out.println((i+1) + ". (s) " + allGroups.get(i));
					} else{
						System.out.println((i+1) + ". ( ) " + allGroups.get(i));
					}
					agIndex++;
				}
			} catch(Exception e){
				System.out.println("No groups");
				
			}				
				
			
		} else if(inputString.split("\\s+")[0].equalsIgnoreCase("sg") && loggedIn == true && sg == false){
			// handles sg
			sg = true;
			
			System.out.println("subscribed groups");
			
			try{
				sgN = Integer.parseInt(inputString.split(" ")[1]);
			} catch(ArrayIndexOutOfBoundsException e){
			}			
			
			try{
				for(int i = 0; i < sgN; i++){
					int x =  getNumNewPosts(myGroups.get(i))[0];
					int y =  getNumNewPosts(myGroups.get(i))[1];
					System.out.println((i+1) + ".\t" + (x-y) +"\t" + myGroups.get(i));
					File seen = new File("src/users/" + name + "/" + myGroups.get(i) + "/seen");
					FileWriter bw = new FileWriter(seen);
					bw.write(x + "");
					bw.close();
				}
				
			} catch(Exception e){
				
			}
			sgIndex = sgN;

			
		} else if(inputString.split("\\s+")[0].equalsIgnoreCase("rg") && loggedIn == true && rg == false){
			rg = true;
			try{
				rgN = Integer.parseInt(inputString.split("\\s+")[2]); 
				pcN = rgN;
			} catch(Exception e){
				rgN = 5;
			}
		
			curGroup = inputString.split("\\s+")[1];
			if(test){
				File folder = new File("src/users/" + name + "/" +curGroup);
				File[] listOfFiles = folder.listFiles();
			    for (int i = 0; i < listOfFiles.length; i++) {
			    	if (listOfFiles[i].isFile()) {
			    		if(!listOfFiles[i].getName().equals("seen")){
			    			BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
				    		seenPosts.add(br.readLine());
				    		br.close();
				    		test = false;
			    		}
			    		
			    	}
			    }
				   
			}
			 
		    sortPosts =  new ArrayList<String>(allPosts);
			sortPosts.removeAll(seenPosts);
			sortPosts.addAll(seenPosts);
			
			try{
				if(myGroups.contains(inputString.split("\\s+")[1])){
					sendStuff(inputString);
					for(int i = 0; i < rgN; i++){
						if(seenPosts.contains(sortPosts.get(i))){
							System.out.println((i+1) + "  " + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						} else{
							System.out.println((i+1) + " N" + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						}
							
						rgIndex++;
					}

					System.out.println("read groups");		
				} else{
					System.out.println("not subscribed to " + inputString.split("\\s+")[1]);
					rg = false;
				}
				
			} catch(Exception e){
				// handles bad empty arguement for rg and less than N posts 
				if(inputString.split("\\s+").length == 1){
					System.out.println("Must input a subscribed group: rg [groupName]");
					rg = false;					
				} else{
					System.out.println("All posts displayed");
					rgIndex = sortPosts.size();
					rgN = rgIndex;
				}
				
			}	
		} else if(inputString.split("\\s+")[0].equals("s") && (ag == true || sg == true)){
			// handles subscription for ag and sg
			String[] numList = inputString.split("\\s+");

			subscribeUser(numList, agIndex, agN);

			
		} else if(inputString.split("\\s+")[0].equals("u") && (ag == true || sg == true)){
			// handles unsubscribe for ag and sg
			String[] numList = inputString.split("\\s+");
			if(ag)
				unsubscribeUser(numList, agIndex, agN, "ag");
			else if(sg)
				unsubscribeUser(numList, sgIndex, sgN, "sg");
			
		} else if(inputString.equals("n") && (ag || sg || rg || reading)){
			//n command for ag and sg and rg		
			try{
				if(ag && (agIndex < allGroups.size())){	
					for(int i = agIndex; i < agIndex + agN; i++){
						if(myGroups.contains(allGroups.get(i))){
							System.out.println((i+1-agIndex) + ". (s) " + allGroups.get(i));
						} else{
							System.out.println((i+1-agIndex) + ". ( ) " + allGroups.get(i));
						}
					}
					System.out.println(agIndex);
				} else if(sg && (sgIndex < myGroups.size())){					
					for(int i = sgIndex; i < sgIndex + sgN; i++)
						System.out.println((i+1-sgIndex) + ". 0 " + myGroups.get(i));
				} else if(rg && (rgIndex < allPosts.size())){
					System.out.println(rg);
					for(int i = rgIndex; i < rgIndex + rgN; i++){
						if(seenPosts.contains(sortPosts.get(i))){
							System.out.println((i+1-rgIndex) + "  " + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						} else{
							System.out.println((i+1-rgIndex) + " N" + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						}	
					}
				} else if(reading && pcIndex < postComm.size()){
					try{
						for(int i = pcIndex; i < pcIndex + pcN; i++){						
								System.out.println(postComm.get(i));
						}
						pcIndex += pcN;
					} catch(Exception e){
						pcIndex = postComm.size(); 
						System.out.println("All lines displayed");
					}
				}else {
					// the case when all things are displayed
					if(reading){
						for(int i = 0; i < rgN; i++){
							System.out.println((i+1) + " N" + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);	
						}
						rgIndex = 0;
						rg = true;
						reading = false;
						
					} else{
						ag = false;
						sg = false;
						rg = false;
						agIndex = 0;
						agN = 5;
						sgIndex = 0;
						sgN = 5;
						rgIndex = 0;
						rgN = 5;
						System.out.println("exiting menu");
						
					}
					
				}
			} catch(IndexOutOfBoundsException e){

				System.out.println("All groups displayed");
			}
			if(ag || rg || sg){
				agIndex+=agN;
				sgIndex+=sgN;
				rgIndex+=rgN;
			}
				
			
		} else if(inputString.split("\\s+")[0].equals("r") && (rg == true)){
			try{// this case for r [n]
				String postName = sortPosts.get(rgIndex - rgN + Integer.parseInt(inputString.split("\\s+")[1])-1).split("##")[0];
				
				seenPosts.add(sortPosts.get(rgIndex - rgN + Integer.parseInt(inputString.split("\\s+")[1])-1));
				for(int i =0; i < seenPosts.size(); i++)
					System.out.println(seenPosts.get(i));
				System.out.println("Marked as read: " + postName);
				sortPosts.removeAll(seenPosts);
				sortPosts.addAll(seenPosts);
			} catch(Exception e){
				// this case is for r [x-y]
				int x = Integer.parseInt(inputString.split("\\s+")[1].split("-")[0]);
				int y = Integer.parseInt(inputString.split("\\s+")[1].split("-")[1]);
				if(x < 1 || y > rgN){
					System.out.println("Invalid range: [" + x + "-" + y + "]" );
				} else{
					for(int i  = x; i <= y; i++){
						String postName = sortPosts.get(rgIndex - rgN + i-1).split("##")[0];
						
						seenPosts.add(sortPosts.get(rgIndex - rgN + i-1));
						System.out.println("Marked as read: " + postName);

						
					}
					sortPosts.removeAll(seenPosts);
					sortPosts.addAll(seenPosts);
					Set<String> dup = new LinkedHashSet<>(sortPosts);
					sortPosts = new ArrayList<>(dup);
					Set<String> dupa = new LinkedHashSet<>(seenPosts);
					sortPosts = new ArrayList<>(dupa);

				}
				
				
			}
			
		} else if(inputString.matches("[0-9]+") && rg == true){
			// reading a post when the uder inputs a number in the rg
			reading = true;
			System.out.println(rgIndex);
			System.out.println(rgN);
			System.out.println(Integer.parseInt(inputString));


			sendStuff("READING@@@" + sortPosts.get(rgIndex - rgN + Integer.parseInt(inputString)-1) + "@@@" +curGroup);
			
			String postName = allPosts.get(rgIndex - rgN + Integer.parseInt(inputString)-1).split("###")[0];
			seenPosts.add(allPosts.get(rgIndex - rgN + Integer.parseInt(inputString)-1));

			System.out.println(seenPosts.get(0));
			System.out.println("Marked as read: " + postName);
			sortPosts =  new ArrayList<String>(allPosts);
			sortPosts.removeAll(seenPosts);
			sortPosts.addAll(seenPosts);
			
			
			rg = false;
		} else if(inputString.equals("p") && rg == true){
			System.out.println("posting mode");
			Scanner sc = new Scanner(System.in);
			System.out.println("Subject line:");
			String subject = sc.nextLine();
			
			
			String content = "";
			String input = "";
			System.out.println("The content:");
			while (!input.equals(".")){
				input = sc.nextLine();
				if(!input.equals("."))
					content += (input + "\n");
			    
			}
		    System.out.println("Adding post: " + subject + "\n" + content);
		    
		    sendStuff("MAKE_POST " + curGroup + "###" + subject + "###" + name + "###" + content);
		    
			rgIndex = 0;
			try{
					for(int i = 0; i < rgN; i++){
						if(seenPosts.contains(sortPosts.get(i))){
							System.out.println((i+1) + "  " + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						} else{
							System.out.println((i+1) + " N" + "\t" 
									+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
											+ "\t\t"  + sortPosts.get(i).split("##")[0]);
						}
							
						rgIndex++;
					}
			} catch(Exception e){
				System.out.println("All posts displayed");
			}	
	
	    

	
		} else if(inputString.equals("logout")){
			logOutUser(name, myGroups);
			loggedIn = false;
			s.close();
			mainLoop = false;
		} else if(inputString.equals("q") && (ag || rg || sg || reading)){
			if(reading){
				System.out.println("Exiting post");
				reading = false;
				rg = true;
				pcIndex = 0;
				rgIndex = 0;
				for(int i = 0; i < rgN; i++){
					if(seenPosts.contains(sortPosts.get(i))){
						System.out.println((i+1) + "  " + "\t" 
								+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
										+ "\t\t"  + sortPosts.get(i).split("##")[0]);
					} else{
						System.out.println((i+1) + " N" + "\t" 
								+ sortPosts.get(i).split("##")[1].split(": ")[1].split(", ")[1].split(" EST")[0] 
										+ "\t\t"  + sortPosts.get(i).split("##")[0]);
					}
						
					rgIndex++;
				}
			} else{

				ag = false;
				sg = false;
				rg = false;
				agIndex = 0;
				agN = 5;
				sgIndex = 0;
				sgN = 5;
				rgIndex = 0;
				rgN = 5;
				System.out.println("exiting menu");
			}
		} else if(inputString.equals("test")){
			testFunc();
		} else{
			System.out.println("'" + inputString + "' is not a valid command ");	
		}

	}
	
		

	}
	
	
	private static void subscribeUser(String[] numList, int agIndex, int agN) throws IOException {

		try{
			for(int i = 1; i < numList.length; i++){
				String groupName = allGroups.get(agIndex - agN + (Integer.parseInt(numList[i])-1));
				File folder = new File("src/users/" + name + "/" + groupName);
				if(folder.mkdir()){
					System.out.println("Subscribed to: " + groupName);
					myGroups.add(groupName);
					File seen = new File("src/users/" + name + "/" + groupName + "/seen");
					BufferedWriter bw = new BufferedWriter(new FileWriter(seen));
					bw.write("0");
					bw.flush();
					bw.close();

				}
				else
					System.out.println("Already subscribed to " + groupName);
			}					
		} catch(Exception e){
			
		}
	}	
	
	private static void unsubscribeUser(String[] numList, int index, int n, String com) {

		for(int i = 1; i < numList.length; i++){
			String groupName = "";
			System.out.println(com);
			if(com.equals("ag"))
				groupName = allGroups.get(index - n + (Integer.parseInt(numList[i])-1));
			else if(com.equals("sg")){
				System.out.println(index);
				groupName = myGroups.get(index - n + (Integer.parseInt(numList[i])-1));
			}
				 
			File folder = new File("src/users/" + name + "/" + groupName);
			File[] listOfFiles = folder.listFiles();
			
			try {// The directory has to be empty before .delete can be used()
				for(File s: listOfFiles){
					//System.out.println(s.getName() + "deleted");
				    s.delete();
				}
			} catch (Exception e){
				
			}
			
			if(folder.delete()){
				myGroups.remove(groupName);
				System.out.println("Unsubscribed to " + groupName);
			}				
			else
				System.out.println("Not subscribed to " + groupName);
			
		}		
	}

	private static int[] getNumNewPosts(String group){
		int[] xy = new int[2];
		try {
			int x = sendStuff("TEST " + group);
    		BufferedReader br = new BufferedReader(new FileReader("src/users/" + name + "/" + group + "/seen"));
    		
    		int y = Integer.parseInt(br.readLine());
    		br.close();
    		xy[0] = x;
    		xy[1] = y;
    		return xy;

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xy;
	}
	
	
	

	public static void testFunc(){ 
		File folder = new File("src/servergroups");
		File[] listOfFiles = folder.listFiles();
		String listGroup = "";
		    for (int i = 0; i < listOfFiles.length; i++) {
		      /*if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else */
		    	if (listOfFiles[i].isDirectory()) {
		    		listGroup = listGroup + listOfFiles[i].getName() + ";" ;
		    	}
		    }
		    System.out.println(listGroup);
	}
	
	

	// sends info the user server with protocols
	public static int sendStuff(String s) throws IOException{
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(ip);
		
		//InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[10240];
		byte[] receiveData = new byte[10240];
		sendData = s.getBytes();
		
		int portno = Integer.parseInt(port);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portno);
		
		
		clientSocket.send(sendPacket);	// sends to the server
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket); // receive from the server
		
		String modifiedSentence = new String(receivePacket.getData());
		try{
			modifiedSentence = modifiedSentence.substring(0, modifiedSentence.indexOf(0));
		} catch(Exception e){
			System.out.println("??");
		}// parses the string for the server

		// sends to the server login and get the server groups from the server
		if(modifiedSentence.split(" ")[0].equals("LOGIN_GOOD")){
			
			loggedIn = true;
			String username = modifiedSentence.substring(modifiedSentence.indexOf(" "), modifiedSentence.length()).trim();
			System.out.println("Hello: " + username);
			
			openUserNew(username);
			
		} else if(modifiedSentence.split(" ")[0].equals("ALL_GROUPS")){
			// gets all the groups from the server
			allGroups = Arrays.asList(modifiedSentence.split(" ")[1].split(";"));
			
			System.out.println("Got all groups");
		} else if(modifiedSentence.split("\\s+")[0].equals("ALL_POSTS")){
			//gets all the posts for a specific group
			allPosts = Arrays.asList(modifiedSentence.substring(10).split(";"));		
			
			// probably useless
			File folder = new File("src/users/" + name + "/" +curGroup);
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);
			    for (int i = 0; i < listOfFiles.length; i++) {
			    	if (listOfFiles[i].isFile()) {
			    		if(!listOfFiles[i].getName().equals("seen")){
			    			BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
				    		br.readLine(); br.readLine(); br.readLine();
				    		br.close();
				    		//seenPosts.add(listOfFiles[i].getName()+ "##" + br.readLine());
			    		}
			    		
			    	}
			    }
			    
		    sortPosts =  new ArrayList<String>(allPosts);
			sortPosts.removeAll(seenPosts);
			sortPosts.addAll(seenPosts);// checks if already seen posts
			
		} else if(modifiedSentence.split("\\s+")[0].equals("READ_POSTS")){
			postComm = Arrays.asList(modifiedSentence.substring(11).split("\r\n\r\n"));
			
			//opens the 
			try{
				for(int i = 0; i < pcIndex + pcN; i++)
					System.out.println(modifiedSentence.substring(11).split("\r\n\r\n")[i]);	
			} catch(Exception e){
				System.out.println("All lines displayed");
			}
			
			pcIndex = pcIndex + pcN;
		} else if(modifiedSentence.split("\\s+")[0].equals("MAKE_POSTS")){
			//seenPosts = Arrays.asList(modifiedSentence.substring(11).split(";"));
			System.out.println("fdshfbhdfbhdfbfhdfbgfhdf " + modifiedSentence.substring(11));
			seenPosts.add(0, modifiedSentence.substring(11));
			System.out.println("got make posts " + modifiedSentence);
		} else if(modifiedSentence.split("\\s+")[0].equals("TEST")){
			clientSocket.close();
			return Integer.parseInt(modifiedSentence.split("\\s+")[1]);
		}
		
		
		clientSocket.close();
		return -1;
	}
	
	// logs out the user and saves the data
	public static void logOutUser(String name, List<String> myGroups2) throws IOException{
		
		try {
			// saves all the seen posts
			for(int i = 0; i < seenPosts.size(); i++){
				File myFoo = new File("src/users/" + name + "/" + curGroup + "/" + seenPosts.get(i).split("##")[0]);
				FileWriter fooWriter = new FileWriter(myFoo, false);
				fooWriter.write(seenPosts.get(i));
				fooWriter.flush();
				fooWriter.close();
			}
			System.out.println("Logged out");
			
		    
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	// logs in the user by loading the user data
	// checks subscribed by reading all the directories which represents the group
	public static void openUserNew(String fileName) throws IOException{
		File folder = new File("src/users/" + fileName);
		try{
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);
			String subList = "";
			    for (int i = 0; i < listOfFiles.length; i++) {
			    	if (listOfFiles[i].isDirectory()) {
			    		subList = subList + listOfFiles[i].getName()+";" ;
			    	}
			    }
			    myGroups = Arrays.asList(subList.split(";"));
			    myGroups = new ArrayList<>(myGroups);
		} catch(NullPointerException e){
			folder.mkdir();
		}		

		    name = fileName;
	}
	
	
}
