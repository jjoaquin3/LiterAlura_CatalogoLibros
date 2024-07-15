package com.jjoaquin3.literalura.repository;

import com.jjoaquin3.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long>
{
    List<Book> findByTitle(String title);

    List<Book> findBylanguagesContains(String language);
}
