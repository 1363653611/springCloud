<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>服务预警通知</title>
</head>
<body>

<p>服务状态：${status}</p>
<p>服务域名：${ipAddress}</p>
<p>服务名：${instanceName}</p>
<p>节点ID：${instanceId}</p>
<#if message??>
    异常原因：${message}
</#if>
<#if startup??>
    启动时间：${startup}
</#if>
<#if details??>
    <span style="font-weight:bold;">服务详细信息：</span><br>
    <span>${details}</span>
</#if>
</body>
</html>