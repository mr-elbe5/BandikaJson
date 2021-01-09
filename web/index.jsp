<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.application.Application" %>
<%@ page import="de.elbe5.request.RequestKeys" %>
<<%@ page contentType="text/html;charset=UTF-8" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    ContentData contentData = Application.getContent().getContentRoot();
    String url = request.getRequestURL().toString();
    String uri = request.getRequestURI();
    int idx = url.lastIndexOf(uri);
    url = url.substring(0, idx);
    if (contentData!=null)
        url += contentData.getUrl();
    else if (rdata.isLoggedIn())
        url +="/ctrl/admin/openSystemAdministration";
    else
        url +="/blank.jsp";
%>
<html>
<head>
    <title>Title</title>
    <meta http-equiv="Refresh" content="0; url='<%=url%>'" />
</head>
<body>
</body>
</html>
