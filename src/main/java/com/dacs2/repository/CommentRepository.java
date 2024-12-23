package com.dacs2.repository;

import com.dacs2.model.Comment;
import com.dacs2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByLevel(Integer level);

    List<Comment> findByProductAndUser_Role(Product product, String userRole);

    Comment findByParentComment(Comment parentComment);
}
