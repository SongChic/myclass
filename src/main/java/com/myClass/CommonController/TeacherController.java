package com.myClass.CommonController;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.myClass.Model.Classes;
import com.myClass.Model.Member;
import com.myClass.Model.TestPaper;
import com.myClass.Service.ClassesService;
import com.myClass.Service.TeacherService;
import com.myClass.Service.TestPaperService;
import com.myClass.Service.UserService;

@Controller
public class TeacherController {
	@Autowired
	UserService userService;
	
	@Autowired
	TeacherService teacherService;
	
	@Autowired
	ClassesService classesService;
	
	@Autowired
	TestPaperService testPaperService;
	
	@RequestMapping(value="/teacher/classes/manageClasses")
	public ModelAndView manageClasses (
			Principal principal,
			@RequestParam(value="type", defaultValue="1") int type,
			HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView("/teacher/manage-classes");
		Member member = userService.get(principal.getName());
		
		List<Map<String, Object>> classes = classesService.getList(member.getId());
		
		
//		classes.add(classes.size() + 1, classesService.number(member.getId()));
		
		mav.addObject("classes", classes);
		mav.addObject("member", member);
		return mav;
	}
	
//	@RequestMapping(value="/teacher/classes/classRoom")
//	public ModelAndView ClassRoom (
//			Principal principal,
//			@RequestParam( value="id", defaultValue="0", required=false ) int id,
//			HttpServletRequest request, HttpServletResponse response) {
//		
//		ModelAndView mav = new ModelAndView("/common/class-room");
//		Member member = userService.get(principal.getName());
//		
//		Classes classes = new Classes();
//		
//		List<Map<String, Object>> classStudent = new ArrayList<Map<String,Object>>();
//		
//		if ( id > 0 ) {
//			classes = classesService.get(id);
//			classStudent = classesService.getStudentList(id);
//		}
//		
//		
//		mav.addObject("classes", classes);
//		mav.addObject("classStudent", classStudent);
//		mav.addObject("teacher", member);
//		return mav;
//	}
	
	@RequestMapping(value="/teacher/classes/setClasses")
	public ModelAndView setClasses(
			Principal principal,
			@RequestParam( value="id", defaultValue="0", required=false ) int id,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("/teacher/set-classes");
		
		Member member = userService.get(principal.getName());
		
		Classes classes = new Classes();
		
		if ( id > 0 ) {
			classes = classesService.get(id);
		}
		
		mav.addObject("classes", classes);
		mav.addObject("member", member);
		
		return mav;
	}
	
	@RequestMapping(value="/teacher/classes/manageClassMember")
	public ModelAndView manageClassMember(
			@RequestParam( value="classId", required=true ) int classId,
			HttpServletRequest request, HttpServletResponse response
			) {
		
		int teacherId = teacherService.getTeacherId(classId);
		ModelAndView mav = new ModelAndView("/teacher/manage-class-member");
		mav.addObject("student", teacherService.myStudentList(teacherId));
		return mav;
	}
	
	@RequestMapping( value="/teacher/member/findStudent" )
	public ModelAndView findStudent (
			Principal principal, 
			@RequestParam( value="classId", required=true ) int classId,
			HttpServletRequest request, HttpServletResponse response
			) {
		ModelAndView mav = new ModelAndView("/teacher/find-student");
		Member teacher = userService.get(principal.getName());
		
		List<Map<String, Object>> studentItem = teacherService.findStudent( teacher.getId() );
		
		for ( Map<String, Object> item : studentItem ) {
			System.out.println( item.get("id") );
		}
		
		mav.addObject("student", studentItem);
		return mav;
	}
	
	@RequestMapping(value="/teacher/members/manageMembers")
	public ModelAndView manageMember (
//			@RequestParam(value="teacherId") int teacherId,
			Principal principal,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("/teacher/member/manage-member");
		
		Member teacher = userService.get(principal.getName());
		
		List<Map<String, Object>> student = teacherService.manageMember(teacher.getId());
		List<Map<String, Object>> students = new ArrayList<Map<String,Object>>();
		
		for ( int i = 0; i < student.size(); i++ ) {
			Map<String, Object> list = new HashMap<String, Object>();
			list.put("profile", student.get(i).get("profile"));
			list.put("student_state", student.get(i).get("student_state"));
			list.put("state", student.get(i).get("state"));
			list.put("id", student.get(i).get("id"));
			list.put("name", student.get(i).get("name"));
			list.put("school", student.get(i).get("school"));
			
			String classId = String.valueOf(student.get(i).get("teacher_class_id"));
			
			if ( classId.length() <= 0 ) {
				classId = "0";
			}
			
			List<Map<String, Object>> className = teacherService.getClassName(classId);
			String classNames = "";
			for (int j = 0; j < className.size(); j++) {
				classNames += className.get(j).get("name") + ", ";
			}
			
			if ( classNames.length() > 0 ) {
				classNames = classNames.substring(0, classNames.length() - 2 );
			}
			
			list.put("className", classNames);
			
			students.add(list);
		}
		
		
		mav.addObject("students", students);
		return mav;
	}
	
