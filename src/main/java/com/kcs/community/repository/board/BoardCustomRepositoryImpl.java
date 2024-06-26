package com.kcs.community.repository.board;

import static com.kcs.community.entity.QBoard.board;

import com.kcs.community.entity.Board;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> findByUserId(Long userId) {
        return jpaQueryFactory.selectFrom(board)
                .where(board.user.id.eq(userId))
                .fetch();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        jpaQueryFactory.delete(board)
                .where(board.user.id.eq(userId))
                .execute();
    }
}
