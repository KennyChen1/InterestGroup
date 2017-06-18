import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestServer{
	public static void main(String args[]) throws Exception	{
		boolean notHosted = true;
		int portno = 5000;
		DatagramSocket serverSocket = null;
		while(notHosted){
			try{
				serverSocket = new DatagramSocket(portno);
				System.out.println("running on port " + portno);
				notHosted = false;
			} catch(java.net.BindException e){
				portno++;
			}
		}
		
		
		byte[] receiveData;
		byte[] sendData;
		while(true){
			receiveData = new byte[10000];
			sendData = new byte[10000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			System.out.println("RECEIVED: " + sentence);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			
			String receivedString = new String(receiveData, "UTF8");
			receivedString = receivedString.substring(0, receivedString.indexOf(0));

			if(receivedString.split(" ")[0].equalsIgnoreCase("login")){
				String username = receivedString.substring(receivedString.indexOf(" "), receivedString.length());
				sendData = ("LOGIN_GOOD " + username).getBytes();
				
			} else if(receivedString.equals("ag") || receivedString.split(" ")[0].equalsIgnoreCase("ag")){
				
				//BufferedReader reader = null;
				
				try {
					
					File folder = new File("src/servergroups");
					File[] listOfFiles = folder.listFiles();
					Arrays.sort(listOfFiles);
					String listGroup = "";
					    for (int i = 0; i < listOfFiles.length; i++) {
					      /*if (listOfFiles[i].isFile()) {
					        System.out.println("File " + listOfFiles[i].getName());
					      } else */
					    	if (listOfFiles[i].isDirectory()) {
					    		listGroup = listGroup + listOfFiles[i].getName() + ";" ;
					    		System.out.println("Directory " + listOfFiles[i].getName());
					    	}
					    }
				    
					/*reader = new BufferedReader(new FileReader("src/servergroups/groups"));*/
				    sendData = ("ALL_GROUPS " + listGroup).getBytes();
				    
				    System.out.println("content" + sendData);
				    
				} catch (Exception e) {
				    System.out.println("no groups");
				}
			} else if(receivedString.split("\\s+")[0].equalsIgnoreCase("rg")){
				try {					
					File folder = new File("src/servergroups/" + receivedString.split("\\s+")[1]);
					File[] listOfFiles = folder.listFiles();
					Arrays.sort(listOfFiles);
					String listGroup = "";
					    for (int i = 0; i < listOfFiles.length; i++) {
					    	if (listOfFiles[i].isFile()) {
					    		BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
					    		br.readLine(); br.readLine(); br.readLine();
					    		listGroup = listGroup + listOfFiles[i].getName()+ "##" + br.readLine() + ";" ;
					    		System.out.println("Directory " + listOfFiles[i].getName());
					    	}
					    }
				    
				    sendData = ("ALL_POSTS " + listGroup).getBytes();
				    
				    System.out.println("ALL_POSTS" + sendData.toString());
				    
				} catch (Exception e) {
				    System.out.println("no groups");
				}
			} else if(receivedString.split("@@@")[0].equalsIgnoreCase("READING")){
				//This gets the post when the user enters a number in list reading mode, rg
				try {					
					File folder = new File("src/servergroups/" + receivedString.split("@@@")[2] + 
							"/" + receivedString.split("@@@")[1].split("##")[0]);
					String listGroup = "";
			    		BufferedReader br = new BufferedReader(new FileReader(folder));
			    		String str = "";
			    		while((str = br.readLine()) != null){
			    			
				    		listGroup = listGroup + str + "\r\n\r\n" ;
			    		}
			    		System.out.println("Directory " + listGroup);
				    
				    sendData = ("READ_POSTS " + listGroup).getBytes();
				    
				    System.out.println("ALL_POSTS" + sendData.toString());
				    
				} catch (Exception e) {
					e.printStackTrace();
				    System.out.println("no groups");
				}
			}  else if(receivedString.split("\\s+")[0].equals("MAKE_POST")){
				try {
					String[] arg = receivedString.substring(10).split("###");
					// 0 is group
					// 1 is post tile
					// 2 is name
					// 3 is content
					File folder = new File("src/servergroups/" + arg[0] + 
							"/" + arg[1]);
						//System.out.println(receivedString.split("@@@")[0].substring(10));
						BufferedWriter bw = new BufferedWriter(new FileWriter(folder));
			    		bw.write("Group: " + arg[0] + "\n");
			    		bw.write("Subject: " + arg[1] + "\n");
			    		bw.write("Name: " + arg[2] + "\n");
			    		DateFormat df = new SimpleDateFormat("E, MMM d HH:mm:ss z YYYY");
			    		Calendar calobj = Calendar.getInstance();
			    		bw.write("Date: " + df.format(calobj.getTime()) + "\n\n");
			    		bw.write(arg[3]);
			    		bw.flush();
			    		bw.close();
			   		
			    	sendData = ("MAKE_POSTS " + arg[1] + "##" + df.format(calobj.getTime())).getBytes();
				    
				} catch (Exception e) {
					e.printStackTrace();
				}/*
				
				try {					
					File folder = new File("src/servergroups/" + receivedString.split("@@@")[2]);
					File[] listOfFiles = folder.listFiles();
					Arrays.sort(listOfFiles);
					String listGroup = "";
					    for (int i = 0; i < listOfFiles.length; i++) {
					    	if (listOfFiles[i].isFile()) {
					    		BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
					    		br.readLine(); br.readLine(); br.readLine();
					    		listGroup = listGroup + listOfFiles[i].getName()+ "##" + br.readLine() + ";" ;
					    		System.out.println("Directory " + listOfFiles[i].getName());
					    	}
					    }
				    
				    sendData = ("MAKE_POSTS " + listGroup).getBytes();
				} catch(Exception e){
					e.printStackTrace();
				}
				*/
				
			} else if(receivedString.split("\\s+")[0].equals("TEST")){
				sendData = ("TEST " + getNumberOfPosts(receivedString.split("\\s+")[1])).getBytes();
			} else{
			
				String capitalizedSentence = sentence.toUpperCase();
				sendData = capitalizedSentence.getBytes();
			}
			
			
			
			
			
			
			DatagramPacket sendPacket =
			new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}
		
	}
	
	private static int getNumberOfPosts(String group) throws IOException{
		
		
		File folder = new File("src/servergroups/" + group);
		File[] listOfFiles = folder.listFiles();
		/*
		int totalPosts = 0;
		String fileText = "";
		for (int i = 0; i < listOfFiles.length; i++) {
	    	if (listOfFiles[i].isFile()) {
	    		BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
	    		String str = "";
	    		while((str = br.readLine()) != null){
	    			fileText = fileText + str;
	    		}
	    		br.close();	    		
	    	}
	    }
		Pattern p = Pattern.compile("\f\b");
		Matcher m = p.matcher(fileText);
		
		while(m.find()){
			totalPosts++;
		}*/
		return listOfFiles.length;
	}
}