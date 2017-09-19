package envrefresh;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.DeployDetails;
import com.sforce.soap.metadata.DeployMessage;
import com.sforce.soap.metadata.DeployOptions;
import com.sforce.soap.metadata.DeployResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
//import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.*;

/**
 * Copied from RetrieveProfilesViaSoql. The functionality of the main method is
 * to wrap the ant-salesforce.jar functionality
 * 
 * 
 */
public class RetrieveViaMetadataApi implements Runnable {
	public static String username = "vgudidevuni@salesforce.com.orangeuat";
	public static String password = "";
	public static String endpoint = "https://login.salesforce.com/services/Soap/u/36.0";
	public static String key = "01db1f3e49405d7c";
	public static String propFileName = null;
	public static boolean debug = true;

	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	public static String sessionId;
	public static PartnerConnection sfdc;
	public static PartnerConnection sourceSfdc;
	public static PartnerConnection destinySfdc;
	public static MetadataConnection sfdcMetadataRetrive;
	public static MetadataConnection sfdcMetadataDeploy;
	// public static Hashtable<String, ArrayList<String>> dataStructure = new
	// Hashtable<String, ArrayList<String>>();
	public static Map<String, String> sourceOrg = new HashMap<String, String>();
	public static Map<String, String> destinyOrg = new HashMap<String, String>();
	public static String retrieveTarget = "test";
	public static String metdataSyncRecord;
	public static Map<String, String> metdataSyncRecordResults = new HashMap<String, String>();
	public static AsyncResult ar = null;
	public static String zipFilePath = String.format("%s/retrieve.zip", "test");
	public static String backupZipFilePath = String.format("%s/retrieve.zip", "test");
	
	public static String RELEASE_VERSION = "37.0";
	// public static MetadataConnection sfdcMetadata;

