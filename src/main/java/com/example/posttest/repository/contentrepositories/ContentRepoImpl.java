package com.example.posttest.repository.contentrepositories;


import com.example.posttest.entitiy.Content;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContentRepoImpl implements ContentRepo {



    private final EntityManager em;








    public Optional<Content> findbytitle(String titles){
        String query="select c from Content c where c.title=:titles";

        try {
            Content content = em.createQuery(query, Content.class)
                    .setParameter("titles", titles)
                    .getSingleResult();
            return Optional.of(content);

        }
        catch(Exception e){

            return Optional.empty();
        }


    }





}
