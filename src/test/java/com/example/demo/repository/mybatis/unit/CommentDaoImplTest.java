package com.example.demo.repository.mybatis.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.dto.board.BoardFormDto;
import com.example.demo.dto.comment.CommentDto;
import com.example.demo.repository.mybatis.board.BoardDaoImpl;
import com.example.demo.repository.mybatis.comment.CommentDaoImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class CommentDaoImplTest {

    @Autowired
    private CommentDaoImpl target;

    @Autowired
    private BoardDaoImpl boardDao;

    private BoardFormDto boardFormDto = new BoardFormDto();

    private List<CommentDto> fixture = new ArrayList<>();

    @BeforeEach
    void setUp() {
        assertNotNull(target);
        assertNotNull(boardDao);

        fixture.clear();
        target.deleteAll();

        createBoardFormDto();
        assertTrue(1 == boardDao.insert(boardFormDto));
    }


    /**
     *
     * 0. 카운팅
     * - 0. 예외 - x
     * - 1. 실패 - x
     * - 2. 성공
     *  - 2-0. 여러건 등록하고 전체 카운팅
     *
     * 1. 등록
     * - 0. 예외
     *  - 0-0. 게시글 번호가 없는 경우 -> DataIntegrityViolationException
     *  - 0-1. 작성자가 없는 경우 -> DataIntegrityViolationException
     *  - 0-2. 내용이 없는 경우 -> DataIntegrityViolationException
     *  - 0-3. 내용이 500자 초과인 경우 -> DataIntegrityViolationException
     *
     * - 1. 실패 - x
     *
     * - 2. 성공
     *  - 2-0. 특정 게시글에 댓글 여러개 등록하기
     *
     * 2. 특정 게시글과 과련된 댓글 조회
     * - 0. 예외 - x
     *
     * - 1. 실패
     *  - 1-0. 게시글 번호가 없는 경우 -> 조회 x
     *
     * - 2. 성공
     *  - 2-0. 특정 게시글에 댓글 여러개 등록하고 전체 조회해보기
     *
     * 3. 특정 댓글 조회
     * - 0. 예외 - x
     *
     * - 1. 실패
     *  - 1-0. 댓글 번호가 없는 경우 -> 조회 x
     *
     * - 2. 성공
     *  - 2-0. 특정 게시글에 댓글 여러개 등록하고 각각 댓글 조회해보기
     *  - 2-1. 특정 게시글에 댓글 여러개 등록하고 랜덤으로 댓글 조회해보기
     *
     * 4. 전체 조회
     * - 0. 예외 - x
     * - 1. 실패 - x
     * - 2. 성공
     * - 2-0. 특정 게시글에 댓글 여러개 등록하고 전체 조회해보기
     *
     * 5. 댓글 수정
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     * 6. 좋아요 증가
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     * 7. 싫어요 증가
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     * 8. 특정 게시글과 관련된 댓글 전체 삭제
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     * 9. 특정 댓글 삭제
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     * 10. 전체 삭제
     * - 0. 예외
     * - 1. 실패
     * - 2. 성공
     *
     */

    @DisplayName("2-2-0. 여러건 등록하고 전체 카운팅")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test(int cnt) {
        /**
         * cnt 개수 만큼 픽스처에 dto를 생성함
         * 생성된 dto를 일일이 insert함. 총 cnt 개수 만큼 insert됨
         * count()를 호출했을 때 cnt가 리턴되는지 확인
         */
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }
        assertTrue(cnt == target.count());
    }

    @DisplayName("1-0-0. 게시글 번호가 없는 경우 -> DataIntegrityViolationException")
    @Test
    public void test1() {
        var commentDto = createCommentDto(0, 0);
        assertThrows(DataIntegrityViolationException.class,
                () -> target.insert(commentDto)
        );
    }

    @DisplayName("1-0-1. 작성자가 없는 경우 -> DataIntegrityViolationException")
    @Test
    public void test2() {
        var commentDto = createCommentDto(boardFormDto.getBno(), 0);
        commentDto.setWriter(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> target.insert(commentDto)
        );
    }

    @DisplayName("1-0-2. 내용이 없는 경우 -> DataIntegrityViolationException")
    @Test
    public void test3() {
        var commentDto = createCommentDto(boardFormDto.getBno(), 0);
        commentDto.setContent(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> target.insert(commentDto)
        );
    }


    @DisplayName("1-0-3. 내용이 500자 초과인 경우 -> DataIntegrityViolationException")
    @Test
    public void test5() {
        var commentDto = createCommentDto(boardFormDto.getBno(), 0);
        commentDto.setContent("a".repeat(501));
        assertThrows(DataIntegrityViolationException.class,
                () -> target.insert(commentDto)
        );
    }

    @DisplayName("2-0-0. 게시글에 댓글 여러개 등록하기")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test6(int cnt) {
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }

        List<CommentDto> foundComments = target.selectByBno(boardFormDto.getBno());
        assertTrue(cnt == foundComments.size());

        sort(fixture, foundComments);
        for (int i=0; i<cnt; i++) {
            assertTrue(isSameCommentDto(fixture.get(i), foundComments.get(i)));
        }
    }

    @DisplayName("2-1-0. 게시글 번호가 없는 경우 -> 조회 x")
    @Test
    public void test7() {
        List<CommentDto> foundComments = target.selectByBno(0);
        assertTrue(foundComments.isEmpty());
    }

    @DisplayName("2-2-0. 특정 게시글에 댓글 여러개 등록하고 전체 조회해보기")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test8(int cnt) {
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }

        List<CommentDto> foundComments = target.selectByBno(boardFormDto.getBno());
        assertTrue(cnt == foundComments.size());

        sort(fixture, foundComments);
        for (int i=0; i<cnt; i++) {
            assertTrue(isSameCommentDto(fixture.get(i), foundComments.get(i)));
        }
    }

    @DisplayName("3-1-0. 댓글 번호가 없는 경우 -> 조회 x")
    @Test
    public void test9() {
        CommentDto foundComment = target.selectByCno(0);
        assertNull(foundComment);
    }

    @DisplayName("3-2-0. 특정 게시글에 댓글 여러개 등록하고 각각 댓글 조회해보기")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test10(int cnt) {
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }

        List<CommentDto> foundComments = target.selectByBno(boardFormDto.getBno());
        assertTrue(cnt == foundComments.size());

        for (var foundComment : foundComments) {
            var commentDto = target.selectByCno(foundComment.getCno());
            assertTrue(isSameCommentDto(foundComment, commentDto));
        }

    }


    @DisplayName("3-2-1. 특정 게시글에 댓글 여러개 등록하고 랜덤으로 댓글 조회해보기")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test11(int cnt) {
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }

        List<CommentDto> foundComments = target.selectByBno(boardFormDto.getBno());
        assertTrue(cnt == foundComments.size());

        sort(fixture, foundComments);
        int randomIdx = chooseRandom(cnt);
        assertTrue(isSameCommentDto(fixture.get(randomIdx), foundComments.get(randomIdx)));
    }

    @DisplayName("4-2-0. 특정 게시글에 댓글 여러개 등록하고 전체 조회해보기")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 15, 20})
    public void test12(int cnt) {
        createFixture(boardFormDto.getBno(), cnt);
        for (var commentDto : fixture) {
            assertTrue(1 == target.insert(commentDto));
        }

        List<CommentDto> foundComments = target.selectAll();
        assertTrue(cnt == foundComments.size());

        sort(fixture, foundComments);
        for (int i=0; i<cnt; i++) {
            assertTrue(isSameCommentDto(fixture.get(i), foundComments.get(i)));
        }
    }




    private CommentDto createCommentDto(int bno, int i) {
        CommentDto commentDto = new CommentDto();
        commentDto.setBno(bno);
        commentDto.setWriter("writer" + i);
        commentDto.setContent("content" + i);
        commentDto.setReg_id("reg_id");
        commentDto.setUp_id("up_id");
        return commentDto;
    }

    private void createFixture(int bno, int cnt) {
        for (int i = 0; i < cnt; i++) {
            var commentDto = createCommentDto(bno, i);
            fixture.add(commentDto);
        }
    }


    private void createBoardFormDto() {
        boardFormDto = new BoardFormDto();

        boardFormDto.setCate_code("0101");
        boardFormDto.setId("id");
        boardFormDto.setTitle("title");
        boardFormDto.setWriter("writer");
        boardFormDto.setView_cnt(0);
        boardFormDto.setReco_cnt(0);
        boardFormDto.setNot_reco_cnt(0);
        boardFormDto.setContent("content");
        boardFormDto.setComt("comt");
        boardFormDto.setReg_date("2021-01-01");
        boardFormDto.setReg_id("reg_id");
        boardFormDto.setUp_date("2021-01-01");
        boardFormDto.setUp_id("up_id");

    }

    private boolean isSameCommentDto(CommentDto dto1, CommentDto dto2) {
        return dto1.getBno().equals(dto2.getBno())
                && dto1.getWriter().equals(dto2.getWriter())
                && dto1.getContent().equals(dto2.getContent());
    }

    private void sort(List<CommentDto> list1, List<CommentDto> list2) {
        list1.sort((o1, o2) -> o1.getWriter().compareTo(o2.getWriter()));
        list2.sort((o1, o2) -> o1.getWriter().compareTo(o2.getWriter()));
    }

    private int chooseRandom(int size) {
        return (int) (Math.random() * size);
    }
}