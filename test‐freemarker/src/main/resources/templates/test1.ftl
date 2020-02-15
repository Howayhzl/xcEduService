<!DOCTYPE html>
<html>
<head>
        <meta charset="utf‐8">
        <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<table>
    <tr>
        <td>序号</td>    
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <td>${stu_index+1}</td>    
        <td>${stu.name}</td>
        <td>${stu.age}</td>
        <td>${stu.money}</td>
    </#list>
    <br>
</table>
</body>
</html>