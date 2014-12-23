<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
    <head>
        <style>
            body {
                text-align: left;
            }
            
            .container {
                width: auto;
            }

            .space {
                padding-bottom: 5px;
            }

            .tab-pane {
                clear: both;
                padding-left: 5px;
                padding-top: 10px;
            }

            .prettyprint.linenums {
                -webkit-box-shadow: inset 40px 0 0 #fbfbfc, inset 41px 0 0 #ececf0;
                -moz-box-shadow: inset 40px 0 0 #fbfbfc, inset 41px 0 0 #ececf0;
                box-shadow: inset 40px 0 0 #fbfbfc, inset 41px 0 0 #ececf0;
            }

            .linenums ol {
                margin: 0 0 9px 30px;
            }

            .linenums li {
                list-style-type: decimal;
                padding-left: 12px;
                color: #bebec5;
                background: transparent;
                line-height: 18px;
                text-shadow: 0 1px 0 #fff;
            }

            .hideline ol {
                margin-left: 0px;
            }

            .hideline li {
                list-style-type: none;
                padding-left: 0px;
            }
        </style>
        <script type="text/javascript">
            jQuery(document).ready(function () {                
                var end = /[^/]+$/g;
                var endValue = end.exec(window.location.pathname)[0];

                $("#twitter").attr("data-url", "http://www.webmotion-framework.org/showcase/" + endValue);
                var id = '#' + endValue;
                $(id).addClass("label select");

                var tab = $(id).parents("div");
                tab.addClass("active");
                $('#tab_' + tab[0].id).addClass("active");

                var value = $(id).text();
                $('#title').text($("#showcase>a").text());
                $('#subtitle').text(value);
            });
        </script>
    </head>

    <body>
        <div class="container-fluid">
            <div class="page-header page-header-icon" style="border: none;">
                <h1 style="background: url('<c:url value="/img/My_Computer.png"/>') no-repeat;"><span id="title"></span> <small><span id="subtitle"></span></small></h1>
            </div>
            <!--<span class="label label-info">New</span>-->
            <div class="row-fluid">
                <div class="span2 well" style="padding: 5px; margin-bottom: 10px; min-width: 200px;">
                    <ul class="nav nav-pills" style="margin: 0px; border-bottom: 1px solid #DEE6ED;">
                        <li id="tab_mapping"><a href="#mapping" data-toggle="tab">Mapping</a></li>
                        <li id="tab_render"><a href="#render" data-toggle="tab">Render</a></li>
                        <li id="tab_misc"><a href="#misc" data-toggle="tab">Misc</a></li>
                    </ul>

                    <div class="tab-content">
                        <div id="mapping" class="tab-pane fade in">
                            <ul>
                                <li><a id="hello" href="./hello">Hello world !</a></li>
                                <li><strong>Action</strong>
                                    <ul>
                                        <li><a id="act" href="./act">Action</a></li>
                                        <li><a id="view" href="./view">View</a></li>
                                        <li><a id="url" href="./url">Url</a></li>
                                        <li class="space"><a id="forward" href="./forward">Forward</a></li>

                                        <li><a id="helloParameters" href="./helloParameters">Parameters</a></li>
                                        <li><a id="helloDefaultParameters" href="./helloDefaultParameters">Default parameters</a></li>
                                        <li><a id="pattern" href="./pattern">Pattern parameters</a></li>
                                        <li><a id="login" href="./login">Object parameters</a></li>
                                        <li><a id="hellos" href="./hellos">Indexed parameters</a></li>
                                        <li><a id="helloNames" href="./helloNames">Object indexed parameters</a></li>
                                        <li class="space"><a id="select" href="./select">Parameters static</a></li>

                                        <li><a id="dynamic" href="./dynamic">Dynamic action</a></li>
                                        <li><a id="text" href="./text">Dynamic view</a></li>
                                        <li><a id="helloView" href="./helloView">Dynamic jsp view</a></li>
                                        <li><a id="wikipedia" href="./wikipedia">Dynamic url or redirect</a></li>
                                        <li class="space"><a id="dynamic_forward" href="./dynamic_forward">Dynamic forward</a></li>

                                        <li><a id="form" href="./form">Method</a></li>
                                        <li class="space"><a id="media" href="./media">Multiple method</a></li>

                                        <li><a id="static" href="./static">Static resources</a></li>
                                    </ul>
                                </li>
                                <li><strong>Filter</strong>
                                    <ul>
                                        <li><a id="filter" href="./filter">Do process</a></li>
                                        <li><a id="chain" href="./chain">Do chain</a></li>
                                        <li><a id="condition" href="./condition">Render</a></li>
                                        <li><a id="decorator" href="./decorator">Default parameters</a></li>
                                    </ul>
                                </li>
                                <li><strong>Error</strong>
                                    <ul>
                                        <li><a id="code" href="./code">Code http</a></li>
                                        <li><a id="exception" href="./exception">Exception</a></li>
                                        <li><a id="notfound" href="./notfound">All</a></li>
                                        <li><a id="npe" href="./npe">View</a></li>
                                    </ul>
                                </li>
                                <li><strong>Extension</strong>
                                    <ul>
                                        <li><a id="blog" href="./blog">Action</a></li>
                                        <li><a id="stats" href="./stats">Pattern</a></li>
                                    </ul>
                                </li>
                                <li><strong>Properties</strong>
                                    <ul>
                                        <li><a id="properties" href="./properties">Basic</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                        <div id="render" class="tab-pane fade in">
                            <ul>
                                <li><strong>Basic</strong>
                                    <ul>
                                        <li><a id="index" href="./index">View</a></li>
                                        <li><a id="helloModel" href="./helloModel">Model</a></li>
                                        <li><a id="save" href="./save">Url or redirect</a></li>
                                        <li><a id="first" href="./first">Action</a></li>
                                        <li><a id="internal" href="./internal">Forward</a></li>
                                        <li><a id="content" href="./content">Content</a></li>
                                        <li><a id="stream" href="./stream">Stream</a></li>
                                        <li><a id="application" href="./application">Download</a></li>
                                        <li><a id="load" href="./load">Reload page</a></li>
                                        <li><a id="forbidden" href="./forbidden">Error</a></li>
                                        <li><a id="nocontent" href="./nocontent">Status</a></li>
                                    </ul>
                                </li>
                                <li><strong>Data</strong>
                                    <ul>
                                        <li><a id="api" href="./api">Default render</a></li>
                                        <li><a id="xml" href="./xml">XML</a></li>
                                        <li><a id="json" href="./json">JSON</a></li>
                                        <li><a id="jsonp" href="./jsonp">JSONP</a></li>
                                        <li><a id="template" href="./template">StringTemplate</a></li>
                                        <li><a id="feed" href="./feed">RSS/ATOM</a></li>
                                    </ul>
                                </li>
                                <li><strong>Websocket</strong>
                                    <ul>
                                        <li><a id="ws_text" href="./ws_text">Text</a></li>
                                        <li><a id="ws_json" href="./ws_json">JSON</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                        <div id="misc" class="tab-pane fade in">
                            <ul>
                                <li><strong>Action</strong>
                                    <ul>
                                        <li><a id="shop" href="./shop">Validation</a></li>
                                        <li><a id="message" href="./message">Flash message</a></li>
                                        <li><a id="cookie" href="./cookie">Cookie manager</a></li>
                                        <li><a id="cookie_object" href="./cookie_object">Cookie manager<br/>(object)</a></li>
                                        <li><a id="client_session" href="./client_session">Client session</a></li>
                                        <li><a id="file" href="./file">File</a></li>
                                        <li><a id="upload" href="./upload">File with progress</a></li>
                                        <li><a id="page" href="./page">Include</a></li>
                                        <li><a id="calc" href="./calc">AJAX</a></li>
                                        <li><a id="async" href="./async">Async</a></li>
                                    </ul>
                                </li>
                                <li><strong>Server</strong>
                                    <ul>
                                        <li><a id="listener" href="./listener">Server listener</a></li>
                                        <li><a id="converter" href="./converter">Converter</a></li>
                                        <li><a id="injector" href="./injector">Injector</a></li>
                                        <li><a id="global" href="./global">Global controller</a></li>
                                    </ul>
                                </li>
                                <li><strong>Convention</strong>
                                    <ul>
                                        <li><a id="convention_controller" href="./convention_controller">Controller</a></li>
                                        <li><a id="convention_allFilter" href="./convention_allFilter">All filter</a></li>
                                        <li><a id="convention_packageFilter" href="./convention_packageFilter">Package filter</a></li>
                                    </ul>
                                </li>
                                <li><strong>Extras</strong>
                                    <ul>
                                        <li><a id="spring" href="./spring">Spring</a></li>
                                        <li><a id="sitemesh" href="./sitemesh">SiteMesh</a></li>
                                        <li><a id="jpa" href="./jpa">Hibernate/JPA</a></li>
                                        <li><a id="shiro" href="./shiro">Shiro</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="span8">
                    <ul class="nav nav-tabs">
                        <li class="active"><a href="#demo" data-toggle="tab">Démo</a></li>
                        <c:forEach var="file" items="${files}" varStatus="status">
                                <li><a href="#${status.index}" data-toggle="tab">${file.name}</a></li>
                        </c:forEach>
                    </ul>

                    <div class="tab-content">

                        <div class="tab-pane active fade in" id="demo">
                            <c:forEach var="path" items="${path_demo}">
                                <div class="alert alert-info" style="margin-bottom: 5px;">
                                    <a class="btn btn-primary" style="float: right; position: relative;top: -5px;right: -21px;" href="<c:url value="${path}"/>" target="_blank">Try it »</a>
                                    http://serverName:port/contextPath<strong>${path}</strong>
                                </div>
                                <iframe src="<c:url value="${path}"/>" style="margin-bottom: 20px; width: 100%; height: auto;background-color: #F4F7FB;border: 1px solid #DEE6ED;-webkit-border-radius: 3px;-moz-border-radius: 3px;border-radius: 3px;">
                                </iframe>
                            </c:forEach>
                        </div>

                        <c:forEach var="file" items="${files}" varStatus="status">
                            <div id="${status.index}" class="tab-pane fade in">
                                <div class="alert alert-info" style="margin-bottom: 5px;">
                                    <c:if test="${not empty file.content}">
                                        <a id="hideLines${status.index}" class="btn btn-primary" style="float: right; position: relative;top: -5px;right: -21px;" onclick="$(content${status.index}).removeClass('linenums');$(content${status.index}).addClass('hideline');$('#hideLines${status.index}').hide();$('#showLines${status.index}').show();" target="_blank">Hide lines »</a>
                                        <a id="showLines${status.index}" class="btn btn-primary" style="display: none; float: right; position: relative;top: -5px;right: -21px;" onclick="$(content${status.index}).removeClass('hideline');$(content${status.index}).addClass('linenums');$('#hideLines${status.index}').show();$('#showLines${status.index}').hide();" target="_blank">Show lines »</a>
                                    </c:if>
                                    <strong>Path :</strong> ${file.path}
                                </div>
                                    <c:if test="${empty file.content}">
                                        <c:set var="filePathSplit" value="${fn:split(file.path, '/')}"/>
                                        <c:set var="filePathSplitLength" value="${fn:length(filePathSplit)}"/>
                                        <c:forEach var="path" items="${filePathSplit}" begin ="${filePathSplitLength - 1}" end="${filePathSplitLength}">
                                            <img src="<c:url value="/${path}"/>"/>
                                        </c:forEach>

                                    </c:if>
                                    <c:if test="${not empty file.content}">
                                        <pre id="content${status.index}" class="prettyprint linenums" style="background-color: #F4F7FB;border: 1px solid #DEE6ED;padding: 8.5px;">
${file.content}</pre>
                                    </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>

            </div>
        </div>
    </body>
</html>
