<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.myClass.Common.MyclassCommon"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set value="${pageContext.request.contextPath }" var="ctx"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@include file="/WEB-INF/views/include/meta.jsp" %>
<%
	long date = new Date().getTime();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>

<link rel="stylesheet" type="text/css" href="${ctx }/css/fullcalendar-setting.css">
<link rel="stylesheet" type="text/css" href="${ctx }/css/main.css">

<c:set value="<%= date %>" var="date" />
<c:set value="${member.mainColor }" var="mainColor"/>
<style type="text/css">
	#header {
		background-color : <%= MyclassCommon.mainColor[(Integer)pageContext.getAttribute("mainColor")] %>
	}
	.drop-item {
		background-color : <%= MyclassCommon.mainColor[(Integer)pageContext.getAttribute("mainColor")] %>
	}
</style>


</head>
<body>
	<div id="wrap">
		<%@include file="/WEB-INF/views/include/header.jsp" %>
		<div id="container">
			<div class="row">
				<div class="calendar-wrap">
					<div class="calendar-btn calendar-show mouse-pointer"><i class="fa fa-chevron-down"></i></div>
					
					<div class="calendar">
					<!-- calendar -->
					</div>
				</div>
				
				<div class="content row">
					<div class="col-md-8 col-sm-7">
						<h4>우리반</h4>
						
						<div class="classes-wrap row">
						
							<c:forEach items="${classes }" var="classes">
							
								<fmt:formatDate value="${classes.start_date }" pattern='yyyy-MM-dd HH:mm:ss' var="format_date" />
								<c:set value="${format_date }" var="start_date" />
								<%
									Date dateParse = sdf.parse((String) pageContext.getAttribute("start_date"));
									long parseDate = dateParse.getTime();
								%>
								<c:set value="<%= parseDate %>" var="parseDate"/>
								<c:if test="${classes.finished == 0 and parseDate < date  }">
										
										<div class="classes-thumbnail-wrap item mouse-pointer" data-item="${classes.id }">
											<div class="classes-thumbnail box-layout">
											
											<c:choose>
												<c:when test="${not empty classes.picture and classes.classes_view_type == 2 }">
													<div class="lazy classes-back-img" data-original="${ctx }/img/data/${classes.picture }" style="background-image:url('${ctx }/img/no_class_img.jpg')">
														<p>${classes.name }</p>
													</div>
												</c:when>
												<c:when test="${classes.classes_view_type == 1 }">
													<c:set value="${classes.color }" var="color"/>
													<div class="classes-back-color" style="background:<%= MyclassCommon.classColor[(Integer) pageContext.getAttribute("color")] %>">
														${classes.name }
													</div>
												</c:when>
											</c:choose>
											
												<div class="classes-info">
													<ul>
														<li>기간 : <fmt:formatDate value="${classes.start_date }" pattern="yyyy-MM-dd"/> ~ <fmt:formatDate value="${classes.end_date }" pattern="yyyy-MM-dd"/></li>
														<li>시간 : <fmt:formatDate value="${classes.start_date }" pattern="HH:mm"/> ~ <fmt:formatDate value="${classes.end_date }" pattern="HH:mm"/></li>
														<li class="divider top-margin"></li>
														<li> ${classes.teacher_name }</li>
													</ul>
												</div>
											</div>
										</div>
									
									</c:if>
									
							</c:forEach>
							
							<c:if test="${fn:length(classes) == 0 }">
								<p>현재 진행중인 수업이 없습니다.</p>
							</c:if>
							
							
						</div>
					</div>
					
					<div class="col-md-4 col-sm-5">
						<h4>숙제 / 시험</h4>
						
						<!-- dummy model (s) -->
						<div class="box-layout">
							<div class="box-wrap">
								<h4>시험지 제목</h4>
								
								<ul>
									<li>기간 : 2016.04.06 ~ 2016.04.08</li>
									<li>제한시간 : 160분</li>
									<li>과목 : 수학</li>
									<li>교재 : 수학</li>
								</ul>
							</div>
							<p class="bg-primary">
								출제 선생님 : 송석우
							</p>
						</div>
						
						<!-- dummy model (e) -->
					</div>
				</div>
			</div>
		</div>
		
		<div id="footer">
		</div>
	</div>

<script type="text/javascript">
var ctx = "${ctx }";
</script>

<%@include file="/WEB-INF/views/include/common-lib.jsp" %>
<script type="text/javascript" src="${ctx }/js/fullcalendar-setting.js" ></script>	
<script type="text/javascript" src="${ctx }/js/library/freewall.js" ></script>	
<script type="text/javascript">
	$(".calendar-btn").on("click", function(event){
		var state = $(this).attr("class");
		
		if (state.indexOf("calendar-show") > -1) {
			$(".calendar").slideDown();
			$(this).removeClass("calendar-show")
				   .addClass("calendar-hide")
				   .find("i")
				   .removeClass("fa-chevron-down")
				   .addClass("fa-chevron-up");
		} else {
			$(".calendar").slideUp();
			$(this).removeClass("calendar-hide")
				   .addClass("calendar-show")
				   .find("i")
				   .removeClass("fa-chevron-up")
				   .addClass("fa-chevron-down");
		}
		
	});
	
	var wall = new Freewall(".classes-wrap");
	wall.reset({
		selector : '.item',
		animate : true,
		cellW : 170,
		cellH : 190,
		onResize : function() {
			wall.refresh();
		}
	});
	wall.fitWidth();
	
	$(".classes-thumbnail-wrap").on("click", function ( event ) {
		var userType = "${member.userType }",
			id = $(this).attr("data-item");
		if ( userType === "1" ) {
			location.href="${ctx }/teacher/classes/classRoom?id=" + id;
		} else if ( userType === "3" ) {
			
		}
	});
	
</script>
</body>
</html>