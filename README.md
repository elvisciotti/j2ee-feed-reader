J2EE RSS/ATOM feed reader
=========================

Web application built with Java 5,composed by admin area, user registration and login, feed selection, and Applet to download and read news caching content.

Built in 2010

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

![Applet1](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/applet-5.gif)

![Applet2](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/applet-6.gif)

User registration form
![Applet](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-edit-user-details.gif)

Admin area to edit RSS list
![Applet](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-manage-feeds.gif)

<!--
User login, registration, edit details

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-register.gif)

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-login.gif)

![User](https://raw.github.com/elvisciotti/j2ee-feed-reader/master/screenshots/jsp-manage-feeds-list.gif)
-->