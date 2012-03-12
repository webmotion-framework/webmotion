<!--
  #%L
  Webmotion website
  
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
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>WebMotion</title>
        <meta name="description" content="">
        <meta name="author" content="">

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

        <style type="text/css">
            .tab-pane {
                clear: both;
                padding-left: 5px;
                padding-top: 10px;
            }
        </style>
        
        <script type="text/javascript">
            <c:url value="/main" var="main_url"/>
            <c:url value="/news" var="news_url"/>
            <c:url value="/documentation" var="documentation_url"/>
            <c:url value="/showcase/hello" var="showcase_url"/>
            <c:url value="/download" var="download_url"/>
            <c:url value="/contacts" var="contacts_url"/>

            jQuery(document).ready(function () {
                $('#news').addClass("active");
                
                $("#main>a").attr("href", "${main_url}")
                $("#news>a").attr("href", "${news_url}")
                $("#documentation>a").attr("href", "${documentation_url}")
                $("#showcase>a").attr("href", "${showcase_url}")
                $("#download>a").attr("href", "${download_url}")
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
                    <a class="brand" href="<c:url value="/"/>">WebMotion</a>

                    <div class="nav-collapse">
                        <jsp:include page="/menu?sub=include" />
                        
                        <ul class="nav pull-right">
                            <li><a href="<c:url value="/?language=fr"/>">fr</a></li>
                            <li><a href="<c:url value="/?language=en"/>">en</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="container">
            <div class="content">
                <div class="page-header page-header-icon">
                    <h1 style="background: url('./img/URL_History.png') no-repeat;">Actualités <small>Dernières actualités sur WebMotion</small></h1>
                </div>
                <jsp:include page="/feed/atom?url=http://projects.debux.org/projects/webmotion/news.atom?key=dc265eb5724019b905604e959d420686e3bba50b"/>
            </div>
        </div>
        
        <hr/>
        <footer style="text-align: center">
            <p>Powered by WikiMotion and WebMotion</p>
        </footer>

    </body>
</html>
