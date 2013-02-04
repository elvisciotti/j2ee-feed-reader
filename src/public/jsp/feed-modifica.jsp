<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="twi518.NewsXmlFactory.*"%>
<%@page import="twi518.servlets.*"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>modifica feed della categoria</title>
<link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
</head>

<body>
<%@ include file="../xhtml/header.html" %>
<% String username = new CookieFactory().getUsername(request); %>
<% NewsXmlFactory nxf = new NewsXmlFactory(username+".xml");  %>
<% Feed f = nxf.getFeedByIds(request.getParameter("idcat"), request.getParameter("idfeed")); %>
<h3>Modifica fonte</h3>
<a href="feed-elenco.jsp?idcat=<%= request.getParameter("idcat") %>">Torna all'elenco feeds</a>
<form id="form1" name="form1" method="post" action="../Operazioni">
  <ul class="ulform">
    <li>Nome feed::<br /> 
      <input name="nome" type="text" id="nome" value="<%= f.getNome() %>" size="80" />
    </li>
    <li>
      Fonte
        <label>
        <input name="urlprim" type="text" id="urlprim" value="<%= f.getUrlPrimaria() %>" size="65" />
      </label>
    </li>
	<li> link 2:
      <label>
      <input name="link2" type="text" id="link2" value="<%= f.getUrl(0) %>" size="65" />
      </label>
</li>
    <li>  link 3:
      <label>
      <input name="link3" type="text" id="link3" value="<%= f.getUrl(1) %>" size="65" />
      </label>
</li>
    <li> link 4:
      <label>
      <input name="link4" type="text" id="link4" value="<%= f.getUrl(2) %>" size="65" />
      </label>
</li>
	
  </ul>
  <input type="submit" name="Submit" value="MODIFICA"  class="grande" />
  <input name="azione" type="hidden" id="azione" value="modificafeed" />
  <input name="idcat" type="hidden" value="<%= request.getParameter("idcat") %>" />
  <input name="idfeed" type="hidden" value="<%= request.getParameter("idfeed") %>" />
  <input name="username" type="hidden" id="username" value="<%= username %>" />
</form>
<p>&nbsp;</p>
</body>
</html>
