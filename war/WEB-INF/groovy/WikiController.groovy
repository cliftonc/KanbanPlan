import com.kanbanplan.domain.WikiPage
import com.googlecode.objectify.*
import com.google.appengine.api.users.User
import org.codehaus.jackson.map.ObjectMapper

// Default action is show if page is passed
def action
if(params.page && !params.action) {
	action = "show"
} else { // Otherwise the default action is list
	action = params.action ? params.action : "list"
}

// Manage the URL mappings
def baseUrl
if(params.project == "none") {
	baseUrl = "/wiki"
} else {
	baseUrl = "/project/" + params.project + "/wiki"
}
request.baseUrl = baseUrl

// Redirect if the user is not logged in
if(!user){
	if(params.action == "jsonshow") {		
		ObjectMapper mapper = new ObjectMapper();
		response.contentType = "application/json"
		def status = ["status":"error","statusDescription":"You must create a session to access the rest API."]
		mapper.writeValue(out, status);		
	} else {
		redirect(users.createLoginURL(baseUrl))	
	}
}

jsonshow = {
	
	def pageExists = true
	WikiPage page
	
	def ofy = dao.ofy()

	try {
		page = ofy.get(new Key<WikiPage>(WikiPage.class, params.project + "/" + params.page));    // equivalent, more convenient
	} catch (Exception e) {
		// Page doesn't exist
		pageExists = false
	}	
	
	
	ObjectMapper mapper = new ObjectMapper();
	response.contentType = "application/json"
	
	if(pageExists) {		
		mapper.writeValue(out, page);		
	} else {
		def status = ["status":"error","statusDescription":"The requested page does not exist."]
		mapper.writeValue(out, status);	
	}
	
}

show = {

	def pageExists = true
	WikiPage page
	
	def ofy = dao.ofy()	
	
	try {
	    page = ofy.get(new Key<WikiPage>(WikiPage.class, params.project + "/" + params.page));    // equivalent, more convenient
	} catch (Exception e) {
		// Page doesn't exist
		pageExists = false		
	}
		
	if(!pageExists) {
		// Simple create
		page = new WikiPage()
		page.id = params.project + "/" + params.page
		page.page = params.page
		page.wikiText = "This is a default wiki page, please edit the page using the links to create your own content!"
		page.createDate = new Date()
		page.author = user
		page.project = params.project
		ofy.put(page);
		redirect(baseUrl + "?page=" + page.page)
	} else {
		request.page = page	
		forward "/pages/wiki/WikiPage.gtpl"
	} 
	
}	

update = {
				
		def pageExists = true
		WikiPage page
		
		def ofy = dao.ofy()		
		def old_key = new Key<WikiPage>(WikiPage.class, params.project + "/" + params.page_temp)
		
		try {
			// Get the previous page
			page = ofy.get(old_key);    // equivalent, more convenient
		} catch (Exception e) {
			// Page doesn't exist
			pageExists = false
		}
			
		if(!pageExists) {
			
			// Simple create
			redirect(baseUrl + "?error=Page not found: " + params.page_temp)
			
		} else {		
						
			if(params.page != params.page_temp) {			
				
				page.id = params.project + "/" + params.page						
				params.retain_old = params.retain_old ? params.retain_old : "off"		
				if(params.retain_old != "on") {
					ofy.delete(old_key);
				}
				
			}
			
			page.page = params.page
			page.wikiText = params.wiki_text
			page.createDate = new Date()
			page.author = user
			
			ofy.put(page)
			
			redirect(baseUrl + "?page=" + page.page)
						
		}
		
}

list = {
	def ofy = dao.ofy()
	Query<WikiPage> q = ofy.query(WikiPage.class).filter("project", params.project);
	request.entities = q 
	forward "/pages/wiki/WikiPage.gtpl"
}

delete = {

	def ofy = dao.ofy()
	WikiPage page = ofy.get(new Key<WikiPage>(WikiPage.class, params.project + "/" + params.page));    // equivalent, more convenient	
	ofy.delete(page);
	redirect(baseUrl)
	
}

deleteAll = {
	
	def ofy = dao.ofy()
	
	// Gets the keys for all objects	
	Iterable<Key<WikiPage>> allPages = ofy.query(WikiPage.class).fetchKeys();

	// Useful for deleting items
	ofy.delete(allPages);
	redirect(baseUrl)
	
}

this."$action".call()