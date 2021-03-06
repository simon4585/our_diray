package org.edu.controller;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.edu.service.IF_BoardService;
import org.edu.service.IF_MemberService;
import org.edu.vo.BoardVO;
import org.edu.vo.MemberVO;
import org.edu.vo.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Inject
	private IF_MemberService memberService;
	
	@Inject
	private IF_BoardService boardService;
	
	/*
	 * 게시글 추천
	 */
	@RequestMapping(value = "recommend_Board" , method=RequestMethod.GET)
	public String recommendBoard(@ModelAttribute("pageVO") PageVO pageVO, @RequestParam("bno") Integer bno,Locale locale, Model model , BoardVO boardVO) throws Exception {
		
		boardService.recommendBoard(bno);
		return "redirect:trip_view?bno="+boardVO.getBno()+"&page=" + pageVO.getPage();
	}
	
	/*
	 * 게시판 글 삭제
	 */
	@RequestMapping(value = "board_delete" , method=RequestMethod.POST)
	public String boardDelete(@RequestParam("bno") Integer bno, Locale locale, RedirectAttributes rdat) throws Exception {
		boardService.deleteBoard(bno);
		rdat.addFlashAttribute("msg", "삭제");
		return "redirect:trip_boardlist";
	}
	
	/**
	 * 국내여행지 메인폼.
	 * @throws Exception 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws Exception {
		
		return "main";
	}
	
	/*
	 * 회원가입 폼입니다.
	 */
	@RequestMapping(value="/signUp", method=RequestMethod.GET)
	public String signUp(Locale locale) throws Exception{
		
	   return "signUp";	
	}
	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	   public String signUp(MemberVO memberVO, Locale locale, RedirectAttributes rdat) throws Exception {
	      String new_pw = memberVO.getUser_pw(); //1234
	      if(new_pw !="") {
	       //스프링 시큐리티 4.x BCryptPasswordEncoder 암호화 사용
	           BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder(10);
	           String bcryptPassword = bcryptPasswordEncoder.encode(new_pw);//예, 1234 -> 암호화 처리됨
	           memberVO.setUser_pw(bcryptPassword); //DB에 들어가기전 값 set 시킴   
	    }     memberService.insertMember(memberVO);
	      rdat.addFlashAttribute("msg", "회원가입");
	      return "redirect:/";
	   }
	
	
	/**
	 * 국내여행지게시판리스트 입니다.
	 * @throws Exception 
	 */
	   @RequestMapping(value = "trip_boardlist", method = RequestMethod.GET)
	   public String trip_boardlist(@ModelAttribute("pageVO") PageVO pageVO , Locale locale, Model model, HttpServletRequest request) throws Exception {
		 //초기 메뉴를 클릭시 /admin/board/list?searchBoard=notice 데이터전송
			HttpSession session = request.getSession();
			if(pageVO.getSearchBoard() != null) {
				//최초 세션 만들어짐
				session.setAttribute("session_bod_type", pageVO.getSearchBoard());
			} else {
				//일반링크 클릭시 /admin/board/view?page=2...
				//만들어진 세션 사용(아래)
				pageVO.setSearchBoard((String) session.getAttribute("session_bod_type"));
			}
			//PageVO pageVO = new PageVO();//매개변수로 받기전 테스트용
			if(pageVO.getPage() == null) {
				pageVO.setPage(1);//초기 page변수값 지정
			}
			pageVO.setPerPageNum(10);//1페이지당 보여줄 게시물 수 강제지정
			pageVO.setTotalCount(boardService.countBno(pageVO));//강제로 입력한 값을 쿼리로 대체OK.
			List<BoardVO> list = boardService.selectBoard(pageVO);
			//모델클래스로 jsp화면으로 boardService에서 셀렉트한 list값을 boardList변수명으로 보낸다.
			//model { list -> boardList -> jsp }
			model.addAttribute("boardList", list);
			model.addAttribute("pageVO", pageVO);
		      	
	      return "trip_boardlist";
}
	   
	   /**
		 * 국내여행지 게시글 상세히보기 입니다.
	 * @throws Exception 
		 */
		   @RequestMapping(value = "trip_view", method = RequestMethod.GET)
		   public String trip_view(@ModelAttribute("pageVO") PageVO pageVO, @RequestParam("bno") Integer bno,Locale locale, Model model) throws Exception {
			   BoardVO boardVO = boardService.viewBoard(bno);
			   model.addAttribute("boardVO", boardVO);
			   model.addAttribute("pageVO", pageVO);
		       return "trip_view";
	}
		   /**
			 * 국내여행지 게시글쓰기 입니다.
			 */
			   @RequestMapping(value = "trip_boardwrite", method = RequestMethod.GET)
			   public String trip_boardwrite(Locale locale) {
			      
			      return "trip_boardwrite";
		}
		   /**
			 * 국내여행지 게시글쓰기 입니다.
		 * @throws Exception 
			 */
			   @RequestMapping(value = "trip_boardwrite", method = RequestMethod.POST)
			   public String trip_boardwrite(BoardVO boardVO,Locale locale, Model model) throws Exception {
			       boardService.insertBoard(boardVO);
				  return "redirect:trip_boardlist";
			}
		
		/**
		 *  로그인 페이지 파일 입니다.
		 */
		   @RequestMapping(value = "/login", method = RequestMethod.GET)
		   public String login(Locale locale, Model model) {
		      
		      return "login";
	}
		   /**
		    * 스프링 시큐리티 secutiry-context.xml설정한 로그인 처리 결과 화면
		    * @param locale
		    * @param request
		    * @param rdat
		    * @return
		    * @throws Exception 
		    */
		   @RequestMapping(value = "/login_success", method = RequestMethod.GET)
		   public String login_success(Locale locale,HttpServletRequest request, RedirectAttributes rdat) throws Exception {
		      logger.info("Welcome login_success! The client locale is {}.", locale);
		      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		      String userid = "";//아이디
		      String levels = "";//ROLE_ANONYMOUS
		      Boolean enabled = false;
		      Integer point = 0;
		      Object principal = authentication.getPrincipal();
		      if (principal instanceof UserDetails) {
		         //인증이 처리되는 로직(아이디,암호를 스프링시큐리티 던져주고 인증은 스프링에서 알아서 해줌.)
		         enabled = ((UserDetails)principal).isEnabled();
		      }
		      HttpSession session = request.getSession();//세션을 초기화 시켜줌.
		      if (enabled) { //인증처리가 완료된 사용자의 권한체크(아래)
		         Collection<? extends GrantedAuthority>  authorities = authentication.getAuthorities();
		         if(authorities.stream().filter(o -> o.getAuthority().equals("ROLE_ANONYMOUS")).findAny().isPresent())
		         {levels = "ROLE_ANONYMOUS";}
		         if(authorities.stream().filter(o -> o.getAuthority().equals("ROLE_USER,")).findAny().isPresent())
		         {levels = "ROLE_USER,";}
		         if(authorities.stream().filter(o -> o.getAuthority().equals("ROLE_ADMIN")).findAny().isPresent())
		         {levels = "ROLE_ADMIN";}
		         userid =((UserDetails)principal).getUsername();
		         //userid =((UserDetails)principal).getpoint();
		         //로그인 세션 저장
		         session.setAttribute("session_enabled", enabled);//인증확인
		         session.setAttribute("session_userid", userid);//사용자아이디
		         session.setAttribute("session_levels", levels);//사용자권한
		         //=========== 상단은 스프링시큐리티에서 기본제공하는 세션 변수처리
		         //=========== 하단은 우리가 추가한는 세션 변수처리
		         //회원이름 구하기 추가
		         MemberVO memberVO = memberService.viewMember(userid);
		         
		         session.setAttribute("session_point", memberVO.getPoint()); //포인트
		         session.setAttribute("session_username", memberVO.getUser_name());//사용자명
		           }
		      System.out.println("------------------포인트 값 출력" + point);
		      System.out.println("------------------세션 값 출력: " + enabled);
		      System.out.println("------------------ 사용자 아이디 출력: " + userid);
		      System.out.println("------------------사용자 권한출력: " + levels);
		      rdat.addFlashAttribute("msg", "로그인");//result 데이터를 숨겨서 전송
		      return "redirect:/";//새로고침 자동 등록 방지를 위해서 아래처럼 처리
		   }
		   
		  
		   
		   
	
}
