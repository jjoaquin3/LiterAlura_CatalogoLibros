package com.jjoaquin3.literalura.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjoaquin3.literalura.model.Author;
import com.jjoaquin3.literalura.model.Book;
import com.jjoaquin3.literalura.model.dto.BookResponseDTO;
import com.jjoaquin3.literalura.model.dto.AuthorResponseDTO;
import com.jjoaquin3.literalura.model.dto.GutendexResponseDTO;
import com.jjoaquin3.literalura.repository.IAuthorRepository;
import com.jjoaquin3.literalura.repository.IBookRepository;
import com.jjoaquin3.literalura.util.ApiClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;

@Service
public class Gutendexservice
{
    private final String API_URL = "https://gutendex.com/books/";
    private final ApiClient API_CLIENT = new ApiClient();
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private IBookRepository bookRepository;
    private IAuthorRepository authorRepository;

    public Gutendexservice(IBookRepository bookRepository, IAuthorRepository authorRepository)
    {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void findAll()
    {
        try
        {
            String jsonResponseApi = API_CLIENT.get(API_URL).orElseThrow(() -> new RuntimeException("Error al obtener la respuesta de la API"));
            GutendexResponseDTO responseApi = OBJECT_MAPPER.readValue(jsonResponseApi, GutendexResponseDTO.class);
            System.out.println(responseApi.getBooks().stream().limit(5).toList());
        }
        catch (RuntimeException e)
        {
            System.err.println("Excepción al obtener la lista de libros: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("Excepción inesperada al obtener la lista de libros: " + e.getMessage());
        }
    }

    public void findByTitle(String title)
    {
        try
        {
            //String apiUrl = (API_URL + "?search=" + title).trim().replace(" ", "+");
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            String apiUrl = API_URL + "?search=" + encodedTitle;

            String jsonResponseApi = API_CLIENT.get(apiUrl).orElseThrow(() -> new RuntimeException("Error al obtener la respuesta de la API"));
            GutendexResponseDTO responseApi = OBJECT_MAPPER.readValue(jsonResponseApi, GutendexResponseDTO.class);
            BookResponseDTO bookDTO = responseApi.getBooks().get(0);
            System.out.println(bookDTO);
            System.out.println(" ");
            List<Book> librosDB = bookRepository.findByTitle(bookDTO.getTitle());
            if (!librosDB.isEmpty())
            {
                System.out.println("El libro '" + bookDTO.getTitle() + "' ya existe en la base de datos.");
                return;
            }
            saveBookAndAuthors(bookDTO);
            System.out.println("Se ha guardado el libro '" + bookDTO.getTitle() + "' y sus autores en la base de datos.");
        }
        catch (RuntimeException e)
        {
            System.err.println("Excepción al obtener la lista de libros en DB: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("Excepción inesperada al obtener la lista de libros: " + e.getMessage());
        }
    }

    @Transactional
    private void saveBookAndAuthors(BookResponseDTO bookDTO)
    {
        // Convertir el DTO del libro a entidad Book
        Book book = convertToBookEntity(bookDTO);

        // Guardar el libro en la base de datos
        book = bookRepository.save(book);

        // Lista para almacenar los autores asociados al libro
        List<Author> associatedAuthors = new ArrayList<>();

        // Convertir los DTOs de autores a entidades Author
        for (AuthorResponseDTO authorDTO : bookDTO.getAuthors())
        {
            // Buscar todos los autores que coincidan con el nombre
            List<Author> existingAuthors = authorRepository.findByName(authorDTO.getName());

            Author author;
            if (!existingAuthors.isEmpty())
            {
                // Si se encontró algún autor con el nombre, usar el primero encontrado
                author = existingAuthors.get(0);
            }
            else
            {
                // Si no existe ningún autor con el nombre, crear uno nuevo y guardarlo
                author = convertToAuthorEntity(authorDTO);
                author = authorRepository.save(author);
            }

            // Agregar el autor a la lista de autores asociados al libro
            associatedAuthors.add(author);
        }

        // Asociar los autores al libro y viceversa
        for (Author author : associatedAuthors)
        {
            book.getAuthors().add(author);
            //author.getBooks().add(book);
        }

        // Guardar el libro actualizado con los autores asociados
        bookRepository.save(book);
        System.out.println("Libro Guardado");
    }

    private Book convertToBookEntity(BookResponseDTO bookDTO)
    {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setLanguages(bookDTO.getLanguages().toString());
        book.setDownload_count(bookDTO.getDownloadCount());
        book.setAuthors(new HashSet<>()); // Inicializar la lista de autores
        return book;
    }

    private Author convertToAuthorEntity(AuthorResponseDTO authorDTO)
    {
        Author author = new Author();
        author.setName(authorDTO.getName());
        author.setBirthYear(authorDTO.getBirthYear());
        author.setDeathYear(authorDTO.getDeathYear());
        return author;
    }

    public void findAllAuthors()
    {
        List<Author> authors = authorRepository.findAll();
        authors.forEach(author ->
        {
            String authorInfo = "ID: " + author.getId() + ", Name: " + author.getName() + ", Birth Year: " + author.getBirthYear() + ", Death Year: " + author.getDeathYear();
            System.out.println(authorInfo);
        });
    }

    public void findAllBooks()
    {
        List<Book> books = bookRepository.findAll();
        System.out.println("Libros: "+ books.size());
        books.forEach(book ->
        {
            String bookInfo = "ID: " + book.getId() + ", Title: " + book.getTitle() + ", Languages: " + book.getLanguages() + ", Download Count: " + book.getDownload_count();
            System.out.println(bookInfo);
        });
    }

    public void findByAuthorsAliveInYear(int birthYear, int deathYear)
    {
        List<Author> authors = authorRepository.findByBirthYearLessThanOrDeathYearGreaterThan(birthYear, deathYear);
        System.out.println("Autores: "+ authors.size());
        authors.forEach(author ->
        {
            String authorInfo = "ID: " + author.getId() + ", Name: " + author.getName() + ", Birth Year: " + author.getBirthYear() + ", Death Year: " + author.getDeathYear();
            System.out.println(authorInfo);
        });
    }

    public void findBylanguagesContains(String language)
    {
        List<Book> books = bookRepository.findBylanguagesContains(language);
        System.out.println("Libros: "+ books.size());
        books.forEach(book ->
        {
            String bookInfo = "ID: " + book.getId() + ", Title: " + book.getTitle() + ", Languages: " + book.getLanguages() + ", Download Count: " + book.getDownload_count();
            System.out.println(bookInfo);
        });
    }

}