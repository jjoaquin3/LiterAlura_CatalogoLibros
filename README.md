# # Catalogo Libros
![GitHub](https://img.shields.io/github/license/dropbox/dropbox-sdk-java)
![version](https://img.shields.io/badge/version-1.0.1-blue)

Proyecto de desarrollo de un Catálogo de Libros que ofrezca interacción textual (vía consola) con los usuarios, proporcionando al menos 5 opciones de interacción. Los libros se buscarán a través de una API específica.

## Indice

El proyecto consta de los siguiente pasos y están disponibles en la sección adyacente:

1. [Requerimientos](#requerimientos)
2. [Estructura Proyecto](#estructura-proyecto)
3. [Filtrado](#filtrado)
4. [Consumo API](#consumo-api)
5. [Manejo Respuestas](#manejo-respuestas)
6. [Endpoint](#endpoint)
7. [Consideraciones](#consideraciones)
8. [Ejemplos](#ejemplos)

## Requerimientos

El proyecto nace como un forma de demostrar la capacidades de elegir las librerias, la organización y bien implementar una solución para la obtención de información para la conversion de monedas.

| Recursos                    | Información                                   |
|-----------------------------|-----------------------------------------------|
| Eclipse Temurin JDK 17      | https://adoptium.net/es/temurin/releases/     |
| Spring Boot 3.3.1           | https://maven.apache.org/            |
| Maven Project 4.0.0         | https://maven.apache.org/            |
| Project lombok 1.18.30      | https://projectlombok.org/setup/maven         |
| ModelMapper 3.2.0           | https://modelmapper.org         | |
| GitHub                      | https://github.com/jjoaquin3 |

Estos serian los principales, las otras libreria se pueden encontrar en pom.xml del API

## Estructura Proyecto

Estructura principal del proyecto
```plaintext
├───src
│   ├───main
│      ├───java
│      │   └───com
│      │       └───jjoaquin3
│      │           └───literalura
│      │               ├───endpoint
│      │               ├───model
│      │               │   └───dto
│      │               ├───repository
│      │               ├───service
│      │               └───util
│      └───resources
│          ├───static
│           └───templates
├───docker-db
    └───initdb
```

- endpoint: Divide la comunciación entre el usuario y la logica de negocio.
- service: Capa logica que gestion realiza las operaciones en este caso seria usar los parámetros que envia el EndPoint y usar el ApiCliente para consultar la API de https://gutendex.com/.
- repository: Capa que permite las operaciones y comunicación con la DB
- model: Represtanción de las entidades tanto para request, reponse o bien entidades, en este caso solo se uso para response.
- util: Tiene los auxiliares, en este caso un ApiCliente para el consumo de APIs.
- resources: Para establecer configuraciones generales y comportamientos del aplicativo
- docker-db: Archivo para aprovisionar un contenedor que albergara la DB


## Filtrado

En la versión actual se limitó a ciertas opciones de comunicación en un menú:
```sh
**********************************************
Bienvenid@ a tu LiterAlura "Catálogo de Libros"
**********************************************
Elige una opcion:
1. Listar Libros (5)
2. Buscar Libro x Título
3. Listar Libros Registrados
4. Listar Autores Registrados
5. Listar Autores vivos en determinado año
6. Listar Libros por idioma
7. Salir
**********************************************
Opción:
```

Posterior la lectura de la opción 2
```sh
Ingrese el título que desea buscar:
```

Posterior la lectura de la opción 5
```sh
Ingrese el año donde el autor estaba vivo:
```

Posterior la lectura de la opción 6
```sh
Seleccione el numeral (1,2,3...) del idioma que desea buscar
1. es
2. en
3. fr
4. regresar: 
```

posterior realiza alguna operación se solicita dar enter para regresar al menú
```sh
Presiona Enter para cargar el menú...
```

Finalmente si se selecciona la opcion 7, un mensaje de despedia y cierre.
```sh
Opción: 7
Gracias por utilizar nuestro servicio, ten un buen día c:
```

## Consumo API

#### **ApiClient.java**

Establece una clase custom para el consumo a travez del verbo GET.

```java
public Optional<String> get(String uri)
{
    HttpRequest request = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(uri))
            .timeout(Duration.ofSeconds(16))
            .header("Content-Type", "application/json")
            .build();

    try
    {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return Optional.of(response.body());
    }
    catch (IOException | InterruptedException e)
    {
        System.err.println("Exception: " + e.getMessage());
        return Optional.empty();
    }
}
```

## Manejo Respuestas

Explorando la documentación (https://github.com/garethbjohnson/gutendex) para nuestro requerimiento las posibles respuestas son.

#### **Success**
Notar que results es un arreglo de los libros encontrados
```json
{
  "count": 26,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": 73796,
      "title": "I pescatori di trepang",
      "authors": [
        {
          "name": "Salgari, Emilio",
          "birth_year": 1862,
          "death_year": 1911
        }
      ],
      "translators": [],
      "subjects": [
        "Adventure stories",
        "Oceania -- Fiction"
      ],
      "bookshelves": [],
      "languages": [
        "it"
      ],
      "copyright": false,
      "media_type": "Text",
      "formats": {
        "text/html": "https://www.gutenberg.org/ebooks/73796.html.images",
        "application/epub+zip": "https://www.gutenberg.org/ebooks/73796.epub3.images",
        "application/x-mobipocket-ebook": "https://www.gutenberg.org/ebooks/73796.kf8.images",
        "text/plain; charset=us-ascii": "https://www.gutenberg.org/ebooks/73796.txt.utf-8",
        "application/rdf+xml": "https://www.gutenberg.org/ebooks/73796.rdf",
        "image/jpeg": "https://www.gutenberg.org/cache/epub/73796/pg73796.cover.medium.jpg",
        "application/octet-stream": "https://www.gutenberg.org/cache/epub/73796/pg73796-h.zip"
      },
      "download_count": 475
    }
  ]
}
```

#### **Error**
```json
{
  "count": 0,
  "next": null,
  "previous": null,
  "results": []
}
```

#### **CurrencyResponseDTO**
Nos permite mapear las respuestas del API de Gutendex a DTOs con ModelMapper.

```java
public class GutendexResponseDTO
{
    @JsonAlias("results")
    List<BookResponseDTO> books;
}
```
```Java
public class BookResponseDTO
{
    private String title;
    private List<AuthorResponseDTO> authors;
    private List<String> languages;

    @JsonAlias("download_count")
    private int downloadCount;
}
```
```Java
public class AuthorResponseDTO
{
    private String name;
    @JsonAlias("birth_year")
    private Integer birthYear;

    @JsonAlias("death_year")
    private Integer deathYear;
}
```


#### **Repository**

Definimos Interfaces que heredan de JpaRepository para las operaciones CRUD

Tambien hacemos uso de los repository para consultar y guardar.

```Java
@Repository
public interface IBookRepository extends JpaRepository<Book, Long>
{
    List<Book> findByTitle(String title);

    List<Book> findBylanguagesContains(String language);
}
```

```Java
@Repository
public interface IAuthorRepository extends JpaRepository<Author, Long>
{
    List<Author> findByName(String name);
    List<Author> findByBirthYearLessThanOrDeathYearGreaterThan(int birth_year, int death_year);
}
```

#### **Gutendexservice**
Hacemos uso de ApiCliente luego de construir la URL, pero con ObjectMapper mapeamos el JSON de la respuesta a nuestros DTO.


```Java
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
```

```Java
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
```

```Java
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
```

## Endpoint

#### **LiterAlura**

Permite comunicación entre el Cliente, Sistema cargando el menu y el manejo de instancias del services y propia.
Principalmente el siguiente metodo:

```java
public void showMenu()
{
    Scanner scanner = new Scanner(System.in);
    Gutendexservice gutendexService = new Gutendexservice(bookRepository, authorRepository);

    int option;
    while (true)
    {
        try
        {
            System.out.println("**********************************************");
            System.out.println("Bienvenid@ a tu LiterAlura \"Catálogo de Libros\"");
            System.out.println("**********************************************");
            System.out.println("Elige una opcion:");
            System.out.println("1. Listar Libros (5)");
            System.out.println("2. Buscar Libro x Título");
            System.out.println("3. Listar Libros Registrados");
            System.out.println("4. Listar Autores Registrados");
            System.out.println("5. Listar Autores vivos en determinado año");
            System.out.println("6. Listar Libros por idioma");
            System.out.println("7. Salir");
            System.out.println("**********************************************");
            System.out.print("Opción: ");

            option = Integer.parseInt(scanner.nextLine());
            if (option >= 1 && option <= 6) showMenuOptions(scanner, gutendexService, option);
            else if (option == 7) break;
            else System.out.println("Opción no válida. Por favor elija una opción del 1 al 7.");

            System.out.println();
            System.out.println("Presiona Enter para cargar el menú...");
            scanner.nextLine();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine();
        }
    }
    System.out.println("Gracias por utilizar nuestro servicio, ten un buen día c:");
    scanner.close();
}
```

## Consideraciones

- Para el desarrollo se configuro una instancia de docker con postgresql 16 que pueda servir como la DB
``` yml
version: '3.8'

services:
  postgres:
    image: postgres:16
    container_name: LITERALURA_DB
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: ALURA_TEST
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./initdb:/docker-entrypoint-initdb.d
    ports:
      - "5432:5432"
    restart: always

volumes:
  pgdata:
```

- Al docker se le configuró 1 usuario que tendra acceso a la DB y al esquema del programa
- 
```ssh
-- Crear el esquema LITERALURA
CREATE SCHEMA "LITERALURA";

-- Configurar la diferencia entre mayúsculas y minúsculas
ALTER DATABASE "ALURA_TEST" SET standard_conforming_strings = 'on';

-- Crear el usuario user_Test con contraseña passwort_Test
CREATE USER "user_Test" WITH PASSWORD 'password_Test';

-- Dar permisos al usuario user_Test sobre el esquema LITERALURA
GRANT USAGE ON SCHEMA "LITERALURA" TO "user_Test";
GRANT CREATE ON SCHEMA "LITERALURA" TO "user_Test";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "LITERALURA" TO "user_Test";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "LITERALURA" TO "user_Test";

-- Dar permisos al usuario user_Test para crear objetos dentro del esquema LITERALURA
ALTER DEFAULT PRIVILEGES IN SCHEMA "LITERALURA" GRANT ALL ON TABLES TO "user_Test";
ALTER DEFAULT PRIVILEGES IN SCHEMA "LITERALURA" GRANT ALL ON SEQUENCES TO "user_Test";
``` 

- Se realizo una configuraciones los entity en relación de muchos a muchos generando una tabla intermedia (master detalle)

```java
public class Author
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "birth_year", nullable = true)
    private Integer birthYear;

    @Column(name = "death_year", nullable = true)
    private Integer deathYear;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<Book> books;
}
```

```java
public class Book
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 1024)
    private String title;

    @Column(name = "languages", nullable = false, length = 512)
    private String languages;

    @Column(name = "download_count", nullable = false)
    private int download_count;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book"), inverseJoinColumns = @JoinColumn(name = "author"))
    private Set<Author> authors;
}

```

La configuración @ManyToMany generó la siguiente tabla "book_author"
```sql
-- "LITERALURA".book_author definition

-- Drop table

-- DROP TABLE "LITERALURA".book_author;

CREATE TABLE "LITERALURA".book_author (
	book int8 NOT NULL,
	author int8 NOT NULL,
	CONSTRAINT book_author_pkey PRIMARY KEY (book, author),
	CONSTRAINT fkhgit4rar2knbqrcc9tyfeifdy FOREIGN KEY (author) REFERENCES "LITERALURA".author(id),
	CONSTRAINT fkn5ugh6f9cp0a7o9jn2pmvm9mr FOREIGN KEY (book) REFERENCES "LITERALURA".book(id)
);
```



- Se realizo configuraciones para realizar con URLEncoder para no tener problemas con caracteres en la URL

``` Java
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());        
```

## Ejemplos

- Buscar Libro x Título
```ssh
**********************************************
Bienvenid@ a tu LiterAlura "Catálogo de Libros"
**********************************************
Elige una opcion:
1. Listar Libros (5)
2. Buscar Libro x Título
3. Listar Libros Registrados
4. Listar Autores Registrados
5. Listar Autores vivos en determinado año
6. Listar Libros por idioma
7. Salir
**********************************************
Opción: 2
Ingrese el título que desea buscar: noche

BookResponseDTO(title=El libro de las mil noches y una noche; t. 1, authors=[AuthorResponseDTO(name=Anonymous, birthYear=null, deathYear=null)], languages=[es], downloadCount=2181)
Libro Guardado
Se ha guardado el libro 'El libro de las mil noches y una noche; t. 1' y sus autores en la base de datos.

Presiona Enter para cargar el menú...
```

- Listar Libros Registrados
```ssh
**********************************************
Bienvenid@ a tu LiterAlura "Catálogo de Libros"
**********************************************
Elige una opcion:
1. Listar Libros (5)
2. Buscar Libro x Título
3. Listar Libros Registrados
4. Listar Autores Registrados
5. Listar Autores vivos en determinado año
6. Listar Libros por idioma
7. Salir
**********************************************
Opción: 3

Libros: 4
ID: 1, Title: Romeo and Juliet, Languages: [en], Download Count: 77782
ID: 2, Title: Hamlet, Prince of Denmark, Languages: [en], Download Count: 2899
ID: 3, Title: The Philippine Islands, 1493-1898, Volume 33, 1519-1522: Explorations by early navigators, descriptions of the islands and their peoples, their history and records of the Catholic missions, as related in contemporaneous books and manuscripts, showing the political, economic, commercial and religious conditions of those islands from their earliest relations with European nations to the close of the nineteenth century, Languages: [en], Download Count: 724
ID: 4, Title: El libro de las mil noches y una noche; t. 1, Languages: [es], Download Count: 2181
```

- Listar Libros por idioma
``` ssh
**********************************************
Bienvenid@ a tu LiterAlura "Catálogo de Libros"
**********************************************
Elige una opcion:
1. Listar Libros (5)
2. Buscar Libro x Título
3. Listar Libros Registrados
4. Listar Autores Registrados
5. Listar Autores vivos en determinado año
6. Listar Libros por Idioma
7. Salir
**********************************************
Opción: 6
Seleccione el numeral (1,2,3...) del idioma que desea buscar
1. es
2. en
3. fr
4. regresar 
Opcion: 1

   Hibernate: select b1_0.id,b1_0.download_count,b1_0.languages,b1_0.title from "LITERALURA".book b1_0 where b1_0.languages like ? escape '\'
   Libros: 1
   ID: 4, Title: El libro de las mil noches y una noche; t. 1, Languages: [es], Download Count: 2181

Presiona Enter para cargar el menú...
```

Fin c:

