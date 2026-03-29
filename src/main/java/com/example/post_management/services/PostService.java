package com.example.post_management.services;

import com.example.post_management.models.Category;
import com.example.post_management.models.Post;
import com.example.post_management.models.Tag;
import com.example.post_management.models.User;
import com.example.post_management.models.enums.PostStatus;
import com.example.post_management.repositories.CategoryRepository;
import com.example.post_management.repositories.PostRepository;
import com.example.post_management.repositories.TagRepository;
import com.example.post_management.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public Post createPost(Long authorId, Post post){
        User author = userRepository.findById(authorId).orElseThrow(()->new RuntimeException("author not found with id :"+authorId));
        if (!author.isEnabled()) {
            throw new RuntimeException("your account is disabled , you're not allowed to create any post");
        }
        int wordCount = post.getContent().split("\\s+").length;
        post.setAuthor(author);
        post.setSlug(generateSlug(post.getTitle()));
        post.setReadingTime(Math.max(1, wordCount / 200));
        post.setPostStatus(PostStatus.DRAFT);
        if(post.getExcerpt() == null || post.getExcerpt().isBlank()){
            post.setExcerpt(post.getContent().substring(0, Math.min(150, post.getContent().length())));
        }
        return postRepository.save(post);
    }

    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElseThrow(()-> new RuntimeException("post not found with id : "+postId));
    }
    public Post getPostBySlug(String slug){
        return postRepository.findPostBySlug(slug).orElseThrow(()-> new RuntimeException("post not found with slug : "+ slug));
    }
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
    public List<Post> getAllPublishedPosts(){
        return postRepository.findPostByPostStatus(PostStatus.PUBLISHED);
    }
    public List<Post> getPostsByAuthorId(Long authorId){
        return postRepository.findPostsByAuthorId(authorId);
    }
    public List<Post> getPublishedPostByAuthor(Long authorId){
        return postRepository.findPostsByAuthorIdAndPostStatus(authorId, PostStatus.PUBLISHED);
    }
    public List<Post> getDraftsByAuthor(Long authorId){
        return postRepository.findPostsByAuthorIdAndPostStatus(authorId, PostStatus.DRAFT);
    }
    public List<Post> getAllDrafts(){
        return postRepository.findPostByPostStatus(PostStatus.DRAFT);
    }
    public List<Post> getPostsByCategory(Long categoryId){
        categoryRepository.findById(categoryId).orElseThrow(()-> new RuntimeException("category not found with id :"+categoryId));
        return postRepository.findPostsByCategoryId(categoryId);
    }
    public List<Post> getPublishedPostsByCategory(Long categoryId){
        categoryRepository.findById(categoryId).orElseThrow(()-> new RuntimeException("category not found with id :"+categoryId));
        return postRepository.findPostsByPostStatusAndCategoryId(PostStatus.PUBLISHED, categoryId);
    }
    public List<Post> getPostsByTag(Tag tag){
        return postRepository.findPostsByTagsContaining(tag);
    }
    public List<Post> getLastPublishedPosts(){
        return postRepository.findPostsByPostStatusOrderByCreatedAtDesc(PostStatus.PUBLISHED);
    }
    public List<Post> searchPostsByTitle(String title){
        return postRepository.findPostsByTitleContainingIgnoreCase(title);
    }
    public List<Post> getPostsByRangeDate(LocalDateTime start, LocalDateTime end){
        return postRepository.findPostsByCreatedAtBetween(start,end);
    }

    public Post updatePost(Long postId, Post updatedPost){
        Post existing = getPostById(postId);
        if(updatedPost.getTitle() != null){
            existing.setTitle(updatedPost.getTitle());
            existing.setSlug(generateSlug(updatedPost.getTitle()));
        }
        if(updatedPost.getContent() != null){
            existing.setContent(updatedPost.getContent());
            int wordCount = updatedPost.getContent().split("\\s+").length;
            existing.setReadingTime(Math.max(1, wordCount / 200));
            if (updatedPost.getExcerpt() == null || updatedPost.getExcerpt().isBlank()) {
                existing.setExcerpt(updatedPost.getContent().substring(0, Math.min(150, updatedPost.getContent().length())));
            }
        }
        if (updatedPost.getExcerpt() != null) existing.setExcerpt(updatedPost.getExcerpt());
        if (updatedPost.getCoverImage() != null) existing.setCoverImage(updatedPost.getCoverImage());
        if (updatedPost.getCategory() != null) existing.setCategory(updatedPost.getCategory());
        return postRepository.save(existing);
    }
    public Post updateCoverImage(Long postId, String newCoverImage){
        Post post = getPostById(postId);
        post.setCoverImage(newCoverImage);
        return postRepository.save(post);
    }
    public Post updatePostCategory(Long postId, Long categoryId){
        Post post = getPostById(postId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new RuntimeException("category not found"));
        post.setCategory(category);
        return postRepository.save(post);
    }

    public Post addTagsToPost(Long postId, Long tagId){
        Post post = getPostById(postId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(()-> new RuntimeException("tag not found "));
        if(post.getTags().contains(tag)){
            throw new RuntimeException("tag already added to post");
        }
        post.getTags().add(tag);
        return postRepository.save(post);
    }
    public Post removeTagsFromPost(Long postId, Long tagId){
        Post post = getPostById(postId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(()-> new RuntimeException("tag not found"));
        if (!post.getTags().contains(tag)){
            throw new RuntimeException("tag not found on post");
        }
        post.getTags().remove(tag);
        return postRepository.save(post);
    }

    public Post publishPost(Long postId){
        Post post = getPostById(postId);
        if (post.getPostStatus().equals(PostStatus.PUBLISHED)){
            throw new RuntimeException("post already published");
        }
        post.setPostStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
    public Post unpublishPost(Long postId){
        Post post = getPostById(postId);
        if (!post.getPostStatus().equals(PostStatus.PUBLISHED)){
            throw new RuntimeException("post is already a draft");
        }
        post.setPostStatus(PostStatus.DRAFT);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }
    public Post incrementView(Long postId){
        Post post = getPostById(postId);
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }
    public Post rejectPost(Long postId){
        Post post = getPostById(postId);
        if (post.getPostStatus().equals(PostStatus.REJECTED)){
            throw new RuntimeException("POST ALREADY REJECTED");
        }
        post.setPostStatus(PostStatus.REJECTED);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }
    public void deletePost(Long postId){
        Post post = getPostById(postId);
        postRepository.delete(post);
    }
    public List<Post> getAllRejectedPost(){
        return postRepository.findPostByPostStatus(PostStatus.REJECTED);
    }
    private String generateSlug(String title) {
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
        if (postRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        return slug;
    }

}
