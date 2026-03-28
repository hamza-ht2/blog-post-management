package com.example.post_management.services;

import com.example.post_management.models.Tag;
import com.example.post_management.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }
    public Tag getTagById(Long tagId){
        return tagRepository.findById(tagId).orElseThrow(()-> new RuntimeException("tag not found with id :"+tagId));
    }
    public Tag getTagByName(String name){
        return tagRepository.findTagByName(name).orElseThrow(()-> new RuntimeException("tag not found with this name :"+name));
    }
    public Tag createTag(Tag tag){
        if (tagRepository.existsByName(tag.getName())){
            throw new RuntimeException("tag already exist");
        }
        return tagRepository.save(tag);
    }
    public Tag updateTagInfo(Long tagId, Tag updatedOne){
        Tag tag = getTagById(tagId);
        if (updatedOne.getName() != null) tag.setName(updatedOne.getName());
        return tagRepository.save(tag);
    }
    public void deleteTag(Long tagId){
        tagRepository.deleteById(tagId);
    }
}
