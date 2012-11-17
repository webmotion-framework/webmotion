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

<html lang="${language}">
    <head>
        <meta charset="utf-8">
        <c:if test="${language == 'fr'}">
            <meta name="description" content="WebMotion est un framework web Java fondé sur le standard Java EE 6. La principal fonctionnalité est d’assurer la couche de présentation par le biais de pages ou de templates. Mais il propose également la création d’API REST pour les appels AJAX. Le framework est basé sur un fichier de mapping permettant de définir des actions suivant des événements, comme par exemple lancer une action Java selon une URL.">
        </c:if>
        <c:if test="${language != 'fr'}">
            <meta name="description" content="WebMotion is a Java web framework based on the Java EE6 standard. The main functionality consists in assuring the presentation layer with pages or templates. But it also offers the creation of REST APIs for the AJAX calls. The framework is based on a mapping file which enables to define actions based on events, such as launching a Java action according to a URL.">
        </c:if>
            
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
    
        <script type="text/javascript">
            <c:url value="/main" var="main_url"/>
            <c:if test="${layout_url == '/main'}" >
                <c:set var="main_active" value="active"/>
            </c:if>

            <c:url value="/documentation" var="documentation_url"/>
            <c:if test="${layout_url == '/documentation' 
                          || layout_url == '/begin'
                          || layout_url == '/mapping'
                          || layout_url == '/action'
                          || layout_url == '/extensions'
                          || layout_url == '/advanced'
                          || layout_url == '/extras'
                          || layout_url == '/wikimotion'
                          || layout_url == '/changelog'}" >
                <c:set var="documentation_active" value="active"/>
            </c:if>

            <c:url value="/showcase" var="showcase_url"/>
            <c:if test="${layout_url == '/showcase'}" >
                <c:set var="showcase_active" value="active"/>
            </c:if>

            <c:url value="/download" var="download_url"/>
            <c:if test="${layout_url == '/download'}" >
                <c:set var="download_active" value="active"/>
            </c:if>

            <c:url value="/contacts" var="contacts_url"/>
            <c:if test="${layout_url == '/contacts'}" >
                <c:set var="contacts_active" value="active"/>
            </c:if>
                            
            jQuery(document).ready(function () {
                $('#main').addClass("${main_active}");
                $("#main>a").attr("href", "${main_url}")
                
                $('#documentation').addClass("${documentation_active}");
                $("#documentation>a").attr("href", "${documentation_url}")
                
                $('#showcase').addClass("${showcase_active}");
                $("#showcase>a").attr("href", "${showcase_url}")
                
                $('#download').addClass("${download_active}");
                $("#download>a").attr("href", "${download_url}")
                
                $('#contacts').addClass("${contacts_active}");
                $("#contacts>a").attr("href", "${contacts_url}")
            });
        </script>
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
                    <a class="brand" href="<c:url value="/"/>">${site_name}</a>

                    <div class="nav-collapse">
                        <jsp:include page="/menu?sub=include" />
                        
                        <ul class="nav pull-right">
                            <li>
                                <a style="display: none;" href="https://twitter.com/share" class="twitter-share-button" data-lang="${sessionScope["javax.servlet.jsp.jstl.fmt.locale.session"]}" data-url="http://www.webmotion-framework.org${layout_url}" data-size="large">Tweet</a>
                                <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
                            </li>
                            <li>
                            <c:forEach items="${languages}" var="lang">
                                <a href="<c:url value="/?language=${lang}"/>"><img alt="${lang}" src="<c:url value="/img/${lang}.png"/>"/></a>
                            </c:forEach>
                            </li>
                        </ul>
                        
                    </div>
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
            <p>Powered by WikiMotion and WebMotion - version 2.3.2 - <a href="http://www.gnu.org/licenses/lgpl-3.0.html">License LGPL</a></p>
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
