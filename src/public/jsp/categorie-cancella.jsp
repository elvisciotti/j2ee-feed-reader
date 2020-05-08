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
<title>Gestione categorie feeds utente loggato</title>
<link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
</head>
<body>
		<%@ include file="../xhtml/header.html" %>
       <% String username = new CookieFactory().getUsername(request);
	   if (username==null) out.print("loggati"); else { %>
        <% NewsXmlFactory nxf = new NewsXmlFactory(username+".xml"); 
		CategoriaFeed cf = nxf.getCatById(request.getParameter("id")); %>
        <form id="form1" name="form1" method="post" action="../Operazioni">
<p>Sicuro di voler cancellare la categoria  <strong><%= cf.getNome() %></strong> ?</p>
            <p>
              <input type="submit" name="Submit" value="conferma" class="grande" />
              <input name="azione" type="hidden" id="azione" value="cancellacategoria" />
              <input name="username" type="hidden" id="username" value="<%= username %>" />
                <input name="id" type="hidden" id="id" value="<%= cf.getId() %>" />
            oppure <a href="categorie-elenco.jsp">Annulla</a></p>
        </form>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
<%--
    This example uses JSTL, uncomment the taglib directive above.
    To test, display the page like this: index.jsp?sayHello=true&name=Murphy
    --%>
        <%--
    <c:if test="${param.sayHello}">
        <!-- Let's welcome the user ${param.name} -->
        Hello ${param.name}!
    </c:if>
    --%>
    <% } %>
</body>
</html>
