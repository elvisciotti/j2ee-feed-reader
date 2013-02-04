<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="twi518.userXmlFactory.*"%>
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
        <title>Registrazione</title>
        <link href="../style-sheets/stili.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
	
	
        <%
        Enumeration in = request.getParameterNames();
        if(!in.hasMoreElements()){
        %>
        <h1>Registrazione</h1>
        <div id = "form_registrazione">
            <form action="registrazione.jsp" method="post" name="registrazione" id="registrazione">
              <h3> Immettere i dati nei seguenti campi </h3> 
                 <h5> (Tutti i campi sono obbligatori) </h5>
                <fieldset>
                    <p> Nome:<input type="text" name="nome" value="" /> </p> 
                    <p> Cognome:<input type="text" name="cognome" value=""  /> </p>
                    <p> Username:<input type="text" name="username" value=""  /> </p>
                    <p> Password:<input type="password" name="password" value=""  /> </p>
                    <p> Conferma password:<input type="password" name="conferma" value=""  /> </p>
                    <input type="submit" value="Ok" name="registra" />
                </fieldset>
            </form>
        </div>
        <%
        }else{
            
            
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String conferma = request.getParameter("conferma");
            
            String error = "";
            boolean err = false;
            
            if(new UserXml().doubleUsername(username)){
                error = "Il nome utente è gia stato utilizzato, sceglierne un altro.";
                err = true;
            }
            if(username.equals("utenti")){
                error = "Impossibile utilizzare questo nome";
                err = true;
            }
            if(password.compareTo(conferma)!= 0){
                error = "Le due password non combaciano!";
                err = true;
            }
            if(nome.isEmpty()||cognome.isEmpty()||username.isEmpty()||password.isEmpty()||conferma.isEmpty()){
                error = "Non hai compilato tutti i campi.";
                err = true;
            }
            if(password.length()<6){
                error = "La password deve essere lunga almeno 6 caratteri";
                err = true;
            }
            if(err){
        %>
        <h1>Registrazione</h1>
        <div id = "form_registrazione">
            <form action="registrazione.jsp" method="post" name="registrazione" id="registrazione">
                <p> </p>
                <h3> Immettere i dati nei seguenti campi </h3> </p>
                <p> </p>
                <h5> <%=error%> </h5> </p>
                <fieldset>
                    <p> Nome:<input type="text" name="nome" value="" /> </p> 
                    <p> Cognome:<input type="text" name="cognome" value="" /> </p>
                    <p> Username:<input type="text" name="username" value="" /> </p>
                    <p> Password:<input type="password" name="password" value="" /> </p>
                    <p> Conferma password:<input type="password" name="conferma" value="" /> </p>
                    <input type="submit" value="Ok" name="registra" />
                </fieldset>
            </form>
        </div> 
        <%
            }else{
                new UserXml().addUser(username,password,nome,cognome);
        //Cookie u = new Cookie("username",username);
        //u.setMaxAge(7200);
        //response.addCookie(u);
        %>
        <h1> Registrazione effettuata</h1>
        <h2><%=cognome%> <%=nome%></h2>
        <p> <a href="../index.jsp"> Effettua il login dalla Homepage</a> </p>
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
