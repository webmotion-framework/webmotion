<%--
  #%L
  WebMotion test
  $Id:$
  $HeadURL:$
  %%
  Copyright (C) 2011 - 2015 Debux
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
  --%>
<!DOCTYPE html>
<html>
    <head>
        <title>Echo Chat</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <script>
            var connection;
            var output;
            
            function init() {
                var url = window.location.toString().replace("http", "ws").replace("index", "ws");
                connection = new WebSocket(url);
                output = document.getElementById("output");
            
                connection.onopen = function () {
                    connection.send("Open");
                };

                connection.onclose = function () {
                    connection.send("Close");
                };

                connection.onerror = function (error) {
                    console.log(error);
                    writeToScreen("Error " + error);
                };

                connection.onmessage = function (e) {
                    console.log(e);
                    writeToScreen(e.data);
                };
            }
            
            function send() {
                var form = document.getElementById("chat");
                var message = form.elements['message'].value;
                connection.send(message);
            }

            function writeToScreen(message) {
                var pre = document.createElement("p");
                pre.style.wordWrap = "break-word";
                pre.innerHTML = message;
                output.appendChild(pre);
            }
        </script>
    </head>
    <body onload="init()">
        <h1>Echo Chat</h1>
        <div id="output" style="width: 300px; height: 150px; border: 1px solid black; overflow: auto;"></div>
        
        <form id="chat" onsubmit="send(); return false;">
            Message <input type="text" name="message"/> 
            <input type="submit" value="Send"/>
        </form>
        
    </body>
</html>
