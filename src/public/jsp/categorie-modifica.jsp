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
<title>Modifica categoria </title>
<link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
</head>


<body>
<% String username = new CookieFactory().getUsername(request); %>
<%@ include file="../xhtml/header.html" %>
<% NewsXmlFactory nxf = new NewsXmlFactory(username+".xml"); %>
<% CategoriaFeed cf = nxf.getCatById(request.getParameter("id")); %>
<h3>Modifica categoria </h3>
<a href="categorie-elenco.jsp">torna</a>
<form id="form1" name="form1" method="post" action="../Operazioni">
  <ul class="ulform">
    <li>Nome* 
      <input name="idcat" type="hidden" id="idcat" value="<%= cf.getId() %>" />
      <br />
        <input name="nuovonome" type="text" value="<%= cf.getNome() %>" size="80" />
    </li>
    
    <li> note <br />
    <textarea name="nuovanota"><%= cf.getNote() %></textarea>
      <br />
    </li></ul>
  <input type="submit" name="Submit" value="Modifica" class="grande" />
  <input name="azione" type="hidden" id="azione" value="modificacategoria" />
  <input name="username" type="hidden" id="username" value="<%= username %>" />
</form>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p></p>
<p>&nbsp;</p>
<%@ include file="../xhtml/footer.html" %>
</body>
</html>
