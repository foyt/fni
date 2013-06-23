package fi.foyt.fni.utils.gdocs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gdata.client.GoogleAuthTokenFactory.UserToken;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.acl.AclScope.Type;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListEntry.MediaType;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.DrawingEntry;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.RevisionFeed;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

import fi.foyt.fni.persistence.model.materials.GoogleDocumentType;

/**
 * Google Documents List Service client, code is based on Google's DocumentList example code.
 */
public class GoogleDocumentsClient {
  
  private static final String APPLICATION = "Forge & Illusion";
 
  public GoogleDocumentsClient(String token) {
    this.docsService = new DocsService(APPLICATION);
    this.docsService.setHeader("GData-Version", "3.0");
    this.docsService.setHeader("Authorization", "Bearer " + token);
    this.spreadsheetsService = new GoogleService(SPREADSHEETS_SERVICE_NAME, APPLICATION);
    this.spreadsheetsService.setHeader("Authorization", "Bearer " + token);
  }

  public GoogleDocumentsClient(String username, String password) throws AuthenticationException {
    this.docsService = new DocsService(APPLICATION);
    this.docsService.setHeader("GData-Version", "3.0");
    this.docsService.setUserCredentials(username, password);
    this.spreadsheetsService = new GoogleService(SPREADSHEETS_SERVICE_NAME, APPLICATION);
    this.spreadsheetsService.setUserCredentials(username, password);
  }
	
  /**
   * Create a new item in the DocList.
   * 
   * @param title
   *          the title of the document to be created.
   * @param type
   *          the type of the document to be created. One of "spreadsheet", "presentation", "document", or "drawing".
   * 
   * @throws GooleDocumentsException
   * @throws ServiceException
   * @throws IOException
   * @throws MalformedURLException
   */
  public DocumentListEntry createNew(String title, GoogleDocumentType type) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    if (title == null || type == null) {
      throw new GooleDocumentsException("null title or type");
    }

