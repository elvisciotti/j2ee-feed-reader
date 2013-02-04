<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="twi518.NewsXmlFactory.*"%>
<%@page import="java.util.*"%>
<%@page import="twi518.userXmlFactory.*"%>
<%@page import="twi518.servlets.*"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Modifica</title>
		 <link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
    <%
    Enumeration in = request.getParameterNames();
    String username = new CookieFactory().getUsername(request);
    String old_nome = new UserXml().getUserName(username);
    String old_cognome = new UserXml().getUserSurname(username);
    if(!in.hasMoreElements()){
    %>
    <h1>Modifica i tuoi dati</h1>
    <div id = "form_modifica">
	    <form name="modifica_dati" action="modifica_dati_utente.jsp" method="POST">
	<h3> Immettere i dati nei seguenti campi </h3>
		 <h5>( tutti i campi sono obbligatori )  </h5>
		 <fieldset>
		    <p> Nome:<input type="text" name="nome" value="<%=old_nome%>" width="40" /> </p> 
		    <p> Cognome:<input type="text" name="cognome" value="<%=old_cognome%>" width="40" /> </p>
		    <p> Username: <%=username%> </p>
		    <p> Nuova password:<input type="password" name="password" value="" width="40" /> </p>
		    <p> Conferma nuova password:<input type="password" name="conferma" value="" width="40" /> </p>
		    <input type="submit" value="salva modifiche" name="modifica" />
		oppure torna all'<a href="../index.jsp">homepage 
		 </a>
		 </fieldset>
	    </form>
	</div> 
    <%
    }else{
	String new_nome = request.getParameter("nome");
	String new_cognome = request.getParameter("cognome");
	String new_password = request.getParameter("password");
	String new_conferma = request.getParameter("conferma");
	String error = "";
	boolean err = false;
		if(new_password.compareTo(new_conferma)!= 0){
			error = "Le due password non combaciano!";
			err = true;
		}
		if(new_nome.isEmpty()||new_cognome.isEmpty()||new_password.isEmpty()||new_conferma.isEmpty()){
			error = "Non hai compilato tutti i campi.";
			err = true;
		}
		if(new_password.length()<6){
			error = "La password deve essere lunga almeno 6 caratteri";
			err = true;
		}
	    if(err){
    %>
    <h1>Modifica i tuoi dati</h1>
    <div id = "form_modifica">
	    <form name="modifica_dati" action="modifica_dati_utente.jsp" method="POST">
		<p> <h3> Immettere i dati nei seguenti campi </h3> </p>
		<p> <h5> <%=error%> </h5> </p>
		<fieldset>
		    <p> Nome:<input type="text" name="nome" value="" width="40" /> </p> 
		    <p> Cognome:<input type="text" name="cognome" value="" width="40" /> </p>
		    <p> Username:<%=username%> </p>
		    <p> Nuova password:<input type="password" name="password" value="" width="40" /> </p>
		    <p> Conferma nuova password:<input type="password" name="conferma" value="" width="40" /> </p>
		    <input type="submit" value="Ok" name="modifica" />
		</fieldset>
	    </form>
	</div> 
    <%
    }else{
	new UserXml().modifyUser(username,new_password,new_nome,new_cognome);
	new CookieFactory().setUsername(response,username);
    %>
        <h2> Modifica effettuata</h2>
        <p><%=new_cognome%> <%=new_nome%></p>
	<p> <a href="../index.jsp"> Homepage </a></p>
    <%
    }}
    %>
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
    
    </body>
</html>
