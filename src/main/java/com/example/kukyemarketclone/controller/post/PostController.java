package com.example.kukyemarketclone.controller.post;

import com.example.kukyemarketclone.aop.AssignMemberId;
import com.example.kukyemarketclone.dto.post.PostCreateRequest;
import com.example.kukyemarketclone.dto.post.PostUpdateRequest;
import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.service.post.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Post Controller", tags = "Post")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @ApiOperation(value = "게시글 생성", notes = "게시글을 생성한다")
    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId //인증된 사용자의 정보를 통해서 게시글의 작성자 직접 지정
    public Response create(@Valid @ModelAttribute PostCreateRequest req){
        //게시글 데이터를 이미지와 함께 전달받기 위해 요청하는 Content-Type이 multipart/form-data를 이용해야함
        // 따라서 파라미터에 @ModelAttribute를 선언 - 제약조건 위배시 BindException 발생
        return Response.success(postService.create(req));
    }

    @ApiOperation(value = "게시글 조회", notes = "게시글을 조회한다")
    @GetMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@ApiParam(value="게시글 id",required = true) @PathVariable Long id){
        return Response.success(postService.read(id));
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다")
    @DeleteMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "게시글 id",required = true) @PathVariable Long id){
        postService.delete(id);
        return Response.success();
    }

    @ApiOperation(value="게시글 수정", notes = "게시글을 수정한다")
    @PutMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@ApiParam(value = "게시글 id", required = true)
                               @PathVariable Long id, @Valid @ModelAttribute PostUpdateRequest req){
        return Response.success(postService.update(id, req));
    }
}
