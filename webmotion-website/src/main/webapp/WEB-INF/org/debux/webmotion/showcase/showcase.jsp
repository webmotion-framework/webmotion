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
        <meta charset="utf-8">
        <meta name="description" content="">
        <meta name="author" content="">

        <link rel="icon" type="image/png" href="<c:url value="/img/favicon.png"/>">
        <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/img/favicon.ico"/>">
        
        <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- Le styles -->
        <link rel="stylesheet/less" href="<c:url value="/js/bootstrap/bootstrap.less"/>">
        <script src="<c:url value="/js/bootstrap/less-1.1.3.min.js"/>"></script>
        
        <link rel="stylesheet" href="<c:url value="/js/prettify/prettify.css"/>" type="text/css"/>
        <script type="text/javascript" src="<c:url value="/js/prettify/prettify.js"/>"></script>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
        <script src="<c:url value="/js/bootstrap/bootstrap-dropdown.js"/>"></script>
        <script src="<c:url value="/js/bootstrap/bootstrap-tabs.js"/>"></script>

        <style type="text/css">
            body {
/*                background-color: #ededed;*/
                padding-top: 60px;
            }
            
            .tab_menu {
/*                height: 410px;*/
                clear: both;
                padding-left: 5px;
                padding-top: 10px;
/*                border-radius: 0px 0px 4px 4px;*/
/*                border-width: 0px 1px 1px 1px;*/
/*                border-style: solid;*/
/*                border-color: #DDD;*/
            }
        </style>
        
        <script type="text/javascript">
            jQuery(document).ready(function () {
                var end = /[^/]+$/g;
                var id = '#' + end.exec(window.location.pathname)[0];
                $(id).addClass("label select");
                
                var tab = $(id).parents("div");
                tab.addClass("active");
                $('#tab_' + tab[0].id).addClass("active");
                
                var value = $(id).text();
                $('#subtitle').text(value);
            });
        </script>
    </head>

    <body onload="prettyPrint()">
                
        <div class="topbar">
            <div class="fill">
                <div class="container">
                    <a class="brand" href="<c:url value="/"/>">WebMotion</a>

                    <ul class="nav">
                        <li><a href="<c:url value="/main"/>">À propos</a></li>
                        <li class="active"><a href="<c:url value="/documentation"/>">Documentation</a></li>
                        <li><a href="<c:url value="/download"/>">Téléchargement</a></li>
                        <li><a href="<c:url value="/contacts"/>">Contacts</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="container-fluid">
            <div class="page-header" style="border: none;">
                <h1 style="background: url('<c:url value="/img/My_Computer.png"/>') no-repeat; padding-left: 70px;height: 64px;line-height: 40px;">Démonstration <small><span id="subtitle"></span></small></h1>
            </div>

            <div class="sidebar well" style="padding: 5px;">
                <ul class="pills" data-tabs="tabs" style="margin: 0px; border-bottom: 1px solid #DEE6ED;">
                    <li id="tab_mapping"><a href="#mapping">Mapping</a></li>
                    <li id="tab_render"><a href="#render">Render</a></li>
                    <li id="tab_misc"><a href="#misc">Misc</a></li>
                </ul>
                
                <div class="tab-content">
                    <div id="mapping" class="tab_menu">
                        <ul>
                            <li><a id="hello" href="./hello">Hello world !</a></li>
                            <li><strong>Action</strong>
                                <ul>
                                    <li><a id="act" href="./act">Action</a></li>
                                    <li><a id="view" href="./view">View</a></li>
                                    <li><a id="url" href="./url">Url</a></li>
                                    <li><a id="helloParameters" href="./helloParameters">Parameters</a></li>
                                    <li><a id="helloDefaultParameters" href="./helloDefaultParameters">Default parameters</a></li>
                                    <li><a id="pattern" href="./pattern">Pattern parameters</a></li>
                                    <li><a id="login" href="./login">Renamed parameters</a></li>
                                    <li><a id="select" href="./select">Parameters static</a></li>
                                    <li><a id="dynamic" href="./dynamic">Dynamic action</a></li>
                                    <li><a id="text" href="./text">Dynamic view</a> <span class="label notice">New</span></li>
                                    <li><a id="helloView" href="./helloView">Dynamic jsp view</a> <span class="label notice">New</span></li>
                                    <li><a id="wikipedia" href="./wikipedia">Dynamic url</a> <span class="label notice">New</span></li>
                                    <li><a id="form" href="./form">Method</a></li>
                                    <li><a id="media" href="./media">Multiple method</a> <span class="label notice">New</span></li>
                                    <li><a id="readme" href="./readme">Static resources</a></li>
                                </ul>
                            </li>
                            <li><strong>Filter</strong>
                                <ul>
                                    <li><a id="filter" href="./filter">Do process</a></li>
                                    <li><a id="condition" href="./condition">Render</a></li>
                                </ul>
                            </li>
                            <li><strong>Error</strong>
                                <ul>
                                    <li><a id="code" href="./code">Code http</a></li>
                                    <li><a id="exception" href="./exception">Exception</a></li>
                                    <li><a id="notfound" href="./notfound">All</a> <span class="label notice">New</span></li>
                                    <li><a id="npe" href="./npe">View</a></li>
                                </ul>
                            </li>
                            <li><strong>Extension</strong>
                                <ul>
                                    <li><a id="blog" href="./blog">Action</a> <span class="label notice">New</span></li>
                                    <li><a id="spring" href="./spring">Spring</a> <span class="label notice">New</span></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                    <div id="render" class="tab_menu">
                        <ul>
                            <li><strong>Basic</strong>
                                <ul>
                                    <li><a id="first" href="./first">Action</a></li>
                                    <li><a id="index" href="./index">View</a></li>
                                    <li><a id="helloModel" href="./helloModel">Model</a></li>
                                    <li><a id="save" href="./save">Url</a></li>
                                    <li><a id="content" href="./content">Content</a></li>
                                    <li><a id="stream" href="./stream">Stream</a> <span class="label notice">New</span></li>
                                    <li><a id="load" href="./load">Reload page</a></li>
                                    <li><a id="forbidden" href="./forbidden">Error</a> <span class="label notice">New</span></li>
                                    <li><a id="nocontent" href="./nocontent">Status</a> <span class="label notice">New</span></li>
                                </ul>
                            </li>
                            <li><strong>Data</strong>
                                <ul>
                                    <li><a id="xml" href="./xml">XML</a></li>
                                    <li><a id="json" href="./json">JSON</a></li>
                                    <li><a id="jsonp" href="./jsonp">JSONP</a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                    <div id="misc" class="tab_menu">
                        <ul>
                            <li><strong>Action</strong>
                                <ul>
                                    <li><a id="page" href="./page">Include</a></li>
                                    <li><a id="calc" href="./calc">AJAX</a></li>
                                    <li><a id="message" href="./message">Flash message</a></li>
                                    <li><a id="shop" href="./shop">Validation</a> <span class="label notice">New</span></li>
                                </ul>
                            </li>
                            <li><strong>File</strong>
                                <ul>
                                    <li><a id="file" href="./file">File</a></li>
                                    <li><a id="upload" href="./upload">File with progress</a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <div class="content">
                <div class="row" style="min-height: 740px;">
                    <div class="span16">
                        <ul class="tabs" data-tabs="tabs">
                            <li class="active"><a href="#demo">Démo</a></li>
                            <c:forEach var="file" items="${files}" varStatus="status">
                                    <li><a href="#${status.index}">${file.name}</a></li>
                            </c:forEach>
                        </ul>

                        <div class="tab-content">

                            <div class="active" id="demo">
                                <c:forEach var="path" items="${path_demo}">
                                    <div class="alert-message block-message info" style="margin-bottom: 5px;">
                                        <p>http://serverName:port/contextPath/<strong>${fn:substringAfter(path, '/showcase/action/')}</strong></p>
                                    </div>
                                    <iframe src="<c:url value="${path}"/>" style="margin-bottom: 20px; width: 98%;height: auto;background-color: #F4F7FB;border: 1px solid #DEE6ED;padding: 8.5px;-webkit-border-radius: 3px;-moz-border-radius: 3px;border-radius: 3px;">
                                    </iframe>
                                </c:forEach>
                            </div>
                            
                            <c:forEach var="file" items="${files}" varStatus="status">
                                <div id="${status.index}">
                                    <div class="alert-message block-message info" style="margin-bottom: 5px;">
                                        <p><strong>Path :</strong> ${file.path}</p>
                                    </div>
                                    <pre class="prettyprint" style="background-color: #F4F7FB;border: 1px solid #DEE6ED;padding: 8.5px;">
${file.content}
                                    </pre>
                                </div>
                            </c:forEach>
                            
                        </div>
                    </div>
                </div>
                
                <footer style="text-align: center">
                    <p>Powered by WikiMotion and WebMotion</p>
                </footer>
            </div>
            

        </div> <!-- /container -->

    </body>
</html>
