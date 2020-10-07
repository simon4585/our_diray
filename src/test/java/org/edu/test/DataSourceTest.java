package org.edu.test;

import java.sql.Connection;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.edu.service.IF_BoardService;
import org.edu.service.IF_MemberService;
import org.edu.vo.BoardVO;
import org.edu.vo.MemberVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
@WebAppConfiguration
public class DataSourceTest {

   @Inject
   private DataSource ds;
   
   @Inject
   private IF_MemberService memberService;
   
   @Inject
   private IF_BoardService boardService;
   
   
   @Test
   public void testConnection()throws Exception{
      Connection con = ds.getConnection();
      System.out.println("데이터베이스 커넥션 성공 : " + con);
   }
   @Test
   public void testDeleteMember() throws Exception {
      memberService.deleteMember("user02");
   }
   
   @Test
   public void testUpdateMember() throws Exception {
	   MemberVO memberVO = new MemberVO();
	      memberVO.setUser_id("simon");
	      memberVO.setUser_pw("1234");
	      memberVO.setUser_name("시몬3");
	      memberVO.setEmail("simon3@test.com");
	      memberVO.setPoint(100);
	      memberVO.setEnabled(true);
	      memberVO.setLevels("ROLE_ADMIN");
	      memberService.insertMember(memberVO);
	   }
       
   @Test
   public void testInsertMember() throws Exception {
      MemberVO memberVO = new MemberVO();
      memberVO.setUser_id("admin");
      memberVO.setUser_pw("1234");
      memberVO.setUser_name("시몬");
      memberVO.setEmail("simon@test.com");
      memberVO.setPoint(100);
      memberVO.setEnabled(true);
      memberVO.setLevels("ROLE_ADMIN");
      memberService.insertMember(memberVO);
   }
   @Test
   public void testInsertBoard() throws Exception {
      BoardVO boardVO = new BoardVO();
      boardVO.setBno(5);
      boardVO.setTitle("위대한 여정");
      boardVO.setContent("발걸음");
      boardVO.setWriter("우정호");
      boardVO.setView_count(0);
      boardVO.setReply_count(0);
      boardService.insertBoard(boardVO);
   }
   @Test
   public void recommendBoard() throws Exception{
	  
	  boardService.recommendBoard(57);
   }
   
   }
