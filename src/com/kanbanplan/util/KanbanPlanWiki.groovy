package com.kanbanplan.util

import static ys.wikiparser.Utils.*;
import ys.wikiparser.WikiParser;

public class KanbanPlanWiki {

   private static class Parser extends WikiParser {

   public Parser(String wikiText) {
	 super();
	 HEADING_LEVEL_SHIFT=0;
	 parse(wikiText);
   }

   public static String renderXHTML(String wikiText) {
	 return new Parser(wikiText).toString();
   }

   @Override
   protected void appendImage(String text) {
	 super.appendImage(text);
   }

   @Override
   protected void appendLink(String text) {
	 
	 String[] link=split(text, (char) "|");	 
	 
	 URI uri=null;
	 try { // validate URI
	   uri=new URI(link[0].trim());
	 }
	 catch (URISyntaxException e) {
	 }
	 if (uri!=null && uri.isAbsolute()) {
	   sb.append("<a href=\""+escapeHTML(uri.toString())+"\" rel=\"nofollow\">");
	   sb.append(escapeHTML(unescapeHTML(link.length>=2 && !isEmpty(link[1].trim())? link[1]:link[0])));
	   sb.append("</a>");
	 } else {
	
		 
	   String[] type=split(link[0], (char) "/");
	   
	   def page
	   def project
	   def fullUrl
	   def desc
	   	   
	   if(type.length >=2) { // Means this is a specified link!
		    
		    project = type[0]
			page = type[1]
			
			if(isEmpty(project.trim())) { // Root wiki			
				fullUrl = "/wiki?page="+escapeHTML(escapeURL(page))
			} else {
				fullUrl = "/project/" + project.trim() + "/wiki?page="+escapeHTML(escapeURL(page))
			}
			
	   } else {
	   		page = link[0]
			fullUrl = "wiki?page="+escapeHTML(escapeURL(page))
	   }
	   
	   desc = escapeHTML(unescapeHTML(link.length>=2 && !isEmpty(link[1].trim()) ? link[1] : page))
	   
	   sb.append("<a href=\""+fullUrl+"\">");
	   sb.append(desc);
	   sb.append("</a>");
	   
	   
	 }
   }

   @Override
   protected void appendMacro(String text) {
	 if ("TOC".equals(text)) {
	   super.appendMacro(text); // use default
	 }
	 else if ("My-macro".equals(text)) {
	   sb.append("{{ My macro output }}");
	 }
	 else {
	   super.appendMacro(text);
	 }
   }
 }

 public static String escapeURL(String s) {
   try {
	 return URLEncoder.encode(s, "utf-8");
   }
   catch (UnsupportedEncodingException e) {
	 e.printStackTrace();
	 return null;
   }
 }

}