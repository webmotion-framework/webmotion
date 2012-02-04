<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
  #%L
  Webmotion in test
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 Debux
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as 
  published by the Free Software Foundation, either version 3 of the 
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.
  
  You should have received a copy of the GNU General Lesser Public 
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-3.0.html>.
  #L%
-->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="${language}">
    <head>
        <meta charset="utf-8">
        <title>${site_name}</title>
        
        <link rel="icon" type="image/png" href="<c:url value="/img/favicon.png"/>">
        <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/img/favicon.ico"/>">
        
        <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]--> 
        
        <!-- Le styles --> 
        <link rel="stylesheet" href="<c:url value="/bootstrap/css/bootstrap.css"/>">
        <link rel="stylesheet" href="<c:url value="/prettify/prettify.css"/>" type="text/css"/>
        <link rel="stylesheet" href="<c:url value="/css/style.css"/>">
        
        <script src="http://twitter.github.com/bootstrap/assets/js/jquery.js"></script> 
        <script type="text/javascript" src="<c:url value="/prettify/prettify.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/bootstrap/js/bootstrap.js"/>"></script>
    </head>

    <body onload="prettyPrint()">

        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <a class="brand" href="<c:url value="/"/>">${site_name}</a>

                    <ul class="nav">
                        <c:url value="/main" var="main_url"/>
                        <c:if test="${layout_url == '/main'}" >
                            <c:set var="main_active" value="active"/>
                        </c:if>
                        <li class="${main_active}"><a href="${main_url}">À propos</a></li>
                        
                        <c:url value="/documentation" var="documentation_url"/>
                        <c:if test="${layout_url == '/documentation' 
                                      || layout_url == '/begin'
                                      || layout_url == '/mapping'
                                      || layout_url == '/action'
                                      || layout_url == '/extensions'
                                      || layout_url == '/advanced'}" >
                            <c:set var="documentation_active" value="active"/>
                        </c:if>
                        <li class="${documentation_active}"><a href="${documentation_url}">Documentation</a></li>
                        
                        <c:url value="/download" var="download_url"/>
                        <c:if test="${layout_url == '/download'}" >
                            <c:set var="download_active" value="active"/>
                        </c:if>
                        <li class="${download_active}"><a href="${download_url}">Téléchargement</a></li>
                        
                        <c:url value="/contacts" var="contacts_url"/>
                        <c:if test="${layout_url == '/contacts'}" >
                            <c:set var="contacts_active" value="active"/>
                        </c:if>
                        <li class="${contacts_active}"><a href="${contacts_url}">Contacts</a></li>
                    </ul>
                    
                    <ul class="nav secondary-nav">
                        <c:forEach items="${languages}" var="lang">
                            <li><a href="<c:url value="/?language=${lang}"/>">${lang}</a></li>
                        </c:forEach>
                    </ul>
                </div> 
            </div>
        </div>

        <div class="container">
            <div class="content">
                <jsp:include page="${layout_url}" />
            </div>
        </div>
            
        <hr/>
        <footer style="text-align: center">
            <p>Powered by WikiMotion and WebMotion</p>
        </footer>

    </body>
</html>
