<%--
  Created by IntelliJ IDEA.
  User: gregg varona
  Date: 2/7/2016
  Time: 11:57 AM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Calendar Web Application</title>
  <meta charset="UTF-8"/>
  <link rel='stylesheet' href='${pageContext.request.contextPath}/assets/fullcalendar-2.6.0/fullcalendar.css' />
  <link rel='stylesheet' href='${pageContext.request.contextPath}/assets/bootstrap-3.1.1/bootstrap.min.css' />
  <script src='${pageContext.request.contextPath}/assets/fullcalendar-2.6.0/lib/jquery.min.js'></script>
  <script src='${pageContext.request.contextPath}/assets/fullcalendar-2.6.0/lib/moment.min.js'></script>
  <script src='${pageContext.request.contextPath}/assets/fullcalendar-2.6.0/fullcalendar.js'></script>
  <script src='${pageContext.request.contextPath}/assets/fullcalendar-2.6.0/lib/moment.min.js'></script>
  <script src='${pageContext.request.contextPath}/assets/bootstrap-3.1.1/bootstrap.min.js'></script>
  <script>var ctx = "${pageContext.request.contextPath}"</script>
</head>
<body>
  <section class="container">
    <div class="col-lg-12 col-md-12">
      <h1>Calendar Web Application</h1>
    </div>
    <div class="col-lg-4 col-md-4 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h2 class="panel-title">Calendar seleciton</h2>
        </div>
        <div class="panel-body">
          <form id="calendar-selection">
            <button id="calendar-selection-btn" class="btn btn-default">Update Calendar</button>
          </form>
        </div>
      </div>
      <div class="panel panel-default">
        <div class="panel-heading">
          <h2 class="panel-title">Forward to a friend</h2>
        </div>
        <div class="panel-body">
          <div class="row">
            <div id="recepients" class="col-lg-12 col-md-12">
              <div class="input-group">
                <input type="email" name="recepient" class="form-control" placeholder="Email address">
                <span class="input-group-btn">
                  <button class="btn btn-default remove-recepient" type="button">x</button>
                </span>
              </div><!-- /input-group -->
              <button id="add-recepient" class="btn btn-default">Add a recepient</button>
              <button id="send" class="btn btn-default">Send</button>
            </div><!-- /.col-lg-6 -->
          </div><!-- /.row -->
        </div>
      </div>
    </div>
    <div id='calendar' class="col-lg-8 col-md-8 col-xs-12"></div>
  </section>

  <script src='${pageContext.request.contextPath}/assets/app.js'></script>
</body>
</html>
