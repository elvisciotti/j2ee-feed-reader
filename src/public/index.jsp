<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="twi518.NewsXmlFactory.*"%>
<%@page import="twi518.servlets.*"%>
<%@page import="java.io.*"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>
<!DOCTYPE html  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Feeds aggregator - Homepage</title>
        <link href="style-sheets/stili.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
       <!--<a href="/index.jsp"><img src="multimedia/logo.gif" alt="Feeds aggr" width="441" height="129" border="0" /></a>-->
        <h1><img src="multimedia/rss_icon.gif" alt="logo" width="50" height="50" /> Aggregatore feed RSS </h1>
        <div class="main">
            <div class="partesx">
                <% 
                String username = new CookieFactory().getUsername(request);
                
                if (username.equals("")) { %>
                <h4>Login</h4>
                <form action="Login" method="post">
                   <label>  Username:
                    <input name="username" type="text" size="10" /></label>
                    <br />
                    Password:
                    <input type="password" size="10" name="password"  />
                    <label> <br />
                        <input type="submit" name="Submit" value="Login" />
                    </label>
                    <br />
                    <p><a href="jsp/registrazione.jsp">Registrati</a></p>
                </form>
                
                <% } else { %>
                <h5><%= username %> loggato</h5>
                <ul>
                    <li><a href="jsp/categorie-elenco.jsp"><strong>Gestione fonti </strong></a></li>
					<li><a href="jsp/modifica_dati_utente.jsp">modifica dati utente</a></li>
                    <li> <a href="Logout">Logout</a></li>
                    
                </ul>
                
                <% } %>
            </div>
            <div class="partedx">
                <% if (!username.equals("")) { %>
                <h3>Visualizzazione contenuti </h3>
                <applet codebase="applet" code="twi518.applet1.Applet.class" 
			archive="applet1.jar,lib1.jar,lib4.jar,lib5.jar,twi.jar" width="600" height="400">
                <param name="username" value="<%= username %>" />
			</applet><hr />
                <% } %>
                <p>Effettua il login, quindi inserisci le categorie con i feeds. Per ogni feed puoi specificare più urls.</p>
                <p>Dopo il login, in questa pagina verrà visualizzata un applet che consentirà di visualizzare in modo organizzato i contenuti dei feeds inseriti, tramite caricamento dinamico (comunicazione con l'application) successivo di categorie, feeds, titoli e quindi contenuti (description dei titoli delle fonti RSS).<br />
                    I contenuti già caricati verranno mantenuti in memoria, senza essere richiesti nuovamente.<br />
                    L'application legge solo la prima volta la url del feed RSS e mantiene in memoria l'albero DOM di tutti contenuti, quindi la richiesta delle description non comporta un overhead per l'applicazione.
                </p>
                <p>Utente di esempio con feeds gi&agrave; inseriti: <b>utente</b> (password: utente)</p>
                <p><a href="doc.html">doc.html</a></p>
            </div>
			
        </div>
		<%@ include file="xhtml/footer.html" %>
    </body>
</html>
