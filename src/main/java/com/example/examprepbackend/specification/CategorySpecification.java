package com.example.examprepbackend.specification;

import com.example.examprepbackend.entity.CategoryQuestion;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

        public static Specification<CategoryQuestion> hasNameLike(String name) {
            return (root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        }

        public static Specification<CategoryQuestion> hasId(Integer id) {
            return (root, query, cb) ->
                    cb.equal(root.get("id"), id);
        }
    public static Specification<CategoryQuestion> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }
}
