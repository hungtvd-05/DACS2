package com.dacs2.service;

import com.dacs2.model.Comment;
import com.dacs2.model.Product;
import com.dacs2.model.UserDtls;

import java.util.List;

public interface CommentService {

    public Comment addReply(Long commentId, String replyComment, UserDtls userDtls);

    public List<Comment> getAllReplyByAdmin(Product product);

}
