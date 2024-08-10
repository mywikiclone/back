package com.example.posttest.repository.contentrepositories;

import com.example.posttest.entitiy.Content;

import java.util.Optional;

public interface ContentRepo {


   Optional<Content> findbytitle(String titles);




}
