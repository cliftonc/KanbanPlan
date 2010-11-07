package com.kanbanplan.domain

import javax.persistence.*
import com.google.appengine.api.users.User

class WikiPage
{	
	@Id String id
	String project
	String page
	User author
	String wikiText	
	Date createDate = new Date()
}