	@RequestMapping(value="/teacher/exam/setTestPaper")
	public ModelAndView setTestPaper(
			@RequestParam(required=false, defaultValue="0") int id,
			HttpServletRequest request, HttpServletResponse response){
		
		TestPaper testPaper = null;
		JSONArray list = null;
		
		if ( id > 0 ) {
			testPaper = testPaperService.getTestPeper(id);
			
			List<Map<String, Object>> questionList = testPaperService.viewQuestion(id);
			list = new JSONArray();
			JSONArray question = new JSONArray();
			JSONArray questionArr = new JSONArray();
			JSONArray questionBody = new JSONArray();
			
			JSONObject questionInfo = new JSONObject();
			
			int num = 1;
			int i = 1;
			for ( Map<String, Object> item : questionList) {
				JSONObject questionItem = new JSONObject();
				if ( num == (Integer) item.get("question_num") ) {
					if ( !questionInfo.containsKey("title") ) {
						questionInfo.put("title", item.get("title"));
						questionInfo.put("answer", item.get("answer"));
					}
					
					questionItem.put("question", item.get("question"));
					questionBody.add(questionItem);
				}
				
				if ( num != (Integer) item.get("question_num") || questionList.size() == i ) {
					questionArr = new JSONArray();
					questionInfo.put("question", questionBody);
					questionArr.add(questionInfo);
					
					list.add(questionArr);
					question = new JSONArray();
					
					questionItem = new JSONObject();
					questionInfo = new JSONObject();
					questionBody = new JSONArray();
					num ++;
					
					if ( num == (Integer) item.get("question_num") ) {
						if ( !questionInfo.containsKey("title") ) {
							questionInfo.put("title", item.get("title"));
							questionInfo.put("answer", item.get("answer"));
						}
						
						questionItem.put("question", item.get("question"));
						questionBody.add(questionItem);
					}
					
				}
				i++;
				
			}
		}
		
		ModelAndView mav = new ModelAndView("/teacher/exam/set-test-paper");
		
		mav.addObject("id", id);
		mav.addObject("list", list);
		mav.addObject("testPaper", testPaper);
		return mav;
	}
	@RequestMapping(value="/teacher/exam/viewTestPaper")
	public ModelAndView viewTestPaper(
			Principal principal,
			HttpServletRequest request, HttpServletResponse response){
		
		Member teacher = userService.get(principal.getName());
		
		List<Map<String, Object>> testPaper = testPaperService.viewTestPaper( teacher.getId() );
		
		ModelAndView mav = new ModelAndView("/teacher/exam/view-test-paper");
		
		mav.addObject("testPaper", testPaper);
		return mav;
	}
	
	@RequestMapping(value="/teacher/exam/viewQuestion")
	public ModelAndView viewQuestion (
			@RequestParam(value="id") int id,
			HttpServletRequest request, HttpServletResponse response
			) {
		
		List<Map<String, Object>> question = testPaperService.viewQuestion(id);
		TestPaper testPaper = testPaperService.getTestPeper(id);
		
		ArrayList<Object> testPaperItem = new ArrayList<Object>();
		
		int num = 1;
		int i = 0;
		List<Map<String, Object>> questionArr = new ArrayList<Map<String,Object>>();
		Map<String, Object> list = new HashMap<String, Object>();
		for ( Map<String, Object> item : question ) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			if ( !list.containsKey("title") ) {
				list.put("title", item.get("title"));
				list.put("answer", item.get("answer"));
			}
			
			if ( num == (Integer) item.get("question_num") ) {
				map.put("question", item.get("question"));
				questionArr.add(map);
				questionArr.size();
			}
			if ( num != (Integer) item.get("question_num") || question.size() - 1 == i ) {
				list.put("question", questionArr);
				testPaperItem.add(list);
				questionArr = new ArrayList<Map<String,Object>>();
				list = new HashMap<String, Object>();
				
				num++;
				
				if ( num == (Integer) item.get("question_num") ) {
					map.put("question", item.get("question"));
					questionArr.add(map);
					questionArr.size();
				}
				
			}
			i++;
			
		}
		
		
		ModelAndView mav = new ModelAndView("/teacher/exam/view-question");
		
		mav.addObject("testPaperItem", testPaperItem);
		mav.addObject("testPaper", testPaper);
		mav.addObject("question", question);
		return mav;
	}
}
