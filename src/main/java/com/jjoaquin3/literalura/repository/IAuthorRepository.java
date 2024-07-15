package com.jjoaquin3.literalura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jjoaquin3.literalura.model.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAuthorRepository extends JpaRepository<Author, Long>
{
    List<Author> findByName(String name);
    List<Author> findByBirthYearLessThanOrDeathYearGreaterThan(int birth_year, int death_year);
}