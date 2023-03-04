<!DOCTYPE html>
<%-- start scriplet --%>
<%
 String sqlStatement = (String) session.getAttribute("sqlStatement");
 if (sqlStatement == null) sqlStatement = "select * from suppliers";
 String message = (String) session.getAttribute("message");
 if (message == null) message = " ";
%>
<%-- end scriptlet --%>


<html lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width">

<style>
  .grid-container {
    display: grid;
    background-color: green;
    padding: 5px;
  }

  .header {
    background-color: black;
    border: 1px solid green;
    font-size: 30px;
    text-align: center;
    padding: 20px;
    }

  .main {
    background-color: black;
    border: 1px solid green;
    font-size: 30px;
    text-align: center;
  }

  .results {
    background-color: black;
    border: 1px solid green;
    font-size: 30px;
    text-align: center;
  }

  .title {
    color: white;
  }

  #inputArea {
    background-color: black;
    color: white;
    font-size: 15px;
  }

  form {
    display: inline;
  }

  mark {
    color: red;
    background-color: black;
  }

  #data {
    color: white;
    font-size: 18px;
    border: 1px solid;
    border-color: white;
    margin-left: auto;
    margin-right: auto;

  }

  table, th {
    border: 1px solid white;
  }
  </style>

  <script>
  function eraseText() {
    inputArea.value = "";
    }

    function eraseData() {
      data.remove();
      //document.getElementById("data").innerHTML = "";
    }

  </script>

  </head>
  <body style="background-color: black">

  <div class="grid-container">
    <div class="header">
      <p class="title">Spring 2022 Project 4 Enterprise Database System<br><br>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</p>
    </div>

    <div class="main">
      <p style="font-size:15px;color:white;">You are connected to the Project 4 Enterprise System database as a <mark>client-level</mark> user.<br>Please enter any valid SQL query or update command in the box below</p>
      <form action = "clientUserApp" method = "post"><!-- the action will be the path to our java .class via .xml -->
      <textarea id="inputArea" name="sqlStatement" rows="10" cols="100"></textarea>
  <br><br>
          <input type = "submit" value = "Execute Command" />
        </form>

         <input type = "submit" value = "Clear Form" onclick="eraseText();" />
        <input type = "submit" value = "Clear Results" onclick="eraseData(); " />

  <p style="font-size:15px;color:white;">All execution results appear below</p>
    </div>

    <div class="results">
      <p style="font-size:18px;color:blue;">Database Results:</p>
      <table id="data" name = "message">
      <%-- JSP expression to access servlet variable: message --%>
      <%=message%>
      </table>
    </div>
  </div>

  </body>
  </html>