    try {
      DocumentListEntry newEntry = GoogleDocumentsUtils.getDocumentListEntryClass(type).newInstance();
      newEntry.setTitle(new PlainTextConstruct(title));
      return docsService.insert(buildUrl(URL_DEFAULT + URL_DOCLIST_FEED), newEntry);
    } catch (InstantiationException e) {
      throw new GooleDocumentsException("Could not initialize document type " + type);
    } catch (IllegalAccessException e) {
      throw new GooleDocumentsException("Could not initialize document type " + type);
    }
  }
  
  public DocumentListFeed getHomeDocumentListFeed() throws GooleDocumentsException, IOException, ServiceException {
    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_HOME + "?showfolders=true");
    return docsService.getFeed(url, DocumentListFeed.class);
  }

  /**
   * Gets a feed containing the documents.
   * 
   * @param category
   *          what types of documents to list: "all": lists all the doc objects (documents, spreadsheets, presentations) "folders": lists all doc objects
   *          including folders. "documents": lists only documents. "spreadsheets": lists only spreadsheets. "pdfs": lists only pdfs. "presentations": lists
   *          only presentations. "starred": lists only starred objects. "trashed": lists trashed objects.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public DocumentListFeed getDocsListFeed(String category) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (category == null) {
      throw new GooleDocumentsException("null category");
    }

    URL url;

    if (category.equals("all")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);
    } else if (category.equals("folders")) {
      String[] parameters = { PARAMETER_SHOW_FOLDERS };
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_FOLDER, parameters);
    } else if (category.equals("documents")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_DOCUMENT);
    } else if (category.equals("spreadsheets")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_SPREADSHEET);
    } else if (category.equals("pdfs")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_PDF);
    } else if (category.equals("presentations")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_PRESENTATION);
    } else if (category.equals("starred")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_STARRED);
    } else if (category.equals("trashed")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_TRASHED);
    } else {
      return null;
    }

    return docsService.getFeed(url, DocumentListFeed.class);
  }
  
  public DocumentEntry getDocumentEntry(String resourceId) throws IOException, ServiceException, GooleDocumentsException {
    return (DocumentEntry) getDocumentListEntry(DocumentEntry.class, resourceId);
  }
  
  public DrawingEntry getDrawingEntry(String resourceId) throws IOException, ServiceException, GooleDocumentsException {
    return (DrawingEntry) getDocumentListEntry(DrawingEntry.class, resourceId);
  }
  
  public FolderEntry getFolderEntry(String resourceId) throws IOException, ServiceException, GooleDocumentsException {
    return (FolderEntry) getDocumentListEntry(FolderEntry.class, resourceId);
  }
  
  public DocumentListEntry getDocumentListEntry(String entryId) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    return (DocumentListEntry) getDocumentListEntry(DocumentListEntry.class, entryId);
  }
  
  private DocumentListEntry getDocumentListEntry(Class<? extends DocumentListEntry> entryClass, String resourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }
    
    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId);

    return docsService.getEntry(url, entryClass);
  }

  /**
   * Gets the feed for all the objects contained in a folder.
   * 
   * @param folderResourceId
   *          the resource id of the folder to return the feed for the contents.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public DocumentListFeed getFolderDocsListFeed(String folderResourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (folderResourceId == null) {
      throw new GooleDocumentsException("null folderResourceId");
    }
    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + folderResourceId + URL_FOLDERS);
    return docsService.getFeed(url, DocumentListFeed.class);
  }

  /**
   * Gets a feed containing the documents.
   * 
   * @param resourceId
   *          the resource id of the object to fetch revisions for.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public RevisionFeed getRevisionsFeed(String resourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_REVISIONS);

    return docsService.getFeed(url, RevisionFeed.class);
  }

  /**
   * Search the documents, and return a feed of docs that match.
   * 
   * @param searchParameters
   *          parameters to be used in searching criteria.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public DocumentListFeed search(Map<String, String> searchParameters) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    return search(searchParameters, null);
  }

  /**
   * Search the documents, and return a feed of docs that match.
   * 
   * @param searchParameters
   *          parameters to be used in searching criteria. accepted parameters are: "q": Typical search query "alt": "author": "updated-min": Lower bound on the
   *          last time a document' content was changed. "updated-max": Upper bound on the last time a document' content was changed. "edited-min": Lower bound
   *          on the last time a document was edited by the current user. This value corresponds to the app:edited value in the Atom entry, which represents
   *          changes to the document's content or metadata. "edited-max": Upper bound on the last time a document was edited by the current user. This value
   *          corresponds to the app:edited value in the Atom entry, which represents changes to the document's content or metadata. "title": Specifies the
   *          search terms for the title of a document. This parameter used without title-exact will only submit partial queries, not exact queries.
   *          "title-exact": Specifies whether the title query should be taken as an exact string. Meaningless without title. Possible values are true and
   *          false. "opened-min": Bounds on the last time a document was opened by the current user. Use the RFC 3339 timestamp format. For example:
   *          2005-08-09T10:57:00-08:00 "opened-max": Bounds on the last time a document was opened by the current user. Use the RFC 3339 timestamp format. For
   *          example: 2005-08-09T10:57:00-08:00 "owner": Searches for documents with a specific owner. Use the email address of the owner. "writer": Searches
   *          for documents which can be written to by specific users. Use a single email address or a comma separated list of email addresses. "reader":
   *          Searches for documents which can be read by specific users. Use a single email address or a comma separated list of email addresses.
   *          "showfolders": Specifies whether the query should return folders as well as documents. Possible values are true and false.
   * @param category
   *          define the category to search. (documents, spreadsheets, presentations, starred, trashed, folders)
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public DocumentListFeed search(Map<String, String> searchParameters, String category) throws IOException, MalformedURLException, ServiceException,
      GooleDocumentsException {
    if (searchParameters == null) {
      throw new GooleDocumentsException("searchParameters null");
    }

    URL url;

    if (category == null || category.equals("")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);
    } else if (category.equals("documents")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_DOCUMENT);
    } else if (category.equals("spreadsheets")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_SPREADSHEET);
    } else if (category.equals("presentations")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_PRESENTATION);
    } else if (category.equals("starred")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_STARRED);
    } else if (category.equals("trashed")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_TRASHED);
    } else if (category.equals("folders")) {
      url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_FOLDER);
    } else {
      throw new GooleDocumentsException("invaild category");
    }

    Query qry = new Query(url);

    for (String key : searchParameters.keySet()) {
      qry.setStringCustomParameter(key, searchParameters.get(key));
    }

    return docsService.query(qry, DocumentListFeed.class);
  }
  
  public DrawingEntry uploadDrawing(InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException, GooleDocumentsException {
    DrawingEntry newDrawing = new DrawingEntry();
    return (DrawingEntry) uploadFile(newDrawing, stream, mediaType, title);
  }
  
	public DrawingEntry uploadDrawing(byte[] data, MediaType mediaType, String title) throws IOException, ServiceException, GooleDocumentsException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
    try {
    	return uploadDrawing(stream, mediaType, title);
    } finally {
    	stream.close();
    }
  }

  public DocumentEntry uploadDocument(byte[] data, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException, GooleDocumentsException {
    ByteArrayInputStream stream = new ByteArrayInputStream(data);
    try {
    	return uploadDocument(stream, mediaType, title);
    } finally {
    	stream.close();
    }
  }

  public DocumentEntry uploadDocument(InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException, GooleDocumentsException {
    DocumentEntry newDocument = new DocumentEntry();
    return (DocumentEntry) uploadFile(newDocument, stream, mediaType, title);
  }
  
  private DocumentListEntry uploadFile(DocumentListEntry documentListEntry, InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    MediaStreamSource mediaSource = new MediaStreamSource(stream, mediaType.getMimeType());
    MediaContent content = new MediaContent();
    content.setMediaSource(mediaSource);
    content.setMimeType(new ContentType(mediaType.getMimeType()));
    documentListEntry.setContent(content);

    documentListEntry.setTitle(new PlainTextConstruct(title));

    return docsService.insert(buildUrl(URL_DEFAULT + URL_DOCLIST_FEED), documentListEntry);
  }
  
  public DocumentEntry updateDocument(DocumentEntry documentEntry, InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException {
    return (DocumentEntry) updateFile(documentEntry, stream, mediaType, title);
  }
  
  public DrawingEntry updateDrawing(DrawingEntry drawingEntry, InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException {
    return (DrawingEntry) updateFile(drawingEntry, stream, mediaType, title);
  }
  
  private DocumentListEntry updateFile(DocumentListEntry documentListEntry, InputStream stream, DocumentListEntry.MediaType mediaType, String title) throws IOException, ServiceException {
    MediaSource mediaSource = new MediaStreamSource(stream, mediaType.getMimeType());
    documentListEntry.setMediaSource(mediaSource);
    documentListEntry.updateMedia(true);
//    
//    MediaStreamSource mediaSource = new MediaStreamSource(stream, mediaType.getMimeType());
//    MediaContent content = new MediaContent();
//    content.setMediaSource(mediaSource);
//    content.setMimeType(new ContentType(mediaType.getMimeType()));
//    documentListEntry.setContent(content);
//    service.updateMedia(new URL(documentListEntry.getEditLink().getHref()), documentListEntry.getClass(), mediaSource);
    return documentListEntry;
  }

  /**
   * Trash an object.
   * 
   * @param resourceId
   *          the resource id of object to be trashed.
   * @param delete
   *          true to delete the permanently, false to move it to the trash.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void trashObject(String resourceId, boolean delete) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }

    String feedUrl = URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId;
    if (delete) {
      feedUrl += "?delete=true";
    }

    docsService.delete(buildUrl(feedUrl), getDocumentListEntry(DocumentListEntry.class, resourceId).getEtag());
  }

  /**
   * Remove an object from a folder.
   * 
   * @param resourceId
   *          the resource id of an object to be removed from the folder.
   * @param folderResourceId
   *          the resource id of the folder to remove the object from.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void removeFromFolder(String resourceId, String folderResourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null || folderResourceId == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + folderResourceId + URL_FOLDERS + "/" + resourceId);
    docsService.delete(url, getDocumentListEntry(DocumentListEntry.class, resourceId).getEtag());
  }

  /**
   * Downloads a file.
   * 
   * @param exportUrl
   *          the full url of the export link to download the file from.
   * @param filepath
   *          path and name of the object to be saved as.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void downloadFile(URL exportUrl, OutputStream outStream) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (exportUrl == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    MediaContent mc = new MediaContent();
    mc.setUri(exportUrl.toString());
    MediaSource ms = docsService.getMedia(mc);

    InputStream inStream = null;

    try {
      inStream = ms.getInputStream();

      int c;
      while ((c = inStream.read()) != -1) {
        outStream.write(c);
      }
    } finally {
      if (inStream != null) {
        inStream.close();
      }
      if (outStream != null) {
        outStream.flush();
        outStream.close();
      }
    }
  }

  /**
   * Downloads a spreadsheet file.
   * 
   * @param filepath
   *          path and name of the object to be saved as.
   * @param resourceId
   *          the resource id of the object to be downloaded.
   * @param format
   *          format to download the file to. The following file types are supported: spreadsheets: "ods", "pdf", "xls", "csv", "html", "tsv"
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void downloadSpreadsheet(String resourceId, OutputStream outStream, String format) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null || format == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    UserToken docsToken = (UserToken) docsService.getAuthTokenFactory().getAuthToken();
    UserToken spreadsheetsToken = (UserToken) spreadsheetsService.getAuthTokenFactory().getAuthToken();
    docsService.setUserToken(spreadsheetsToken.getValue());

    HashMap<String, String> parameters = new HashMap<String, String>();
    parameters.put("key", resourceId.substring(resourceId.lastIndexOf(':') + 1));
    parameters.put("exportFormat", format);

    // If exporting to .csv or .tsv, add the gid parameter to specify which
    // sheet to export
    if (format.equals(DOWNLOAD_SPREADSHEET_FORMATS.get("csv")) || format.equals(DOWNLOAD_SPREADSHEET_FORMATS.get("tsv"))) {
      parameters.put("gid", "0"); // download only the first sheet
    }

    URL url = buildUrl(SPREADSHEETS_HOST, URL_DOWNLOAD + "/spreadsheets" + URL_CATEGORY_EXPORT, parameters);

    downloadFile(url, outStream);

    // Restore docs token for our DocList client
    docsService.setUserToken(docsToken.getValue());
  }

  /**
   * Downloads a document.
   * 
   * @param filepath
   *          path and name of the object to be saved as.
   * @param resourceId
   *          the resource id of the object to be downloaded.
   * @param format
   *          format to download the file to. The following file types are supported: documents: "doc", "txt", "odt", "png", "pdf", "rtf", "html"
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void downloadDocument(String resourceId, OutputStream outStream, String format) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null || format == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }
    
    String[] parameters = { "docID=" + resourceId, "exportFormat=" + format };
    URL url = buildUrl(URL_DOWNLOAD + "/documents" + URL_CATEGORY_EXPORT, parameters);

    downloadFile(url, outStream);
  }

  /**
   * Downloads a presentation.
   * 
   * @param filepath
   *          path and name of the object to be saved as.
   * @param resourceId
   *          the resource id of the object to be downloaded.
   * @param format
   *          format to download the file to. The following file types are supported: presentations: "pdf", "ppt", "png", "swf", "txt"
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void downloadPresentation(String resourceId, OutputStream outStream, String format) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null || format == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    String[] parameters = { "docID=" + resourceId, "exportFormat=" + format };
    URL url = buildUrl(URL_DOWNLOAD + "/presentations" + URL_CATEGORY_EXPORT, parameters);

    downloadFile(url, outStream);
  }
  
  /**
   * 
   * @param resourceId
   * @param outStream
   * @param format jpeg, pdf, png, svg
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void downloadDrawing(String resourceId, OutputStream outStream, String format) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null || format == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }
    
    String[] parameters = { "id=" + resourceId, "exportFormat=" + format };
    URL url = buildUrl(URL_DOWNLOAD + "/drawings" + URL_CATEGORY_EXPORT, parameters);
    
    downloadFile(url, outStream);
  }

  /**
   * Moves a object to a folder.
   * 
   * @param resourceId
   *          the resource id of the object to be moved to the folder.
   * @param folderId
   *          the id of the folder to move the object to.
   *  
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public DocumentListEntry moveObjectToFolder(String resourceId, String folderId) throws IOException, MalformedURLException, ServiceException,
      GooleDocumentsException {
    if (resourceId == null || folderId == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    DocumentListEntry doc = new DocumentListEntry();
    doc.setId(buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId).toString());

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + folderId + URL_FOLDERS);
    return docsService.insert(url, doc);
  }

  /**
   * Gets the access control list for a object.
   * 
   * @param resourceId
   *          the resource id of the object to retrieve the ACL for.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public AclFeed getAclFeed(String resourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_ACL);

    return docsService.getFeed(url, AclFeed.class);
  }
  
  public boolean hasAclRole(String resourceId, String userName, Collection<AclRole> roles) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    Set<AclRole> userRoles = getAclRoles(resourceId, userName);
    for (AclRole userRole : userRoles) {
      for (AclRole role : roles) {
        if (role.equals(userRole))
          return true;
      } 
    }
    
    return false;
  }
  
  public Set<AclRole> getAclRoles(String resourceId, String userName) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    Set<AclRole> roles = new HashSet<AclRole>();
    
    AclFeed aclFeed = getAclFeed(resourceId);
    for (AclEntry aclEntry : aclFeed.getEntries()) {
      if (aclEntry.getScope().getType() == Type.USER) {
        if (userName.equals(aclEntry.getScope().getValue())) {
          roles.add(aclEntry.getRole());
        }
      }
    }
    
    return roles;
  }

  /**
   * Add an ACL role to an object.
   * 
   * @param role
   *          the role of the ACL to be added to the object.
   * @param scope
   *          the scope for the ACL.
   * @param resourceId
   *          the resource id of the object to set the ACL for.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public AclEntry addAclRole(AclRole role, AclScope scope, String resourceId) throws IOException, MalformedURLException, ServiceException,
      GooleDocumentsException {
    if (role == null || scope == null || resourceId == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    AclEntry entry = new AclEntry();
    entry.setRole(role);
    entry.setScope(scope);
    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_ACL);

    return docsService.insert(url, entry);
  }

  /**
   * Change the ACL role of a file.
   * 
   * @param role
   *          the new role of the ACL to be updated.
   * @param scope
   *          the new scope for the ACL.
   * @param resourceId
   *          the resource id of the object to be updated.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public AclEntry changeAclRole(AclRole role, AclScope scope, String resourceId) throws IOException, ServiceException, GooleDocumentsException {
    if (role == null || scope == null || resourceId == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_ACL);

    return docsService.update(url, scope, role);
  }

  /**
   * Remove an ACL role from a object.
   * 
   * @param scope
   *          scope of the ACL to be removed.
   * @param email
   *          email address to remove the role of.
   * @param resourceId
   *          the resource id of the object to remove the role from.
   * 
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws GooleDocumentsException
   */
  public void removeAclRole(String scope, String email, String resourceId) throws IOException, MalformedURLException, ServiceException, GooleDocumentsException {
    if (scope == null || email == null || resourceId == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_ACL + "/" + scope + "%3A" + email);

    docsService.delete(url);
  }

  /**
   * Returns the format code based on a file extension, and object id.
   * 
   * @param resourceId
   *          the resource id of the object you want the format for.
   * @param ext
   *          extension of the file you want the format for.
   * 
   * @throws GooleDocumentsException
   */
  public String getDownloadFormat(String resourceId, String ext) throws GooleDocumentsException {
    if (resourceId == null || ext == null) {
      throw new GooleDocumentsException("null passed in for required parameters");
    }

    if (resourceId.indexOf("document") == 0) {
      if (DOWNLOAD_DOCUMENT_FORMATS.containsKey(ext)) {
        return DOWNLOAD_DOCUMENT_FORMATS.get(ext);
      }
    } else if (resourceId.indexOf("presentation") == 0) {
      if (DOWNLOAD_PRESENTATION_FORMATS.containsKey(ext)) {
        return DOWNLOAD_PRESENTATION_FORMATS.get(ext);
      }
    } else if (resourceId.indexOf("spreadsheet") == 0) {
      if (DOWNLOAD_SPREADSHEET_FORMATS.containsKey(ext)) {
        return DOWNLOAD_SPREADSHEET_FORMATS.get(ext);
      }
    }
    throw new GooleDocumentsException("invalid document type");
  }

  /**
   * Gets the suffix of the resourceId. If the resourceId is "document:dh3bw3j_0f7xmjhd8", "dh3bw3j_0f7xmjhd8" will be returned.
   * 
   * @param resourceId
   *          the resource id to extract the suffix from.
   * 
   * @throws GooleDocumentsException
   */
  public String getResourceIdSuffix(String resourceId) throws GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }

    if (resourceId.indexOf("%3A") != -1) {
      return resourceId.substring(resourceId.lastIndexOf("%3A") + 3);
    } else if (resourceId.indexOf(":") != -1) {
      return resourceId.substring(resourceId.lastIndexOf(":") + 1);
    }
    throw new GooleDocumentsException("Bad resourceId");
  }

  /**
   * Gets the prefix of the resourceId. If the resourceId is "document:dh3bw3j_0f7xmjhd8", "document" will be returned.
   * 
   * @param resourceId
   *          the resource id to extract the suffix from.
   * 
   * @throws GooleDocumentsException
   */
  public String getResourceIdPrefix(String resourceId) throws GooleDocumentsException {
    if (resourceId == null) {
      throw new GooleDocumentsException("null resourceId");
    }

    if (resourceId.indexOf("%3A") != -1) {
      return resourceId.substring(0, resourceId.indexOf("%3A"));
    } else if (resourceId.indexOf(":") != -1) {
      return resourceId.substring(0, resourceId.indexOf(":"));
    } else {
      throw new GooleDocumentsException("Bad resourceId");
    }
  }

  /**
   * Builds a URL from a patch.
   * 
   * @param path
   *          the path to add to the protocol/host
   * 
   * @throws MalformedURLException
   * @throws GooleDocumentsException
   */
  private URL buildUrl(String path) throws MalformedURLException, GooleDocumentsException {
    if (path == null) {
      throw new GooleDocumentsException("null path");
    }

    return buildUrl(path, null);
  }

  /**
   * Builds a URL with parameters.
   * 
   * @param path
   *          the path to add to the protocol/host
   * @param parameters
   *          parameters to be added to the URL.
   * 
   * @throws MalformedURLException
   * @throws GooleDocumentsException
   */
  private URL buildUrl(String path, String[] parameters) throws MalformedURLException, GooleDocumentsException {
    if (path == null) {
      throw new GooleDocumentsException("null path");
    }

    return buildUrl(DEFAULT_HOST, path, parameters);
  }

  /**
   * Builds a URL with parameters.
   * 
   * @param domain
   *          the domain of the server
   * @param path
   *          the path to add to the protocol/host
   * @param parameters
   *          parameters to be added to the URL.
   * 
   * @throws MalformedURLException
   * @throws GooleDocumentsException
   */
  private URL buildUrl(String domain, String path, String[] parameters) throws MalformedURLException, GooleDocumentsException {
    if (path == null) {
      throw new GooleDocumentsException("null path");
    }

    StringBuffer url = new StringBuffer();
    url.append("https://" + domain + URL_FEED + path);

    if (parameters != null && parameters.length > 0) {
      url.append("?");
      for (int i = 0; i < parameters.length; i++) {
        url.append(parameters[i]);
        if (i != (parameters.length - 1)) {
          url.append("&");
        }
      }
    }

    return new URL(url.toString());
  }

  /**
   * Builds a URL with parameters.
   * 
   * @param domain
   *          the domain of the server
   * @param path
   *          the path to add to the protocol/host
   * @param parameters
   *          parameters to be added to the URL as key value pairs.
   * 
   * @throws MalformedURLException
   * @throws GooleDocumentsException
   */
  private URL buildUrl(String domain, String path, Map<String, String> parameters) throws MalformedURLException, GooleDocumentsException {
    if (path == null) {
      throw new GooleDocumentsException("null path");
    }

    StringBuffer url = new StringBuffer();
    url.append("https://" + domain + URL_FEED + path);

    if (parameters != null && parameters.size() > 0) {
      Set<Map.Entry<String, String>> params = parameters.entrySet();
      Iterator<Map.Entry<String, String>> itr = params.iterator();

      url.append("?");
      while (itr.hasNext()) {
        Map.Entry<String, String> entry = itr.next();
        url.append(entry.getKey() + "=" + entry.getValue());
        if (itr.hasNext()) {
          url.append("&");
        }
      }
    }

    return new URL(url.toString());
  }
  
  private static final String DEFAULT_HOST = "docs.google.com";

  private static final String SPREADSHEETS_SERVICE_NAME = "wise";
  private static final String SPREADSHEETS_HOST = "spreadsheets.google.com";

  private static final String URL_FEED = "/feeds";
  private static final String URL_DOWNLOAD = "/download";
  private static final String URL_DOCLIST_FEED = "/private/full";

  private static final String URL_DEFAULT = "/default";
  private static final String URL_FOLDERS = "/contents";
  private static final String URL_ACL = "/acl";
  private static final String URL_REVISIONS = "/revisions";

  private static final String URL_CATEGORY_DOCUMENT = "/-/document";
  private static final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
  private static final String URL_CATEGORY_PDF = "/-/pdf";
  private static final String URL_CATEGORY_PRESENTATION = "/-/presentation";
  private static final String URL_CATEGORY_STARRED = "/-/starred";
  private static final String URL_CATEGORY_TRASHED = "/-/trashed";
  private static final String URL_CATEGORY_HOME = "/-/home";
  private static final String URL_CATEGORY_FOLDER = "/-/folder";
  private static final String URL_CATEGORY_EXPORT = "/Export";

  private static final String PARAMETER_SHOW_FOLDERS = "showfolders=true";

  private DocsService docsService;
  private GoogleService spreadsheetsService;
  private final static Map<String, String> DOWNLOAD_DOCUMENT_FORMATS = new HashMap<String, String>();
  private final static Map<String, String> DOWNLOAD_SPREADSHEET_FORMATS = new HashMap<String, String>();
  private final static Map<String, String> DOWNLOAD_PRESENTATION_FORMATS = new HashMap<String, String>();

  static {
    DOWNLOAD_DOCUMENT_FORMATS.put("doc", "doc");
    DOWNLOAD_DOCUMENT_FORMATS.put("txt", "txt");
    DOWNLOAD_DOCUMENT_FORMATS.put("odt", "odt");
    DOWNLOAD_DOCUMENT_FORMATS.put("pdf", "pdf");
    DOWNLOAD_DOCUMENT_FORMATS.put("png", "png");
    DOWNLOAD_DOCUMENT_FORMATS.put("rtf", "rtf");
    DOWNLOAD_DOCUMENT_FORMATS.put("html", "html");
    DOWNLOAD_DOCUMENT_FORMATS.put("zip", "zip");

    DOWNLOAD_PRESENTATION_FORMATS.put("pdf", "pdf");
    DOWNLOAD_PRESENTATION_FORMATS.put("png", "png");
    DOWNLOAD_PRESENTATION_FORMATS.put("ppt", "ppt");
    DOWNLOAD_PRESENTATION_FORMATS.put("swf", "swf");
    DOWNLOAD_PRESENTATION_FORMATS.put("txt", "txt");

    DOWNLOAD_SPREADSHEET_FORMATS.put("xls", "xls");
    DOWNLOAD_SPREADSHEET_FORMATS.put("ods", "ods");
    DOWNLOAD_SPREADSHEET_FORMATS.put("pdf", "pdf");
    DOWNLOAD_SPREADSHEET_FORMATS.put("csv", "csv");
    DOWNLOAD_SPREADSHEET_FORMATS.put("tsv", "tsv");
    DOWNLOAD_SPREADSHEET_FORMATS.put("html", "html");
  }


}
