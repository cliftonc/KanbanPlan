package com.kanbanplan.domain

import javax.persistence.*
import com.google.appengine.api.users.User

class Item
{
	@Id String id
	String project
	String iteration	
	String title
	String description
	String status
	User assignedTo
	User owner
	Date createDate = new Date()
}