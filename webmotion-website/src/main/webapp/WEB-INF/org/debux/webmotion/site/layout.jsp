<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<!--
  #%L
  WebMotion website
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 - 2012 Debux
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

<html lang="${sessionScope.language}">
    <head>
        <meta charset="utf-8">
        
        <c:if test="${sessionScope.language == 'fr'}">
            <meta name="description" content="WebMotion est un framework web Java fondé sur le standard Java EE 6. La principal fonctionnalité est d’assurer la couche de présentation par le biais de pages ou de templates. Mais il propose également la création d’API REST pour les appels AJAX. Le framework est basé sur un fichier de mapping permettant de définir des actions suivant des événements, comme par exemple lancer une action Java selon une URL.">
        </c:if>
        <c:if test="${sessionScope.language == 'en'}">
            <meta name="description" content="WebMotion is a Java web framework based on the Java EE6 standard. The main functionality consists in assuring the presentation layer with pages or templates. But it also offers the creation of REST APIs for the AJAX calls. The framework is based on a mapping file which enables to define actions based on events, such as launching a Java action according to a URL.">
        </c:if>
            
        <title>WebMotion</title>
        
        <link rel="icon" type="image/png" href="<c:url value="/img/favicon.png"/>">
        <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/img/favicon.ico"/>">
        
        <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]--> 
        
        <!-- Le styles --> 
        <link rel="stylesheet" href="<c:url value="/lib/bootstrap/css/bootstrap.css"/>">
        <link rel="stylesheet" href="<c:url value="/lib/prettify/prettify.css"/>" type="text/css"/>
        <link rel="stylesheet" href="<c:url value="/css/style.css"/>">
        
        <script src="http://code.jquery.com/jquery-1.7.2.min.js"></script> 
        <script type="text/javascript" src="<c:url value="/lib/prettify/prettify.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/lib/bootstrap/js/bootstrap.js"/>"></script>
    
        <script type="text/javascript">
            <c:url value="/main" var="main_url"/>
            <c:url value="/documentation" var="documentation_url"/>
            <c:url value="/tutorial" var="tutorial_url"/>
            <c:url value="/showcase" var="showcase_url"/>
            <c:url value="/download" var="download_url"/>
            <c:url value="/contacts" var="contacts_url"/>

            jQuery(document).ready(function () {
                var l = location.toString();
                
                l.indexOf("${main_url}") != -1 && $('#main').addClass("active");
                $("#main>a").attr("href", "${main_url}");
                
                l.indexOf("${documentation_url}") != -1 && $('#documentation').addClass("active");
                $("#documentation>a").attr("href", "${documentation_url}");
                
                l.indexOf("${tutorial_url}") != -1 && $('#tutorial').addClass("active");
                $("#tutorial>a").attr("href", "${tutorial_url}");
                
                l.indexOf("${showcase_url}") != -1 && $('#showcase').addClass("active");
                $("#showcase>a").attr("href", "${showcase_url}");
                
                l.indexOf("${download_url}") != -1 && $('#download').addClass("active");
                $("#download>a").attr("href", "${download_url}")
                
                l.indexOf("${contacts_url}") != -1 && $('#contacts').addClass("active");
                $("#contacts>a").attr("href", "${contacts_url}");
            });
        </script>
        
        <sitemesh:write property='head'/>
    </head>

    <body onload="prettyPrint()">

        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
                        <span class="icon-bar"></span> 
                        <span class="icon-bar"></span> 
                        <span class="icon-bar"></span> 
                    </a> 
                    <a class="brand" href="<c:url value="/"/>">WebMotion</a>

                    <div class="nav-collapse">
                        <c:if test="${sessionScope.language == 'fr'}">
                            <%@include file="menu_fr.jsp"%>
                        </c:if>
                        <c:if test="${sessionScope.language == 'en'}">
                            <%@include file="menu_en.jsp"%>
                        </c:if>
                        
                        
                        <ul class="nav pull-right">
                            <li>
                                <a style="display: none;" href="https://twitter.com/share" class="twitter-share-button" data-lang="${sessionScope["javax.servlet.jsp.jstl.fmt.locale.session"]}" data-url="http://www.webmotion-framework.org${layout_url}" data-size="large">Tweet</a>
                                <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
                            </li>
                            <li>
                                <a href="<c:url value="/main?lang=en"/>"><img alt="en" src="<c:url value="/img/en.png"/>"/></a>
                                <a href="<c:url value="/main?lang=fr"/>"><img alt="fr" src="<c:url value="/img/fr.png"/>"/></a>
                            </li>
                        </ul>
                        
                    </div>
                </div> 
            </div>
        </div>

        <div class="container">
            <div class="content">
                <sitemesh:write property='body'/>
            </div>
        </div>
            
        <hr/>
        <footer style="text-align: center">
            <p>Powered by WebMotion - version 2.5 - <a href="http://www.gnu.org/licenses/lgpl-3.0.html">License LGPL</a> - © 2014</p>
        </footer>

        <!-- Piwik -->
        <script type="text/javascript">
            var pkBaseURL = (("https:" == document.location.protocol) ? "https://piwik.debux.org/" : "http://piwik.debux.org/");
            document.write(unescape("%3Cscript src='" + pkBaseURL + "piwik.js' type='text/javascript'%3E%3C/script%3E"));
        </script><script type="text/javascript">
            try {
                var piwikTracker = Piwik.getTracker(pkBaseURL + "piwik.php", 1);
                piwikTracker.trackPageView();
                piwikTracker.enableLinkTracking();
            } catch( err ) {}
        </script><noscript><p><img src="http://piwik.debux.org/piwik.php?idsite=1" style="border:0" alt="" /></p></noscript>
        <!-- End Piwik Tracking Code -->

    </body>
</html>
