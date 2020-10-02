package org.edu.service;

import java.util.List;

import javax.inject.Inject;

import org.edu.dao.IF_BoardDAO;
import org.edu.vo.BoardVO;
import org.edu.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardServiceImpl implements IF_BoardService{

 @Inject
 private IF_BoardDAO boardDAO;
	
	@Override
	public void insertBoard(BoardVO boardVO) throws Exception {
		boardDAO.insertBoard(boardVO);
		
	}

	@Override
	public List<BoardVO> selectBoard(PageVO pageVO) throws Exception {
		return boardDAO.selectBoard(pageVO);
		}

	@Override
	public void updateBoard(BoardVO boardVO) throws Exception {
		boardDAO.updateBoard(boardVO);
		
	}

	@Override
	public void deleteBoard(Integer bno) throws Exception {
		boardDAO.deleteBoard(bno);
		
	}

	@Override
	public BoardVO viewBoard(Integer bno) throws Exception {
		return boardDAO.viewBoard(bno);
	}

	@Override
	public int countBno(PageVO pageVO) throws Exception {
		return boardDAO.countBno(pageVO);
	}
   
  
}