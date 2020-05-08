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
	   if (username == null) out.print("loggati"); else { %>
        <% NewsXmlFactory nxf = new NewsXmlFactory(username+".xml");  %>
    <p>Utente loggato: <b><%= username %></b> - <a href="../Logout">logout</a> - <a href="modifica_dati_utente.jsp">modifica dati utente</a> - <a href="../index.jsp">homepage</a> </p>
        <%  CategoriaFeed[] listaCategorie = nxf.getElencoCat();
        if (listaCategorie.length == 0)
            out.print("<p>( Nessuna categoria inserita) </p>");
        else {    
        %>
		 <h3>Elenco categorie</h3>
        <table width="760" border="0" cellspacing="0" cellpadding="0" class="tabella_bordata">
            <thead>
                <tr>
                    <th width="275">nome categoria </th>
                    <th width="300">operazioni</th>
                </tr>
            </thead>
            <% 
for (int i=0; i<listaCategorie.length; i++) {
               CategoriaFeed cf = listaCategorie[i]; %>
            <tr>
                <td ><p><strong><%= cf.getNome() %></strong><br />
                  <%= cf.getNote() %>
                  <br />
                  <br />
                  <a href="feed-elenco.jsp?idcat=<%= cf.getId() %>">Gestione fonti </a></p></td>
                <td>
                    <ul>
<li><a href="categorie-modifica.jsp?id=<%= cf.getId() %>">modifica nome e opzioni </a> -
						<a href="categorie-cancella.jsp?id=<%= cf.getId() %>">elimina</a></li>
                  </ul>	</td>
            </tr>
            <% } %>
        </table>
        <% } %>
        <h3>Crea una nuova categoria</h3>
        <form id="form1" name="form1" method="post" action="../Operazioni">
            <ul class="ulform">
                <li>Nome categoria* <br />
                    <input name="nome" type="text" value="" size="60" />
                </li>
                <li>
                  <label>Note: <br />
                  <textarea name="note" cols="40" rows="3" id="note"></textarea>
</label><br />
              </li>
            </ul>
            <input type="submit" name="Submit" value="Crea" class="grande" />
            <input name="azione" type="hidden" id="azione" value="inseriscicategoria" />
            <input name="username" type="hidden" id="username" value="<%= username %>" />
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
    <%@ include file="../xhtml/footer.html" %>
	</body>
</html>
