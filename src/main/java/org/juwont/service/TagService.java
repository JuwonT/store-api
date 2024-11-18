package org.juwont.service;

import lombok.RequiredArgsConstructor;
import org.juwont.entity.Tag;
import org.juwont.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repository;

    public Tag createTag(final String name) {
        Tag tag = new Tag(name);
        repository.create(tag);
        return tag;
    }

    @Transactional
    public Tag getOrCreateTag(final String name) {
        return findByName(name).orElseGet(() -> createTag(name));
    }

    private Optional<Tag> findByName(final String name) {
        return repository.findByName(name);
    }
}