	public static void main(String[] args) throws Exception {
		try {
			// Connect to the org (deployed org) from which the Unpackaged
			// Manifest(attachment) have to be
			// retrieved
			log("Connect to the org (deployed org)  from which the Unpackaged Manifest(attachment) have to be  retrieved");
			sfdc = connectSFOrg("TestJerry"); // connecting to Aloha (code
												// deployed org)

			// Fetches the Unpackaged Manifest from the org.
			generateUnpackagedManifest(sfdc, retrieveTarget);
		} catch (Exception e) {
			log("ERROR: Error in main.");
			log(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/*
	 * Based on the Source and Destination Orgs. Set the Org Credentials with
	 * Properties file.
	 */

	public static PartnerConnection connectSFOrg(String orgName) {

		log("######################################### ORG:" + orgName);
		propFileName = orgName.toLowerCase(); 
		Properties props = new Properties();
		String filename = "config/" + propFileName + ".properties";
		try {
			props.load(new FileInputStream(filename));
			username = props.getProperty("sfadmin_username", username);
			password = props.getProperty("sfadmin_password", password);
			endpoint = props.getProperty("sf_soapEndpoint", endpoint);
			log("Loaded properties from : " + filename);
			System.out.println("Loaded properties from " + filename);			
			log("Username: " + username);
			log("Password: " + password);
			log("key: " + key);
			password = PasswordTool.getDecodedString(key, password);
			

			//log("Decoded Password: " + password);

			//System.out.println("Logging into Salesforce. UserName=" + username + " and Pwd=" + password);

			PartnerConnection psfdc = connect(endpoint, username, password);
			return psfdc;
		} catch (GeneralSecurityException e) {
			
			log("ERROR : Unable to login in org=" + orgName + " with userId= " + username);
			e.printStackTrace();
			System.exit(-1);
		} catch (FileNotFoundException e) {
			log("ERROR: Unable to load the " + filename + " file, will use the default values.");
			System.exit(-1);
		} catch (Exception e) {
			log("Exception in connecting to Org connectSFOrg() ");
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	/*
	 * Makes the actual Partner Connection based on the set Properties file to
	 * the appropriate Org
	 */

	public static PartnerConnection connect(String endpoint, String username, String password) {
		PartnerConnection sfdcTemp = null;
		try {
			ConnectorConfig cc = new ConnectorConfig();
			cc.setUsername(username);
			cc.setPassword(password);
			cc.setAuthEndpoint(endpoint);
			sfdcTemp = new PartnerConnection(cc);
			log("Success");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to log into Salesforce: " + e.getMessage());
		}

		return sfdcTemp;
	}

	/**
	 * Retrieve pretty much everything via the metadata api, and unzip the
	 * resulting file.
	 * 
	 * @param objs
	 *            Hashtable mapping metadata type to the list of names for that
	 *            type
	 * @param profiles
	 *            List of profiles for which we want the metadata
	 * @return Hashtable mapping from profile filenames to XML
	 * @throws Exception
	 *             Deletes the unnecessary files stored Deploys the zip file
	 *             into Destination org through Metadata Api
	 * 
	 * 
	 */
	public static Hashtable<String, String> getMetadata(Hashtable<String, ArrayList<String>> objs,
			String retrieveTarget) throws Exception {
		System.out.println("Forming metadata API request.");
		ArrayList<PackageTypeMembers> ptms = new ArrayList<PackageTypeMembers>();
		for (String metadataType : objs.keySet()) {
			PackageTypeMembers ptmObjs = new PackageTypeMembers();
			ptmObjs.setMembers(objs.get(metadataType).toArray(new String[0]));
			ptmObjs.setName(metadataType);
			ptms.add(ptmObjs);
		}

		com.sforce.soap.metadata.Package p = new com.sforce.soap.metadata.Package();
		p.setVersion(RELEASE_VERSION); // Setting the Version
		p.setTypes(ptms.toArray(new PackageTypeMembers[0]));
		RetrieveRequest rr = new RetrieveRequest();
		rr.setUnpackaged(p);
		ar = sfdcMetadataRetrive.retrieve(rr);

		// Kick off a thread that waits until the result returns:
		SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss ");
		System.out.printf("Downloading metadata... %s\n", sdf.format(Calendar.getInstance().getTime()));

		Thread t = new Thread(new RetrieveViaMetadataApi());
		t.start();
		t.join();

		System.out.println("Download complete.");
		
		RetrieveRequest rr1 = new RetrieveRequest();
		rr1.setUnpackaged(p);
		ar = sfdcMetadataDeploy.retrieve(rr1);
		takeBackup();
		System.out.println("Backup complete.");

		unzipAndZipDeplomentFiles(zipFilePath);
		File toDelete = new File("test/FileShape");
		deleteFileDirectory(toDelete);

		System.out.println("Zip file Path to deployments is IS IT SAME? : " + zipFilePath);
		try (FileInputStream fis = new FileInputStream(zipFilePath)) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[1024];
				int read = -1;
				while (fis.available() > 0) {
					//
					read = fis.read(buffer);
					baos.write(buffer, 0, read);
				}
				deployPackage(baos);   // Final Deploy -- Vishnu
			}

		} catch (IOException exp) {
			exp.printStackTrace();
		}

		return null;
	}

	public static String join(String s, List<String> sc) {
		StringBuffer retval = new StringBuffer();
		if (sc.size() < 1) {
			return null;
		}
		for (int i = 0; i < sc.size() - 1; i++) {
			retval.append(sc.get(i));
			retval.append(s);
		}
		return retval.toString();
	}

	@Override
	public synchronized void run() {
		long timeout = 5000; // sleep n/1000 seconds between checks
		boolean complete = false;
		try {
			RetrieveResult rr = null;
			do {
				SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss ");
				System.out.printf("Waiting for completion... %s\n", sdf.format(Calendar.getInstance().getTime()));

				Thread.sleep(timeout);
				rr = sfdcMetadataRetrive.checkRetrieveStatus(ar.getId(), true);
				complete = rr.isDone();
			} while (!complete);
			byte[] zipFileContents = rr.getZipFile();
			if (zipFilePath != null) {
				FileOutputStream fos = new FileOutputStream(zipFilePath);
				fos.write(zipFileContents);
				fos.close();
			}
		} catch (InterruptedException | ConnectionException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(String s) {
		System.out.print("Error1234667654: " + s);
	}

	public static void log(String s) {
		if (debug) {
			System.out.println(s);
		}
	}

	public static String join(String[] sa) {
		return join(sa, ",");
	}

	public static String join(String[] sa, String joinWith) {
		return join(sa, joinWith, null);
	}

	public static String join(String[] sa, String joinWith, String wrap) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < sa.length; i++) {
			String s = sa[i];
			if (wrap != null) {
				s = String.format("%2$s%1$s%2$s", s, wrap);
			}
			sb.append(s);
			if (i < sa.length - 1) {
				sb.append(joinWith);
			}
		}
		return sb.toString();
	}

	/*
	 * Takes the Partner connection as argument and Makes a metada connection to
	 * the connected org to do the actual retrieval and Deployments
	 */

	public static MetadataConnection metadataConnect(PartnerConnection pc) throws ConnectionException {
		try {
			ConnectorConfig cc = pc.getConfig();

			LoginResult lr = pc.login(cc.getUsername(), cc.getPassword());
			ConnectorConfig cc2 = new ConnectorConfig();
			cc2.setSessionId(cc.getSessionId());
			cc2.setServiceEndpoint(lr.getMetadataServerUrl());
			return new MetadataConnection(cc2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to log into Salesforce: " + e.getMessage());
		}
	}

	public static QueryResult query(PartnerConnection stub, String query) throws RemoteException, ConnectionException {
		log("Query:\n" + query);
		return stub.query(query);
	}

	private static DocumentBuilder documentBuilder;

	private static Document fileToDocument(String filename)
			throws ParserConfigurationException, SAXException, IOException {
		if (documentBuilder == null) {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		FileInputStream fis = new FileInputStream(filename);
		return documentBuilder.parse(fis);
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

	/*
	 * 
	 * the code logic begins from here Queries the Salesforce Metadata Sync
	 * Object record that are ready to sync
	 * 
	 * And the Queries each of the Records attachemnts
	 */
	public static void generateUnpackagedManifest(PartnerConnection connectSf, String pretrieveTarget) {
		try {
			String query = "select Id, CreatedDate, Source_Org__c, Destination_Org__c from Metadata_Sync__c where IsSynced__c = 'Ready To Sync'";
			String strIds = "";
			QueryResult result = connectSf.query(query);

			System.out.println("Salesforce Object Structure: " + result);
			if (result.getSize() > 0) {
				boolean done = false;
				while (!done) {

					for (SObject record : result.getRecords()) {
						if (strIds.equals("")) {
							strIds = "'" + (String) record.getField("Id") + "'";
							sourceOrg.put((String) record.getField("Id"), (String) record.getField("Source_Org__c"));
							destinyOrg.put((String) record.getField("Id"),
									(String) record.getField("Destination_Org__c"));
							System.out.println("Map is holding" + (String) record.getField("Id") + "and the value"
									+ sourceOrg.get((String) record.getField("Id")));
						} else {
							strIds += ",'" + (String) record.getField("Id") + "'";
							sourceOrg.put((String) record.getField("Id"), (String) record.getField("Source_Org__c"));
							destinyOrg.put((String) record.getField("Id"),
									(String) record.getField("Destination_Org__c"));
							System.out.println("Map is holding" + (String) record.getField("Id") + "and the value"
									+ sourceOrg.get((String) record.getField("Id")));
						}
						System.out.println("###### record.Id: " + (String) record.getField("Id"));
						System.out.println("###### record.CreatedDate: " + (String) record.getField("CreatedDate"));
					}
					if (result.isDone()) {
						done = true;
					} else {
						result = connectSf.queryMore(result.getQueryLocator());
					}
				}

				System.out.println("###### Metadata Sync all Records: " + strIds);
				String attachQuery = "SELECT Id,Body,Name,ParentId FROM Attachment where ParentId in (" + strIds
						+ ") ORDER BY Name Asc";

				QueryResult queryResultsAttach = connectSf.query(attachQuery);
				if (queryResultsAttach.isDone()) {
					System.out.println("1st If Done is printing");
					writeAttachmentsToXMLFiles(queryResultsAttach);
					stampSyncedMedataSyncRecords(strIds);
				} else {
					System.out.println("1st Else If Done is printing");
					writeAttachmentsToXMLFiles(queryResultsAttach);
					queryAllAvailableAttachments(queryResultsAttach, strIds);
				}
			} else {
				log("No pending changes to be deployed. Record count=0");
			}

		} catch (Exception ex) {
			System.out.println("Exception in main : " + ex);
			ex.printStackTrace();
		}

	}

	/*
	 * Below method was recursively called until all the attachments are
	 * queried.
	 */

	public static void queryAllAvailableAttachments(QueryResult qRes, String mdIds) throws Exception {
		if (qRes.isDone()) {
			// qRes = pc.queryMore(qRes.getQueryLocator());
			// writeAttachmentsToXMLFiles(qRes, pretrieveTarget, pc);
			System.out.println("Entered last If Done is printing");
			stampSyncedMedataSyncRecords(mdIds);
		} else {
			System.out.println("Max elses Done is printing");
			qRes = sfdc.queryMore(qRes.getQueryLocator());
			writeAttachmentsToXMLFiles(qRes);
			queryAllAvailableAttachments(qRes, mdIds);

		}

	}

	/*
	 * Text attachemnts are converted into XML files and are stored in the local
	 * directory
	 */

	public static void writeAttachmentsToXMLFiles(QueryResult qRs) throws Exception {
		// List<String> listFileNames
		for (SObject attachmentMore : qRs.getRecords()) {
			String attBody = (String) attachmentMore.getField("Body");
			byte[] attBody2 = Base64.getDecoder().decode(attBody);
			String attBody3 = new String(attBody2, UTF_8);
			String fileFullNameWithExt = (String) attachmentMore.getField("Name");
			String fileFullName = fileFullNameWithExt.split("\\.")[0];
			String fileName1 = fileFullName;
			System.out.println(fileName1);
			String filename2 = fileName1 + ".xml";
			System.out.println("Printing Map Size" + sourceOrg.size());
			String selectedSourceOrg1 = sourceOrg.get((String) attachmentMore.getField("ParentId"));
			String selectedDestinationOrg = destinyOrg.get((String) attachmentMore.getField("ParentId"));
			// selectedSourceOrg =
			// sourceOrg.get((String)attachmentMore.getField("Id"));
			System.out.println("Sourceeeeeeee: " + selectedSourceOrg1);
			java.io.FileWriter fw = new java.io.FileWriter(fileName1 + ".xml");
			fw.write(attBody3);
			fw.close();
			String zipFileName = "%s/" + fileName1 + ".zip";
			String backupZipFileName = "%s/" +"Backup-" + fileName1 + ".zip";
			zipFilePath = String.format(zipFileName, "test"); // System.getenv("BUILD_NUMBER"));
			backupZipFilePath = String.format(backupZipFileName, "test");
			System.out.println("Zip file Path is: " + zipFilePath);
			metdataSyncRecord = (String) attachmentMore.getField("ParentId");
			System.out.println("Metadata Synced Record: " + metdataSyncRecord);
			if (metdataSyncRecordResults.get((String) attachmentMore.getField("ParentId")) != null) {
				String deployVal = metdataSyncRecordResults.get((String) attachmentMore.getField("ParentId"))
						+ fileName1;
				metdataSyncRecordResults.replace((String) attachmentMore.getField("ParentId"), deployVal);
			}
			if (metdataSyncRecordResults.get((String) attachmentMore.getField("ParentId")) == null) {
				metdataSyncRecordResults.put((String) attachmentMore.getField("ParentId"), fileName1);
			}
			retrieveZipFileFromXmlFile(filename2, selectedSourceOrg1, selectedDestinationOrg);
			/*
			 * Carry the Record Id and file Name
			 */

		}
	}

	/*
	 * 
	 * Passing the XML attachemnts to the source org from the Metadata Sync
	 * Record and Made a Partenr and Metada Connections to that org and
	 * Retrieved the zip file that can be deployed into the server.
	 */

	public static void retrieveZipFileFromXmlFile(String pdataFileName, String srcOrg, String dstOrg) throws Exception {
		System.out.println("Building up retrieve.xml");

		// Pull the metadata names from the retrieve.xml-formatted file
		Hashtable<String, ArrayList<String>> dataStructure = new Hashtable<String, ArrayList<String>>();
		String filename2 = pdataFileName;
		System.out.println("Retrival file Input XML: " + filename2);
		XPath xp = XPathFactory.newInstance().newXPath();
		Document doc = fileToDocument(filename2);
		System.out.println("Printing the document: " + doc);
		NodeList typeNodes = (NodeList) xp.evaluate("/Package/types/name/text()", doc, XPathConstants.NODESET);
		for (int i = 0; i < typeNodes.getLength(); i++) {
			String metadataType = typeNodes.item(i).getNodeValue();
			System.out.printf("Working on %s metadata type.\n", metadataType);
			NodeList nameNodes = (NodeList) (xp.evaluate("../../members/text()", typeNodes.item(i),
					XPathConstants.NODESET));
			for (int j = 0; j < nameNodes.getLength(); j++) {
				String name = nameNodes.item(j).getNodeValue();
				if (!dataStructure.containsKey(metadataType)) {
					dataStructure.put(metadataType, new ArrayList<String>());
				}
				// System.out.printf("Adding %s to the %s list\n", name,
				// metadataType);
				dataStructure.get(metadataType).add(name);
			}
		}
		System.out.println("Is same source org coming:" + srcOrg);
		sourceSfdc = connectSFOrg(srcOrg);
		System.out.println("Connecting to Metadata API");
		sfdcMetadataRetrive = metadataConnect(sourceSfdc);

		// restricting the destination org Supportforce TestJerry for testing
		//dstOrg = "Supportforce TestJerry";

		destinySfdc = connectSFOrg(dstOrg);
		System.out.println("Connecting to Metadata API");
		sfdcMetadataDeploy = metadataConnect(destinySfdc);

		getMetadata(dataStructure, retrieveTarget);
	}

	/*
	 * After the deployment is completed, Marks the record with Deployment
	 * status
	 */
	public static void stampSyncedMedataSyncRecords(String recIds) {
		try {
			String recId = recIds.replace("'", "");
			String[] eachRecId = recId.split(",");
			SObject[] metadataUnsyncedRecords = new SObject[eachRecId.length];
			for (int i = 0; i < eachRecId.length; i++) {
				SObject updateMetadataSynced = new SObject();
				updateMetadataSynced.setType("Metadata_Sync__c");

				updateMetadataSynced.setId(eachRecId[i]);
				String deploymentStatus = metdataSyncRecordResults.get(eachRecId[i]);
				updateMetadataSynced.setField("Target_Org_Deployment_Status__c", deploymentStatus);
				if (deploymentStatus.contains("Failed")) {
					updateMetadataSynced.setField("IsSynced__c", "False");
				} else {
					updateMetadataSynced.setField("IsSynced__c", "True");
				}
				metadataUnsyncedRecords[i] = updateMetadataSynced;
			}
			SaveResult[] saveResults = sfdc.update(metadataUnsyncedRecords);
			for (int j = 0; j < saveResults.length; j++) {
				System.out.println("\nItem: " + j);
				if (saveResults[j].isSuccess()) {
					System.out
							.println("Metadata Sync Record with an ID of " + saveResults[j].getId() + " was updated.");
				}
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

	public static void uploadZipFileAttachemnts() {
		/*
		 * 
		 * No Attachment Object
		 */
	}
	
	public static void takeBackup() {
		long timeout = 5000; // sleep n/1000 seconds between checks
		boolean complete = false;
		try {
			RetrieveResult rr = null;
			do {
				SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat
						.getDateInstance();
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss ");
				System.out.printf("Waiting for completion... %s\n",
						sdf.format(Calendar.getInstance().getTime()));

				Thread.sleep(timeout);
				rr = sfdcMetadataDeploy.checkRetrieveStatus(ar.getId(), true);
				complete = rr.isDone();
			} while (!complete);
			byte[] zipFileBackupContents = rr.getZipFile();
			if (backupZipFilePath != null) {
				FileOutputStream fos = new FileOutputStream(backupZipFilePath);
				fos.write(zipFileBackupContents);
				fos.close();
			}
			
		} catch (InterruptedException | ConnectionException | IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 
	 * The actual deployement of the zip file into Destination org by making a
	 * partner and Metadata connection with the destination org from the
	 * Metadata Sync Record.
	 */

	public static void deployPackage(ByteArrayOutputStream baos) throws ConnectionException, InterruptedException {
		DeployOptions deployOptions = new DeployOptions();
		deployOptions.setSinglePackage(true);
		deployOptions.setRollbackOnError(false);
		deployOptions.setCheckOnly(false);

		// AsyncResult ar =
		// sfdcMetadata.deploy(Base64.getEncoder().encodeToString(baos.toByteArray()).getBytes(),
		// deployOptions);
		AsyncResult ar = sfdcMetadataDeploy.deploy(baos.toByteArray(), deployOptions);
		String asyncProcessId = ar.getId();
		DeployResult dr = null;
		do {
			Thread.sleep(5000);
			dr = sfdcMetadataDeploy.checkDeployStatus(asyncProcessId, true);
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss ");
			System.out.printf("Waiting for completion... %s\n", sdf.format(Calendar.getInstance().getTime()));
		} while (!dr.isDone());

		if (dr.getSuccess()) {
			log("Package Success\n");
			// metdataSyncRecord = (String)attachmentMore.getField("ParentId");
			if (metdataSyncRecordResults.get(metdataSyncRecord) != null) {
				String successVal = metdataSyncRecordResults.get(metdataSyncRecord) + ": Success\n";
				metdataSyncRecordResults.put(metdataSyncRecord, successVal);
			}

		} else {
			log("Package Failure\n");
			String statusCode = dr.getErrorStatusCode() != null ? dr.getErrorStatusCode().toString()
					: "No code returned";
			System.out.printf("Package Status Code: %s\nPackage Error Message: %s\n", statusCode, dr.getErrorMessage());
			if (metdataSyncRecordResults.get(metdataSyncRecord) != null) {
				System.out.println("Metadata Synced Record: " + metdataSyncRecord);
				System.out.println(
						"Metadata Synced Entire Value from map: " + metdataSyncRecordResults.get(metdataSyncRecord));
				String errorVal = metdataSyncRecordResults.get(metdataSyncRecord) + ": Failed -- Error Message: "
						+ dr.getErrorMessage() + "\n";
				metdataSyncRecordResults.replace(metdataSyncRecord, errorVal);
				System.out.println("Metadat Record: " + metdataSyncRecord + "Deployment Status: " + errorVal);
			}
		}
		DeployDetails dd = dr.getDetails();
		System.out.println("\n#########   Component Successes:   #########   ");
		System.out.println("Component Type, Full Name, Problem, Is Success, Is Created, Is Changed");
		for (DeployMessage dm : dd.getComponentSuccesses()) {
			System.out.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", dm.getComponentType(), dm.getFullName(),
					dm.getProblem(), String.valueOf(dm.isSuccess()), String.valueOf(dm.isCreated()),
					String.valueOf(dm.isChanged()));
		}
		System.out.println("\n#########   Component Failures:   #########   ");
		System.out.println("Component Type, Full Name, Problem, Is Success, Is Created, Is Changed");
		for (DeployMessage dm : dd.getComponentFailures()) {
			System.out.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", dm.getComponentType(), dm.getFullName(),
					dm.getProblem(), String.valueOf(dm.isSuccess()), String.valueOf(dm.isCreated()),
					String.valueOf(dm.isChanged()));
		}
	}

	/*
	 * The file received the Retrieval Metod is not suitable for the Deployments
	 * So change the structure of the file to remove the top direcory and zip
	 * the contents back.
	 */

	public static void unzipAndZipDeplomentFiles(String bigZipFilePath) throws Exception {
		System.out.println("File I know: " + bigZipFilePath);
		File dir = new File(bigZipFilePath);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(bigZipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				System.out.println("file Names in ze: " + fileName);
				File newFile = new File("test/FileShape" + File.separator + fileName);
				System.out.println("New File: " + newFile);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();

			// Now zip the file from the Unpackaged folder.
			File f1 = new File("test/FileShape/unpackaged/");
			File f2 = new File(zipFilePath);
			zip(f1, f2);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * Zip the files back
	 */

	public static void zip(File directory, File zipfile) throws IOException {
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try {
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while (!queue.isEmpty()) {
				directory = queue.pop();
				for (File kid : directory.listFiles()) {
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory()) {
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		} finally {
			res.close();
		}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	/*
	 * Deletes the folder structure created to generate a zip file suitable for
	 * deployments.
	 */

	public static boolean deleteFileDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				System.out.println("Deleting File Name: " + children[i]);
				boolean success = deleteFileDirectory(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			//System.out.println("How many times deleting");
		}
		return dir.delete();
	}

}
