import com.kanbanplan.domain.Project
import com.kanbanplan.domain.ProjectUser
import com.kanbanplan.domain.ProjectRole
import com.googlecode.objectify.*
import com.google.appengine.api.users.User
import org.codehaus.jackson.map.ObjectMapper

// Default action is show if page is passed
def action
action = params.action ? params.action : "list"

// Redirect if the user is not logged in
def baseUrl
if(params.project) {
	baseUrl = "/project/" + params.project
	action = params.action ? params.action : "show"
} else {
	baseUrl = "/project"
	action = params.action ? params.action : "list"
}
request.baseUrl = baseUrl

if(!user){
	redirect(users.createLoginURL(baseUrl))	
}

show = {

	def projectExists = true
	Project project
	
	def ofy = dao.ofy()	
	
	try {
	    project = ofy.get(new Key<Project>(Project.class, params.project));    // equivalent, more convenient
	} catch (Exception e) {
		// Page doesn't exist
		projectExists = false		
	}
		
	if(!projectExists) {		
		redirect("/project")
	} else {
		request.project = project	
		forward "/pages/project/ShowProject.gtpl"
	} 
	
}	

jsonlist = {

	def ofy = dao.ofy()
	Query<Project> q
	
	if(params.search) {
		q = ofy.query(Project.class).filter("id =", params.search).filter("owner =", user);
	} else {
		q = ofy.query(Project.class).filter("owner =", user);
	}
	
	ObjectMapper mapper = new ObjectMapper();
	response.contentType = "application/json"
	
	def results = []
	
	for(Project p in q) {
		results << p
	}
	
	def offset = params.offset ? params.offset : 0
	def json = ["status":"ok","offset":offset,"count":results.size,"results":results]
	
	mapper.writeValue(out, json);
	
		
	

}

update = {
				
	def projectExists = true
	Project project
	
	def ofy = dao.ofy()
	
	try {
		project = ofy.get(new Key<Project>(Project.class, params.project));    // equivalent, more convenient
	} catch (Exception e) {
		// Page doesn't exist
		projectExists = false
	}
		
	if(!projectExists) {
	
		// Simple create
		project = new Project()
		project.id = params.project
		project.createDate = new Date()
		project.owner = user	
		project.projectDescription = params.projectDescription			
		ofy.put(project);
		
		ObjectMapper mapper = new ObjectMapper();
		response.contentType = "application/json"
		def status = ["status":"ok","project":project.id,"statusDescription":"Project created successfully."]
		mapper.writeValue(out, status);
				
	} else {
			
		ObjectMapper mapper = new ObjectMapper();
		response.contentType = "application/json"
		def status = ["status":"ok","project":project.id,"statusDescription":"Project found and updated."]
		mapper.writeValue(out, status);
	
	}

}

list = {
	def ofy = dao.ofy()
	Query<Project> q = ofy.query(Project.class);
	request.entities = q 
	forward "/pages/project/ListProject.gtpl"
}

delete = {

	def ofy = dao.ofy()
	Project project = ofy.get(new Key<Project>(Project.class, params.project));    // equivalent, more convenient	
	ofy.delete(project);

	ObjectMapper mapper = new ObjectMapper();
	response.contentType = "application/json"
	def status = ["status":"ok","project":project.id,"statusDescription":"Project found and deleted."]
	mapper.writeValue(out, status);

}

deleteAll = {
	
	def ofy = dao.ofy()
	
	// Gets the keys for all objects	
	Iterable<Key<Project>> allProjects = ofy.query(Project.class).fetchKeys();

	// Useful for deleting items
	ofy.delete(allProjects);
	redirect(baseUrl)
	
}

this."$action".call()