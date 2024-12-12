package com.dacs2.service.impl;

import com.dacs2.model.Comment;
import com.dacs2.model.Product;
import com.dacs2.model.UserDtls;
import com.dacs2.repository.CommentRepository;
import com.dacs2.repository.UserRepository;
import com.dacs2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment addReply(Long commentId, String replyComment, UserDtls userDtls) {

        Comment parent = commentRepository.findById(commentId).get();
        Comment comment = new Comment();

        comment.setContent(replyComment);
        comment.setUser(userDtls);
        comment.setProduct(parent.getProduct());
        comment.setParentComment(parent);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLevel(parent.getLevel() + 1);

        commentRepository.save(comment);

        return comment;
    }

    @Override
    public List<Comment> getAllReplyByAdmin(Product product) {
        return commentRepository.findByProductAndUser_Role(product, "ROLE_ADMIN");
    }
}
