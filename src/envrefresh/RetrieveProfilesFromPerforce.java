package envrefresh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

import com.perforce.p4java.core.file.*;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.impl.mapbased.client.Client;
import com.perforce.p4java.server.*;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

public class RetrieveProfilesFromPerforce {
	// These are Perforce credentials which we'll pull from the designated property
	// files.  
	public static String username = "vgudidevuni@salesforce.com";
	public static String password = "";
	public static String key = "12345";
	public static String serverUri = "p4java://p4proxy-pd1-rep2.soma.salesforce.com:1999";
	public static String propFileName = null;
	public static boolean debug = false;
	
	
	// For testing, use the perforcePath:  //it/qe/Q2R/Docs/POC_WebServiceCallsFromOracleR12/
	public static void main(String[] args) {
		try {
			propFileName = args[0];
			String perforcePath = args[1];
			String output = args[2];
			
			Properties props = new Properties();
			String filename = "config/" + propFileName + ".properties";
			try {
				props.load(new FileInputStream(filename));
				username = props.getProperty("perforce_username", username);
				password = props.getProperty("perforce_password", password);
				serverUri = props.getProperty("perforce_uri", serverUri);
				log("Loaded properties from " + filename);
			} catch (FileNotFoundException e) {
				log("Unable to load the " + filename + " file, will use the default values.");
			}
	
			key = System.getenv("perforce_key");
			
			// Actually this will be done in RetrieveViaMetadataApi, so no need to do this now.
			// Need to check that this user has access to the test environment:
			//String sfUsername = System.getenv("sf_username");
			//String sfPassword = System.getenv("sf_password");
			//checkConnection(endpoint, sfUsername, sfPassword);
			
			// serverUri = System.getenv("perforce_server");
			password = PasswordTool.getDecodedString(key, password);
			
			getFilesFromPerforceDepot(serverUri, username, password, perforcePath, String.format("%s/", output));
		}
		catch (Exception e) {
			print(e.getMessage());
			System.exit(-1);
		}
	}
	
	public static void getFilesFromPerforceDepot(String serverUriString, String user , String passwd, String location ,String destination) throws Exception { 

		if (user == null) { 
			System.out.println("Perforce user could not be determined!"); 
			throw new Exception("Can't determine your perforce username"); 
		} else { 
			System.out.println("Found out that you're " + user + " via properties"); 
		} 

		if (passwd == null) { 
			System.out.println("Perforce password could not be determined!"); 
			throw new Exception("Can't determine your perforce password"); 
		}else { 
			System.out.println("Using your perforce-password set by properties"); 
		} 
		if (serverUriString == null) { 
			System.out.println("Perforce server could not be determined!"); 
			throw new Exception("Can't determine your perforce server"); 
		}else { 
			System.out.println("Using your server set by properties"); 
		} 
		if (location == null) { 
			System.out.println("Perforce location could not be determined!"); 
			throw new Exception("Can't determine your perforce location"); 
		}else { 
			System.out.println("Using location from jenkins parameter "); 
		} 
		if (destination == null) { 
			System.out.println("destination location could not be determined!"); 
			throw new Exception("Can't determine your destination location"); 
		}else { 
			System.out.println("Using destination from jenkins "); 
		} 


		// Instantiating the server
		IServer p4Server = ServerFactory.getServer(serverUriString, null);
		p4Server.connect();

		// Authorizing
		p4Server.setUserName(user);
		p4Server.login(passwd);

		// check server connected successfully
		IServerInfo serverInfo = p4Server.getServerInfo();

		System.out.println("Server info  " + serverInfo );
		System.out.println("Server ServerLicense  " +serverInfo.getServerLicense());
		System.out.println("Server working directory  " +p4Server.getWorkingDirectory() );


		// Creating new client
		IClient tempClient = new Client();

		// Setting up the name and the folder for client
		tempClient.setName("tempClient" + UUID.randomUUID().toString().replace("-", ""));
		tempClient.setRoot(location);
		tempClient.setServer(p4Server);

		// Setting the client as the current one for the server
		p4Server.setCurrentClient(tempClient);

		// Registering the new client on the server
		System.out.println(p4Server.createClient(tempClient));


		// Get client root location		  
		String  filePaths =tempClient.getRoot();
		System.out.println("Client  root  " +tempClient.getRoot() );
		FileOutputStream destStream = null;


		try {
			List<String> list = new ArrayList<String>();

			// Step 1: get depot files

			List<IFileSpec> depotFiles = p4Server.getDepotFiles(FileSpecBuilder.makeFileSpecList(filePaths + "*"), false); 

			// Add files to return list
			if (depotFiles != null) 
			{
				if((depotFiles.isEmpty() == false) &&  
						(depotFiles.get(0) != null) &&
						(depotFiles.get(0).getAction() != null)) {

					for ( Iterator<IFileSpec> iterator = depotFiles.iterator(); iterator.hasNext(); ) {


						String path = iterator.next().getDepotPathString();
						if (path != null) {

							if(path.toLowerCase().endsWith(".profile")) {
								System.out.println("path of the file   " + path);
								String[] parts = path.split("/", -1);
								list.add(parts[parts.length - 1]);
							}
						}
					}
				} else if(depotFiles.isEmpty() == true) 
				{
					// No files in depot... 
					System.out.println("Empty depotFiles"); 
					throw new Exception("Empty depot - no files found in depot " ); 

				}
			}

			// Step 2: transfer files from depot to destination folder 
			if ((list != null))
			{
				if(list.size() == 0) throw new Exception("No profile files found in depot");

				System.out.println("Number profile files found in depot = " + list.size());					

				for(int i = 0; i < list.size(); i++) {

					System.out.println(list.get(i));
					File file = new File(destination+list.get(i));
					file.setExecutable(true);
					file.setReadable(true);
					file.setWritable(true);
					tempClient.setRoot(location+list.get(i));
					String  filePath =tempClient.getRoot();
					try {
						List<IFileSpec> fileList = p4Server.getDepotFiles(
								FileSpecBuilder.makeFileSpecList(filePath), false);
						InputStream p4Content =   p4Server.getFileContents(fileList, false, true);	


						destStream = new FileOutputStream(destination+list.get(i)); 

						byte[] buffer = new byte[ 0xFFFF ]; 
						for ( int len; (len = p4Content.read(buffer)) != -1; ) 
							destStream.write( buffer, 0, len ); 
						destStream.close();
					} catch (P4JavaException e) {

						e.printStackTrace();
					} catch ( IOException e ) { 

						e.printStackTrace(); 
					} finally {
						if (destStream != null) { destStream.close(); }
					}

				}
			}


		} catch (ConnectionException e) {
			e.printStackTrace();
			throw new IOException("Perforce connection problem");
		} catch (AccessException e) {
			e.printStackTrace();
			throw new IOException("Perforce access problem");
		}
		System.out.println(p4Server.deleteClient(tempClient.getName(), false));
	}

	public static void print(String s) {
		System.out.println(s);
	}
	
    public static void log(String s) {
    	if (debug) {
    		System.out.println(s);
    	}
    }

	public static void checkConnection(String endpoint, String username, String password) {
		try {
			ConnectorConfig cc = new ConnectorConfig();
			cc.setUsername(username);
			cc.setPassword(password);
			cc.setAuthEndpoint(endpoint);			
			PartnerConnection sfdc = new PartnerConnection(cc);
			sfdc.login(username, password).getSessionId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to log into Salesforce: " + e.getMessage());
		}
	}

}
