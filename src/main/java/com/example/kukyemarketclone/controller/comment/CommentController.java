package com.example.kukyemarketclone.controller.comment;

import com.example.kukyemarketclone.aop.AssignMemberId;
import com.example.kukyemarketclone.dto.comment.CommentCreateRequest;
import com.example.kukyemarketclone.dto.comment.CommentReadCondition;
import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.service.comment.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "CommentController", tags = "Comment")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록 조회", notes = "댓글 목록을 조회한다")
    @GetMapping("/api/comments")
    @ResponseStatus(HttpStatus.OK)
    public Response readAll(@Valid CommentReadCondition cond){
        return Response.success(commentService.readAll(cond));
    }

    @ApiOperation(value = "댓글 생성", notes = "댓글을 생성한다")
    @PostMapping("/api/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId
    public Response create(@Valid @RequestBody CommentCreateRequest req){
        commentService.create(req);
        return Response.success();
    }

    @ApiOperation(value = "댓글 삭제",notes = "댓글을 삭제한다")
    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "댓글 id",required = true) @PathVariable Long id){
        commentService.delete(id);
        return Response.success();
    }



}
