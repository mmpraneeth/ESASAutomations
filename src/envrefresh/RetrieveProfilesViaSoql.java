package envrefresh;

import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.DescribeMetadataObject;
import com.sforce.soap.metadata.DescribeMetadataResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.*;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.ConnectionException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class RetrieveProfilesViaSoql implements Runnable {
	public static String username = "vgudidevuni@salesforce.com";
	public static String password = "";
	public static String endpoint = "https://login.salesforce.com/services/Soap/u/36.0";
	public static String key = "12345";
	public static String propFileName = null;
	public static boolean debug = false;

	public static String sessionId;
	public static PartnerConnection sfdc;
	public static MetadataConnection sfdcMetadata;

	public static ArrayList<String> fields = new ArrayList<String>();
	public static ArrayList<String> objects = new ArrayList<String>();
	public static ArrayList<String> pages = new ArrayList<String>();
	public static ArrayList<String> classes = new ArrayList<String>();
	public static ArrayList<String> profiles = new ArrayList<String>();
	public static ArrayList<String> recordTypes = new ArrayList<String>();
	public static Hashtable<String,String> decodedToEncoded = new Hashtable<String,String>();
	
	public static void main(String[] args) throws Exception {
		propFileName = args[0];
		String dataFileName = args[1];
		String retrieveTarget = args[2];
		Properties props = new Properties();
		String filename = "config/" + propFileName + ".properties";
		try {
			props.load(new FileInputStream(filename));
			username = props.getProperty("username", username);
			password = props.getProperty("password", password);
			endpoint = props.getProperty("soapEndpoint", endpoint);
			log("Loaded properties from " + filename);
		} catch (FileNotFoundException e) {
			log("Unable to load the " + filename + " file, will use the default values.");
		}

		key = System.getenv("sf_key");		
		
		log("Username: " + username);
		log("Password: " + password);
		log("key: " + key);
		
		password = PasswordTool.getDecodedString(key, password);
		log("Decoded Password: " + password);
		
		System.out.println("Logging into Salesforce.");
		sfdc = connect(endpoint, username, password);

		System.out.println("Building up retrieve.xml");
		
		// Pull the metadata names from the retrieve.xml-formatted file
		filename = dataFileName;
		XPath xp = XPathFactory.newInstance().newXPath();
		Document doc = fileToDocument(filename);
		
		NodeList nodes = (NodeList) xp.evaluate("/Package/types/name[text()='ApexClass']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			classes.add(node.getNodeValue());
		}
		
		nodes = (NodeList) xp.evaluate("/Package/types/name[text()='CustomField']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			fields.add(node.getNodeValue());
		}
		
		nodes = (NodeList) xp.evaluate("/Package/types/name[text()='CustomObject']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			objects.add(node.getNodeValue());
		}

		nodes = (NodeList) xp.evaluate("/Package/types/name[text()='ApexPage']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			pages.add(node.getNodeValue());
		}

		nodes = (NodeList) xp.evaluate("/Package/types/name[text()='Profile']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			profiles.add(node.getNodeValue());
		}

		nodes = (NodeList) xp.evaluate("/Package/types/name[text()='RecordType']/../members/text()", doc,XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			recordTypes.add(node.getNodeValue());
		}

		if (recordTypes.size() > 0) {
			System.out.println("Connecting to Metadata API");
			sfdcMetadata = metadataConnect(sfdc);
		}

		
		String[] profilesArray = profiles.toArray(new String[0]);
		// The profile names in retrieve.xml are encoded with %xx replacing
		// various punctuation characters with their ascii codes
		for (int i = 0; i < profilesArray.length; i++) {
			String s = profilesArray[i];
			if (s.contains("%")) {
				StringBuilder sb = new StringBuilder();
				byte[] ba = s.getBytes();
				for (int j = 0; j < ba.length; j++) {
					byte b = ba[j];
					if (b == '%') {
						byte replacement = Byte.valueOf(String.format("%c%c", ba[j+1], ba[j+2]), 16);
						sb.append((char)replacement);
						j += 2;
					}
					else {
						sb.append((char)b);
					}
				}
				profilesArray[i] = sb.toString();
			}
			// System.out.println("Decoded profile: " + profilesArray[i] + "\t\tEncoded profile: " + s);
			decodedToEncoded.put(profilesArray[i], s);
		}		

		
		// Need to check that the profiles exist, otherwise this will just fabricate data
		HashSet<String> missingProfiles = listMissingProfiles(decodedToEncoded.keySet());
		for (String profile : missingProfiles) {
			System.out.printf("Unable to find profile named %s, removing\n", profile);
			profiles.remove(decodedToEncoded.get(profile));
			decodedToEncoded.remove(profile);
		}
		
		SObject[] objectPerms = queryObjectPermissions(objects.toArray(new String[0]), decodedToEncoded.keySet());
		SObject[] fieldPerms = queryFieldPermissions(fields.toArray(new String[0]), decodedToEncoded.keySet());
		Hashtable<String, HashSet<String>> classPerms = queryXPermissions("ApexClass", classes.toArray(new String[0]), decodedToEncoded.keySet());
		Hashtable<String, HashSet<String>> pagePerms = queryXPermissions("ApexPage", pages.toArray(new String[0]), decodedToEncoded.keySet());
		
		// Need to group the results to profiles so we can print out the equivalent
		// .profile file.
		Hashtable<String, Hashtable<String, ArrayList<SObject>>> profilesDatastructure = new Hashtable<String, Hashtable<String, ArrayList<SObject>>>();

		for (SObject so : objectPerms) {
			buildProfilesDatastructure(so, "objects", profilesDatastructure);
		}

		for (SObject so : fieldPerms) {
			buildProfilesDatastructure(so, "fields", profilesDatastructure);
		}
		
		// classPerms and pagePerms already split up into access per profile

		// Record Types must be pulled via the metadata API
		Hashtable<String,String> profileToXml = null;
		if (recordTypes.size() > 0) {
			Hashtable<String, ArrayList<String>> objs = new Hashtable<String, ArrayList<String>>();
			objs.put("RecordType", recordTypes);
			profileToXml = getMetadataPermissions(objs, profiles);
		}
		
		
		// Print out the equivalent permissions XML for each profile:
		new File(String.format("%s/profiles", retrieveTarget)).mkdirs();
		for (String profile : profiles) {

			Hashtable<String, ArrayList<SObject>> stringToList = profilesDatastructure.get(profile);
						
			String outputFileName = String.format("%s/profiles/%s.profile", retrieveTarget, profile);
			print(String.format("Saving profile \"%s\"\n", outputFileName));
			FileWriter writer = new FileWriter(outputFileName);
					
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<Profile xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n");

			// Order of metadata types is: classAccesses, fieldPermissions, objectPermissions, pageAccesses, recordTypeVisibilities
			// print("Writing class XML\n");
			for (String s : classes) {
				String enabled = "false";
				if (classPerms.get(profile) != null && classPerms.get(profile).contains(s)) {
					enabled = "true";
				}
				writer.write(String.format(
						"\t<classAccesses>\n" +
						"\t\t<apexClass>%s</apexClass>\n" +
						"\t\t<enabled>%s</enabled>\n" +
						"\t</classAccesses>\n",
						s,
						enabled));
			}

			
			if (stringToList != null) {
				if (stringToList.get("fields") != null) {
					// print("Writing FLS XML\n");
					for (SObject so : stringToList.get("fields")) {
						if (so.getType().equals("FieldPermissions")) {
							writer.write(String.format(
							"\t<fieldPermissions>\n" +
							"\t\t<editable>%1$s</editable>\n" +
							"\t\t<field>%2$s</field>\n" +
							"\t\t<readable>%3$s</readable>\n" +
							"\t</fieldPermissions>\n",
							so.getField("PermissionsEdit"),
							so.getField("Field"),
							so.getField("PermissionsRead")));
						}
					}
				}
	
				if (stringToList.get("objects") != null) {			
					// print("Writing Object XML\n");
					for (SObject so : stringToList.get("objects")) {
						if (so.getType().equals("ObjectPermissions")) {
							writer.write(String.format(
							"\t<objectPermissions>\n" +
									"\t\t<allowCreate>%1$s</allowCreate>\n" +
									"\t\t<allowDelete>%4$s</allowDelete>\n" +
									"\t\t<allowEdit>%3$s</allowEdit>\n" +
									"\t\t<allowRead>%2$s</allowRead>\n" +
									"\t\t<modifyAllRecords>%6$s</modifyAllRecords>\n" +
									"\t\t<object>%7$s</object>\n" +
									"\t\t<viewAllRecords>%5$s</viewAllRecords>\n" +
							"\t</objectPermissions>\n",
							so.getField("PermissionsCreate"),
							so.getField("PermissionsRead"),
							so.getField("PermissionsEdit"),
							so.getField("PermissionsDelete"),
							so.getField("PermissionsViewAllRecords"),
							so.getField("PermissionsModifyAllRecords"),
							so.getField("SobjectType")));
						}
					}
				}
			}
			
			// print("Writing Page XML\n");
			for (String s : pages) {
				String enabled = "false";
				if (pagePerms.get(profile) != null && pagePerms.get(profile).contains(s)) {
					enabled = "true";
				}
				writer.write(String.format(
						"\t<pageAccesses>\n" +
						"\t\t<apexPage>%s</apexPage>\n" +
						"\t\t<enabled>%s</enabled>\n" +
						"\t</pageAccesses>\n",
						s,
						enabled));
			}						
			
			if (profileToXml != null) {
				StringWriter sw = new StringWriter();
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				if (profileToXml.containsKey(profile)) {
					Document d = documentBuilder.parse(new ByteArrayInputStream(profileToXml.get(profile).getBytes()));
					nodes = (NodeList) xp.evaluate("/Profile/recordTypeVisibilities", d,XPathConstants.NODESET);
					for (int i = 0; i < nodes.getLength(); i++) {
						t.transform(new DOMSource(nodes.item(i)), new StreamResult(sw));
					}
					writer.write(sw.toString());					
				}
			}
			writer.write("</Profile>\n");
			writer.close();
		}
	}
	
	private static HashSet<String> listMissingProfiles(Set<String> set) {
		HashSet<String> retval = null;
		try {
			SObject[] results = sfdc.query(String.format("select name from profile where name in (%s)",join(set.toArray(new String[0]),",","'"))).getRecords();
			ArrayList<String> existingProfiles = new ArrayList<String>();
			for (SObject result : results) {
				existingProfiles.add(result.getField("Name").toString());
			}
			
			retval = new HashSet<String>(set);
			retval.removeAll(existingProfiles);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		
		return retval;
	}

	public static void buildProfilesDatastructure(SObject so, String metadataType, Hashtable<String, Hashtable<String, ArrayList<SObject>>> profilesDatastructure) {
		SObject sot = (SObject)so.getSObjectField("Parent");
		sot = (SObject)sot.getSObjectField("Profile");
		String profileName = sot.getField("Name").toString();
		
		if (!profilesDatastructure.containsKey(profileName)) {
			profilesDatastructure.put(profileName, new Hashtable<String, ArrayList<SObject>>());
		}

		Hashtable<String, ArrayList<SObject>> pds = profilesDatastructure.get(profileName);		
		if (!pds.containsKey(metadataType)) {
			pds.put(metadataType, new ArrayList<SObject>());
		}
		ArrayList<SObject> sos = pds.get(metadataType);
		sos.add(so);		
	}
	
	public static SObject[] queryObjectPermissions(String[] objects, Set<String> set) throws RemoteException, ConnectionException {
		String columns = "parent.profile.name,id,SobjectType,PermissionsCreate,PermissionsDelete,PermissionsEdit,PermissionsModifyAllRecords,PermissionsRead,PermissionsViewAllRecords";

		System.out.println("Querying for Object permissions");
		// Need to try to split up long (lots of characters) queries
		ArrayList<String> objectsList = new ArrayList<String>(Arrays.asList(objects));
		ArrayList<SObject> retval = new ArrayList<SObject>();
		while (objectsList.size() > 0) {
			List<String> objectsSubset = objectsList.subList(0, Math.min(objectsList.size(), 100));
			ArrayList<String> filters = new ArrayList<String>();

			String filterString = String.format("sobjecttype in (%s)", join(objectsSubset.toArray(new String[0]), ",", "'"));
			
			filters.add(filterString);
			
			filterString = String.format("parent.profile.name in (%s)", join(set.toArray(new String[0]), ",", "'"));
			filters.add(filterString);
	
			String whereClause = join(filters.toArray(new String[0]), " and ");
			
			String q = String.format("select %s from ObjectPermissions where %s", columns, whereClause);
			System.out.printf("Using SObject query: %s\n", q);
			QueryResult qr = query(sfdc, q);
			retval.addAll(Arrays.asList(qr.getRecords()));
			log(String.format("Query returned %d results", qr.getSize()));
			objectsList.removeAll(objectsSubset);
		}
		return retval.toArray(new SObject[0]);
	}

	public static SObject[] queryFieldPermissions(String[] fields, Set<String> set) throws RemoteException, ConnectionException {
		String columns = "parent.profile.name,Field,Id,PermissionsEdit,PermissionsRead,SobjectType";

		System.out.printf("Need to query for Field Level Security for %d fields and %d profiles\n", fields.length, set.size());

		ArrayList<String> fieldsList = new ArrayList<String>(Arrays.asList(fields));
		ArrayList<SObject> retval = new ArrayList<SObject>();
		while (fieldsList.size() > 0) {
			List<String> objectsSubset = fieldsList.subList(0, Math.min(fieldsList.size(), 100));
			System.out.printf("Querying for %d fields\n", objectsSubset.size());
			ArrayList<String> filters = new ArrayList<String>();
		
			String filterString = String.format("field in (%s)", join(objectsSubset.toArray(new String[0]), ",", "'"));

			filters.add(filterString);
			filterString = String.format("parent.profile.name in (%s)", join(set.toArray(new String[0]), ",", "'"));
			filters.add(filterString);

			String whereClause = join(filters.toArray(new String[0]), " and ");
		
			String q = String.format("select %s from FieldPermissions where %s", columns, whereClause);
			QueryResult qr = query(sfdc, q);
			retval.addAll(Arrays.asList(qr.getRecords()));
			log(String.format("Query returned %d results", qr.getSize()));
			fieldsList.removeAll(objectsSubset);
		}
		return retval.toArray(new SObject[0]);
	}
	
	/**
	 * Permissions for Classes and Pages are tabulated in the SetupEntityAccess object using the class or page ID
	 * @param x
	 * @param objects
	 * @param profiles2.toArray(new String[0])
	 * @return
	 * @throws RemoteException
	 * @throws ConnectionException
	 */
	public static Hashtable<String, HashSet<String>> queryXPermissions(String x, String[] objects, Set<String> set) throws RemoteException, ConnectionException {
		// Need a couple of queries due to the limitation on SetupEntityAccess where it doesn't look like
		// a relationship suitable for the polymorphic TYPEOF/WHEN/THEN query form.  I can't get to the class
		// name with a single query.
		Hashtable<String, HashSet<String>> retval = new Hashtable<String, HashSet<String>>();
		
		if (objects == null || objects.length < 1) {
			return retval;
		}
		
		System.out.printf("Querying for %s permissions\n", x);
		String q = String.format("select id, name from %s where name in (%s)", x, join(objects, ",", "'"));
		QueryResult qr = query(sfdc, q);
		Hashtable<String,String> idsToNames = new Hashtable<String,String>();
		for (SObject so : qr.getRecords()) {
			idsToNames.put((String)so.getField("Name"), (String)so.getField("Id"));
		}

		
		if (idsToNames.size() > 0) {
		
			String columns = "parent.profile.name,SetupEntityId,SetupEntityType";		
	
			ArrayList<String> filters = new ArrayList<String>();		
			filters.add(String.format("SetupEntityId in (%s)", join(idsToNames.values().toArray(new String[0]), ",", "'")));		
			filters.add(String.format("parent.profile.name in (%s)", join(set.toArray(new String[0]), ",", "'")));
	
			String whereClause = join(filters.toArray(new String[0]), " and ");
			
			q = String.format("select %s from SetupEntityAccess where %s", columns, whereClause);
			qr = query(sfdc, q);
			log(String.format("Query returned %d results", qr.getSize()));
			
			
			for (SObject so : qr.getRecords()) {
				SObject sot = (SObject)so.getSObjectField("Parent");
				sot = (SObject)sot.getSObjectField("Profile");
				String profileName = sot.getField("Name").toString();
				if (!retval.containsKey(profileName)) {
					retval.put(profileName, new HashSet<String>());
				}
				retval.get(profileName).add(idsToNames.get((String)so.getField("SetupEntityId")));
			}
		}		
		return retval;
	}

	public static AsyncResult ar = null;
	public static String zipFilePath = String.format("%s/retrieve.zip", System.getenv("BUILD_NUMBER"));

	public void describeMetadata() {
		  try {
		    double apiVersion = 37.0;
		    // Assuming that the SOAP binding has already been established.
		    DescribeMetadataResult res = 
		        sfdcMetadata.describeMetadata(apiVersion);
		    StringBuffer sb = new StringBuffer();
		    if (res != null && res.getMetadataObjects().length > 0) {
		      for (DescribeMetadataObject obj : res.getMetadataObjects()) {
		        sb.append("***************************************************\n");
		        sb.append("XMLName: " + obj.getXmlName() + "\n");
		        sb.append("DirName: " + obj.getDirectoryName() + "\n");
		        sb.append("Suffix: " + obj.getSuffix() + "\n");
		        sb.append("***************************************************\n");
		      }
		    } else {
		      sb.append("Failed to obtain metadata types.");
		    }
		    System.out.println(sb.toString());
		  } catch (ConnectionException ce) {
		    ce.printStackTrace();
		  }
		}

	
	/**
	 * Retrieve permissions on objects via the metadata api, and return the
	 * results as a hashtable mapping from profile filenames to XML.
	 * @param objs Hashtable mapping metadata type to the list of names for that type
	 * @param profiles List of profiles for which we want the metadata
	 * @return Hashtable mapping from profile filenames to XML
	 * @throws ConnectionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static Hashtable<String, String> getMetadataPermissions(Hashtable<String,ArrayList<String>> objs, ArrayList<String> profiles) throws ConnectionException, InterruptedException, IOException {
		System.out.println("Forming metadata API request.");
		ArrayList<PackageTypeMembers> ptms = new ArrayList<PackageTypeMembers>();
		for (String metadataType : objs.keySet()) {
			PackageTypeMembers ptmObjs = new PackageTypeMembers();
			ptmObjs.setMembers(objs.get(metadataType).toArray(new String[0]));
			ptmObjs.setName(metadataType);
			ptms.add(ptmObjs);
		}
		PackageTypeMembers ptmProfiles = new PackageTypeMembers();
		ptmProfiles.setMembers(profiles.toArray(new String[0]));
		ptmProfiles.setName("Profile");
		ptms.add(ptmProfiles);
		com.sforce.soap.metadata.Package p = new com.sforce.soap.metadata.Package();
		p.setTypes(ptms.toArray(new PackageTypeMembers[0]));
		RetrieveRequest rr = new RetrieveRequest();
		rr.setUnpackaged(p);
		ar = sfdcMetadata.retrieve(rr);
		
		// Kick off a thread that waits until the result returns:
		System.out.println("Downloading metadata.");
		Thread t = new Thread(new RetrieveProfilesViaSoql());
		t.start();
		t.join();

		// When the other thread finishes, need to "unzip the file" and get the
		// contents of the .profile files:		
		Hashtable<String, String> retval = new Hashtable<String,String>();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
		Pattern p1 = Pattern.compile(".*/(.*)\\.profile");
		for (ZipEntry zi = zis.getNextEntry(); zi != null; zi = zis.getNextEntry()) {
			// print(String.format("zi.getName() returned %s\n", zi.getName()));
			if (zi.getName().contains("unpackaged/profiles/") && !zi.isDirectory()) {
				StringBuffer sb = new StringBuffer();				
				byte[] ba = new byte[1024];
				while (zis.available() > 0) {
					int count = zis.read(ba);
					if (count > 0) {
						sb.append(new String(ba, 0, count));
					}
				}
				String profileName = zi.getName();
				Matcher m = p1.matcher(profileName);
				profileName = (m.matches()) ? m.group(1) : profileName;
				retval.put(profileName, sb.toString());
			}
		}
		zis.close();
		System.out.println("Download complete.");
		
		return retval;
	}
	
	@Override
	public synchronized void run() {
		long timeout = 5000;  // sleep n/1000 seconds between checks
		boolean complete = false;
		try {
			RetrieveResult rr = null;
			do {
				Thread.sleep(timeout);
				rr = sfdcMetadata.checkRetrieveStatus(ar.getId(), true);
				complete = rr.isDone();
				System.out.println("Waiting for completion...");
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
		System.out.print(s);
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
    
	public static PartnerConnection connect(String endpoint, String username, String password) {
		sfdc = null;
		try {
			ConnectorConfig cc = new ConnectorConfig();
			cc.setUsername(username);
			cc.setPassword(password);
			cc.setAuthEndpoint(endpoint);			
			sfdc = new PartnerConnection(cc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to log into Salesforce: " + e.getMessage());
		}

		return sfdc;
	}
	
	public static MetadataConnection metadataConnect(PartnerConnection pc) throws ConnectionException {
		try {
			ConnectorConfig cc = pc.getConfig();
			
			LoginResult lr = pc.login(cc.getUsername(), cc.getPassword());
			ConnectorConfig cc2 = new ConnectorConfig();
	        cc2.setSessionId( cc.getSessionId());
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
	private static Document fileToDocument(String filename) throws ParserConfigurationException, SAXException, IOException {
		if (documentBuilder == null) {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		FileInputStream fis = new FileInputStream(filename);
		return documentBuilder.parse(fis);
	}


}
