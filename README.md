J2EE RSS/ATOM feed reader
=========================

Web application built with Java JSP + Java socket server + Applet.
Data store on disk, XML format.

# Features
 * User registration, login and user detail editing
 * CRUD operations for RSS/ATOM feeds, organized in 2-leves categories
 * Applet to dynamically talk to the server (Java daemon) in XML. 
   A tree recursively displays the data, lazy-loaded from the application server.
   The data is (hierarchy): main category -> subcategory -> feed ->  news from the feed
 * Array Cache implemented from both the applet (data received from the server)
   and application server (no multiple requests to the ATOM/RSS feeds)

# Screenhosts

Applet

![Applet](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/applet-5.gif)

![Applet](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/applet-6.gif)

<!--
User login, registration, edit details

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-register.gif)

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-login.gif)

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-manage-feeds-list.gif)
-->