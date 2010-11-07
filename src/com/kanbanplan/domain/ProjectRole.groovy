
package com.kanbanplan.domain

import javax.persistence.*
import com.google.appengine.api.users.User

class ProjectRole
{	
	@Id String id
	String description
	Boolean isAdministrator	
	Boolean canCreate
	Boolean canDelete
	Boolean canComment	
	Date createDate = new Date()
}


