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
<title>Feed della categoria</title>
<link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
    </head>
<body>
<%@ include file="../xhtml/header.html" %>
<% String username = new CookieFactory().getUsername(request); %>
<% NewsXmlFactory nxf = new NewsXmlFactory(username+".xml");  %>
<%CategoriaFeed cf = nxf.getCatById(request.getParameter("idcat")); %>
<h4><%= cf.getNome() %></h4>
<%= cf.getNote()  %>
<br />
<br />
<a href="categorie-elenco.jsp">Torna all'elenco categorie</a>
<br />
<br />
Feeds inseriti nella categoria:
<table width="760" border="0" cellspacing="0" cellpadding="0" class="tabella_bordata">
  <thead>
    <tr>
      <th width="81">ID</th>
      <th width="989">nome</th>
      <th width="122">link primario </th>
      <th width="84">&nbsp;</th>
      <th width="84">&nbsp;</th>
    </tr>
  </thead>
  <% for (int i=0; i<cf.getFeeds().length; i++) {
		  Feed f = cf.getFeed(i); %>
  <tr>
    <td ><%= f.getId() %></td>
    <td ><strong><%= f.getNome() %></strong></td>
    <td>Link primario
      <ul>
	<li><a href="<%= f.getUrlPrimaria() %>" target="_blank"><strong><%= f.getUrlPrimaria() %></strong></a>
	</li>
	</ul>
	<% String[] urls = f.getUrls();
	if  (urls.length!=0) { %>
	Altri links<ul>
	<% 
	for (int j=0; j < urls.length; j++){ %>
      <li><a href="<%= urls[j] %>" target="_blank"><%= urls[j] %></a></li>
     <% } %>
    </ul>
	<% } %>
	</td>
    <td><a href="feed-modifica.jsp?idcat=<%= cf.getId() %>&amp;idfeed=<%= f.getId() %>">modifica</a></td>
    <td><a href="feed-cancella.jsp?idcat=<%= cf.getId() %>&amp;idfeed=<%= f.getId() %>">elimina</a>	</td>
  </tr>
   <% } %>
</table>
<h3>Aggiungi feed </h3>
<form id="form1" name="form1" method="post" action="../Operazioni">
  <ul class="ulform">
    <li>nome<br /> 
      <input name="nome" type="text" id="nome" value="" size="50" />
    </li>
    <li>
      link primario
        <label>
      <input name="urlprim" type="text" id="urlprim" size="65" />
      </label>
    </li>
    <li> link 2:
      <label>
      <input name="link2" type="text" id="link2" size="65" />
      </label>
</li>
    <li>  link 3:
      <label>
      <input name="link3" type="text" id="link3" size="65" />
      </label>
</li>
    <li> link 4:
      <label>
      <input name="link4" type="text" id="link4" size="65" />
      </label>
</li>
</ul>
  <input type="submit" name="Submit" value="Inserisci" class="grande" />
  <input name="azione" type="hidden" id="azione" value="inseriscifeed" />
  <input name="idcat" type="hidden" id="idcat" value="<%= cf.getId() %>" />
  <input name="username" type="hidden" id="username" value="<%= username %>" />
</form>
<p>&nbsp;</p>
<%@ include file="../xhtml/footer.html" %>
</body>
</html>
