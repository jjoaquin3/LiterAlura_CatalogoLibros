package com.jjoaquin3.literalura;

import com.jjoaquin3.literalura.endpoint.LiterAlura;
import com.jjoaquin3.literalura.repository.IAuthorRepository;
import com.jjoaquin3.literalura.repository.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner
{
    @Autowired
    IBookRepository bookRepository;

    @Autowired
    IAuthorRepository authorRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        LiterAlura literAlura = new LiterAlura(bookRepository, authorRepository);
        literAlura.showMenu();
    }
}
