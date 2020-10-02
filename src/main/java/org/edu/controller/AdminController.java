package org.edu.controller;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.edu.service.IF_BoardService;
import org.edu.service.IF_MemberService;
import org.edu.vo.BoardVO;
import org.edu.vo.MemberVO;
import org.edu.vo.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
		   @RequestMapping(value = "/admin_MemberWrite", method = RequestMethod.GET)
		   public String memberWrite(Locale locale, Model model) throws Exception {
		      
		      return "admin/admin_MemberWrite";
		   }
		   @RequestMapping(value = "/admin_MemberWrite", method = RequestMethod.POST)
		   public String memberWrite( MemberVO memberVO, Locale locale, RedirectAttributes rdat) throws Exception {
		      String new_pw = memberVO.getUser_pw(); //1234
		      if(new_pw !="") {
		       //스프링 시큐리티 4.x BCryptPasswordEncoder 암호화 사용
		           BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder(10);
		           String bcryptPassword = bcryptPasswordEncoder.encode(new_pw);//예, 1234 -> 암호화 처리됨
		           memberVO.setUser_pw(bcryptPassword); //DB에 들어가기전 값 set 시킴   
		    }     memberService.insertMember(memberVO);
		      rdat.addFlashAttribute("msg", "입력");
		      return "redirect:/admin";
		   }
		/**
		 * 관리자 게시판리스트입니다..
		 * @throws Exception 
		 */
		@RequestMapping(value = "/admin_Board", method = RequestMethod.GET)
		public String home(@ModelAttribute("pageVO") PageVO pageVO , Locale locale, Model model, HttpServletRequest request) throws Exception {
			//PageVO pageVO = new PageVO();//매개변수로 받기전에 테스트용
		     if(pageVO.getPage() == null) { //초기 page변수값 지정
		        pageVO.setPage(1);
		     } 
		      pageVO.setPerPageNum(10); //1페이지당 보여줄 게시물 수 강제지정 
		      pageVO.setTotalCount(boardService.countBno(pageVO));//강제로 입력한 값을 쿼리로 대체OK.
		      List<BoardVO> list = boardService.selectBoard(pageVO);
		      //모델클래스로 jsp화면으로 boardService에서 셀렉트한 list값을 boardList변수명으로 보낸다.
		      //model { list -> boardList -> jsp }
		      model.addAttribute("boardList", list);
		      model.addAttribute("pageVO" , pageVO);
			return "admin/admin_Board";
		}
		/**
		 * 관리자 멤버리스트입니다..
		 * @throws Exception 
		 */
		@RequestMapping(value = "/admin_Member", method = RequestMethod.GET)
		public String member_list(@ModelAttribute("pageVO") PageVO pageVO , Locale locale, Model model, HttpServletRequest request) throws Exception {
			if(pageVO.getPage()==null) {
		       pageVO.setPage(1);     
		      }
		      pageVO.setPerPageNum(10);
		      pageVO.setTotalCount(memberService.countUserId(pageVO));
		      List<MemberVO> list = memberService.selectMember(pageVO);
		      //모델클래스로 jsp화면으로 memberService에서 셀렉트한 list값을 memberList변수명으로 보낸다.
		      //model { list -> memberList -> jsp }
		      model.addAttribute("memberList", list);
		      model.addAttribute("pageVO", pageVO);
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
