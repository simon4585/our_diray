package org.edu.controller;

import java.util.Locale;

import javax.inject.Inject;

import org.edu.service.IF_BoardService;
import org.edu.service.IF_MemberService;
import org.edu.vo.MemberVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/admin")
public class AdminController {
	
		
		private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
		
		@Inject
		private IF_MemberService memberService;
		
		@Inject
		private IF_BoardService boardService;
		
		
		/**
		    * 회원관리 > 등록 입니다.
		    * @throws Exception 
		    */
		   @RequestMapping(value = "/member/write", method = RequestMethod.GET)
		   public String memberWrite(Locale locale, Model model) throws Exception {
		      
		      return "admin/member_write";
		   }
		   @RequestMapping(value = "/member/write", method = RequestMethod.POST)
		   public String memberWrite( MemberVO memberVO, Locale locale, RedirectAttributes rdat) throws Exception {
		      String new_pw = memberVO.getUser_pw(); //1234
		      if(new_pw !="") {
		       //스프링 시큐리티 4.x BCryptPasswordEncoder 암호화 사용
		           BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder(10);
		           String bcryptPassword = bcryptPasswordEncoder.encode(new_pw);//예, 1234 -> 암호화 처리됨
		           memberVO.setUser_pw(bcryptPassword); //DB에 들어가기전 값 set 시킴   
		    }     memberService.insertMember(memberVO);
		      rdat.addFlashAttribute("msg", "입력");
		      return "redirect:/admin/member/list";
		   }
		/**
		 * 관리자 게시판리스트입니다..
		 * @throws Exception 
		 */
		@RequestMapping(value = "/admin_Board", method = RequestMethod.GET)
		public String home(Locale locale, Model model) throws Exception {
			
			return "admin/admin_Board";
		}
		/**
		 * 관리자 멤버리스트입니다..
		 * @throws Exception 
		 */
		@RequestMapping(value = "/admin_Member", method = RequestMethod.GET)
		public String member_list(Locale locale, Model model) throws Exception {
			
			return "admin/admin_Member";
		}
		/**
		 * 관리자 홈입니다..
		 * @throws Exception 
		 */
		@RequestMapping(value = "", method = RequestMethod.GET)
		public String admin_home(Locale locale, Model model) throws Exception {
			
			return "admin/admin_home";
		}
		
}
