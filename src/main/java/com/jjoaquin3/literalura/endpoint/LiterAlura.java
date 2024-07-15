package com.jjoaquin3.literalura.endpoint;

import com.jjoaquin3.literalura.repository.IAuthorRepository;
import com.jjoaquin3.literalura.repository.IBookRepository;
import com.jjoaquin3.literalura.service.Gutendexservice;

import java.util.Scanner;

public class LiterAlura
{
    private IBookRepository bookRepository;
    private IAuthorRepository authorRepository;

    public LiterAlura(IBookRepository bookRepository, IAuthorRepository authorRepository)
    {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

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

    public void showMenuOptions(Scanner scanner, Gutendexservice gutendexService, int option)
    {
        switch (option)
        {
            case 1:
                gutendexService.findAll();
                break;
            case 2:
                String title = this.scannerNextString(scanner, "Ingrese el título que desea buscar: ");
                gutendexService.findByTitle(title);
                break;
            case 3:
                gutendexService.findAllBooks();
                break;
            case 4:
                gutendexService.findAllAuthors();
                break;
            case 5:
                int anio = this.scannerNextInt(scanner,"Ingrese el año donde el autor estaba vivo: ");
                gutendexService.findByAuthorsAliveInYear(anio, anio);
                break;
            case 6:
                String sms = """
                        Seleccione el numeral (1,2,3...) del idioma que desea buscar
                        1. es
                        2. en
                        3. fr
                        4. regresar:\s""";
                int selection = this.scannerNextInt(scanner,sms);
                switch (selection)
                {
                    case 1:
                        gutendexService.findBylanguagesContains("es");
                        break;
                    case 2:
                        gutendexService.findBylanguagesContains("en");
                        break;
                    case 3:
                        gutendexService.findBylanguagesContains("fr");
                        break;
                    case 4:
                        break;
                }
                break;
        }
    }

    private String scannerNextString(Scanner scanner, String sms)
    {
        System.out.print(sms);
        return (scanner.nextLine()).trim().toUpperCase();
    }

    private Integer scannerNextInt(Scanner scanner, String sms)
    {
        try
        {
            System.out.print(sms);
            return Integer.parseInt(scanner.nextLine());
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine();
        }
        return 0;
    }
}
