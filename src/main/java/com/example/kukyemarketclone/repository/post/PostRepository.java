package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
